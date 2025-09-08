package com.example.myapplication.orderData

import android.content.Context
import com.example.myapplication.registerData.UserDatabase
import android.util.Log

class OrderManager(private val context: Context) {
    private val database = UserDatabase.getDatabase(context)
    private val orderDao = database.orderDao()

    suspend fun saveOrder(order: Order) {
        try {
            orderDao.insertOrder(order)
            Log.d("OrderManager", "Order saved successfully: ${order.orderId}")
        } catch (e: Exception) {
            Log.e("OrderManager", "Error saving order", e)
            throw e
        }
    }

    suspend fun getUserOrders(userPhoneNumber: String): List<Order> {
        return try {
            val orders = orderDao.getOrdersByUser(userPhoneNumber)
            Log.d("OrderManager", "Retrieved ${orders.size} orders for user: $userPhoneNumber")
            orders
        } catch (e: Exception) {
            Log.e("OrderManager", "Error getting user orders", e)
            emptyList()
        }
    }

    suspend fun getAllOrders(): List<Order> {
        return try {
            val orders = orderDao.getAllOrders()
            Log.d("OrderManager", "Retrieved ${orders.size} total orders")
            orders
        } catch (e: Exception) {
            Log.e("OrderManager", "Error getting all orders", e)
            emptyList()
        }
    }

    suspend fun getOrderById(orderId: String): Order? {
        return try {
            orderDao.getOrderById(orderId)
        } catch (e: Exception) {
            Log.e("OrderManager", "Error getting order by ID", e)
            null
        }
    }

    suspend fun updateOrderStatus(orderId: String, newStatus: String) {
        try {
            val order = orderDao.getOrderById(orderId)
            order?.let {
                val updatedOrder = it.copy(status = newStatus)
                orderDao.updateOrder(updatedOrder)
                Log.d("OrderManager", "Order status updated: $orderId -> $newStatus")
            }
        } catch (e: Exception) {
            Log.e("OrderManager", "Error updating order status", e)
        }
    }
}

object UserSession {
    var currentUserPhone: String? = null

    fun setCurrentUser(phoneNumber: String) {
        currentUserPhone = phoneNumber
        Log.d("UserSession", "User session set: $phoneNumber")
    }

    fun getCurrentUser(): String? {
        Log.d("UserSession", "Current user: $currentUserPhone")
        return currentUserPhone
    }

    fun clearSession() {
        Log.d("UserSession", "User session cleared")
        currentUserPhone = null
    }
}