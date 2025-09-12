package com.example.myapplication.voucher

import android.content.Context
import com.example.myapplication.orderData.OrderDatabase
import com.example.myapplication.registerData.UserRepository

class VoucherManager private constructor(private val context: Context) {
    private val database = OrderDatabase.getDatabase(context)
    private val voucherDao = database.voucherDao()
    private val userRepository = UserRepository()

    companion object {
        @Volatile
        private var INSTANCE: VoucherManager? = null

        fun getInstance(context: Context): VoucherManager {
            return INSTANCE ?: synchronized(this) {
                val instance = VoucherManager(context.applicationContext)
                INSTANCE = instance
                instance
            }
        }
    }

    suspend fun initializeDefaultVouchers() {
        val existingVouchers = voucherDao.getAllVouchers()
        if (existingVouchers.isEmpty()) {
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
            defaultVouchers.forEach { voucherDao.insertVoucher(it) }
        }
    }

    suspend fun getAllVouchers(): List<VoucherEntity> {
        return voucherDao.getAllVouchers()
    }

    suspend fun addVoucher(voucher: VoucherEntity) {
        voucherDao.insertVoucher(voucher)
    }

    suspend fun redeemVoucher(userPhoneNumber: String, code: String): Boolean {
        val voucher = voucherDao.getVoucherByCode(code)

        if (voucher != null &&
            voucher.isActive &&
            voucher.currentUsage < voucher.maxUsage &&
            voucher.expiryDate > System.currentTimeMillis()) {

            val existingUserVouchers = voucherDao.getUserVouchers(userPhoneNumber)
            if (existingUserVouchers.any { it.voucherId == voucher.id }) {
                return false // User has already redeemed this voucher
            }

            return giveVoucherToUser(userPhoneNumber, voucher)
        }
        return false
    }

    suspend fun giveVoucherToUser(userPhoneNumber: String, voucher: VoucherEntity): Boolean {
        val existingUserVouchers = voucherDao.getUserVouchers(userPhoneNumber)

        // Check if user already has this voucher
        if (existingUserVouchers.any { it.voucherId == voucher.id}) {
            return false
        }

        val userVoucher = UserVoucherEntity(
            id = "UV_${System.currentTimeMillis()}_${userPhoneNumber.takeLast(4)}",
            userPhoneNumber = userPhoneNumber,
            voucherId = voucher.id,
            isUsed = false,
            usedDate = null
        )

        voucherDao.insertUserVoucher(userVoucher)

        // Update voucher usage count
        val updatedVoucher = voucher.copy(currentUsage = voucher.currentUsage + 1)
        voucherDao.updateVoucher(updatedVoucher)

        return true
    }
    suspend fun deleteVoucher(voucherId: String) {
        voucherDao.deleteVoucher(voucherId)
    }

    suspend fun getUserVouchers(userPhoneNumber: String): List<Pair<UserVoucherEntity, VoucherEntity>> {
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
        val userVouchers = voucherDao.getUserVouchers(userPhoneNumber)
        val userVoucher = userVouchers.find { it.id == userVoucherId && !it.isUsed }

        if (userVoucher != null) {
            val updatedUserVoucher = userVoucher.copy(
                isUsed = true,
                usedDate = System.currentTimeMillis()
            )
            voucherDao.updateUserVoucher(updatedUserVoucher)
            return true
        }
        return false
    }

    fun calculateDiscount(voucher: VoucherEntity, orderTotal: Double): Double {
        if (orderTotal < voucher.minOrderAmount) return 0.0

        return when (voucher.discountType) {
            "FIXED" -> minOf(voucher.discountAmount, orderTotal)
            "PERCENTAGE" -> orderTotal * (voucher.discountAmount / 100.0)
            else -> 0.0
        }
    }



}