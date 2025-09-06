package com.example.myapplication


import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartPage(navController: NavController) {
    val cartItems = Cart.getItems()
    var selectedVoucher by remember { mutableStateOf("None") }
    val vouchers = listOf("None", "RM2 OFF", "10% Discount")

    // 计算总价
    val subtotal = cartItems.sumOf { it.price }
    val discount = when (selectedVoucher) {
        "RM2 OFF" -> 2.0
        "10% Discount" -> subtotal * 0.1
        else -> 0.0
    }
    val total = subtotal - discount

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Your Cart") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            // 商品列表
            LazyColumn(
                modifier = Modifier.weight(1f)
            ) {
                items(cartItems) { item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = item.imageResId),
                            contentDescription = item.name,
                            modifier = Modifier.size(60.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(item.name, style = MaterialTheme.typography.bodyLarge)
                            Text("RM ${item.price}", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }

            // Voucher
            Text("Voucher", style = MaterialTheme.typography.bodyLarge)
            DropdownMenuBox(
                selectedVoucher = selectedVoucher,
                vouchers = vouchers,
                onVoucherSelected = { selectedVoucher = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Total
            Text("Subtotal: RM %.2f".format(subtotal))
            Text("Discount: RM %.2f".format(discount))
            Text("Total: RM %.2f".format(total), style = MaterialTheme.typography.titleLarge)

            Spacer(modifier = Modifier.height(20.dp))

            // Proceed 按钮
            Button(
                onClick = { navController.navigate("payment") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Proceed to Payment")
            }
        }
    }
}

@Composable
fun DropdownMenuBox(
    selectedVoucher: String,
    vouchers: List<String>,
    onVoucherSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        OutlinedButton(onClick = { expanded = true }) {
            Text(selectedVoucher)
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            vouchers.forEach { voucher ->
                DropdownMenuItem(
                    text = { Text(voucher) },
                    onClick = {
                        onVoucherSelected(voucher)
                        expanded = false
                    }
                )
            }
        }
    }
}
