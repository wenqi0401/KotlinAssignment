package com.example.myapplication

import android.app.Activity
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import kotlinx.coroutines.delay


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


    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.Red
    ) { paddingValues ->
        Column(
            modifier = Modifier.padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.logomixue),
                contentDescription = "Logo",
                modifier = Modifier.size(300.dp),
                contentScale = ContentScale.Crop
            )

            Text(
                text = "Forget Password",
                fontStyle = FontStyle.Italic,
                fontFamily = FontFamily.Serif,
                fontSize = 35.sp,
                color = Color.White
            )

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
                            color = Color.Red,
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
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                        ) {
                            Text("OK", color = Color.White)
                        }
                    }
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

            // Back to Login Button
            TextButton(
                onClick = { navController.navigateUp() }
            ) {
                Text("Back to Login", fontSize = 16.sp, color = Color.Gray)
            }
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
            text = "Enter your phone number to receive OTP",
            color = Color.White,
            fontSize = 16.sp,
            textAlign = TextAlign.Center
        )

        TextField(
            value = uiState.phoneNumber,
            onValueChange = { viewModel.setPhoneNumber(it) },
            label = { Text("Phone Number") },
            placeholder = { Text("e.g., 0123456789") },
            modifier = Modifier.fillMaxWidth(),
            enabled = !uiState.isLoading,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Red,
                unfocusedIndicatorColor = Color.Gray,
                focusedLabelColor = Color.Red,
                cursorColor = Color.Red
            )
        )

        Button(
            onClick = { viewModel.sendOTP() },
            modifier = Modifier.fillMaxWidth(),
            enabled = !uiState.isLoading && uiState.phoneNumber.length >= 10,
            colors = ButtonDefaults.buttonColors(
                containerColor = if (!uiState.isLoading && uiState.phoneNumber.length >= 10)
                    Color.White else Color.Gray
            )
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    color = Color.Red
                )
            } else {
                Text("Send OTP", color = Color.Red, fontSize = 18.sp)
            }
        }
    }
}

@Composable
fun OTPVerificationStep(viewModel: ForgetPasswordViewModel, uiState: ForgetPasswordUiState) {
    var countdown by remember { mutableStateOf(60) }

    LaunchedEffect(Unit) {
        while (countdown > 0) {
            delay(1000)
            countdown--
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Enter the 6-digit OTP sent to\n${uiState.phoneNumber}",
            color = Color.White,
            fontSize = 16.sp,
            textAlign = TextAlign.Center
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            repeat(6) { index ->
                OutlinedTextField(
                    value = if (index < uiState.otpCode.length) uiState.otpCode[index].toString() else "",
                    onValueChange = { value ->
                        if (value.length <= 1 && value.all { it.isDigit() }) {
                            viewModel.updateOTPCode(index, value)
                        }
                    },
                    modifier = Modifier.size(50.dp),
                    textStyle = androidx.compose.ui.text.TextStyle(
                        fontSize = 18.sp,
                        textAlign = TextAlign.Center
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.White,
                        unfocusedIndicatorColor = Color.Gray
                    )
                )
            }
        }

        if (countdown > 0) {
            Text(
                text = "Resend OTP in ${countdown}s",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 14.sp
            )
        } else {
            TextButton(
                onClick = {
                    viewModel.sendOTP()
                    countdown = 60
                }
            ) {
                Text("Resend OTP", color = Color.White)
            }
        }

        Button(
            onClick = { viewModel.verifyOTP() },
            modifier = Modifier.fillMaxWidth(),
            enabled = !uiState.isLoading && uiState.otpCode.length == 6,
            colors = ButtonDefaults.buttonColors(
                containerColor = if (!uiState.isLoading && uiState.otpCode.length == 6)
                    Color.White else Color.Gray
            )
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    color = Color.Red
                )
            } else {
                Text("Verify OTP", color = Color.Red, fontSize = 18.sp)
            }
        }
    }
}

@Composable
fun PasswordResetStep(viewModel: ForgetPasswordViewModel, uiState: ForgetPasswordUiState) {
    var confirmPassword by remember { mutableStateOf("") }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Create a new password",
            color = Color.White,
            fontSize = 16.sp,
            textAlign = TextAlign.Center
        )

        TextField(
            value = uiState.newPassword,
            onValueChange = { viewModel.setNewPassword(it) },
            label = { Text("New Password") },
            placeholder = { Text("Min 8 chars, letters & numbers") },
            modifier = Modifier.fillMaxWidth(),
            enabled = !uiState.isLoading,
            visualTransformation = PasswordVisualTransformation(),
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Red,
                unfocusedIndicatorColor = Color.Gray,
                focusedLabelColor = Color.Red,
                cursorColor = Color.Red
            )
        )

        TextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Confirm New Password") },
            placeholder = { Text("Re-enter your new password") },
            modifier = Modifier.fillMaxWidth(),
            enabled = !uiState.isLoading,
            visualTransformation = PasswordVisualTransformation(),
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Red,
                unfocusedIndicatorColor = Color.Gray,
                focusedLabelColor = Color.Red,
                cursorColor = Color.Red
            )
        )

        Button(
            onClick = {
                viewModel.resetPassword()
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !uiState.isLoading &&
                    uiState.newPassword.length >= 8 &&
                    uiState.newPassword == confirmPassword,
            colors = ButtonDefaults.buttonColors(
                containerColor = if (!uiState.isLoading &&
                    uiState.newPassword.length >= 8 &&
                    uiState.newPassword == confirmPassword)
                    Color.White else Color.Gray
            )
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    color = Color.Red
                )
            } else {
                Text("Reset Password", color = Color.Red, fontSize = 18.sp)
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
                    color = Color.Green,
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
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Green)
                ) {
                    Text(
                        text = "Go to Login",
                        color = Color.White,
                        fontWeight = FontWeight.Medium
                    )
                }
            },
        )
    }
}
