package com.example.myapplication.voucher

import androidx.room.*

@Dao
interface VoucherDao {
    @Query("SELECT * FROM vouchers")
    suspend fun getAllVouchers(): List<VoucherEntity>

    @Query("SELECT * FROM vouchers WHERE code = :code AND isActive = 1")
    suspend fun getVoucherByCode(code: String): VoucherEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVoucher(voucher: VoucherEntity)

    @Update
    suspend fun updateVoucher(voucher: VoucherEntity)

    @Query("DELETE FROM vouchers WHERE id = :voucherId")
    suspend fun deleteVoucher(voucherId: String)

    @Query("SELECT * FROM user_vouchers WHERE userPhoneNumber = :userPhoneNumber")
    suspend fun getUserVouchers(userPhoneNumber: String): List<UserVoucherEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserVoucher(userVoucher: UserVoucherEntity)

    @Update
    suspend fun updateUserVoucher(userVoucher: UserVoucherEntity)

    @Query("DELETE FROM user_vouchers WHERE id = :userVoucherId")
    suspend fun deleteUserVoucher(userVoucherId: String)

    @Query("SELECT * FROM user_vouchers")
    suspend fun getAllUserVouchers(): List<UserVoucherEntity>


}