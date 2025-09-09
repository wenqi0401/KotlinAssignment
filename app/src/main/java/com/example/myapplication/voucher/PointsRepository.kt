package com.example.myapplication.voucher

import android.content.Context
import com.example.myapplication.orderData.OrderRepository
import com.example.myapplication.orderData.UserSession

class PointsRepository(private val context: Context) {
    private val orderRepository = OrderRepository(context)

    suspend fun getUserPoints(): UserPoints? {
        val phone = UserSession.getCurrentUser() ?: return null
        val orders = orderRepository.getUserOrders(phone)

        // 简单规则：1 RM = 1 积分
        val totalPoints = orders.sumOf { it.subtotal.toInt() }
        val redeemedPoints = 0 // TODO: 以后从数据库/transaction表获取

        return UserPoints(
            userPhoneNumber = phone,
            totalPoints = totalPoints,
            availablePoints = totalPoints - redeemedPoints
        )
    }

    suspend fun getTransactions(): List<PointTransaction> {
        val phone = UserSession.getCurrentUser() ?: return emptyList()
        // TODO: 从数据库加载。先用假数据
        return listOf(
            PointTransaction("1", phone, 50, "EARN", "ORDER123", "Earned from order", System.currentTimeMillis()),
            PointTransaction("2", phone, 20, "REDEEM", null, "Redeemed for voucher", System.currentTimeMillis())
        )
    }
}
