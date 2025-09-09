package com.example.myapplication

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.myapplication.voucher.*

// Admin Voucher Management Screen
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminVoucherManagementScreen(navController: NavController) {
    var showCreateDialog by remember { mutableStateOf(false) }
    val vouchers = VoucherManager.getAllVouchers()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Manage Vouchers", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF4CAF50))
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showCreateDialog = true },
                containerColor = Color(0xFF4CAF50)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Voucher", tint = Color.White)
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(vouchers) { voucher ->
                AdminVoucherCard(voucher = voucher)
            }

            if (vouchers.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "No vouchers created yet\nClick + to create your first voucher",
                                textAlign = TextAlign.Center,
                                color = Color.Gray
                            )
                        }
                    }
                }
            }
        }
    }

    if (showCreateDialog) {
        CreateVoucherDialog(
            onDismiss = { showCreateDialog = false },
            onConfirm = { voucher ->
                VoucherManager.addVoucher(voucher)
                showCreateDialog = false
            }
        )
    }
}

@Composable
fun AdminVoucherCard(voucher: Voucher) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (voucher.isActive) Color.White else Color(0xFFF5F5F5)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        voucher.description,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = if (voucher.isActive) Color.Black else Color.Gray
                    )
                    Text(
                        "Code: ${voucher.code}",
                        color = Color(0xFF4CAF50),
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp
                    )
                }

                // Status badge
                Box(
                    modifier = Modifier
                        .background(
                            if (voucher.isActive) Color(0xFF4CAF50) else Color.Gray,
                            RoundedCornerShape(12.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        if (voucher.isActive) "Active" else "Inactive",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Discount info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "Discount: ${if (voucher.discountType == "FIXED") "RM${voucher.discountAmount.toInt()}" else "${voucher.discountAmount.toInt()}%"}",
                    fontSize = 14.sp,
                    color = Color.Red,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    "Min Order: RM${"%.2f".format(voucher.minOrderAmount)}",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Usage info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "Usage: ${voucher.currentUsage}/${voucher.maxUsage}",
                    fontSize = 14.sp,
                    color = Color.Gray
                )

                val expiryDate = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault())
                    .format(java.util.Date(voucher.expiryDate))
                Text(
                    "Expires: $expiryDate",
                    fontSize = 14.sp,
                    color = if (voucher.expiryDate < System.currentTimeMillis()) Color.Red else Color.Gray
                )
            }

            // Progress bar for usage
            if (voucher.maxUsage > 0) {
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = (voucher.currentUsage.toFloat() / voucher.maxUsage.toFloat()),
                    modifier = Modifier.fillMaxWidth(),
                    color = Color(0xFF4CAF50),
                    trackColor = Color(0xFFE0E0E0)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateVoucherDialog(
    onDismiss: () -> Unit,
    onConfirm: (Voucher) -> Unit
) {
    var code by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var discountAmount by remember { mutableStateOf("") }
    var discountType by remember { mutableStateOf("FIXED") }
    var minOrderAmount by remember { mutableStateOf("") }
    var maxUsage by remember { mutableStateOf("") }
    var expiryDate by remember { mutableStateOf("") } // dd/MM/yyyy 格式

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create New Voucher") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = code,
                    onValueChange = { code = it },
                    label = { Text("Voucher Code") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = discountAmount,
                    onValueChange = { discountAmount = it },
                    label = { Text("Discount Amount") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                // Discount type selection
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = discountType == "FIXED",
                        onClick = { discountType = "FIXED" }
                    )
                    Text("Fixed Amount")
                    Spacer(modifier = Modifier.width(12.dp))
                    RadioButton(
                        selected = discountType == "PERCENTAGE",
                        onClick = { discountType = "PERCENTAGE" }
                    )
                    Text("Percentage")
                }
                OutlinedTextField(
                    value = minOrderAmount,
                    onValueChange = { minOrderAmount = it },
                    label = { Text("Minimum Order Amount") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                OutlinedTextField(
                    value = maxUsage,
                    onValueChange = { maxUsage = it },
                    label = { Text("Max Usage") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                OutlinedTextField(
                    value = expiryDate,
                    onValueChange = { expiryDate = it },
                    label = { Text("Expiry Date (dd/MM/yyyy)") },
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    try {
                        val dateFormat = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault())
                        val expiryMillis = dateFormat.parse(expiryDate)?.time ?: System.currentTimeMillis()
                        val voucher = Voucher(
                            code = code,
                            description = description,
                            discountAmount = discountAmount.toDoubleOrNull() ?: 0.0,
                            discountType = discountType,
                            minOrderAmount = minOrderAmount.toDoubleOrNull() ?: 0.0,
                            maxUsage = maxUsage.toIntOrNull() ?: 0,
                            currentUsage = 0,
                            expiryDate = expiryMillis,
                            isActive = true

                        )
                        onConfirm(voucher)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
            ) {
                Text("Create", color = Color.White)
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
