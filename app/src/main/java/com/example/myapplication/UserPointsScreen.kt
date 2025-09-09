package com.example.myapplication

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.myapplication.voucher.PointTransaction
import com.example.myapplication.voucher.UserPoints
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserPointsScreen(
    navController: NavHostController,
    initialPoints: UserPoints = UserPoints(
        userPhoneNumber = "unknown",
        totalPoints = 0,
        availablePoints = 0
    )
) {
    // ✅ 使用 remember 保存状态（不需要传假数据）
    var userPoints by remember { mutableStateOf(initialPoints) }
    var transactions by remember { mutableStateOf(listOf<PointTransaction>()) }
    var redeemPoints by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }

    fun redeem(points: Int) {
        if (points > 0 && points <= userPoints.availablePoints) {
            // 1. 更新积分
            userPoints = userPoints.copy(
                availablePoints = userPoints.availablePoints - points
            )

            // 2. 生成交易记录
            val newTransaction = PointTransaction(
                id = UUID.randomUUID().toString(),
                userPhoneNumber = userPoints.userPhoneNumber,
                points = points,
                type = "REDEEM",
                orderId = null,
                description = "Redeemed $points points",
                timestamp = System.currentTimeMillis()
            )
            transactions = listOf(newTransaction) + transactions

            // 3. 清空输入框 & 提示
            redeemPoints = ""
            message = "Successfully redeemed $points points!"
        } else {
            message = "Invalid points amount"
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Points", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF4CAF50))
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Current Points Overview
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("⭐ My Points", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(12.dp))
                    PointInfoRow("Total Points", "${userPoints.totalPoints}")
                    PointInfoRow("Available Points", "${userPoints.availablePoints}")
                }
            }

            // Redeem Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("🎁 Redeem Points", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(12.dp))

                    OutlinedTextField(
                        value = redeemPoints,
                        onValueChange = { redeemPoints = it },
                        label = { Text("Enter points to redeem") },
                        placeholder = { Text("e.g., 100") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(12.dp))

                    Button(
                        onClick = {
                            redeemPoints.toIntOrNull()?.let { redeem(it) }
                                ?: run { message = "Please enter a valid number" }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                    ) {
                        Text("Redeem", color = Color.White)
                    }

                    if (message.isNotEmpty()) {
                        Text(
                            message,
                            color = if (message.contains("Success")) Color.Green else Color.Red,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            }

            // Transaction History
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("📜 Recent Transactions", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(12.dp))

                    if (transactions.isEmpty()) {
                        Text("No transactions yet", color = Color.Gray)
                    } else {
                        transactions.take(5).forEach { txn ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    txn.description,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    (if (txn.type == "EARN") "+ " else "- ") + txn.points,
                                    color = if (txn.type == "EARN") Color(0xFF4CAF50) else Color.Red,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PointInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = 14.sp, color = Color.Gray)
        Text(value, fontSize = 14.sp, fontWeight = FontWeight.Medium)
    }
}
