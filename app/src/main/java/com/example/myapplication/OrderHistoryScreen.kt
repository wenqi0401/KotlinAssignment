package com.example.myapplication

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.myapplication.orderData.Order
import com.example.myapplication.orderData.OrderItem
import com.example.myapplication.orderData.OrderRepository
import com.example.myapplication.orderData.UserSession
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderHistoryScreen(navController: NavHostController) {
    val context = LocalContext.current
    val repository = remember { OrderRepository(context) }
    var orders by remember { mutableStateOf<List<Order>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showRatingDialog by remember { mutableStateOf(false) }
    var currentOrderForRating by remember { mutableStateOf<Order?>(null) }
    var rating by remember { mutableStateOf(0) }
    var feedback by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    // Function to refresh orders
    suspend fun refreshOrders() {
        try {
            val currentUserPhone = UserSession.getCurrentUser()
            if (currentUserPhone != null) {
                val userOrders = repository.getUserOrders(currentUserPhone)
                orders = userOrders
                Log.d(
                    "OrderHistory",
                    "Refreshed ${userOrders.size} orders for user: $currentUserPhone"
                )
            }
        } catch (e: Exception) {
            Log.e("OrderHistory", "Error refreshing orders", e)
        }
    }

    LaunchedEffect(Unit) {
        try {
            val currentUserPhone = UserSession.getCurrentUser()
            Log.d("OrderHistory", "Current user phone: $currentUserPhone")

            if (currentUserPhone != null) {
                val userOrders = repository.getUserOrders(currentUserPhone)
                orders = userOrders
                Log.d("OrderHistory", "Found ${userOrders.size} orders for user: $currentUserPhone")
            } else {
                errorMessage = "No user logged in"
                Log.d("OrderHistory", "No user session found")
            }
        } catch (e: Exception) {
            Log.e("OrderHistory", "Error loading orders", e)
            errorMessage = "Error loading orders: ${e.message}"
        } finally {
            isLoading = false
        }
    }

    if (showRatingDialog && currentOrderForRating != null) {
        RatingDialog(
            order = currentOrderForRating!!,
            rating = rating,
            feedback = feedback,
            onRatingChange = { rating = it },
            onFeedbackChange = { feedback = it },
            onDismiss = {
                showRatingDialog = false
                rating = 0
                feedback = ""
                currentOrderForRating = null
            },
            onSubmit = {
                // This is now a suspend function
                try {
                    repository.updateOrderRating(
                        currentOrderForRating!!.orderId,
                        rating,
                        feedback
                    )

                    // Refresh orders to get updated data
                    refreshOrders()

                    Log.d("OrderHistory", "Rating updated successfully")

                    // Close dialog and reset state
                    showRatingDialog = false
                    rating = 0
                    feedback = ""
                    currentOrderForRating = null

                } catch (e: Exception) {
                    Log.e("OrderHistory", "Error updating rating", e)
                    // You might want to show an error message to user here
                }
            }
        )
    }

    // Rest of your Scaffold code remains the same...
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Order History",
                        fontSize = 20.sp,
                        textAlign = TextAlign.Center,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Red
                )
            )
        }
    ) { padding ->
        // Your existing UI code continues here...
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color.Red)
            }
        } else if (errorMessage != null) {
            // Your error UI...
        } else if (orders.isEmpty()) {
            // Your empty state UI...
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(orders) { order ->
                    OrderSummaryCard(
                        order = order,
                        onRateClick = { orderToRate ->
                            currentOrderForRating = orderToRate
                            showRatingDialog = true
                        } ,

                        navController = navController
                    )


                }
            }
        }
    }
}

@Composable
fun OrderSummaryCard(
    order: Order,
    onRateClick: (Order) -> Unit,
    navController: NavHostController

) {
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(6.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)

    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color.Red.copy(alpha = 0.1f), RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🍦", fontSize = 20.sp)
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            "MIXUE ${(order.deliveryAddress ?: "").take(15)}...",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color.Black
                        )
                        Text(
                            formatDate(order.orderDate),
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                    }
                }
                StatusBadge(status = order.status ?: "Unknown")
            }

            Spacer(modifier = Modifier.height(16.dp))
            Divider(color = Color.Gray.copy(alpha = 0.3f))
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                "📋 Order Items",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            order.items.forEach { item ->
                OrderItemRow(orderItem = item)
                Spacer(modifier = Modifier.height(8.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))
            Divider(color = Color.Gray.copy(alpha = 0.3f))
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "💰 Price Details",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color.Black
                )
                IconButton(
                    onClick = { isExpanded = !isExpanded },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = if (isExpanded) "Collapse" else "Expand",
                        tint = Color.Gray
                    )
                }
            }

            if (isExpanded) {
                Spacer(modifier = Modifier.height(12.dp))
                PriceDetailRow("Subtotal", order.subtotal)
                PriceDetailRow("Delivery Fee", order.deliveryFee)
                PriceDetailRow("Tax (6%)", order.tax)
                if (order.voucher > 0) {
                    PriceDetailRow("Voucher", -order.voucher, isDiscount = true)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Divider(color = Color.Gray.copy(alpha = 0.3f))
                Spacer(modifier = Modifier.height(8.dp))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Total Amount",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color.Black
                )
                Text(
                    "RM ${"%.2f".format(order.total)}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color.Red
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Column {
                Text(
                    "📋 Order Details",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color.Black,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                OrderDetailRow("📱 Phone", order.phoneNumber ?: "N/A")
                OrderDetailRow("📍 Address", order.deliveryAddress ?: "N/A")
                OrderDetailRow("💳 Payment", order.paymentMethod?.uppercase() ?: "UNKNOWN")
                if (!order.comment.isNullOrEmpty()) {
                    OrderDetailRow("💬 Comment", order.comment)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = { navController.navigate("help") },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Gray),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("HELP", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                Button(
                    onClick = { onRateClick(order) },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (order.rating > 0) Color.Gray else Color.Red
                    ),
                    shape = RoundedCornerShape(8.dp),
                    enabled = order.rating == 0
                ) {
                    Text(
                        if (order.rating > 0) "RATED" else "RATE",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun OrderItemRow(orderItem: OrderItem) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (orderItem.imageResId != 0) {
            Image(
                painter = painterResource(id = orderItem.imageResId),
                contentDescription = orderItem.name ?: "",
                modifier = Modifier
                    .size(50.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
        } else {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.Gray.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Text("?", color = Color.Gray)
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                orderItem.name ?: "Unknown Item",
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                color = Color.Black
            )
            Text(
                "Qty: ${orderItem.quantity}",
                color = Color.Gray,
                fontSize = 12.sp
            )
        }

        Text(
            "RM ${"%.2f".format(orderItem.price * orderItem.quantity)}",
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = Color.Black
        )
    }
}

@Composable
fun PriceDetailRow(label: String, amount: Double, isDiscount: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = 14.sp, color = Color.Gray)
        Text(
            "RM ${"%.2f".format(amount)}",
            fontSize = 14.sp,
            color = if (isDiscount) Color.Green else Color.Gray,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun OrderDetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Text(
            label,
            fontSize = 14.sp,
            color = Color.Gray,
            modifier = Modifier.width(80.dp)
        )
        Text(
            value,
            fontSize = 14.sp,
            color = Color.Black,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun StatusBadge(status: String) {
    val backgroundColor = when (status) {
        "Preparing" -> Color(0xFFFFF3CD)
        "On the way" -> Color(0xFFD1ECF1)
        "Delivered" -> Color(0xFFD4EDDA)
        else -> Color.LightGray
    }
    val textColor = when (status) {
        "Preparing" -> Color(0xFF856404)
        "On the way" -> Color(0xFF0C5460)
        "Delivered" -> Color(0xFF155724)
        else -> Color.DarkGray
    }
    Box(
        modifier = Modifier
            .background(backgroundColor, RoundedCornerShape(20.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(text = status, color = textColor, fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}

fun formatDate(timestamp: Long): String {
    return try {
        val sdf = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
        sdf.format(Date(timestamp))
    } catch (e: Exception) {
        "Unknown Date"
    }
}


@Composable
fun RatingDialog(
    order: Order,
    rating: Int,
    feedback: String,
    onRatingChange: (Int) -> Unit,
    onFeedbackChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onSubmit: suspend () -> Unit // Make this suspend
) {
    var isSubmitting by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    AlertDialog(
        onDismissRequest = { if (!isSubmitting) onDismiss() },
        title = {
            Text("Rate Your Order", fontWeight = FontWeight.Bold)
        },
        text = {
            Column {
                Text(
                    "How would you rate your experience?",
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // Star Rating
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    for (i in 1..5) {
                        IconButton(
                            onClick = { if (!isSubmitting) onRatingChange(i) },
                            modifier = Modifier.size(40.dp),
                            enabled = !isSubmitting
                        ) {
                            Icon(
                                if (i <= rating) Icons.Filled.Star else Icons.Outlined.Star,
                                contentDescription = "$i stars",
                                tint = if (i <= rating) Color(0xFFFFD700) else Color.Gray,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Feedback
                OutlinedTextField(
                    value = feedback,
                    onValueChange = { if (!isSubmitting) onFeedbackChange(it) },
                    label = { Text("Feedback (optional)") },
                    placeholder = { Text("Share your experience...") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3,
                    enabled = !isSubmitting
                )

                if (isSubmitting) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Color.Red
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Submitting rating...", fontSize = 12.sp, color = Color.Gray)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    coroutineScope.launch {
                        isSubmitting = true
                        try {
                            onSubmit()
                        } catch (e: Exception) {
                            Log.e("RatingDialog", "Error submitting rating", e)
                        } finally {
                            isSubmitting = false
                        }
                    }
                },
                enabled = rating > 0 && !isSubmitting,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
            ) {
                Text("Submit Rating")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = !isSubmitting
            ) {
                Text("Cancel")
            }
        }
    )
}
