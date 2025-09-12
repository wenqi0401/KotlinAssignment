// Order.kt
package com.example.myapplication.orderData

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

@Entity(tableName = "orders")
@TypeConverters(OrderConverters::class)
data class Order(
    @PrimaryKey val orderId: String,
    val userPhoneNumber: String,
    val items: List<OrderItem>,

    val subtotal: Double,
    val deliveryFee: Double,
    val tax: Double,
    val voucher: Double,
    val total: Double,
    val deliveryAddress: String,
    val phoneNumber: String,
    val comment: String,
    val paymentMethod: String,
    val cardNumber: String? = null,
    val orderDate: Long = System.currentTimeMillis(),
    val status: String = "Preparing",
    val rating: Int = 0,
    val feedback: String = ""
)


class OrderConverters {
    @TypeConverter
    fun fromOrderItemList(value: List<OrderItem>): String {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun toOrderItemList(value: String): List<OrderItem> {
        val type = object : TypeToken<List<OrderItem>>() {}.type
        return Gson().fromJson(value, type) ?: emptyList()
    }
}

data class OrderItem(
    val name: String,
    val price: Double,
    val quantity: Int,
    val imageResId: Int,
    val ice: String,
    val sugar: String

)