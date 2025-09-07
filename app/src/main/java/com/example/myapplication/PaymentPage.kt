package com.example.myapplication

import android.R.attr.phoneNumber
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentPage(navController: NavHostController) {
    val cartItems = CartManager.getItems()
    val subtotal = CartManager.calculateTotal()
    val deliveryFee = 4.73
    val taxRate = 0.06
    val tax = subtotal * taxRate
    val voucher = 0.00
    val total = subtotal + deliveryFee + tax - voucher


    // State variables for user inputs
    var address by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var comment by remember { mutableStateOf("") }
    var selectedPaymentMethod by remember { mutableStateOf("visa") }
    var cardNumber by remember { mutableStateOf("") }

    var showOrderSuccess by remember { mutableStateOf(false) }

    if (showOrderSuccess) {
        LaunchedEffect(Unit) {
            kotlinx.coroutines.delay(2000)
            CartManager.clearAll()
            navController.navigate("track_order") {
                popUpTo("menu_main") { inclusive = false }
            }
        }

        OrderSuccessScreen()
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Payment",
                        fontSize = 20.sp,
                        textAlign = TextAlign.Center,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Red
                )
            )
        },
        bottomBar = {
            Button(
                onClick = {
                    val orderId = "MX-" + (1000..9999).random()  // 自动生成订单号
                    CartManager.clearAll()
                    navController.navigate("trackOrder/$orderId/$address/$phoneNumber") {
                        popUpTo("menu_main") { inclusive = false }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    "Place order",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            // Delivery Information Section
            item {
                DeliveryInfoSection()
            }

            // Phone Number Input
            item {
                PhoneNumberSection(
                    phoneNumber = phoneNumber,
                    onPhoneNumberChange = { phoneNumber = it }
                )
            }

            // Address Input
            item {
                AddressSection(
                    address = address,
                    onAddressChange = { address = it }
                )
            }

            // Comment to Rider
            item {
                CommentSection(
                    comment = comment,
                    onCommentChange = { comment = it }
                )
            }

            // Payment Method Selection
            item {
                PaymentMethodSection(
                    selectedMethod = selectedPaymentMethod,
                    onMethodChange = { selectedPaymentMethod = it },
                    cardNumber = cardNumber,
                    onCardNumberChange = { cardNumber = it }
                )
            }

            // Order Summary
            item {
                OrderSummarySection(
                    cartItems = cartItems,
                    subtotal = subtotal,
                    deliveryFee = deliveryFee,
                    tax = tax,
                    voucher = voucher,
                    total = total
                )
            }

            // Bottom spacing
            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

@Composable
fun DeliveryInfoSection() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Delivery logo using drawable resource
            Image(
                painter = painterResource(id = R.drawable.delivery_logo),
                contentDescription = "MIXUE Delivery",
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(30.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    "Delivery in ~ 10 minutes",
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
fun PhoneNumberSection(
    phoneNumber: String,
    onPhoneNumberChange: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            "📞 Phone number",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        OutlinedTextField(
            value = phoneNumber,
            onValueChange = onPhoneNumberChange,
            placeholder = { Text("Enter your phone number") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            singleLine = true
        )
        Divider(modifier = Modifier.padding(top = 12.dp))
    }
}

@Composable
fun AddressSection(
    address: String,
    onAddressChange: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            "📍 Delivery Address",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        OutlinedTextField(
            value = address,
            onValueChange = onAddressChange,
            placeholder = { Text("Enter your delivery address") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            minLines = 2
        )
        Divider(modifier = Modifier.padding(top = 12.dp))
    }
}


@Composable
fun CommentSection(
    comment: String,
    onCommentChange: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            "💬 Comment to Rider",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        OutlinedTextField(
            value = comment,
            onValueChange = onCommentChange,
            placeholder = { Text("Add a comment") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            minLines = 2
        )
        Divider(modifier = Modifier.padding(top = 12.dp))
    }
}

@Composable
fun PaymentMethodSection(
    selectedMethod: String,
    onMethodChange: (String) -> Unit,
    cardNumber: String,
    onCardNumberChange: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            "💳 Payment Method",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        // Visa Card Option
        PaymentOptionItem(
            emoji = "💳",
            title = "Visa Card",
            subtitle = if (selectedMethod == "visa" && cardNumber.isNotEmpty())
                "Visa •••• ${cardNumber.takeLast(4)}" else "Add card details",
            isSelected = selectedMethod == "visa",
            onClick = { onMethodChange("visa") }
        )

        if (selectedMethod == "visa") {
            OutlinedTextField(
                value = cardNumber,
                onValueChange = {
                    if (it.length <= 16 && it.all { char -> char.isDigit() }) {
                        onCardNumberChange(it)
                    }
                },
                placeholder = { Text("Enter 16-digit card number") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                singleLine = true,
                leadingIcon = {
                    Text("💳", fontSize = 20.sp)
                }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // E-Wallet Option
        PaymentOptionItem(
            emoji = "📱",
            title = "E-Wallet",
            subtitle = "Touch 'n Go / GrabPay.",
            isSelected = selectedMethod == "ewallet",
            onClick = { onMethodChange("ewallet") }
        )



        Spacer(modifier = Modifier.height(12.dp))

        // Cash on Delivery Option
        PaymentOptionItem(
            emoji = "💵",
            title = "Cash on Delivery",
            subtitle = "Pay when you receive",
            isSelected = selectedMethod == "cash",
            onClick = { onMethodChange("cash") }
        )

        Divider(modifier = Modifier.padding(top = 12.dp))
    }
}

@Composable
fun PaymentOptionItem(
    emoji: String,
    title: String,
    subtitle: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            emoji,
            fontSize = 24.sp
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                title,
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp
            )
            Text(
                subtitle,
                color = Color.Gray,
                fontSize = 14.sp
            )
        }

        RadioButton(
            selected = isSelected,
            onClick = onClick,
            colors = RadioButtonDefaults.colors(
                selectedColor = Color.Red,
                unselectedColor = Color.Gray
            )
        )

        Text(
            "➤",
            fontSize = 20.sp,
            color = Color.Gray
        )
    }
}

@Composable
fun OrderSummarySection(
    cartItems: List<CartItem>,
    subtotal: Double,
    deliveryFee: Double,
    tax: Double,
    voucher: Double,
    total: Double
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            "🧾 Order Summary",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        // Cart Items
        cartItems.forEach { cartItem ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp, horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = cartItem.item.imageResId),
                    contentDescription = cartItem.item.name,
                    modifier = Modifier
                        .size(60.dp)
                        .clip(RoundedCornerShape(10.dp)),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        cartItem.item.name,
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp
                    )
                    Text(
                        "Qty: ${cartItem.quantity}",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                }

                Text(
                    "RM ${"%.2f".format(cartItem.item.price * cartItem.quantity)}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }

        Divider(modifier = Modifier.padding(vertical = 16.dp))

        // Price Breakdown
        PriceRow("Subtotal", subtotal)
        PriceRow("Delivery Fee", deliveryFee)
        PriceRow("Tax 6%", tax)
        PriceRow("Voucher", -voucher)

        Divider(modifier = Modifier.padding(vertical = 8.dp))

        // Total
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                "Total (Tax)",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
            Text(
                "RM ${"%.2f".format(total)}",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = Color.Red
            )
        }

        Divider(modifier = Modifier.padding(top = 12.dp))
    }
}

@Composable
fun PriceRow(label: String, amount: Double) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            label,
            fontSize = 16.sp,
            color = Color.DarkGray
        )
        Text(
            text = "RM ${"%.2f".format(amount)}",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.End
        )
    }
}


@Composable
fun OrderSuccessScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Success Icon using emoji
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .background(Color.Green.copy(alpha = 0.1f), RoundedCornerShape(60.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "✅",
                    fontSize = 60.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                "Order Placed Successfully!",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                "Your order is being prepared.\nYou will be redirected to track your order.",
                fontSize = 16.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            CircularProgressIndicator(
                color = Color.Red,
                modifier = Modifier.size(40.dp)
            )
        }
    }
}