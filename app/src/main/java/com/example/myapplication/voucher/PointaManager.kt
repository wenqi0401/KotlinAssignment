package com.example.myapplication.voucher

object PointsManager {
    private val userPoints = mutableMapOf<String, UserPoints>()
    private val pointTransactions = mutableMapOf<String, MutableList<PointTransaction>>()

    fun getUserPoints(userPhoneNumber: String): UserPoints {
        return userPoints[userPhoneNumber] ?: UserPoints(userPhoneNumber, 0, 0)
    }

    fun addPoints(userPhoneNumber: String, points: Int, orderId: String) {
        val currentPoints = getUserPoints(userPhoneNumber)
        val newTotalPoints = currentPoints.totalPoints + points
        val newAvailablePoints = currentPoints.availablePoints + points

        userPoints[userPhoneNumber] = UserPoints(
            userPhoneNumber = userPhoneNumber,
            totalPoints = newTotalPoints,
            availablePoints = newAvailablePoints
        )

        // 记录积分交易
        val transactions = pointTransactions.getOrPut(userPhoneNumber) { mutableListOf() }
        transactions.add(
            PointTransaction(
                id = "PT_${System.currentTimeMillis()}",
                userPhoneNumber = userPhoneNumber,
                points = points,
                type = "EARN",
                orderId = orderId,
                description = "Earned from order $orderId",
                timestamp = System.currentTimeMillis()
            )
        )
    }

    fun redeemPointsForVoucher(userPhoneNumber: String, pointsToRedeem: Int): Boolean {
        val currentPoints = getUserPoints(userPhoneNumber)

        if (currentPoints.availablePoints >= pointsToRedeem) {
            val newAvailablePoints = currentPoints.availablePoints - pointsToRedeem

            userPoints[userPhoneNumber] = currentPoints.copy(
                availablePoints = newAvailablePoints
            )

            // 记录积分使用
            val transactions = pointTransactions.getOrPut(userPhoneNumber) { mutableListOf() }
            transactions.add(
                PointTransaction(
                    id = "PT_${System.currentTimeMillis()}",
                    userPhoneNumber = userPhoneNumber,
                    points = -pointsToRedeem,
                    type = "REDEEM",
                    orderId = null,
                    description = "Redeemed $pointsToRedeem points for voucher",
                    timestamp = System.currentTimeMillis()
                )
            )

            return true
        }
        return false
    }

    fun getPointTransactions(userPhoneNumber: String): List<PointTransaction> {
        return pointTransactions[userPhoneNumber] ?: emptyList()
    }

    // 计算订单可获得的积分 (每RM1 = 1积分)
    fun calculatePointsFromOrder(orderTotal: Double): Int {
        return orderTotal.toInt()
    }

    // 积分兑换voucher的汇率 (10积分 = RM1)
    fun convertPointsToVoucherValue(points: Int): Double {
        return points / 10.0
    }
}