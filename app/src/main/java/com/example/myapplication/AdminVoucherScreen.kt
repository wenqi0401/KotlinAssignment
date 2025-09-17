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
            voucherManager = voucherManager,
            userRepository = userRepository,
            onDismiss = { showSendDialog = false },
            onSendToPhone = { phone, v ->
                coroutineScope.launch {
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

    // Error states
    var codeError by remember { mutableStateOf("") }
    var descriptionError by remember { mutableStateOf("") }
    var discountAmountError by remember { mutableStateOf("") }
    var minOrderAmountError by remember { mutableStateOf("") }
    var maxUsageError by remember { mutableStateOf("") }
    var expiryDateError by remember { mutableStateOf("") }
    var generalError by remember { mutableStateOf("") }

    // Loading state
    var isCreating by remember { mutableStateOf(false) }

    // Validation functions
    fun validateCode(): Boolean {
        codeError = when {
            code.isBlank() -> "Voucher code is required"
            code.length < 3 -> "Voucher code must be at least 3 characters"
            code.length > 15 -> "Voucher code must be less than 15 characters"
            !code.matches(Regex("^[A-Z0-9]+$")) -> "Voucher code can only contain uppercase letters and numbers"
            else -> ""
        }
        return codeError.isEmpty()
    }

    fun validateDescription(): Boolean {
        descriptionError = when {
            description.isBlank() -> "Description is required"
            description.length < 5 -> "Description must be at least 5 characters"
            description.length > 100 -> "Description must be less than 100 characters"
            else -> ""
        }
        return descriptionError.isEmpty()
    }

    fun validateDiscountAmount(): Boolean {
        val amount = discountAmount.toDoubleOrNull()
        discountAmountError = when {
            discountAmount.isBlank() -> "Discount amount is required"
            amount == null -> "Please enter a valid number"
            amount <= 0 -> "Discount amount must be greater than 0"
            discountType == "PERCENTAGE" && amount > 100 -> "Percentage discount cannot exceed 100%"
            discountType == "FIXED" && amount > 1000 -> "Fixed discount cannot exceed RM1000"
            else -> ""
        }
        return discountAmountError.isEmpty()
    }

    fun validateMinOrderAmount(): Boolean {
        val amount = minOrderAmount.toDoubleOrNull()
        minOrderAmountError = when {
            minOrderAmount.isBlank() -> "Minimum order amount is required"
            amount == null -> "Please enter a valid number"
            amount < 0 -> "Minimum order amount cannot be negative"
            amount > 10000 -> "Minimum order amount cannot exceed RM10,000"
            else -> ""
        }
        return minOrderAmountError.isEmpty()
    }

    fun validateMaxUsage(): Boolean {
        val usage = maxUsage.toIntOrNull()
        maxUsageError = when {
            maxUsage.isBlank() -> "Max usage is required"
            usage == null -> "Please enter a valid number"
            usage <= 0 -> "Max usage must be greater than 0"
            usage > 10000 -> "Max usage cannot exceed 10,000"
            else -> ""
        }
        return maxUsageError.isEmpty()
    }

    fun validateExpiryDate(): Boolean {
        expiryDateError = try {
            if (expiryDate.isBlank()) {
                "Expiry date is required"
            } else {
                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                dateFormat.isLenient = false
                val parsedDate = dateFormat.parse(expiryDate)
                when {
                    parsedDate == null -> "Invalid date format. Use dd/MM/yyyy"
                    parsedDate.time <= System.currentTimeMillis() -> "Expiry date must be in the future"
                    else -> ""
                }
            }
        } catch (e: Exception) {
            "Invalid date format. Use dd/MM/yyyy (e.g., 31/12/2024)"
        }
        return expiryDateError.isEmpty()
    }

    fun validateAllFields(): Boolean {
        return validateCode() &&
                validateDescription() &&
                validateDiscountAmount() &&
                validateMinOrderAmount() &&
                validateMaxUsage() &&
                validateExpiryDate()
    }

    fun createVoucher() {
        if (!validateAllFields()) {
            return
        }

        isCreating = true
        generalError = ""

        try {
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val expiryMillis = dateFormat.parse(expiryDate)?.time ?: System.currentTimeMillis()

            val voucher = VoucherEntity(
                id = UUID.randomUUID().toString(),
                code = code.trim().uppercase(),
                description = description.trim(),
                discountAmount = discountAmount.toDouble(),
                discountType = discountType,
                minOrderAmount = minOrderAmount.toDouble(),
                maxUsage = maxUsage.toInt(),
                currentUsage = 0,
                expiryDate = expiryMillis,
                isActive = true
            )

            onConfirm(voucher)
        } catch (e: Exception) {
            generalError = "Failed to create voucher: ${e.message}"
            isCreating = false
        }
    }

    AlertDialog(
        onDismissRequest = { if (!isCreating) onDismiss() },
        title = { Text("Create New Voucher") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // General error message
                if (generalError.isNotEmpty()) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = generalError,
                            color = Color.Red,
                            modifier = Modifier.padding(12.dp),
                            fontSize = 14.sp
                        )
                    }
                }

                // Voucher Code
                OutlinedTextField(
                    value = code,
                    onValueChange = {
                        code = it.uppercase()
                        if (codeError.isNotEmpty()) validateCode()
                    },
                    label = { Text("Voucher Code") },
                    isError = codeError.isNotEmpty(),
                    supportingText = if (codeError.isNotEmpty()) {
                        { Text(codeError, color = Color.Red) }
                    } else null,
                    enabled = !isCreating,
                    modifier = Modifier.fillMaxWidth()
                )

                // Description
                OutlinedTextField(
                    value = description,
                    onValueChange = {
                        description = it
                        if (descriptionError.isNotEmpty()) validateDescription()
                    },
                    label = { Text("Description") },
                    isError = descriptionError.isNotEmpty(),
                    supportingText = if (descriptionError.isNotEmpty()) {
                        { Text(descriptionError, color = Color.Red) }
                    } else null,
                    enabled = !isCreating,
                    modifier = Modifier.fillMaxWidth()
                )

                // Discount Amount
                OutlinedTextField(
                    value = discountAmount,
                    onValueChange = {
                        discountAmount = it
                        if (discountAmountError.isNotEmpty()) validateDiscountAmount()
                    },
                    label = { Text("Discount Amount") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = discountAmountError.isNotEmpty(),
                    supportingText = if (discountAmountError.isNotEmpty()) {
                        { Text(discountAmountError, color = Color.Red) }
                    } else null,
                    enabled = !isCreating,
                    modifier = Modifier.fillMaxWidth()
                )

                // Discount Type
                Column {
                    Text("Discount Type", fontSize = 16.sp, fontWeight = FontWeight.Medium)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        RadioButton(
                            selected = discountType == "FIXED",
                            onClick = { if (!isCreating) discountType = "FIXED" }
                        )
                        Text("Fixed Amount (RM)")
                        Spacer(modifier = Modifier.width(16.dp))
                        RadioButton(
                            selected = discountType == "PERCENTAGE",
                            onClick = { if (!isCreating) discountType = "PERCENTAGE" }
                        )
                        Text("Percentage (%)")
                    }
                }

                // Minimum Order Amount
                OutlinedTextField(
                    value = minOrderAmount,
                    onValueChange = {
                        minOrderAmount = it
                        if (minOrderAmountError.isNotEmpty()) validateMinOrderAmount()
                    },
                    label = { Text("Minimum Order Amount (RM)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = minOrderAmountError.isNotEmpty(),
                    supportingText = if (minOrderAmountError.isNotEmpty()) {
                        { Text(minOrderAmountError, color = Color.Red) }
                    } else null,
                    enabled = !isCreating,
                    modifier = Modifier.fillMaxWidth()
                )

                // Max Usage
                OutlinedTextField(
                    value = maxUsage,
                    onValueChange = {
                        maxUsage = it
                        if (maxUsageError.isNotEmpty()) validateMaxUsage()
                    },
                    label = { Text("Max Usage") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = maxUsageError.isNotEmpty(),
                    supportingText = if (maxUsageError.isNotEmpty()) {
                        { Text(maxUsageError, color = Color.Red) }
                    } else null,
                    enabled = !isCreating,
                    modifier = Modifier.fillMaxWidth()
                )

                // Expiry Date
                OutlinedTextField(
                    value = expiryDate,
                    onValueChange = {
                        expiryDate = it
                        if (expiryDateError.isNotEmpty()) validateExpiryDate()
                    },
                    label = { Text("Expiry Date (dd/MM/yyyy)") },
                    placeholder = { Text("31/12/2024") },
                    isError = expiryDateError.isNotEmpty(),
                    supportingText = if (expiryDateError.isNotEmpty()) {
                        { Text(expiryDateError, color = Color.Red) }
                    } else null,
                    enabled = !isCreating,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { createVoucher() },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                enabled = !isCreating
            ) {
                if (isCreating) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(
                    if (isCreating) "Creating..." else "Create",
                    color = Color.White
                )
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                enabled = !isCreating
            ) {
                Text("Cancel")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SendVoucherDialog(
    voucher: VoucherEntity,
    voucherManager: VoucherManager,
    userRepository: UserRepository,
    onDismiss: () -> Unit,
    onSendToPhone: (String, VoucherEntity) -> Unit
) {
    var phoneNumber by remember { mutableStateOf("") }
    var phoneError by remember { mutableStateOf("") }
    var generalError by remember { mutableStateOf("") }
    var generalMessage by remember { mutableStateOf("") }
    var isSending by remember { mutableStateOf(false) }
    var showConfirmDialog by remember { mutableStateOf(false) }
    var userAlreadyHasVoucher by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

    // Validation functions
    fun validatePhoneNumber(): Boolean {
        val cleanPhone = phoneNumber.replace(Regex("[^0-9]"), "")
        phoneError = when {
            phoneNumber.isBlank() -> "Phone number is required"
            cleanPhone.length < 10 -> "Phone number must be at least 10 digits"
            cleanPhone.length > 15 -> "Phone number cannot exceed 15 digits"
            !cleanPhone.matches(Regex("^[0-9]+$")) -> "Phone number can only contain numbers"
            else -> ""
        }
        return phoneError.isEmpty()
    }

    fun checkUserVoucherStatus() {
        if (!validatePhoneNumber()) return

        isSending = true
        generalError = ""
        generalMessage = ""

        coroutineScope.launch {
            try {
                val cleanPhone = phoneNumber.replace(Regex("[^0-9]"), "")

                // Check if user exists
                val userExists = userRepository.checkUserExists(cleanPhone)
                if (!userExists) {
                    generalError = "No user found with phone number: $cleanPhone"
                    isSending = false
                    return@launch
                }

                // Check if user already has this voucher
                val hasVoucher = voucherManager.checkUserHasVoucher(cleanPhone, voucher.code)
                if (hasVoucher) {
                    userAlreadyHasVoucher = true
                    showConfirmDialog = true
                } else {
                    // User doesn't have voucher, proceed with sending
                    userAlreadyHasVoucher = false
                    showConfirmDialog = true
                }

                isSending = false
            } catch (e: Exception) {
                generalError = "Error checking user status: ${e.message}"
                isSending = false
            }
        }
    }

    fun sendVoucher(force: Boolean = false) {
        val cleanPhone = phoneNumber.replace(Regex("[^0-9]"), "")

        isSending = true
        generalError = ""

        coroutineScope.launch {
            try {
                if (userAlreadyHasVoucher && !force) {
                    // This shouldn't happen, but just in case
                    showConfirmDialog = true
                    isSending = false
                    return@launch
                }

                // Send voucher to user (with force option for duplicates)
                voucherManager.giveVoucherToUserForce(cleanPhone, voucher, force)
                generalMessage = "Voucher sent successfully to $cleanPhone"

                // Wait a moment to show success message
                kotlinx.coroutines.delay(1500)
                onSendToPhone(cleanPhone, voucher)

            } catch (e: Exception) {
                generalError = "Failed to send voucher: ${e.message}"
                isSending = false
            }
        }
    }

    AlertDialog(
        onDismissRequest = { if (!isSending) onDismiss() },
        title = { Text("Send Voucher") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

                // Voucher Info Card
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F8E9)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            "Voucher Code: ${voucher.code}",
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2E7D32)
                        )
                        Text(
                            "Description: ${voucher.description}",
                            color = Color(0xFF388E3C)
                        )
                        Text(
                            "Discount: ${if (voucher.discountType == "FIXED") "RM${voucher.discountAmount.toInt()}" else "${voucher.discountAmount.toInt()}%"}",
                            color = Color(0xFF388E3C),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // General error message
                if (generalError.isNotEmpty()) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = generalError,
                            color = Color.Red,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }

                // General success message
                if (generalMessage.isNotEmpty()) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E8)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = generalMessage,
                            color = Color(0xFF2E7D32),
                            fontSize = 14.sp,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }

                // Phone number input
                OutlinedTextField(
                    value = phoneNumber,
                    onValueChange = {
                        phoneNumber = it
                        if (phoneError.isNotEmpty()) validatePhoneNumber()
                        // Clear other messages when user types
                        if (generalError.isNotEmpty()) generalError = ""
                        if (generalMessage.isNotEmpty()) generalMessage = ""
                    },
                    label = { Text("Enter User Phone Number") },
                    placeholder = { Text("0123456789") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    isError = phoneError.isNotEmpty(),
                    supportingText = if (phoneError.isNotEmpty()) {
                        { Text(phoneError, color = Color.Red) }
                    } else null,
                    enabled = !isSending,
                    modifier = Modifier.fillMaxWidth()
                )

                // Helper text
                if (phoneError.isEmpty() && generalError.isEmpty()) {
                    Text(
                        "Enter the phone number of the user you want to send this voucher to.",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { checkUserVoucherStatus() },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                enabled = phoneNumber.isNotBlank() && !isSending && generalMessage.isEmpty()
            ) {
                if (isSending) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(
                    if (isSending) "Checking..." else "Send Voucher",
                    color = Color.White
                )
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                enabled = !isSending
            ) {
                Text("Cancel")
            }
        }
    )

    // Confirmation dialog for users who already have the voucher
    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = {
                showConfirmDialog = false
                isSending = false
            },
            title = {
                Text(
                    if (userAlreadyHasVoucher) "User Already Has Voucher" else "Confirm Send Voucher"
                )
            },
            text = {
                Column {
                    if (userAlreadyHasVoucher) {
                        Text(
                            "DUPLICATE VOUCHER DETECTED",
                            fontWeight = FontWeight.Bold,
                            color = Color.Red,
                            textAlign = TextAlign.Center,
                            fontSize = 16.sp,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "The user with phone number ${phoneNumber.replace(Regex("[^0-9]"), "")} already has this voucher.",
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Voucher Code: ${voucher.code}",
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "Do you want to send it again? This will give them a duplicate voucher.",
                            textAlign = TextAlign.Center,
                            color = Color.Gray
                        )
                    } else {
                        Text(
                            "Send voucher '${voucher.code}' to user ${phoneNumber.replace(Regex("[^0-9]"), "")}?",
                            textAlign = TextAlign.Center
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showConfirmDialog = false
                        sendVoucher(force = userAlreadyHasVoucher)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (userAlreadyHasVoucher) Color.Red else Color(0xFF4CAF50)
                    )
                ) {
                    Text(
                        if (userAlreadyHasVoucher) "Send Anyway" else "Confirm Send",
                        color = Color.White
                    )
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = {
                        showConfirmDialog = false
                        isSending = false
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}