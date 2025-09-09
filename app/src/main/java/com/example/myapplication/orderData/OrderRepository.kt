// OrderRepository.kt
package com.example.myapplication.orderData

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class OrderRepository(private val context: Context) {
    private val database = OrderDatabase.getDatabase(context) // Changed to OrderDatabase
    private val orderDao = database.orderDao()

    suspend fun saveOrder(order: Order) {
        withContext(Dispatchers.IO) {
            try {
                orderDao.insertOrder(order)
                Log.d("OrderRepository", "Order saved: ${order.orderId} for user: ${order.userPhoneNumber}")
            } catch (e: Exception) {
                Log.e("OrderRepository", "Error saving order", e)
                throw e
            }
        }
    }

    suspend fun getUserOrders(userPhoneNumber: String): List<Order> {
        return withContext(Dispatchers.IO) {
            try {
                val orders = orderDao.getOrdersByUser(userPhoneNumber)
                Log.d("OrderRepository", "Retrieved ${orders.size} orders for user: $userPhoneNumber")
                orders
            } catch (e: Exception) {
                Log.e("OrderRepository", "Error getting user orders", e)
                emptyList()
            }
        }
    }

    suspend fun getOrderById(orderId: String): Order? {
        return withContext(Dispatchers.IO) {
            try {
                orderDao.getOrderById(orderId)
            } catch (e: Exception) {
                Log.e("OrderRepository", "Error getting order by ID", e)
                null
            }
        }
    }

    suspend fun updateOrderStatus(orderId: String, newStatus: String) {
        withContext(Dispatchers.IO) {
            try {
                val order = orderDao.getOrderById(orderId)
                order?.let {
                    val updatedOrder = it.copy(status = newStatus)
                    orderDao.updateOrder(updatedOrder)
                    Log.d("OrderRepository", "Order status updated: $orderId -> $newStatus")
                }
            } catch (e: Exception) {
                Log.e("OrderRepository", "Error updating order status", e)
            }
        }
    }

    suspend fun getAllOrders(): List<Order> {
        return withContext(Dispatchers.IO) {
            try {
                orderDao.getAllOrders()
            } catch (e: Exception) {
                Log.e("OrderRepository", "Error getting all orders", e)
                emptyList()
            }
        }
    }
}