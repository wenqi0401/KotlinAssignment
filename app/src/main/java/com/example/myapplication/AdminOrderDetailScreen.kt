package com.example.myapplication

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.myapplication.admin.AdminTopBar
import com.example.myapplication.orderData.OrderRepository
import kotlinx.coroutines.launch

@Composable
fun AdminOrderDetailScreen(
    navController: NavController,
    orderId: String,
    repository: OrderRepository
) {
    var order by remember { mutableStateOf<com.example.myapplication.orderData.Order?>(null) }
    var showUpdateDialog by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(true) }
    val coroutineScope = rememberCoroutineScope()

    // Load order details
    LaunchedEffect(orderId) {
        isLoading = true
        order = repository.getOrderById(orderId)
        isLoading = false
    }

    if (isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = androidx.compose.ui.Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    if (order == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = androidx.compose.ui.Alignment.Center
        ) {
            Text("Order not found", fontSize = 18.sp, color = Color.Red)
        }
        return
    }

    Scaffold(
        topBar = { AdminTopBar("Order #$orderId") { navController.popBackStack() } }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Order information card
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Order Information",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2196F3)
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    OrderInfoRow("📱 Phone", order!!.phoneNumber)
                    OrderInfoRow("📍 Address", order!!.deliveryAddress)
                    OrderInfoRow("📅 Date", java.text.SimpleDateFormat("MMM dd, yyyy HH:mm", java.util.Locale.getDefault()).format(java.util.Date(order!!.orderDate)))
                    OrderInfoRow("💰 Total", "RM ${String.format("%.2f", order!!.total)}")

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("📋 Status:", fontSize = 16.sp, color = Color.Gray)
                        // Simple Text instead of StatusBadge
                        Text(
                            text = order!!.status,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = when (order!!.status.lowercase()) {
                                "preparing" -> Color(0xFFFF9800)
                                "on the way" -> Color(0xFF2196F3)
                                "delivered" -> Color(0xFF4CAF50)
                                else -> Color.Gray
                            }
                        )
                    }
                }
            }

            // Order items
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Order Items",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2196F3)
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    order!!.items.forEach { item ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("${item.name} x${item.quantity}")
                            Text("RM ${String.format("%.2f", item.price * item.quantity)}")
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }

            // Status update buttons
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Update Order Status",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2196F3)
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                coroutineScope.launch {
                                    repository.updateOrderStatus(orderId, "Preparing")
                                    order = repository.getOrderById(orderId)
                                    showUpdateDialog = true
                                }
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFFF9800)
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Preparing", fontSize = 12.sp)
                        }

                        Button(
                            onClick = {
                                coroutineScope.launch {
                                    repository.updateOrderStatus(orderId, "On the way")
                                    order = repository.getOrderById(orderId)
                                    showUpdateDialog = true
                                }
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF2196F3)
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("On the way", fontSize = 12.sp)
                        }

                        Button(
                            onClick = {
                                coroutineScope.launch {
                                    repository.updateOrderStatus(orderId, "Delivered")
                                    order = repository.getOrderById(orderId)
                                    showUpdateDialog = true
                                }
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF4CAF50)
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Delivered", fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // Update confirmation dialog
        if (showUpdateDialog) {
            AlertDialog(
                onDismissRequest = { showUpdateDialog = false },
                title = { Text("Status Updated") },
                text = { Text("Order status has been updated to: ${order!!.status}") },
                confirmButton = {
                    Button(onClick = { showUpdateDialog = false }) {
                        Text("OK")
                    }
                }
            )
        }
    }
}

@Composable
fun OrderInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = 16.sp, color = Color.Gray)
        Text(value, fontSize = 16.sp, fontWeight = FontWeight.Medium)
    }
}