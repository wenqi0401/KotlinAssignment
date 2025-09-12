package com.example.myapplication

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.myapplication.admin.AdminTopBar
import com.example.myapplication.orderData.Order
import com.example.myapplication.orderData.OrderRepository
import kotlinx.coroutines.launch

@Composable
fun AdminOrderDetailScreen(
    navController: NavController,
    orderId: String,
    repository: OrderRepository
) {
    var order by remember { mutableStateOf<Order?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var showUpdateDialog by remember { mutableStateOf(false) }


    LaunchedEffect(orderId) {
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
                    OrderInfoRow(
                        "📅 Date",
                        java.text.SimpleDateFormat(
                            "MMM dd, yyyy HH:mm",
                            java.util.Locale.getDefault()
                        ).format(java.util.Date(order!!.orderDate))
                    )
                    OrderInfoRow("🛒 Items", "${order!!.items.size} items")

                    OrderInfoRow("💰 Total", "RM ${String.format("%.2f", order!!.total)}")

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("📋 Status:", fontSize = 16.sp, color = Color.Gray)
                        StatusBadgeAdmin(order!!.status)
                    }
                }
            }

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
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Start
                        ) {
                            Text(
                                "❄️ ${item.ice}",
                                fontSize = 14.sp,
                                color = Color(0xFF607D8B)
                            )
                            Text(
                                " • ",
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                            Text(
                                "🍯 ${item.sugar}",
                                fontSize = 14.sp,
                                color = Color(0xFF607D8B)
                            )
                        }
                    }
                }
            }

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
                        StatusUpdateButton(
                            "Preparing", Color(0xFFFF9800), orderId, repository,
                            modifier = Modifier.weight(1f)
                        ) { order = it; showUpdateDialog = true }

                        StatusUpdateButton(
                            "On the way", Color(0xFF2196F3), orderId, repository,
                            modifier = Modifier.weight(1f)
                        ) { order = it; showUpdateDialog = true }

                        StatusUpdateButton(
                            "Delivered", Color(0xFF4CAF50), orderId, repository,
                            modifier = Modifier.weight(1f)
                        ) { order = it; showUpdateDialog = true }
                    }

                }
            }


        }

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
fun StatusUpdateButton(
    newStatus: String,
    color: Color,
    orderId: String,
    repository: OrderRepository,
    modifier: Modifier = Modifier,
    onUpdated: (Order?) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()

    Button(
        onClick = {
            coroutineScope.launch {
                repository.updateOrderStatus(orderId, newStatus)
                val updated = repository.getOrderById(orderId)
                onUpdated(updated)
            }
        },
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(containerColor = color),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(newStatus, fontSize = 12.sp)
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