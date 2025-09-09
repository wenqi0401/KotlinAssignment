package com.example.myapplication.voucher

data class Voucher(
    val id: String = java.util.UUID.randomUUID().toString(),
    val code: String,
    val discountAmount: Double,
    val discountType: String, // "FIXED" or "PERCENTAGE"
    val minOrderAmount: Double,
    val expiryDate: Long,
    val isActive: Boolean,
    val maxUsage: Int,
    val currentUsage: Int,
    val description: String
)

data class UserVoucher(
    val id: String,
    val userPhoneNumber: String,
    val voucherId: String,
    val isUsed: Boolean,
    val usedDate: Long?
)

data class UserPoints(
    val userPhoneNumber: String,
    val totalPoints: Int,
    val availablePoints: Int // 扣除已兑换的积分
)

data class PointTransaction(
    val id: String,
    val userPhoneNumber: String,
    val points: Int,
    val type: String, // "EARN" or "REDEEM"
    val orderId: String?,
    val description: String,
    val timestamp: Long
)