package com.example.myapplication.voucher

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

// Homepage新增功能的UI组件
@Composable
fun VoucherCard(voucher: VoucherEntity, onClick: (() -> Unit)? = null) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .let { if (onClick != null) it.clickable { onClick() } else it },
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = voucher.code, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = voucher.description, color = Color.Gray)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Discount: ${if (voucher.discountType == "PERCENTAGE") "${voucher.discountAmount}%" else "RM${voucher.discountAmount}"}",
                color = Color.Red
            )
            Text(text = "Min order: RM${voucher.minOrderAmount}")
        }
    }
}
