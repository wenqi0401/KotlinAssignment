package com.example.myapplication.voucher

import android.content.Context
import android.util.Log
import com.example.myapplication.orderData.OrderDatabase
import com.example.myapplication.registerData.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class VoucherManager private constructor(
    private val context: Context,
    private val firebaseVoucherService: FirebaseVoucherService
) {
    private val database = OrderDatabase.getDatabase(context)
    private val voucherDao = database.voucherDao()
    private val userRepository = UserRepository()

    companion object {
        @Volatile
        private var INSTANCE: VoucherManager? = null

        fun getInstance(context: Context, firebaseVoucherService: FirebaseVoucherService): VoucherManager {
            return INSTANCE ?: synchronized(this) {
                val instance = VoucherManager(context.applicationContext, firebaseVoucherService)
                INSTANCE = instance
                instance
            }
        }
    }

    suspend fun initializeDefaultVouchers() {
        withContext(Dispatchers.IO) {
            try {
                // Check local database first
                val existingVouchers = voucherDao.getAllVouchers()
                if (existingVouchers.isEmpty()) {
                    // Check Firebase for existing vouchers
                    val firebaseVouchers = firebaseVoucherService.getAllVouchersFromFirebase()

                    if (firebaseVouchers.isNotEmpty()) {
                        // Save Firebase vouchers to local database
                        firebaseVouchers.forEach { voucher ->
                            voucherDao.insertVoucher(voucher)
                        }
                        Log.d("VoucherManager", "Loaded ${firebaseVouchers.size} vouchers from Firebase")
                    } else {
                        // Create default vouchers
                        val defaultVouchers = listOf(
                            VoucherEntity(
                                id = "WELCOME5",
                                code = "WELCOME5",
                                discountAmount = 5.0,
                                discountType = "FIXED",
                                minOrderAmount = 15.0,
                                expiryDate = System.currentTimeMillis() + (30 * 24 * 60 * 60 * 1000L),
                                isActive = true,
                                maxUsage = 1000,
                                currentUsage = 0,
                                description = "Welcome discount RM5 off"
                            ),
                            VoucherEntity(
                                id = "NEWUSER15",
                                code = "NEWUSER15",
                                discountAmount = 15.0,
                                discountType = "PERCENTAGE",
                                minOrderAmount = 25.0,
                                expiryDate = System.currentTimeMillis() + (45 * 24 * 60 * 60 * 1000L),
                                isActive = true,
                                maxUsage = 200,
                                currentUsage = 0,
                                description = "New user special 15% discount"
                            )
                        )

                        // Save to both local and Firebase
                        defaultVouchers.forEach { voucher ->
                            voucherDao.insertVoucher(voucher)
                            firebaseVoucherService.syncVoucherToFirebase(voucher)
                        }
                        Log.d("VoucherManager", "Created and synced ${defaultVouchers.size} default vouchers")
                    }
                }
            } catch (e: Exception) {
                Log.e("VoucherManager", "Error initializing vouchers", e)
            }
        }
    }

    suspend fun getAllVouchers(forceRefresh: Boolean = false): List<VoucherEntity> {
        return withContext(Dispatchers.IO) {
            try {
                if (forceRefresh) {
                    return@withContext forceSyncVouchers()
                }

                val localVouchers = voucherDao.getAllVouchers()
                if (localVouchers.isNotEmpty()) {
                    Log.d("VoucherManager", "Retrieved ${localVouchers.size} vouchers from local DB")
                    return@withContext localVouchers
                }

                Log.d("VoucherManager", "No local vouchers found, checking Firebase...")
                val firebaseVouchers = firebaseVoucherService.getAllVouchersFromFirebase()

                firebaseVouchers.forEach { voucher ->
                    voucherDao.insertVoucher(voucher)
                }

                Log.d("VoucherManager", "Retrieved ${firebaseVouchers.size} vouchers from Firebase")
                firebaseVouchers

            } catch (e: Exception) {
                Log.e("VoucherManager", "Error getting all vouchers", e)
                emptyList()
            }
        }
    }


    suspend fun addVoucher(voucher: VoucherEntity) {
        withContext(Dispatchers.IO) {
            try {
                // Save to local database
                voucherDao.insertVoucher(voucher)
                Log.d("VoucherManager", "Voucher saved locally: ${voucher.id}")

                // Sync to Firebase
                firebaseVoucherService.syncVoucherToFirebase(voucher)
                Log.d("VoucherManager", "Voucher synced to Firebase: ${voucher.id}")

            } catch (e: Exception) {
                Log.e("VoucherManager", "Error adding voucher", e)
                throw e
            }
        }
    }

    suspend fun redeemVoucher(userPhoneNumber: String, code: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                // Try to get voucher from local database first
                var voucher = voucherDao.getVoucherByCode(code)

                // If not found locally, try Firebase
                if (voucher == null) {
                    Log.d("VoucherManager", "Voucher not found locally, checking Firebase: $code")
                    voucher = firebaseVoucherService.getVoucherByCodeFromFirebase(code)

                    // If found in Firebase, save to local database
                    voucher?.let {
                        voucherDao.insertVoucher(it)
                        Log.d("VoucherManager", "Voucher saved from Firebase to local DB: $code")
                    }
                }

                if (voucher != null &&
                    voucher.isActive &&
                    voucher.currentUsage < voucher.maxUsage &&
                    voucher.expiryDate > System.currentTimeMillis()) {

                    val existingUserVouchers = getUserVouchersFromDB(userPhoneNumber)
                    if (existingUserVouchers.any { it.first.voucherId == voucher.id }) {
                        return@withContext false // User has already redeemed this voucher
                    }

                    return@withContext giveVoucherToUser(userPhoneNumber, voucher)
                }
                false

            } catch (e: Exception) {
                Log.e("VoucherManager", "Error redeeming voucher", e)
                false
            }
        }
    }

    suspend fun giveVoucherToUser(userPhoneNumber: String, voucher: VoucherEntity): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val existingUserVouchers = getUserVouchersFromDB(userPhoneNumber)

                // Check if user already has this voucher
                if (existingUserVouchers.any { it.first.voucherId == voucher.id }) {
                    return@withContext false
                }

                val userVoucher = UserVoucherEntity(
                    id = "${voucher.code}_${userPhoneNumber.takeLast(4)}", // 例如: WELCOME5_1234
                    userPhoneNumber = userPhoneNumber,
                    voucherId = voucher.id,
                    isUsed = false,
                    usedDate = null
                )

                // Save to local database
                voucherDao.insertUserVoucher(userVoucher)

                // Update voucher usage count locally
                val updatedVoucher = voucher.copy(currentUsage = voucher.currentUsage + 1)
                voucherDao.updateVoucher(updatedVoucher)

                // Sync to Firebase
                firebaseVoucherService.syncUserVoucherToFirebase(userVoucher)
                firebaseVoucherService.syncVoucherToFirebase(updatedVoucher)

                Log.d("VoucherManager", "Voucher given to user and synced: ${voucher.id} -> $userPhoneNumber")
                true

            } catch (e: Exception) {
                Log.e("VoucherManager", "Error giving voucher to user", e)
                false
            }
        }
    }

    suspend fun deleteVoucher(voucherId: String) {
        withContext(Dispatchers.IO) {
            try {
                // Delete from local database
                voucherDao.deleteVoucher(voucherId)
                Log.d("VoucherManager", "Voucher deleted locally: $voucherId")

                // Delete from Firebase
                firebaseVoucherService.deleteVoucherFromFirebase(voucherId)
                Log.d("VoucherManager", "Voucher deleted from Firebase: $voucherId")

            } catch (e: Exception) {
                Log.e("VoucherManager", "Error deleting voucher", e)
                throw e
            }
        }
    }

    suspend fun getUserVouchers(userPhoneNumber: String): List<Pair<UserVoucherEntity, VoucherEntity>> {
        return withContext(Dispatchers.IO) {
            try {
                // Try local database first
                val localUserVouchers = getUserVouchersFromDB(userPhoneNumber)
                if (localUserVouchers.isNotEmpty()) {
                    Log.d("VoucherManager", "Retrieved ${localUserVouchers.size} user vouchers from local DB")
                    return@withContext localUserVouchers
                }

                // If no local user vouchers, try Firebase
                Log.d("VoucherManager", "No local user vouchers found, checking Firebase...")
                val firebaseUserVouchers = firebaseVoucherService.getUserVouchersFromFirebase(userPhoneNumber)
                val allVouchers = getAllVouchers()

                val result = firebaseUserVouchers.mapNotNull { userVoucher ->
                    val voucher = allVouchers.find { it.id == userVoucher.voucherId }
                    if (voucher != null &&
                        !userVoucher.isUsed &&
                        voucher.expiryDate > System.currentTimeMillis() &&
                        voucher.isActive) {

                        // Save to local database
                        voucherDao.insertUserVoucher(userVoucher)
                        Pair(userVoucher, voucher)
                    } else null
                }

                Log.d("VoucherManager", "Retrieved ${result.size} user vouchers from Firebase")
                result

            } catch (e: Exception) {
                Log.e("VoucherManager", "Error getting user vouchers", e)
                emptyList()
            }
        }
    }

    private suspend fun getUserVouchersFromDB(userPhoneNumber: String): List<Pair<UserVoucherEntity, VoucherEntity>> {
        val userVouchers = voucherDao.getUserVouchers(userPhoneNumber)
        val allVouchers = voucherDao.getAllVouchers()

        return userVouchers.mapNotNull { userVoucher ->
            val voucher = allVouchers.find { it.id == userVoucher.voucherId }
            if (voucher != null &&
                !userVoucher.isUsed &&
                voucher.expiryDate > System.currentTimeMillis() &&
                voucher.isActive) {
                Pair(userVoucher, voucher)
            } else null
        }
    }

    suspend fun useVoucher(userPhoneNumber: String, userVoucherId: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val userVouchers = voucherDao.getUserVouchers(userPhoneNumber)
                val userVoucher = userVouchers.find { it.id == userVoucherId && !it.isUsed }

                if (userVoucher != null) {
                    val updatedUserVoucher = userVoucher.copy(
                        isUsed = true,
                        usedDate = System.currentTimeMillis()
                    )

                    // Update local database
                    voucherDao.updateUserVoucher(updatedUserVoucher)

                    // Sync to Firebase
                    firebaseVoucherService.syncUserVoucherToFirebase(updatedUserVoucher)

                    Log.d("VoucherManager", "Voucher used and synced: $userVoucherId")
                    true
                } else {
                    false
                }
            } catch (e: Exception) {
                Log.e("VoucherManager", "Error using voucher", e)
                false
            }
        }
    }

    fun calculateDiscount(voucher: VoucherEntity, orderTotal: Double): Double {
        if (orderTotal < voucher.minOrderAmount) return 0.0

        return when (voucher.discountType) {
            "FIXED" -> minOf(voucher.discountAmount, orderTotal)
            "PERCENTAGE" -> orderTotal * (voucher.discountAmount / 100.0)
            else -> 0.0
        }
    }

    suspend fun checkUserHasVoucher(phoneNumber: String, voucherCode: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                // Try local first
                var voucher = voucherDao.getVoucherByCode(voucherCode)

                // If not found locally, try Firebase
                if (voucher == null) {
                    voucher = firebaseVoucherService.getVoucherByCodeFromFirebase(voucherCode)
                }

                if (voucher != null) {
                    val existingUserVouchers = getUserVouchersFromDB(phoneNumber)
                    existingUserVouchers.any { it.first.voucherId == voucher.id }
                } else {
                    false
                }
            } catch (e: Exception) {
                Log.e("VoucherManager", "Error checking user voucher", e)
                false
            }
        }
    }

    suspend fun giveVoucherToUserForce(userPhoneNumber: String, voucher: VoucherEntity, allowDuplicate: Boolean = false): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                if (!allowDuplicate) {
                    return@withContext giveVoucherToUser(userPhoneNumber, voucher)
                } else {
                    val userVoucher = UserVoucherEntity(
                        id = "${voucher.code}_${userPhoneNumber.takeLast(4)}_${System.currentTimeMillis() / 1000}", // 使用秒级时间戳
                        userPhoneNumber = userPhoneNumber,
                        voucherId = voucher.id,
                        isUsed = false,
                        usedDate = null
                    )

                    // Save to local database
                    voucherDao.insertUserVoucher(userVoucher)

                    // Update voucher usage count locally
                    val updatedVoucher = voucher.copy(currentUsage = voucher.currentUsage + 1)
                    voucherDao.updateVoucher(updatedVoucher)

                    // Sync to Firebase
                    firebaseVoucherService.syncUserVoucherToFirebase(userVoucher)
                    firebaseVoucherService.syncVoucherToFirebase(updatedVoucher)

                    Log.d(
                        "VoucherManager",
                        "Voucher force given to user and synced: ${voucher.id} -> $userPhoneNumber"
                    )
                    true
                }
            } catch (e: Exception) {
                Log.e("VoucherManager", "Error force giving voucher to user", e)
                false
            }
        }
    }

    suspend fun forceSyncVouchers(): List<VoucherEntity> {
        return withContext(Dispatchers.IO) {
            try {
                Log.d("VoucherManager", "Force syncing vouchers from Firebase...")

                val firebaseVouchers = firebaseVoucherService.getAllVouchersFromFirebase()

                voucherDao.deleteAllVouchers()
                firebaseVouchers.forEach { voucher ->
                    voucherDao.insertVoucher(voucher)
                }

                Log.d("VoucherManager", "Force sync completed: ${firebaseVouchers.size} vouchers")
                firebaseVouchers

            } catch (e: Exception) {
                Log.e("VoucherManager", "Error force syncing vouchers", e)
                voucherDao.getAllVouchers()
            }
        }
    }

}