package com.example.myapplication

import android.app.Activity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController

@Composable
fun ForgetPasswordScreen(
    navController: NavHostController,
    viewModel: ForgetPasswordViewModel = viewModel()
) {

    val context = LocalContext.current
    val activity = context as? Activity

    // Set activity reference in ViewModel
    LaunchedEffect(activity) {
        activity?.let { viewModel.setActivity(it) }
    }
    val uiState = viewModel.uiState.collectAsState()
    var showErrorDialog by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }

    // Handle UI state changes
    LaunchedEffect(uiState.value.errorMessage, uiState.value.passwordResetSuccess) {
        if (uiState.value.errorMessage != null) {
            showErrorDialog = true
        }
        if (uiState.value.passwordResetSuccess) {
            showSuccessDialog = true
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Modern gradient background matching LoginScreen
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFDC143C),
                            Color(0xFFFF1744),
                            Color(0xFFE53935)
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo
            Image(
                painter = painterResource(id = R.drawable.logomixue),
                contentDescription = "Logo",
                modifier = Modifier.size(200.dp),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Title
            Text(
                text = "Reset Password",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )

            Text(
                text = getSubtitleForStep(uiState.value.currentStep),
                fontSize = 16.sp,
                color = Color.White.copy(alpha = 0.8f),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Main content card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White.copy(alpha = 0.95f)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(
                    modifier = Modifier.padding(32.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    when (uiState.value.currentStep) {
                        ForgetPasswordStep.PHONE_INPUT -> {
                            PhoneInputStep(viewModel, uiState.value)
                        }
                        ForgetPasswordStep.OTP_VERIFICATION -> {
                            OTPVerificationStep(viewModel, uiState.value)
                        }
                        ForgetPasswordStep.PASSWORD_RESET -> {
                            PasswordResetStep(viewModel, uiState.value)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Back to Login Button
            TextButton(
                onClick = { navController.navigateUp() }
            ) {
                Text(
                    "Back to Login",
                    fontSize = 16.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // Error Dialog
        if (showErrorDialog) {
            AlertDialog(
                onDismissRequest = {
                    showErrorDialog = false
                    viewModel.clearError()
                },
                title = {
                    Text(
                        "Error",
                        color = Color(0xFFE53E3E),
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Text(uiState.value.errorMessage ?: "An error occurred")
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showErrorDialog = false
                            viewModel.clearError()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53E3E)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("OK", color = Color.White)
                    }
                },
                shape = RoundedCornerShape(16.dp)
            )
        }

        if (showSuccessDialog) {
            SuccessDialog(
                showDialog = true,
                onDismiss = {
                    showSuccessDialog = false
                    viewModel.clearSuccessState()
                },
                onNavigateToLogin = {
                    showSuccessDialog = false
                    viewModel.clearSuccessState()
                    navController.navigate("login") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }
    }
}

@Composable
fun PhoneInputStep(viewModel: ForgetPasswordViewModel, uiState: ForgetPasswordUiState) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Enter your phone number",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF2D3748),
            textAlign = TextAlign.Center
        )

        Text(
            text = "We'll send you a verification code",
            fontSize = 14.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )

        OutlinedTextField(
            value = uiState.phoneNumber,
            onValueChange = { viewModel.setPhoneNumber(it) },
            label = { Text("Phone Number") },
            placeholder = { Text("e.g., 0123456789") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = Color(0xFFE53E3E)
                )
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !uiState.isLoading,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFFE53E3E),
                focusedLabelColor = Color(0xFFE53E3E),
                cursorColor = Color(0xFFE53E3E),
                focusedLeadingIconColor = Color(0xFFE53E3E)
            ),
            shape = RoundedCornerShape(16.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = { viewModel.sendOTP() },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            enabled = !uiState.isLoading && uiState.phoneNumber.length >= 10,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFE53E3E),
                disabledContainerColor = Color.Gray.copy(alpha = 0.3f)
            ),
            shape = RoundedCornerShape(16.dp),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                )
            } else {
                Text(
                    "Send Verification Code",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
fun OTPVerificationStep(viewModel: ForgetPasswordViewModel, uiState: ForgetPasswordUiState) {
    var countdown by rememberSaveable { mutableStateOf(60) }

    // One-second ticker tied to countdown
    LaunchedEffect(countdown) {
        if (countdown > 0) {
            kotlinx.coroutines.delay(1000)
            countdown--
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Verification Code",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF2D3748),
            textAlign = TextAlign.Center
        )

        Text(
            text = "Enter the 6-digit code sent to\n${uiState.phoneNumber}",
            fontSize = 14.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            repeat(6) { index ->
                OutlinedTextField(
                    value = if (index < uiState.otpCode.length) uiState.otpCode[index].toString() else "",
                    onValueChange = { value ->
                        if (value.length <= 1 && value.all { it.isDigit() }) {
                            viewModel.updateOTPCode(index, value)
                        }
                    },
                    modifier = Modifier.size(40.dp),
                    textStyle = androidx.compose.ui.text.TextStyle(
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        color = Color(0xFF2D3748)
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFE53E3E),
                        unfocusedBorderColor = Color.Gray.copy(alpha = 0.5f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (countdown > 0) {
            Text(
                text = "Resend code in ${countdown}s",
                color = Color.Gray,
                fontSize = 14.sp
            )
        } else {
            TextButton(
                onClick = {
                    viewModel.sendOTP()
                    countdown = 60 // restart the timer
                }
            ) {
                Text(
                    "Resend Code",
                    color = Color(0xFFE53E3E),
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Button(
            onClick = { viewModel.verifyOTP() },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            enabled = !uiState.isLoading && uiState.otpCode.length == 6,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFE53E3E),
                disabledContainerColor = Color.Gray.copy(alpha = 0.3f)
            ),
            shape = RoundedCornerShape(16.dp),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                )
            } else {
                Text(
                    "Verify Code",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
fun PasswordResetStep(viewModel: ForgetPasswordViewModel, uiState: ForgetPasswordUiState) {
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Create New Password",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF2D3748),
            textAlign = TextAlign.Center
        )

        Text(
            text = "Please reset your password ",
            fontSize = 14.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )

        OutlinedTextField(
            value = uiState.newPassword,
            onValueChange = { viewModel.setNewPassword(it) },
            label = { Text("New Password") },
            placeholder = { Text("Min 8 characters") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = Color(0xFFE53E3E)
                )
            },
            trailingIcon = {
                val image = if (passwordVisible) Icons.Default.Lock else Icons.Default.Done
                val description = if (passwordVisible) "Hide password" else "Show password"
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(imageVector = image, contentDescription = description)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !uiState.isLoading,
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFFE53E3E),
                focusedLabelColor = Color(0xFFE53E3E),
                cursorColor = Color(0xFFE53E3E),
                focusedLeadingIconColor = Color(0xFFE53E3E)
            ),
            shape = RoundedCornerShape(16.dp)
        )

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Confirm Password") },
            placeholder = { Text("Re-enter new password") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = Color(0xFFE53E3E)
                )
            },
            trailingIcon = {
                val image = if (confirmPasswordVisible) Icons.Default.Lock else Icons.Default.Done
                val description = if (confirmPasswordVisible) "Hide password" else "Show password"
                IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                    Icon(imageVector = image, contentDescription = description)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !uiState.isLoading,
            visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFFE53E3E),
                focusedLabelColor = Color(0xFFE53E3E),
                cursorColor = Color(0xFFE53E3E),
                focusedLeadingIconColor = Color(0xFFE53E3E)
            ),
            shape = RoundedCornerShape(16.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                viewModel.resetPassword()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            enabled = !uiState.isLoading &&
                    uiState.newPassword.length >= 8 &&
                    uiState.newPassword == confirmPassword,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFE53E3E),
                disabledContainerColor = Color.Gray.copy(alpha = 0.3f)
            ),
            shape = RoundedCornerShape(16.dp),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                )
            } else {
                Text(
                    "Reset Password",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
fun SuccessDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = {
                Text(
                    text = "Password Reset Successful!",
                    color = Color(0xFF48BB78),
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            text = {
                Text(
                    text = "Your password has been updated successfully. You can now login with your new password.",
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDismiss()
                        onNavigateToLogin()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF48BB78)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "Go to Login",
                        color = Color.White,
                        fontWeight = FontWeight.Medium
                    )
                }
            },
            shape = RoundedCornerShape(16.dp)
        )
    }
}

private fun getSubtitleForStep(step: ForgetPasswordStep): String {
    return when (step) {
        ForgetPasswordStep.PHONE_INPUT -> "We'll help you reset your password"
        ForgetPasswordStep.OTP_VERIFICATION -> "Check your phone for the verification code"
        ForgetPasswordStep.PASSWORD_RESET -> "Almost done! Create a secure password"
    }
}