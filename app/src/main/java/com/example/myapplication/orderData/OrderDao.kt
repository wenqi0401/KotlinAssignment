package com.example.myapplication.orderData

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface OrderDao {

    @Insert
    suspend fun insertOrder(order: Order)

    @Query("SELECT * FROM orders WHERE userPhoneNumber = :userPhoneNumber ORDER BY orderDate DESC")
    suspend fun getOrdersByUser(userPhoneNumber: String): List<Order>

    @Query("SELECT * FROM orders WHERE orderId = :orderId LIMIT 1")
    suspend fun getOrderById(orderId: String): Order?

    @Update
    suspend fun updateOrder(order: Order)

    @Query("SELECT * FROM orders ORDER BY orderDate DESC")
    suspend fun getAllOrders(): List<Order>

    @Query("DELETE FROM orders WHERE orderId = :orderId")
    suspend fun deleteOrder(orderId: String)

    @Query("SELECT AVG(rating) FROM orders WHERE rating > 0")
    suspend fun getAverageRating(): Double

    @Query("SELECT * FROM orders WHERE rating > 0 ORDER BY orderDate DESC")
    suspend fun getRatedOrders(): List<Order>

    @Query("SELECT COUNT(*) FROM orders WHERE rating > 0")
    suspend fun getRatingCount(): Int
}