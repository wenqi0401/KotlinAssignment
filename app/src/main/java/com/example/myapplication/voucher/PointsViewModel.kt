package com.example.myapplication.voucher

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.orderData.UserSession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PointsViewModel(context: Context) : ViewModel() {
    private val repository = PointsRepository(context)

    private val _userPoints = MutableStateFlow<UserPoints?>(null)
    val userPoints: StateFlow<UserPoints?> = _userPoints

    private val _transactions = MutableStateFlow<List<PointTransaction>>(emptyList())
    val transactions: StateFlow<List<PointTransaction>> = _transactions

    fun loadUserData() {
        viewModelScope.launch {
            _userPoints.value = repository.getUserPoints()
            _transactions.value = repository.getTransactions()
        }
    }

    fun redeemPoints(points: Int) {
        val current = _userPoints.value
        if (current != null && current.availablePoints >= points) {
            _userPoints.value = current.copy(
                availablePoints = current.availablePoints - points
            )

            val newTransaction = PointTransaction(
                id = java.util.UUID.randomUUID().toString(),
                userPhoneNumber = current.userPhoneNumber,
                points = points,
                type = "REDEEM",
                orderId = null,
                description = "Redeemed for voucher",
                timestamp = System.currentTimeMillis()
            )
            _transactions.value = _transactions.value + newTransaction

            // TODO: 保存到数据库
        }
    }
}
