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

    suspend fun updateOrderRating(orderId: String, rating: Int, feedback: String) {
        withContext(Dispatchers.IO) {
            try {
                val order = orderDao.getOrderById(orderId)
                order?.let {
                    val updatedOrder = it.copy(rating = rating, feedback = feedback)
                    orderDao.updateOrder(updatedOrder) // Use the proper update function
                    Log.d("OrderRepository", "Order rating updated: $orderId, rating: $rating")
                } ?: run {
                    Log.e("OrderRepository", "Order not found: $orderId")
                }
            } catch (e: Exception) {
                Log.e("OrderRepository", "Error updating order rating", e)
                throw e
            }
        }
    }

    suspend fun getAverageRating(): Double {
        return withContext(Dispatchers.IO) {
            try {
                orderDao.getAverageRating() ?: 0.0
            } catch (e: Exception) {
                Log.e("OrderRepository", "Error getting average rating", e)
                0.0
            }
        }
    }

    suspend fun getRatedOrders(): List<Order> {
        return withContext(Dispatchers.IO) {
            try {
                orderDao.getRatedOrders()
            } catch (e: Exception) {
                Log.e("OrderRepository", "Error getting rated orders", e)
                emptyList()
            }
        }
    }

    suspend fun getRatingCount(): Int {
        return withContext(Dispatchers.IO) {
            try {
                orderDao.getRatingCount()
            } catch (e: Exception) {
                Log.e("OrderRepository", "Error getting rating count", e)
                0
            }
        }
    }
}
