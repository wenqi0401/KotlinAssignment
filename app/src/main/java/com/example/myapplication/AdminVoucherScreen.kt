// Updated AdminVoucherManagementScreen.kt - Removed Send to All Users and Added Delete Functionality
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.myapplication.voucher.*
import com.example.myapplication.registerData.UserRepository
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminVoucherManagementScreen(navController: NavController) {
    val context = LocalContext.current
    val voucherManager = remember { VoucherManager.getInstance(context) }
    val userRepository = remember { UserRepository() }
    val coroutineScope = rememberCoroutineScope()

    var showCreateDialog by remember { mutableStateOf(false) }
    var vouchers by remember { mutableStateOf<List<VoucherEntity>>(emptyList()) }

    // Load vouchers when screen starts
    LaunchedEffect(Unit) {
        voucherManager.initializeDefaultVouchers()
        vouchers = voucherManager.getAllVouchers()
    }

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
                AdminVoucherCard(
                    voucher = voucher,
                    voucherManager = voucherManager,
                    userRepository = userRepository,
                    onVoucherUpdated = {
                        // Refresh voucher list after any changes
                        coroutineScope.launch {
                            vouchers = voucherManager.getAllVouchers()
                        }
                    }
                )
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
            voucherManager = voucherManager,
            onDismiss = { showCreateDialog = false },
            onConfirm = { voucher ->
                coroutineScope.launch {
                    voucherManager.addVoucher(voucher)
                    vouchers = voucherManager.getAllVouchers()
                    showCreateDialog = false
                }
            }
        )
    }
}

@Composable
fun AdminVoucherCard(
    voucher: VoucherEntity,
    voucherManager: VoucherManager,
    userRepository: UserRepository,
    onVoucherUpdated: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var showSendDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

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

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "Usage: ${voucher.currentUsage}/${voucher.maxUsage}",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                val expiryDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    .format(Date(voucher.expiryDate))
                Text(
                    "Expires: $expiryDate",
                    fontSize = 14.sp,
                    color = if (voucher.expiryDate < System.currentTimeMillis()) Color.Red else Color.Gray
                )
            }

            if (voucher.maxUsage > 0) {
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = (voucher.currentUsage.toFloat() / voucher.maxUsage.toFloat()),
                    modifier = Modifier.fillMaxWidth(),
                    color = Color(0xFF4CAF50),
                    trackColor = Color(0xFFE0E0E0)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { showSendDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Send Voucher", color = Color.White)
                }

                Button(
                    onClick = { showDeleteDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Delete", color = Color.White)
                }
            }
        }
    }

    if (showSendDialog) {
        SendVoucherDialog(
            voucher = voucher,
            onDismiss = { showSendDialog = false },
            onSendToPhone = { phone, v ->
                coroutineScope.launch {
                    voucherManager.giveVoucherToUser(phone, v)
                    onVoucherUpdated()
                }
                showSendDialog = false
            }
        )
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Voucher") },
            text = {
                Text("Are you sure you want to delete this voucher?\n\nCode: ${voucher.code}\nDescription: ${voucher.description}\n\nThis action cannot be undone and will not affect users who have already redeemed this voucher.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        coroutineScope.launch {
                            voucherManager.deleteVoucher(voucher.id)
                            onVoucherUpdated()
                        }
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("Delete", color = Color.White)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateVoucherDialog(
    voucherManager: VoucherManager,
    onDismiss: () -> Unit,
    onConfirm: (VoucherEntity) -> Unit
) {
    var code by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var discountAmount by remember { mutableStateOf("") }
    var discountType by remember { mutableStateOf("FIXED") }
    var minOrderAmount by remember { mutableStateOf("") }
    var maxUsage by remember { mutableStateOf("") }
    var expiryDate by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create New Voucher") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = code, onValueChange = { code = it.uppercase() }, label = { Text("Voucher Code") })
                OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Description") })
                OutlinedTextField(value = discountAmount, onValueChange = { discountAmount = it }, label = { Text("Discount Amount") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(selected = discountType == "FIXED", onClick = { discountType = "FIXED" })
                    Text("Fixed Amount")
                    Spacer(modifier = Modifier.width(12.dp))
                    RadioButton(selected = discountType == "PERCENTAGE", onClick = { discountType = "PERCENTAGE" })
                    Text("Percentage")
                }
                OutlinedTextField(value = minOrderAmount, onValueChange = { minOrderAmount = it }, label = { Text("Minimum Order Amount") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                OutlinedTextField(value = maxUsage, onValueChange = { maxUsage = it }, label = { Text("Max Usage") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                OutlinedTextField(value = expiryDate, onValueChange = { expiryDate = it }, label = { Text("Expiry Date (dd/MM/yyyy)") })
            }
        },
        confirmButton = {
            Button(onClick = {
                try {
                    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    val expiryMillis = dateFormat.parse(expiryDate)?.time ?: System.currentTimeMillis()
                    val voucher = VoucherEntity(
                        id = UUID.randomUUID().toString(),
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
            }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))) {
                Text("Create", color = Color.White)
            }
        },
        dismissButton = { OutlinedButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SendVoucherDialog(
    voucher: VoucherEntity,
    onDismiss: () -> Unit,
    onSendToPhone: (String, VoucherEntity) -> Unit
) {
    var phoneNumber by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Send Voucher") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Voucher Code: ${voucher.code}", fontWeight = FontWeight.Bold)
                Text("Description: ${voucher.description}")
                OutlinedTextField(
                    value = phoneNumber,
                    onValueChange = { phoneNumber = it },
                    label = { Text("Enter User Phone Number") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { if (phoneNumber.isNotBlank()) onSendToPhone(phoneNumber, voucher) },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                enabled = phoneNumber.isNotBlank()
            ) {
                Text("Send to Phone", color = Color.White)
            }
        },
        dismissButton = { OutlinedButton(onClick = onDismiss) { Text("Cancel") } }
    )
}