package com.example.myapplication

import com.example.myapplication.voucher.VoucherCard
import com.example.myapplication.voucher.VoucherManager
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
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

    LaunchedEffect(currentUser) {
        if (currentUser != null) {
            userVouchers = voucherManager.getUserVouchers(currentUser)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Vouchers", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Brush.horizontalGradient(
                        colors = listOf(Color.Red, Color(0xFFE57373))
                    ).let { Color.Red }
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            contentPadding = PaddingValues(vertical = 20.dp)
        ) {
            // Redeem voucher section - Enhanced UI
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(8.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFF8F9FA)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("🎫", fontSize = 24.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Redeem Voucher Code",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2E3A59)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = voucherCode,
                            onValueChange = { voucherCode = it.uppercase() },
                            label = { Text("Enter voucher code", color = Color(0xFF6B7280)) },
                            placeholder = { Text("e.g. WELCOME20", color = Color(0xFFD1D5DB)) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color.Red,
                                unfocusedBorderColor = Color(0xFFE5E7EB),
                                focusedLabelColor = Color.Red
                            )
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                if (currentUser != null && voucherCode.isNotEmpty()) {
                                    coroutineScope.launch {
                                        val success = voucherManager.redeemVoucher(currentUser, voucherCode)
                                        if (success) {
                                            voucherCode = ""
                                            showMessage = "Voucher redeemed successfully!"
                                            userVouchers = voucherManager.getUserVouchers(currentUser)
                                        } else {
                                            showMessage = "Invalid or expired voucher code"
                                        }
                                    }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Red,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(12.dp),
                            enabled = voucherCode.isNotEmpty()
                        ) {
                            Text(
                                "Redeem Voucher",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        if (showMessage.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (showMessage.contains("success"))
                                        Color(0xFFDCFCE7) else Color(0xFFFEE2E2)
                                ),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    showMessage,
                                    color = if (showMessage.contains("success"))
                                        Color(0xFF065F46) else Color(0xFFDC2626),
                                    modifier = Modifier.padding(12.dp),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }

            // Available vouchers header - Enhanced
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(4.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF1F2937)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                "Available Vouchers",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                "${userVouchers.size} voucher${if (userVouchers.size != 1) "s" else ""} ready to use",
                                fontSize = 14.sp,
                                color = Color(0xFFD1D5DB)
                            )
                        }

                        Box(
                            modifier = Modifier
                                .size(40.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "${userVouchers.size}",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }

            // User's vouchers
            items(userVouchers) { pair ->
                val (_, voucher) = pair
                VoucherCard(voucher = voucher)
            }

            // Empty state - Enhanced
            if (userVouchers.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(4.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFF9FAFB)
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(40.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                "🎟️",
                                fontSize = 48.sp
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                "No Vouchers Yet",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = Color(0xFF374151),
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "Redeem a voucher code above\nto get started with savings!",
                                textAlign = TextAlign.Center,
                                color = Color(0xFF6B7280),
                                fontSize = 14.sp,
                                lineHeight = 20.sp
                            )
                        }
                    }
                }
            }
        }
    }
}