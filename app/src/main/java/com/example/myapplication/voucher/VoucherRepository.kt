package com.example.myapplication.voucher

class VoucherRepository(private val voucherDao: VoucherDao) {

    suspend fun addVoucher(voucher: VoucherEntity) {
        voucherDao.insertVoucher(voucher)
    }

    suspend fun assignVoucherToUser(userPhoneNumber: String, voucher: VoucherEntity) {
        val userVoucher = UserVoucherEntity(
            id = "UV_${System.currentTimeMillis()}",
            userPhoneNumber = userPhoneNumber,
            voucherId = voucher.id,
            isUsed = false,
            usedDate = null
        )
        voucherDao.insertUserVoucher(userVoucher)
    }

    suspend fun assignVoucherToAllUsers(users: List<String>, voucher: VoucherEntity) {
        users.forEach { phone ->
            assignVoucherToUser(phone, voucher)
        }
    }

    suspend fun getUserVouchers(userPhoneNumber: String): List<UserVoucherEntity> {
        return voucherDao.getUserVouchers(userPhoneNumber)
    }
}
