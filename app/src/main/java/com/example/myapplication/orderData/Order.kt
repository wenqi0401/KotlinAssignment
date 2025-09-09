package com.example.myapplication.orderData

import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName

// Firestore data classes (for Firestore serialization)
data class FirestoreOrder(
    @PropertyName("orderId") val orderId: String = "",
    @PropertyName("userPhoneNumber") val userPhoneNumber: String = "",
    @PropertyName("items") val items: List<FirestoreOrderItem> = emptyList(),
    @PropertyName("subtotal") val subtotal: Double = 0.0,
    @PropertyName("deliveryFee") val deliveryFee: Double = 0.0,
    @PropertyName("tax") val tax: Double = 0.0,
    @PropertyName("voucher") val voucher: Double = 0.0,
    @PropertyName("total") val total: Double = 0.0,
    @PropertyName("deliveryAddress") val deliveryAddress: String = "",
    @PropertyName("phoneNumber") val phoneNumber: String = "",
    @PropertyName("comment") val comment: String = "",
    @PropertyName("paymentMethod") val paymentMethod: String = "",
    @PropertyName("cardNumber") val cardNumber: String? = null,
    @PropertyName("orderDate") val orderDate: Timestamp = Timestamp.now(),
    @PropertyName("status") val status: String = "Preparing"
) {
    fun toOrder(): Order {
        return Order(
            orderId = orderId,
            userPhoneNumber = userPhoneNumber,
            items = items.map { it.toOrderItem() },
            subtotal = subtotal,
            deliveryFee = deliveryFee,
            tax = tax,
            voucher = voucher,
            total = total,
            deliveryAddress = deliveryAddress,
            phoneNumber = phoneNumber,
            comment = comment,
            paymentMethod = paymentMethod,
            cardNumber = cardNumber,
            orderDate = orderDate.seconds * 1000, // Convert to milliseconds
            status = status
        )
    }
}

data class FirestoreOrderItem(
    @PropertyName("name") val name: String = "",
    @PropertyName("price") val price: Double = 0.0,
    @PropertyName("quantity") val quantity: Int = 0,
    @PropertyName("imageResId") val imageResId: Int = 0
) {
    fun toOrderItem(): OrderItem {
        return OrderItem(
            name = name,
            price = price,
            quantity = quantity,
            imageResId = imageResId
        )
    }
}

// Your domain data class (for app logic) - Keep your original Room structure
data class Order(
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