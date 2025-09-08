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
    @PrimaryKey
    val orderId: String,
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
    val status: String = "Preparing"
)

data class OrderItem(
    val name: String,
    val price: Double,
    val quantity: Int,
    val imageResId: Int
)

class OrderConverters {
    @TypeConverter
    fun fromOrderItemList(items: List<OrderItem>): String {
        return Gson().toJson(items)
    }

    @TypeConverter
    fun toOrderItemList(itemsString: String): List<OrderItem> {
        val type = object : TypeToken<List<OrderItem>>() {}.type
        return Gson().fromJson(itemsString, type)
    }
}