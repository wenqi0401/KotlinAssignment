package com.example.myapplication

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myapplication.admin.AdminTopBar
import kotlinx.coroutines.launch
import com.example.myapplication.orderData.Order
import com.example.myapplication.orderData.OrderRepository

@Composable
fun AdminScreen(
    repository: OrderRepository,
    onBack: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var orders by remember { mutableStateOf<List<Order>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    // 加载订单数据
    LaunchedEffect(Unit) {
        orders = repository.getAllOrders()
        isLoading = false
    }

    Scaffold(
        topBar = {
            AdminTopBar(
                title = "Admin Dashboard",
                onBack = onBack
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (isLoading) {
                CircularProgressIndicator()
            } else if (orders.isEmpty()) {
                Text("No orders yet")
            } else {
                LazyColumn {
                    items(orders.size) { index ->
                        val order = orders[index]
                        Card(
                            modifier = Modifier
                                .padding(8.dp)
                                .fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(4.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Order ID: ${order.orderId}")
                                Text("User: ${order.phoneNumber}")
                                Text("Items: ${order.items.joinToString { "${it.name} x${it.quantity}" }}")
                                Text("Amount: RM ${order.total}")
                                Text("Status: ${order.status}")
                                Spacer(Modifier.height(8.dp))
                                Row {
                                    Button(
                                        onClick = {
                                            scope.launch {
                                                repository.updateOrderStatus(order.orderId, "Preparing")
                                                orders = repository.getAllOrders()
                                            }
                                        },
                                        modifier = Modifier.padding(end = 8.dp)
                                    ) {
                                        Text("Preparing")
                                    }
                                    Button(
                                        onClick = {
                                            scope.launch {
                                                repository.updateOrderStatus(order.orderId, "On the way")
                                                orders = repository.getAllOrders()
                                            }
                                        },
                                        modifier = Modifier.padding(end = 8.dp)
                                    ) {
                                        Text("On The Way")
                                    }
                                    Button(
                                        onClick = {
                                            scope.launch {
                                                repository.updateOrderStatus(order.orderId, "Delivered")
                                                orders = repository.getAllOrders()
                                            }
                                        }
                                    ) {
                                        Text("Delivered")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}