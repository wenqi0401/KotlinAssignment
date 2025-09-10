package com.example.myapplication.voucher

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "vouchers")
data class VoucherEntity(
    @PrimaryKey val id: String,
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

@Entity(tableName = "user_vouchers")
data class UserVoucherEntity(
    @PrimaryKey val id: String,
    val userPhoneNumber: String,
    val voucherId: String,
    val isUsed: Boolean,
    val usedDate: Long?
)