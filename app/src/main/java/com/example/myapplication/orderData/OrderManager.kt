package com.example.myapplication.orderData

import android.content.Context
import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.tasks.await

class OrderManager(private val context: Context) {
    private val db = FirebaseFirestore.getInstance()
    private val ordersCollection = db.collection("orders")

    suspend fun saveOrder(order: Order) {
        try {
            // Convert Order to FirestoreOrder
            val firestoreOrder = FirestoreOrder(
                orderId = order.orderId,
                userPhoneNumber = order.userPhoneNumber,
                items = order.items.map { orderItem ->
                    FirestoreOrderItem(
                        name = orderItem.name,
                        price = orderItem.price,
                        quantity = orderItem.quantity,
                        imageResId = orderItem.imageResId
                    )
                },
                subtotal = order.subtotal,
                deliveryFee = order.deliveryFee,
                tax = order.tax,
                voucher = order.voucher,
                total = order.total,
                deliveryAddress = order.deliveryAddress,
                phoneNumber = order.phoneNumber,
                comment = order.comment,
                paymentMethod = order.paymentMethod,
                cardNumber = order.cardNumber,
                orderDate = Timestamp(order.orderDate / 1000, ((order.orderDate % 1000) * 1000).toInt()), // Convert to Timestamp
                status = order.status
            )

            ordersCollection.document(order.orderId)
                .set(firestoreOrder, SetOptions.merge())
                .await()

            Log.d("OrderManager", "Order saved successfully to Firebase: ${order.orderId}")
        } catch (e: Exception) {
            Log.e("OrderManager", "Error saving order to Firebase", e)
            throw e
        }
    }

    suspend fun getUserOrders(userPhoneNumber: String): List<Order> {
        return try {
            val querySnapshot = ordersCollection
                .whereEqualTo("userPhoneNumber", userPhoneNumber)
                .get()
                .await()

            val orders = querySnapshot.documents.mapNotNull { document ->
                try {
                    document.toObject<FirestoreOrder>()?.toOrder()
                } catch (e: Exception) {
                    Log.e("OrderManager", "Error parsing order document: ${document.id}", e)
                    null
                }
            }

            Log.d("OrderManager", "Retrieved ${orders.size} orders from Firebase for user: $userPhoneNumber")
            orders
        } catch (e: Exception) {
            Log.e("OrderManager", "Error getting user orders from Firebase", e)
            emptyList()
        }
    }

    suspend fun getAllOrders(): List<Order> {
        return try {
            val querySnapshot = ordersCollection.get().await()
            val orders = querySnapshot.documents.mapNotNull { document ->
                try {
                    document.toObject<FirestoreOrder>()?.toOrder()
                } catch (e: Exception) {
                    Log.e("OrderManager", "Error parsing order document: ${document.id}", e)
                    null
                }
            }

            Log.d("OrderManager", "Retrieved ${orders.size} total orders from Firebase")
            orders
        } catch (e: Exception) {
            Log.e("OrderManager", "Error getting all orders from Firebase", e)
            emptyList()
        }
    }

    suspend fun getOrderById(orderId: String): Order? {
        return try {
            val document = ordersCollection.document(orderId).get().await()
            if (document.exists()) {
                try {
                    document.toObject<FirestoreOrder>()?.toOrder()
                } catch (e: Exception) {
                    Log.e("OrderManager", "Error parsing order document: $orderId", e)
                    null
                }
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("OrderManager", "Error getting order by ID from Firebase", e)
            null
        }
    }

    suspend fun updateOrderStatus(orderId: String, newStatus: String) {
        try {
            val updateData = hashMapOf<String, Any>(
                "status" to newStatus
            )

            ordersCollection.document(orderId)
                .update(updateData)
                .await()

            Log.d("OrderManager", "Order status updated in Firebase: $orderId -> $newStatus")
        } catch (e: Exception) {
            Log.e("OrderManager", "Error updating order status in Firebase", e)
        }
    }

    // Optional: Real-time updates listener
    fun addOrdersListener(userPhoneNumber: String, onOrdersChanged: (List<Order>) -> Unit) {
        ordersCollection.whereEqualTo("userPhoneNumber", userPhoneNumber)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("OrderManager", "Listen failed.", error)
                    return@addSnapshotListener
                }

                val orders = snapshot?.documents?.mapNotNull { document ->
                    try {
                        document.toObject<FirestoreOrder>()?.toOrder()
                    } catch (e: Exception) {
                        Log.e("OrderManager", "Error parsing order in listener: ${document.id}", e)
                        null
                    }
                } ?: emptyList()

                onOrdersChanged(orders)
            }
    }
}

// UserSession remains the same
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

