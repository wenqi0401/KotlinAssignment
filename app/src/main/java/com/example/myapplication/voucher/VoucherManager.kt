package com.example.myapplication.voucher

object VoucherManager {
    private val userVouchers = mutableMapOf<String, MutableList<UserVoucher>>()
    private val availableVouchers = mutableListOf<Voucher>()

    init {
        // 添加一些预设voucher
        availableVouchers.addAll(listOf(
            Voucher(
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
            Voucher(
                id = "STUDENT10",
                code = "STUDENT10",
                discountAmount = 10.0,
                discountType = "PERCENTAGE",
                minOrderAmount = 20.0,
                expiryDate = System.currentTimeMillis() + (60 * 24 * 60 * 60 * 1000L),
                isActive = true,
                maxUsage = 500,
                currentUsage = 0,
                description = "Student discount 10% off"
            )
        ))
    }

    fun getAllVouchers(): List<Voucher> = availableVouchers

    fun addVoucher(voucher: Voucher) {
        availableVouchers.add(voucher)
    }

    fun redeemVoucher(userPhoneNumber: String, code: String): Boolean {
        val voucher = availableVouchers.find { it.code == code && it.isActive }

        if (voucher != null && voucher.currentUsage < voucher.maxUsage) {
            val userVouchersList = userVouchers.getOrPut(userPhoneNumber) { mutableListOf() }

            // 检查用户是否已经兑换过这个voucher
            val alreadyRedeemed = userVouchersList.any {
                it.voucherId == voucher.id
            }

            if (!alreadyRedeemed) {
                val userVoucher = UserVoucher(
                    id = "UV_${System.currentTimeMillis()}",
                    userPhoneNumber = userPhoneNumber,
                    voucherId = voucher.id,
                    isUsed = false,
                    usedDate = null
                )
                userVouchersList.add(userVoucher)

                // 更新使用次数
                val index = availableVouchers.indexOfFirst { it.id == voucher.id }
                if (index != -1) {
                    availableVouchers[index] = voucher.copy(currentUsage = voucher.currentUsage + 1)
                }
                return true
            }
        }
        return false
    }

    fun getUserVouchers(userPhoneNumber: String): List<Pair<UserVoucher, Voucher>> {
        val userVouchersList = userVouchers[userPhoneNumber] ?: return emptyList()
        return userVouchersList.mapNotNull { userVoucher ->
            val voucher = availableVouchers.find { it.id == userVoucher.voucherId }
            if (voucher != null) {
                Pair(userVoucher, voucher)
            } else null
        }.filter { !it.first.isUsed && it.second.expiryDate > System.currentTimeMillis() }
    }

    fun useVoucher(userPhoneNumber: String, userVoucherId: String): Boolean {
        val userVouchersList = userVouchers[userPhoneNumber]
        if (userVouchersList != null) {
            val index = userVouchersList.indexOfFirst { it.id == userVoucherId && !it.isUsed }
            if (index != -1) {
                userVouchersList[index] = userVouchersList[index].copy(
                    isUsed = true,
                    usedDate = System.currentTimeMillis()
                )
                return true
            }
        }
        return false
    }

    fun calculateDiscount(voucher: Voucher, orderTotal: Double): Double {
        return when (voucher.discountType) {
            "FIXED" -> minOf(voucher.discountAmount, orderTotal)
            "PERCENTAGE" -> orderTotal * (voucher.discountAmount / 100.0)
            else -> 0.0
        }
    }
}