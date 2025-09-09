package com.example.myapplication

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
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
fun AdminOrderListScreen(
    navController: NavController,
    repository: OrderRepository
) {
    var orders by remember { mutableStateOf<List<com.example.myapplication.orderData.Order>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    val coroutineScope = rememberCoroutineScope()

    // Load orders
    LaunchedEffect(Unit) {
        isLoading = true
        orders = repository.getAllOrders()
        isLoading = false
    }

    Scaffold(
        topBar = { AdminTopBar("Order Management") { navController.popBackStack() } }
    ) { innerPadding ->
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (orders.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "📋",
                        fontSize = 64.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "No orders yet",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(innerPadding).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(orders) { order ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                navController.navigate("admin_order_detail/${order.orderId}")
                            },
                        elevation = CardDefaults.cardElevation(4.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        "Order #${order.orderId}",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF2196F3)
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("📱 ${order.phoneNumber}", fontSize = 14.sp)
                                    Text("💰 RM ${String.format("%.2f", order.total)}", fontSize = 14.sp)
                                }

                                // Status Badge
                                StatusBadgeAdmin(order.status)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatusBadgeAdmin(status: String) {
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

    Surface(
        color = backgroundColor,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.padding(4.dp)
    ) {
        Text(
            text = status,
            color = textColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}