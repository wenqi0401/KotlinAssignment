package com.example.myapplication

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Phone
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

@Composable
fun Register(
    navController: NavHostController,
    viewModel: AuthViewModel = viewModel(),
) {
    var confirmPassword by remember { mutableStateOf("") }
    var showErrorDialog by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var registrationAttempted by remember { mutableStateOf(false) }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    val uiState = viewModel.uiState.collectAsState()
    val keyboardController = LocalSoftwareKeyboardController.current
    val phoneError = viewModel.validatePhoneNumber(uiState.value.phoneNumber)
    val passwordError = viewModel.validatePassword(uiState.value.password)

    val confirmPasswordError = viewModel.validateConfirmPassword(uiState.value.password, confirmPassword)

    // Handle registration result - ONLY show dialogs, no navigation here
    LaunchedEffect(uiState.value.errorMessage, uiState.value.isLoading, registrationAttempted) {
        if (registrationAttempted) {
            if (!uiState.value.isLoading) {
                // Registration completed - check result
                if (uiState.value.errorMessage != null) {
                    showErrorDialog = true
                } else {
                    showSuccessDialog = true
                }
                registrationAttempted = false // Reset flag only after handling result
            }
            // If still loading, do nothing - wait for next trigger
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // 渐变背景 - 和登录页面一致
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFDC143C),  // 深红色
                            Color(0xFFFF1744),  // 鲜红色
                            Color(0xFFE53935)   // 偏橙红色
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
            Image(
                painter = painterResource(id = R.drawable.logomixue),
                contentDescription = "Logo",
                modifier = Modifier.size(240.dp),
                contentScale = ContentScale.Fit
            )


            // 标题文本
            Text(
                text = "Create Account",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )

            Text(
                text = "Join us today",
                fontSize = 16.sp,
                color = Color.White.copy(alpha = 0.8f),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            // 注册卡片
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
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // 手机号输入框
                    OutlinedTextField(
                        value = uiState.value.phoneNumber,
                        onValueChange = { input ->
                            val filtered = input.filter { it != ' ' && it != '\n' }
                            viewModel.setPhoneNumber(filtered)
                        },
                        label = { Text("Phone Number") },
                        placeholder = { Text("e.g., 0123456789") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Phone,
                                contentDescription = null,
                                tint = Color(0xFFE53E3E)
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !uiState.value.isLoading,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Phone,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = { keyboardController?.hide() }
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFE53E3E),
                            focusedLabelColor = Color(0xFFE53E3E),
                            cursorColor = Color(0xFFE53E3E),
                            focusedLeadingIconColor = Color(0xFFE53E3E)
                        ),
                        shape = RoundedCornerShape(16.dp),
                        isError = phoneError!= null,
                        supportingText = {
                            Text(
                                text = phoneError ?: "10-15 digits, numbers only",
                                color = if (phoneError != null) Color(0xFFE53E3E) else Color.Gray,
                                fontSize = 12.sp
                            )
                        }
                    )

                    // 密码输入框
                    OutlinedTextField(
                        value = uiState.value.password,
                        onValueChange = { input ->
                            val filtered = input.filter { it != ' ' && it != '\n' }
                            viewModel.setPassword(filtered)
                        },
                        label = { Text("Create Password") },
                        placeholder = { Text("Min 8 chars, letters & numbers") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                tint = Color(0xFFE53E3E)
                            )
                        },

                        trailingIcon = {
                            val icon = if (passwordVisible) painterResource(R.drawable.baseline_visibility_off_24) else painterResource(R.drawable.outline_visibility_24)
                            val description = if (passwordVisible) "Hide password" else "Show password"
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(painter = icon, contentDescription = description)
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !uiState.value.isLoading,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = { keyboardController?.hide() }
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFE53E3E),
                            focusedLabelColor = Color(0xFFE53E3E),
                            cursorColor = Color(0xFFE53E3E),
                            focusedLeadingIconColor = Color(0xFFE53E3E)
                        ),
                        shape = RoundedCornerShape(16.dp),
                        isError = passwordError != null,
                        supportingText = {
                            Text(
                                text = passwordError ?: "At least 8 characters with letters and numbers",
                                color = if (passwordError != null) Color(0xFFE53E3E) else Color.Gray,
                                fontSize = 12.sp
                            )
                        }
                    )

                    // Confirm password field
                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = { input ->
                            confirmPassword = input.filter { it != ' ' && it != '\n' }
                        },
                        label = { Text("Confirm Password") },
                        placeholder = { Text("Re-enter your password") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                tint = Color(0xFFE53E3E)
                            )
                        },
                        trailingIcon = {
                            val icon = if (confirmPasswordVisible) painterResource(R.drawable.baseline_visibility_off_24) else painterResource(R.drawable.outline_visibility_24)
                            val description = if (confirmPasswordVisible) "Hide password" else "Show password"
                            IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                                Icon(painter = icon, contentDescription = description)
                            }
                        },
                        visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !uiState.value.isLoading,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = { keyboardController?.hide() }
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFE53E3E),
                            focusedLabelColor = Color(0xFFE53E3E),
                            cursorColor = Color(0xFFE53E3E),
                            focusedLeadingIconColor = Color(0xFFE53E3E)
                        ),

                        isError = confirmPasswordError != null,
                        supportingText = {
                            Text(
                                text = confirmPasswordError ?: "Re-enter your password to confirm",
                                color = if (confirmPasswordError != null) Color(0xFFE53E3E) else Color.Gray,
                                fontSize = 12.sp
                            )
                        },
                        shape = RoundedCornerShape(16.dp)
                    )

                    // 注册按钮
                    Button(
                        onClick = {
                            val confirmPasswordError = viewModel.validateConfirmPassword(confirmPassword)

                            if(confirmPasswordError != null) {
                                showErrorDialog = true
                                showSuccessDialog = false
                            } else {
                                viewModel.registerUser()
                                registrationAttempted = true
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        enabled = !uiState.value.isLoading && viewModel.isRegisterFormValid(confirmPassword),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFE53E3E),
                            disabledContainerColor = Color.Gray.copy(alpha = 0.3f)
                        ),
                        shape = RoundedCornerShape(16.dp),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                    ) {
                        if (uiState.value.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                "Create Account",
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }


            // 底部返回登录按钮
            TextButton(
                onClick = { navController.navigateUp() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    "Already have an account? Sign In",
                    fontSize = 16.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Medium
                )
            }

            // 错误对话框
            if (showErrorDialog) {
                RegisterErrorDialog(
                    message = uiState.value.errorMessage ?: viewModel.validateConfirmPassword(confirmPassword) ?: "Registration failed",
                    onDismiss = {
                        showErrorDialog = false
                        viewModel.clearErrorMessage()
                    }
                )
            }

            // 成功对话框
            if (showSuccessDialog) {
                RegisterSuccessDialog(
                    onDismiss = {
                        showSuccessDialog = false
                        viewModel.clearErrorMessage()
                        viewModel.clearCredentials()
                        // Navigation happens HERE when user clicks OK
                        navController.navigate("login") {
                            popUpTo("register") { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterPreview() {
    val navController = rememberNavController()
    Register(navController = navController)
}

@Composable
fun RegisterErrorDialog(message: String, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "Registration Error",
                fontWeight = FontWeight.Bold,
                color = Color(0xFFE53E3E)
            )
        },
        text = {
            Text(message, fontSize = 14.sp)
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFE53E3E)
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("OK", color = Color.White)
            }
        },
        shape = RoundedCornerShape(16.dp)
    )
}

@Composable
fun RegisterSuccessDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "Registration Successful!",
                color = Color(0xFF48BB78),
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Text(
                "Account created successfully! You can now login with your phone number.",
                fontSize = 14.sp
            )
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF48BB78)
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Go to Login", color = Color.White)
            }
        },
        shape = RoundedCornerShape(16.dp)
    )
}