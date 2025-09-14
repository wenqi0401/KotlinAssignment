package com.example.myapplication

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.myapplication.orderData.*
import com.example.myapplication.voucher.UserVoucherEntity
import com.example.myapplication.voucher.VoucherEntity
import com.example.myapplication.voucher.VoucherManager
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentPage(navController: NavHostController) {
    val context = LocalContext.current
    val voucherManager = remember { VoucherManager.getInstance(context) }
    val coroutineScope = rememberCoroutineScope()
    val firebaseService = remember { FirebaseService() }
    val repository = remember { OrderRepository(context, firebaseService) }
    val cartItems = CartManager.getItems()
    val subtotal = CartManager.calculateTotal()
    val deliveryFee = 4.73
    val taxRate = 0.06

    // Voucher states
    val currentUser = UserSession.getCurrentUser()
    var userVouchers by remember { mutableStateOf<List<Pair<UserVoucherEntity, VoucherEntity>>>(emptyList()) }
    var selectedVoucher by remember { mutableStateOf<VoucherEntity?>(null) }

    // Calculate discount and totals
    val voucherDiscount = selectedVoucher?.let { voucher ->
        voucherManager.calculateDiscount(voucher, subtotal)
    } ?: 0.0

    val tax = subtotal * taxRate
    val total = subtotal + deliveryFee + tax - voucherDiscount

    // Load user vouchers
    LaunchedEffect(currentUser) {
        if (currentUser != null) {
            voucherManager.initializeDefaultVouchers()
            userVouchers = voucherManager.getUserVouchers(currentUser)
        }
    }

    // State variables for user inputs
    var address by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var comment by remember { mutableStateOf("") }
    var selectedPaymentMethod by remember { mutableStateOf("") }
    var cardNumber by remember { mutableStateOf("") }

    // Validation state
    var showValidationErrors by remember { mutableStateOf(false) }
    var phoneNumberError by remember { mutableStateOf("") }
    var addressError by remember { mutableStateOf("") }
    var paymentMethodError by remember { mutableStateOf("") }
    var cardNumberError by remember { mutableStateOf("") }

    var showOrderSuccess by remember { mutableStateOf(false) }

    // Validation function
    fun validateForm(): Boolean {
        var isValid = true

        // Reset errors
        phoneNumberError = ""
        addressError = ""
        paymentMethodError = ""
        cardNumberError = ""

        // Validate phone number
        if (phoneNumber.isBlank()) {
            phoneNumberError = "Phone number is required"
            isValid = false
        } else if (phoneNumber.length < 10) {
            phoneNumberError = "Phone number must be at least 10 digits"
            isValid = false
        }

        // Validate address
        if (address.isBlank()) {
            addressError = "Delivery address is required"
            isValid = false
        }

        // Validate payment method
        if (selectedPaymentMethod.isBlank()) {
            paymentMethodError = "Please select a payment method"
            isValid = false
        }

        // Validate card number if Visa is selected
        if (selectedPaymentMethod == "visa" && cardNumber.length != 16) {
            cardNumberError = "Please enter a valid 16-digit card number"
            isValid = false
        }

        showValidationErrors = !isValid
        return isValid
    }

    if (showOrderSuccess) {
        LaunchedEffect(Unit) {
            kotlinx.coroutines.delay(2000)
            CartManager.clearAll()
            navController.navigate("order_history") {
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
                    // Validate form before processing
                    if (!validateForm()) {
                        return@Button
                    }

                    val orderId = "MX-" + (1000..9999).random()

                    // 总是使用已登录用户的手机号码作为userPhoneNumber
                    val currentUserPhone = UserSession.getCurrentUser()

                    if (currentUserPhone == null) {
                        // 如果没有用户登录，导航到登录页面
                        navController.navigate("login")
                        return@Button
                    }

                    // 使用付款表单的电话号码作为配送联系方式
                    // 但使用已登录用户的电话号码来关联订单
                    val deliveryPhoneNumber = if (phoneNumber.isNotEmpty()) phoneNumber else currentUserPhone

                    val orderItems = cartItems.map { cartItem ->
                        OrderItem(
                            name = cartItem.item.name,
                            price = cartItem.item.price,
                            quantity = cartItem.quantity,
                            imageResId = cartItem.item.imageResId,
                            ice = cartItem.ice,
                            sugar = cartItem.sugar
                        )
                    }

                    val order = Order(
                        orderId = orderId,
                        userPhoneNumber = currentUserPhone,  // 这将订单链接到已登录的用户
                        items = orderItems,
                        subtotal = subtotal,
                        deliveryFee = deliveryFee,
                        tax = tax,
                        voucher = voucherDiscount,
                        total = total,
                        deliveryAddress = address,
                        phoneNumber = deliveryPhoneNumber,  // 这是配送联系电话
                        comment = comment,
                        paymentMethod = selectedPaymentMethod,
                        cardNumber = if (selectedPaymentMethod == "visa") cardNumber else null
                    )

                    coroutineScope.launch {
                        try {
                            repository.saveOrder(order)

                            //mark voucher as used if one was selected
                            selectedVoucher?.let { voucher ->
                                val userVoucher = userVouchers.find { it.second.id == voucher.id }?.first
                                if (userVoucher != null) {
                                    voucherManager.useVoucher(currentUserPhone, userVoucher.id)
                                }
                            }
                            Log.d("PaymentPage", "Order saved: ${order.orderId} for user: ${order.userPhoneNumber}")
                            showOrderSuccess = true
                        } catch (e: Exception) {
                            Log.e("PaymentPage", "Error saving order", e)
                        }
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
                    onPhoneNumberChange = { phoneNumber = it },
                    errorMessage = if (showValidationErrors) phoneNumberError else "",
                    isError = showValidationErrors && phoneNumberError.isNotEmpty()
                )
            }

            // Address Input
            item {
                AddressSection(
                    address = address,
                    onAddressChange = { address = it },
                    errorMessage = if (showValidationErrors) addressError else "",
                    isError = showValidationErrors && addressError.isNotEmpty()
                )
            }

            // Comment to Rider
            item {
                CommentSection(
                    comment = comment,
                    onCommentChange = { comment = it }
                )
            }

            // Voucher Selection
            item {
                VoucherSelectionSection(
                    userVouchers = userVouchers,
                    selectedVoucher = selectedVoucher,
                    orderTotal = subtotal,
                    onVoucherSelected = { selectedVoucher = it },
                    voucherManager = voucherManager
                )
            }

            // Payment Method Selection
            item {
                PaymentMethodSection(
                    selectedMethod = selectedPaymentMethod,
                    onMethodChange = { selectedPaymentMethod = it },
                    cardNumber = cardNumber,
                    onCardNumberChange = { cardNumber = it },
                    paymentMethodError = if (showValidationErrors) paymentMethodError else "",
                    cardNumberError = if (showValidationErrors) cardNumberError else "",
                    showErrors = showValidationErrors
                )
            }

            // Order Summary
            item {
                OrderSummarySection(
                    cartItems = cartItems,
                    subtotal = subtotal,
                    deliveryFee = deliveryFee,
                    tax = tax,
                    voucher = voucherDiscount,
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
                    .clip(RoundedCornerShape(35.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    "MIXUE Delivery",
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    fontSize = 20.sp
                )
                Text(
                    "Delivery in ~ 15 minutes",
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
    onPhoneNumberChange: (String) -> Unit,
    errorMessage: String = "",
    isError: Boolean = false
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            "📞 Phone number *",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            modifier = Modifier.padding(horizontal = 16.dp),
            color = if (isError) Color.Red else Color.Black
        )
        OutlinedTextField(
            value = phoneNumber,
            onValueChange = onPhoneNumberChange,
            placeholder = { Text("Enter your phone number") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            singleLine = true,
            isError = isError,
            colors = OutlinedTextFieldDefaults.colors(
                errorBorderColor = Color.Red,
                errorLabelColor = Color.Red
            )
        )
        if (errorMessage.isNotEmpty()) {
            Text(
                text = errorMessage,
                color = Color.Red,
                fontSize = 12.sp,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )
        }
        Divider(modifier = Modifier.padding(top = 12.dp))
    }
}

@Composable
fun AddressSection(
    address: String,
    onAddressChange: (String) -> Unit,
    errorMessage: String = "",
    isError: Boolean = false
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            "📍 Delivery Address *",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            modifier = Modifier.padding(horizontal = 16.dp),
            color = if (isError) Color.Red else Color.Black
        )
        OutlinedTextField(
            value = address,
            onValueChange = onAddressChange,
            placeholder = { Text("Enter your delivery address") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            minLines = 2,
            isError = isError,
            colors = OutlinedTextFieldDefaults.colors(
                errorBorderColor = Color.Red,
                errorLabelColor = Color.Red
            )
        )
        if (errorMessage.isNotEmpty()) {
            Text(
                text = errorMessage,
                color = Color.Red,
                fontSize = 12.sp,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )
        }
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
fun VoucherSelectionSection(
    userVouchers: List<Pair<UserVoucherEntity, VoucherEntity>>,
    selectedVoucher: VoucherEntity?,
    orderTotal: Double,
    onVoucherSelected: (VoucherEntity?) -> Unit,
    voucherManager: VoucherManager
) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            "🎫 Apply Voucher",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .clickable { expanded = !expanded },
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    if (selectedVoucher != null) {
                        Text(
                            selectedVoucher.code,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color(0xFF4CAF50)
                        )
                        Text(
                            "Discount: ${if 
                                    (selectedVoucher.discountType == "FIXED") "RM" +
                                    "${selectedVoucher.discountAmount.toInt()}" 
                            else 
                                "${selectedVoucher.discountAmount.toInt()}%"}",
                            color = Color.Red,
                            fontSize = 14.sp
                        )
                        Text(
                            "Saved: RM${"%.2f".format(voucherManager.calculateDiscount(selectedVoucher, orderTotal))}",
                            color = Color.Green,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    } else {
                        Text(
                            "Select a voucher to apply discount",
                            color = Color.Gray,
                            fontSize = 16.sp
                        )
                        Text(
                            "${userVouchers.size} vouchers available",
                            color = Color.Blue,
                            fontSize = 14.sp
                        )
                    }
                }

            }
        }

        if (expanded) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column {
                    // Option to remove selected voucher
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onVoucherSelected(null)
                                expanded = false
                            }
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedVoucher == null,
                            onClick = {
                                onVoucherSelected(null)
                                expanded = false
                            }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("No voucher", fontSize = 16.sp)
                    }

                    if (userVouchers.isNotEmpty()) {
                        Divider()
                    }

                    // Available vouchers
                    userVouchers.forEach { (_, voucher) ->
                        val isEligible = orderTotal >= voucher.minOrderAmount
                        val discount = if (isEligible) voucherManager.calculateDiscount(voucher, orderTotal) else 0.0

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    if (isEligible) {
                                        onVoucherSelected(voucher)
                                        expanded = false
                                    }
                                }
                                .padding(16.dp)
                                .alpha(if (isEligible) 1f else 0.5f),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedVoucher?.id == voucher.id,
                                onClick = {
                                    if (isEligible) {
                                        onVoucherSelected(voucher)
                                        expanded = false
                                    }
                                },
                                enabled = isEligible
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    voucher.code,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = if (isEligible) Color.Black else Color.Gray
                                )
                                Text(
                                    voucher.description,
                                    fontSize = 14.sp,
                                    color = if (isEligible) Color.Gray else Color.LightGray
                                )
                                if (!isEligible) {
                                    Text(
                                        "Min order: RM${"%.2f".format(voucher.minOrderAmount)}",
                                        fontSize = 12.sp,
                                        color = Color.Red
                                    )
                                } else {
                                    Text(
                                        "Valid until: ${SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(voucher.expiryDate))}",
                                        fontSize = 12.sp,
                                        color = Color.Gray
                                    )
                                }
                            }

                            if (isEligible) {
                                Box(
                                    modifier = Modifier
                                        .background(Color.Green.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        "-RM${"%.2f".format(discount)}",
                                        color = Color.Green,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        }

                        if (voucher != userVouchers.last().second) {
                            Divider()
                        }
                    }

                    if (userVouchers.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "No vouchers available",
                                color = Color.Gray,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }

        Divider(modifier = Modifier.padding(top = 12.dp))
    }
}


@Composable
fun PaymentMethodSection(
    selectedMethod: String,
    onMethodChange: (String) -> Unit,
    cardNumber: String,
    onCardNumberChange: (String) -> Unit,
    paymentMethodError: String = "",
    cardNumberError: String = "",
    showErrors: Boolean = false
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            "💳 Payment Method *",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            color = if (showErrors && paymentMethodError.isNotEmpty()) Color.Red else Color.Black
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
                },
                isError = showErrors && cardNumberError.isNotEmpty(),
                colors = OutlinedTextFieldDefaults.colors(
                    errorBorderColor = Color.Red,
                    errorLabelColor = Color.Red
                )
            )
            if (showErrors && cardNumberError.isNotEmpty()) {
                Text(
                    text = cardNumberError,
                    color = Color.Red,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )
            }
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

        if (showErrors && paymentMethodError.isNotEmpty()) {
            Text(
                text = paymentMethodError,
                color = Color.Red,
                fontSize = 12.sp,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )
        }

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