package com.example.myapplication.voucher

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.myapplication.orderData.UserSession

// Homepage新增功能的UI组件
@Composable
fun VoucherSection(navController: NavHostController) {
    val currentUser = UserSession.getCurrentUser()
    val userVouchers = if (currentUser != null) VoucherManager.getUserVouchers(currentUser) else emptyList()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { navController.navigate("voucher_center") },
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "🎫",
                fontSize = 24.sp
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "Voucher Center",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(
                    "${userVouchers.size} available vouchers",
                    color = if (userVouchers.isNotEmpty()) Color(0xFF2196F3) else Color.Gray,
                    fontSize = 14.sp,
                    fontWeight = if (userVouchers.isNotEmpty()) FontWeight.Medium else FontWeight.Normal
                )
            }
            if (userVouchers.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .background(Color.Red, RoundedCornerShape(12.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        "${userVouchers.size}",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
            }
            Icon(
                Icons.Default.ArrowForward,
                contentDescription = "Go to vouchers",
                tint = Color.Blue
            )
        }
    }
}

@Composable
fun PointsSection(navController: NavHostController) {
    val currentUser = UserSession.getCurrentUser()
    val userPoints = if (currentUser != null) PointsManager.getUserPoints(currentUser) else UserPoints("", 0, 0)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { navController.navigate("points_center") },
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "⭐",
                fontSize = 24.sp
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "My Points: ${userPoints.availablePoints}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(
                    "RM${"%.2f".format(PointsManager.convertPointsToVoucherValue(userPoints.availablePoints))} voucher value",
                    color = Color.Gray,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            Icon(
                Icons.Default.ArrowForward,
                contentDescription = "Go to points",
                tint = Color.Green
            )
        }
    }
}

@Composable
fun VoucherCard(voucher: Voucher) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF3E5F5))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Voucher icon
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .background(Color.Red, RoundedCornerShape(30.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    if (voucher.discountType == "FIXED") "RM" else "%",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    voucher.description,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(
                    "Code: ${voucher.code}",
                    color = Color.Gray,
                    fontSize = 14.sp
                )
                Text(
                    "Min order: RM${"%.2f".format(voucher.minOrderAmount)}",
                    color = Color.Gray,
                    fontSize = 12.sp
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    if (voucher.discountType == "FIXED")
                        "RM${"%.0f".format(voucher.discountAmount)}"
                    else
                        "${voucher.discountAmount.toInt()}%",
                    color = Color.Red,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }
        }
    }
}