package com.example.myapplication.voucher

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseVoucherService @Inject constructor() {
    private val firestore = FirebaseFirestore.getInstance()

    // Collections
    private val vouchersCollection = firestore.collection("vouchers")
    private val userVouchersCollection = firestore.collection("user_vouchers")

    // ===== VOUCHER OPERATIONS =====

    suspend fun syncVoucherToFirebase(voucher: VoucherEntity) {
        try {
            vouchersCollection.document(voucher.id)
                .set(voucher)
                .await()
            Log.d("FirebaseVoucherService", "Voucher synced to Firebase: ${voucher.id}")
        } catch (e: Exception) {
            Log.e("FirebaseVoucherService", "Error syncing voucher to Firebase", e)
            throw e
        }
    }

    suspend fun getAllVouchersFromFirebase(): List<VoucherEntity> {
        return try {
            val querySnapshot = vouchersCollection.get().await()
            val vouchers = querySnapshot.documents.mapNotNull { document ->
                document.toObject(VoucherEntity::class.java)
            }
            Log.d("FirebaseVoucherService", "Retrieved ${vouchers.size} vouchers from Firebase")
            vouchers
        } catch (e: Exception) {
            Log.e("FirebaseVoucherService", "Error getting vouchers from Firebase", e)
            emptyList()
        }
    }

    suspend fun getVoucherByCodeFromFirebase(code: String): VoucherEntity? {
        return try {
            val querySnapshot = vouchersCollection
                .whereEqualTo("code", code)
                .get()
                .await()

            val voucher = querySnapshot.documents.firstOrNull()?.toObject(VoucherEntity::class.java)
            Log.d("FirebaseVoucherService", "Retrieved voucher by code from Firebase: $code")
            voucher
        } catch (e: Exception) {
            Log.e("FirebaseVoucherService", "Error getting voucher by code from Firebase", e)
            null
        }
    }

    suspend fun deleteVoucherFromFirebase(voucherId: String) {
        try {
            vouchersCollection.document(voucherId)
                .delete()
                .await()
            Log.d("FirebaseVoucherService", "Voucher deleted from Firebase: $voucherId")
        } catch (e: Exception) {
            Log.e("FirebaseVoucherService", "Error deleting voucher from Firebase", e)
            throw e
        }
    }

    // ===== USER VOUCHER OPERATIONS =====

    suspend fun syncUserVoucherToFirebase(userVoucher: UserVoucherEntity) {
        try {
            userVouchersCollection.document(userVoucher.id)
                .set(userVoucher)
                .await()
            Log.d("FirebaseVoucherService", "User voucher synced to Firebase: ${userVoucher.id}")
        } catch (e: Exception) {
            Log.e("FirebaseVoucherService", "Error syncing user voucher to Firebase", e)
            throw e
        }
    }

    suspend fun getUserVouchersFromFirebase(userPhoneNumber: String): List<UserVoucherEntity> {
        return try {
            val querySnapshot = userVouchersCollection
                .whereEqualTo("userPhoneNumber", userPhoneNumber)
                .get()
                .await()

            val userVouchers = querySnapshot.documents.mapNotNull { document ->
                document.toObject(UserVoucherEntity::class.java)
            }
            Log.d("FirebaseVoucherService", "Retrieved ${userVouchers.size} user vouchers from Firebase for user: $userPhoneNumber")
            userVouchers
        } catch (e: Exception) {
            Log.e("FirebaseVoucherService", "Error getting user vouchers from Firebase", e)
            emptyList()
        }
    }

}