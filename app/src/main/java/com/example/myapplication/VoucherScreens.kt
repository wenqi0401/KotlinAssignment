package com.example.myapplication

import com.example.myapplication.voucher.VoucherCard
import com.example.myapplication.voucher.VoucherManager
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.myapplication.orderData.UserSession
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VoucherCenterScreen(navController: NavHostController) {
    val currentUser = UserSession.getCurrentUser()
    val context = LocalContext.current
    val voucherManager = remember { VoucherManager.getInstance(context) }
    val coroutineScope = rememberCoroutineScope()

    var voucherCode by remember { mutableStateOf("") }
    var showMessage by remember { mutableStateOf("") }
    var userVouchers by remember { mutableStateOf<List<Pair<com.example.myapplication.voucher.UserVoucherEntity, com.example.myapplication.voucher.VoucherEntity>>>(emptyList()) }

    // 初始化加载用户的 vouchers
    LaunchedEffect(currentUser) {
        if (currentUser != null) {
            userVouchers = voucherManager.getUserVouchers(currentUser)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Voucher Center", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Red)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Redeem voucher section
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            "Redeem Voucher Code",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = voucherCode,
                            onValueChange = { voucherCode = it.uppercase() },
                            label = { Text("Enter voucher code") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = {
                                if (currentUser != null && voucherCode.isNotEmpty()) {
                                    coroutineScope.launch {
                                        val success = voucherManager.redeemVoucher(currentUser, voucherCode)
                                        if (success) {
                                            voucherCode = ""
                                            showMessage = "Voucher redeemed successfully!"
                                            userVouchers = voucherManager.getUserVouchers(currentUser) // 刷新列表
                                        } else {
                                            showMessage = "Invalid or expired voucher code"
                                        }
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Blue)
                        ) {
                            Text("Redeem", color = Color.White)
                        }

                        if (showMessage.isNotEmpty()) {
                            Text(
                                showMessage,
                                color = if (showMessage.contains("success")) Color.Green else Color.Red,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                    }
                }
            }

            // Available vouchers title
            item {
                Text(
                    "Available Vouchers (${userVouchers.size})",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Red
                )
            }

            // User's vouchers
            // User's vouchers
            items(userVouchers) { pair ->
                val (_, voucher) = pair
                VoucherCard(voucher = voucher)
            }


            if (userVouchers.isEmpty()) {
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
                                "No vouchers available\nRedeem a code to get started!",
                                textAlign = TextAlign.Center,
                                color = Color.Gray
                            )
                        }
                    }
                }
            }
        }
    }
}
