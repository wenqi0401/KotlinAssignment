package com.example.myapplication.orderData

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class OrderRepository @Inject constructor(
    private val context: Context,
    private val firebaseService: FirebaseService
) {
    private val database = OrderDatabase.getDatabase(context)
    private val orderDao = database.orderDao()

    suspend fun saveOrder(order: Order) {
        withContext(Dispatchers.IO) {
            try {
                // Save to local database
                orderDao.insertOrder(order)
                Log.d("OrderRepository", "Order saved locally: ${order.orderId}")

                // Sync to Firebase
                firebaseService.syncOrderToFirebase(order)
                Log.d("OrderRepository", "Order synced to Firebase: ${order.orderId}")

            } catch (e: Exception) {
                Log.e("OrderRepository", "Error saving order", e)
                throw e
            }
        }
    }

    suspend fun getUserOrders(userPhoneNumber: String): List<Order> {
        return withContext(Dispatchers.IO) {
            try {
                // Try to get from local database first
                val localOrders = orderDao.getOrdersByUser(userPhoneNumber)
                if (localOrders.isNotEmpty()) {
                    Log.d("OrderRepository", "Retrieved ${localOrders.size} orders from local DB")
                    return@withContext localOrders
                }

                // If no local orders, try Firebase
                Log.d("OrderRepository", "No local orders found, checking Firebase...")
                val firebaseOrders = firebaseService.getOrdersFromFirebase(userPhoneNumber)

                // Save Firebase orders to local database
                firebaseOrders.forEach { order ->
                    orderDao.insertOrder(order)
                }

                Log.d("OrderRepository", "Retrieved ${firebaseOrders.size} orders from Firebase")
                firebaseOrders

            } catch (e: Exception) {
                Log.e("OrderRepository", "Error getting user orders", e)
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

                    // Update local database
                    orderDao.updateOrder(updatedOrder)
                    Log.d("OrderRepository", "Order rating updated locally: $orderId")

                    // Sync to Firebase
                    firebaseService.syncRatingToFirebase(updatedOrder)
                    Log.d("OrderRepository", "Rating synced to Firebase: $orderId")

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
                // Try local database first
                val localAvg = orderDao.getAverageRating() ?: 0.0
                if (localAvg > 0) {
                    return@withContext localAvg
                }

                // If no local ratings, try Firebase
                firebaseService.getAverageRatingFromFirebase()

            } catch (e: Exception) {
                Log.e("OrderRepository", "Error getting average rating", e)
                0.0
            }
        }
    }

    // Keep other functions the same but add Firebase sync where needed
    suspend fun updateOrderStatus(orderId: String, newStatus: String) {
        withContext(Dispatchers.IO) {
            try {
                val order = orderDao.getOrderById(orderId)
                order?.let {
                    val updatedOrder = it.copy(status = newStatus)
                    orderDao.updateOrder(updatedOrder)

                    // Sync status update to Firebase
                    firebaseService.syncOrderToFirebase(updatedOrder)

                    Log.d("OrderRepository", "Order status updated: $orderId -> $newStatus")
                }
            } catch (e: Exception) {
                Log.e("OrderRepository", "Error updating order status", e)
            }
        }
    }

    suspend fun getRatedOrders(): List<Order> {
        return withContext(Dispatchers.IO) {
            try {
                // Try local database first
                val localRatedOrders = orderDao.getRatedOrders()
                if (localRatedOrders.isNotEmpty()) {
                    Log.d("OrderRepository", "Retrieved ${localRatedOrders.size} rated orders from local DB")
                    return@withContext localRatedOrders
                }

                // If no local rated orders, try Firebase
                Log.d("OrderRepository", "No local rated orders found, checking Firebase...")
                val firebaseRatedOrders = firebaseService.getRatedOrdersFromFirebase()

                // Save Firebase rated orders to local database
                firebaseRatedOrders.forEach { order ->
                    orderDao.insertOrder(order)
                }

                Log.d("OrderRepository", "Retrieved ${firebaseRatedOrders.size} rated orders from Firebase")
                firebaseRatedOrders

            } catch (e: Exception) {
                Log.e("OrderRepository", "Error getting rated orders", e)
                emptyList()
            }
        }
    }

    suspend fun getRatingCount(): Int {
        return withContext(Dispatchers.IO) {
            try {
                // Try local database first
                val localCount = orderDao.getRatingCount()
                if (localCount > 0) {
                    return@withContext localCount
                }

                // If no local ratings, try Firebase
                firebaseService.getRatingCountFromFirebase()

            } catch (e: Exception) {
                Log.e("OrderRepository", "Error getting rating count", e)
                0
            }
        }
    }

    suspend fun getOrderById(orderId: String): Order? {
        return withContext(Dispatchers.IO) {
            try {
                // Try local database first
                var order = orderDao.getOrderById(orderId)
                if (order != null) {
                    Log.d("OrderRepository", "Order found in local DB: $orderId")
                    return@withContext order
                }

                // If not found locally, try Firebase
                Log.d("OrderRepository", "Order not found locally, checking Firebase: $orderId")
                val firebaseOrders = firebaseService.getOrdersFromFirebase("") // Get all orders
                order = firebaseOrders.find { it.orderId == orderId }

                // If found in Firebase, save to local database
                order?.let {
                    orderDao.insertOrder(it)
                    Log.d("OrderRepository", "Order saved from Firebase to local DB: $orderId")
                }

                order

            } catch (e: Exception) {
                Log.e("OrderRepository", "Error getting order by ID", e)
                null
            }
        }
    }

    suspend fun getAllOrders(): List<Order> {
        return withContext(Dispatchers.IO) {
            try {
                // Try local database first
                val localOrders = orderDao.getAllOrders()
                if (localOrders.isNotEmpty()) {
                    Log.d("OrderRepository", "Retrieved ${localOrders.size} orders from local DB")
                    return@withContext localOrders
                }

                // If no local orders, try Firebase
                Log.d("OrderRepository", "No local orders found, checking Firebase...")
                val firebaseOrders = firebaseService.getAllOrdersFromFirebase()

                // Save Firebase orders to local database
                firebaseOrders.forEach { order ->
                    orderDao.insertOrder(order)
                }

                Log.d("OrderRepository", "Retrieved ${firebaseOrders.size} orders from Firebase")
                firebaseOrders

            } catch (e: Exception) {
                Log.e("OrderRepository", "Error getting all orders", e)
                emptyList()
            }
        }
    }
}