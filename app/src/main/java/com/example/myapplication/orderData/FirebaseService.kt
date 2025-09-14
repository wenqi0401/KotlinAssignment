package com.example.myapplication.orderData

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseService @Inject constructor() {
    private val firestore = FirebaseFirestore.getInstance()
    private val ordersCollection = firestore.collection("orders")
    private val ratingsCollection = firestore.collection("ratings")

    suspend fun syncOrderToFirebase(order: Order) {
        try {
            val orderMap = mapOf(
                "orderId" to order.orderId,
                "userPhoneNumber" to order.userPhoneNumber,
                "items" to order.items.map { item ->
                    mapOf(
                        "name" to item.name,
                        "price" to item.price,
                        "quantity" to item.quantity,
                        "imageResId" to item.imageResId,
                        "ice" to item.ice,
                        "sugar" to item.sugar
                    )
                },
                "subtotal" to order.subtotal,
                "deliveryFee" to order.deliveryFee,
                "tax" to order.tax,
                "voucher" to order.voucher,
                "total" to order.total,
                "deliveryAddress" to order.deliveryAddress,
                "phoneNumber" to order.phoneNumber,
                "comment" to order.comment,
                "paymentMethod" to order.paymentMethod,
                "cardNumber" to order.cardNumber,
                "orderDate" to order.orderDate,
                "status" to order.status,
                "rating" to order.rating,
                "feedback" to order.feedback
            )

            ordersCollection.document(order.orderId)
                .set(orderMap, SetOptions.merge())
                .await()

            Log.d("FirebaseService", "Order synced to Firebase: ${order.orderId}")

            // If order has rating, also sync to ratings collection
            if (order.rating > 0) {
                syncRatingToFirebase(order)
            }

        } catch (e: Exception) {
            Log.e("FirebaseService", "Error syncing order to Firebase", e)
            throw e
        }
    }

    suspend fun syncRatingToFirebase(order: Order) {
        try {
            val ratingMap = mapOf(
                "orderId" to order.orderId,
                "userPhoneNumber" to order.userPhoneNumber,
                "rating" to order.rating,
                "feedback" to order.feedback,
                "orderDate" to order.orderDate,
                "timestamp" to System.currentTimeMillis()
            )

            ratingsCollection.document(order.orderId)
                .set(ratingMap, SetOptions.merge())
                .await()

            Log.d("FirebaseService", "Rating synced to Firebase: ${order.orderId}")

        } catch (e: Exception) {
            Log.e("FirebaseService", "Error syncing rating to Firebase", e)
            throw e
        }
    }

    suspend fun getOrdersFromFirebase(userPhoneNumber: String): List<Order> {
        return try {
            val querySnapshot = ordersCollection
                .whereEqualTo("userPhoneNumber", userPhoneNumber)
                .orderBy("orderDate")
                .get()
                .await()

            querySnapshot.documents.map { document ->
                val data = document.data ?: emptyMap()
                Order(
                    orderId = data["orderId"] as? String ?: "",
                    userPhoneNumber = data["userPhoneNumber"] as? String ?: "",
                    items = (data["items"] as? List<Map<String, Any>>)?.map { itemData ->
                        OrderItem(
                            name = itemData["name"] as? String ?: "",
                            price = itemData["price"] as? Double ?: 0.0,
                            quantity = itemData["quantity"] as? Int ?: 0,
                            imageResId = itemData["imageResId"] as? Int ?: 0,
                            ice = itemData["ice"] as? String ?: "",
                            sugar = itemData["sugar"] as? String ?: ""
                        )
                    } ?: emptyList(),
                    subtotal = data["subtotal"] as? Double ?: 0.0,
                    deliveryFee = data["deliveryFee"] as? Double ?: 0.0,
                    tax = data["tax"] as? Double ?: 0.0,
                    voucher = data["voucher"] as? Double ?: 0.0,
                    total = data["total"] as? Double ?: 0.0,
                    deliveryAddress = data["deliveryAddress"] as? String ?: "",
                    phoneNumber = data["phoneNumber"] as? String ?: "",
                    comment = data["comment"] as? String ?: "",
                    paymentMethod = data["paymentMethod"] as? String ?: "",
                    cardNumber = data["cardNumber"] as? String?,
                    orderDate = data["orderDate"] as? Long ?: 0L,
                    status = data["status"] as? String ?: "Preparing",
                    rating = data["rating"] as? Int ?: 0,
                    feedback = data["feedback"] as? String ?: ""
                )
            }
        } catch (e: Exception) {
            Log.e("FirebaseService", "Error getting orders from Firebase", e)
            emptyList()
        }
    }

    suspend fun getAverageRatingFromFirebase(): Double {
        return try {
            val querySnapshot = ratingsCollection.get().await()
            val ratings = querySnapshot.documents.mapNotNull { it.getDouble("rating") }
            if (ratings.isNotEmpty()) ratings.average() else 0.0
        } catch (e: Exception) {
            Log.e("FirebaseService", "Error getting average rating from Firebase", e)
            0.0
        }
    }
    suspend fun getRatedOrdersFromFirebase(): List<Order> {
        return try {
            val querySnapshot = ordersCollection
                .whereGreaterThan("rating", 0)
                .orderBy("orderDate", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get()
                .await()

            querySnapshot.documents.map { document ->
                val data = document.data ?: emptyMap()
                Order(
                    orderId = data["orderId"] as? String ?: "",
                    userPhoneNumber = data["userPhoneNumber"] as? String ?: "",
                    items = (data["items"] as? List<Map<String, Any>>)?.map { itemData ->
                        OrderItem(
                            name = itemData["name"] as? String ?: "",
                            price = itemData["price"] as? Double ?: 0.0,
                            quantity = itemData["quantity"] as? Int ?: 0,
                            imageResId = itemData["imageResId"] as? Int ?: 0,
                            ice = itemData["ice"] as? String ?: "",
                            sugar = itemData["sugar"] as? String ?: ""
                        )
                    } ?: emptyList(),
                    subtotal = data["subtotal"] as? Double ?: 0.0,
                    deliveryFee = data["deliveryFee"] as? Double ?: 0.0,
                    tax = data["tax"] as? Double ?: 0.0,
                    voucher = data["voucher"] as? Double ?: 0.0,
                    total = data["total"] as? Double ?: 0.0,
                    deliveryAddress = data["deliveryAddress"] as? String ?: "",
                    phoneNumber = data["phoneNumber"] as? String ?: "",
                    comment = data["comment"] as? String ?: "",
                    paymentMethod = data["paymentMethod"] as? String ?: "",
                    cardNumber = data["cardNumber"] as? String?,
                    orderDate = data["orderDate"] as? Long ?: 0L,
                    status = data["status"] as? String ?: "Preparing",
                    rating = data["rating"] as? Int ?: 0,
                    feedback = data["feedback"] as? String ?: ""
                )
            }
        } catch (e: Exception) {
            Log.e("FirebaseService", "Error getting rated orders from Firebase", e)
            emptyList()
        }
    }

    suspend fun getRatingCountFromFirebase(): Int {
        return try {
            val querySnapshot = ratingsCollection.get().await()
            querySnapshot.documents.size
        } catch (e: Exception) {
            Log.e("FirebaseService", "Error getting rating count from Firebase", e)
            0
        }
    }

    suspend fun getAllOrdersFromFirebase(): List<Order> {
        return try {
            val querySnapshot = ordersCollection
                .orderBy("orderDate", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get()
                .await()

            querySnapshot.documents.map { document ->
                val data = document.data ?: emptyMap()
                Order(
                    orderId = data["orderId"] as? String ?: "",
                    userPhoneNumber = data["userPhoneNumber"] as? String ?: "",
                    items = (data["items"] as? List<Map<String, Any>>)?.map { itemData ->
                        OrderItem(
                            name = itemData["name"] as? String ?: "",
                            price = itemData["price"] as? Double ?: 0.0,
                            quantity = itemData["quantity"] as? Int ?: 0,
                            imageResId = itemData["imageResId"] as? Int ?: 0,
                            ice = itemData["ice"] as? String ?: "",
                            sugar = itemData["sugar"] as? String ?: ""
                        )
                    } ?: emptyList(),
                    subtotal = data["subtotal"] as? Double ?: 0.0,
                    deliveryFee = data["deliveryFee"] as? Double ?: 0.0,
                    tax = data["tax"] as? Double ?: 0.0,
                    voucher = data["voucher"] as? Double ?: 0.0,
                    total = data["total"] as? Double ?: 0.0,
                    deliveryAddress = data["deliveryAddress"] as? String ?: "",
                    phoneNumber = data["phoneNumber"] as? String ?: "",
                    comment = data["comment"] as? String ?: "",
                    paymentMethod = data["paymentMethod"] as? String ?: "",
                    cardNumber = data["cardNumber"] as? String?,
                    orderDate = data["orderDate"] as? Long ?: 0L,
                    status = data["status"] as? String ?: "Preparing",
                    rating = data["rating"] as? Int ?: 0,
                    feedback = data["feedback"] as? String ?: ""
                )
            }
        } catch (e: Exception) {
            Log.e("FirebaseService", "Error getting all orders from Firebase", e)
            emptyList()
        }
    }
}
