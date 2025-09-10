package com.example.myapplication

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

@Composable
fun Register(
    navController: NavHostController,
    viewModel: AuthViewModel = viewModel()
) {
    var confirmPassword by remember { mutableStateOf("") }
    var showErrorDialog by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var registrationAttempted by remember { mutableStateOf(false) }

    val uiState = viewModel.uiState.collectAsState()

    // Handle registration result - ONLY show dialogs, no navigation here
    LaunchedEffect(uiState.value.errorMessage, uiState.value.isLoading, registrationAttempted) {
        if (registrationAttempted && !uiState.value.isLoading) {
            if (uiState.value.errorMessage != null) {
                // Show error dialog if there's an error
                showErrorDialog = true
            } else {
                // Show success dialog
                showSuccessDialog = true
            }
            registrationAttempted = false
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
                text = "Register Account",
                fontStyle = FontStyle.Italic,
                fontFamily = FontFamily.Serif,
                fontSize = 40.sp,
                color = Color.White
            )

            // Phone Number Field
            TextField(
                value = uiState.value.phoneNumber,
                onValueChange = { viewModel.setPhoneNumber(it) },
                label = { Text("Phone Number") },
                placeholder = { Text("e.g., 0123456789") },
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.value.isLoading,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Red,
                    unfocusedIndicatorColor = Color.Gray,
                    focusedLabelColor = Color.Red,
                    cursorColor = Color.Red
                ),
                supportingText = {
                    Text(
                        text = "10-15 digits, numbers only",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 12.sp
                    )
                }
            )

            // Password Field
            TextField(
                value = uiState.value.password,
                onValueChange = { viewModel.setPassword(it) },
                label = { Text("Create a Password") },
                placeholder = { Text("Min 8 chars, letters & numbers") },
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.value.isLoading,
                visualTransformation = PasswordVisualTransformation(),
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Red,
                    unfocusedIndicatorColor = Color.Gray,
                    focusedLabelColor = Color.Red,
                    cursorColor = Color.Red
                ),
                supportingText = {
                    Text(
                        text = "At least 8 characters, must contain letters and numbers",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 12.sp
                    )
                }
            )

            // Confirm Password Field
            TextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Confirm Password") },
                placeholder = { Text("Re-enter your password") },
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.value.isLoading,
                visualTransformation = PasswordVisualTransformation(),
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Red,
                    unfocusedIndicatorColor = Color.Gray,
                    focusedLabelColor = Color.Red,
                    cursorColor = Color.Red
                )
            )

            // Register Button
            Button(
                onClick = {
                    val confirmPasswordError = viewModel.validateConfirmPassword(confirmPassword)
                    if (confirmPasswordError != null) {
                        showErrorDialog = true
                    } else {
                        registrationAttempted = true
                        viewModel.registerUser()
                        showSuccessDialog=true
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.value.isLoading && viewModel.isRegisterFormValid(confirmPassword),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (!uiState.value.isLoading && viewModel.isRegisterFormValid(confirmPassword)) Color.White else Color.Gray
                )
            ) {

                if (uiState.value.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        color = Color.Red
                    )

                } else {
                    Text("Register", color = Color.Red, fontSize = 30.sp)
                }
            }

            // Error dialog
            if (showErrorDialog) {
                RegisterErrorDialog(
                    message = uiState.value.errorMessage ?: viewModel.validateConfirmPassword(confirmPassword) ?: "Registration failed",
                    onDismiss = {
                        showErrorDialog = false
                        viewModel.clearErrorMessage()
                    }
                )
            }

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

            // Back to Login Button
            TextButton(
                onClick = { navController.navigateUp() }
            ) {
                Text("Already have account? Login", fontSize = 16.sp, color = Color.Gray)
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
                color = Color.Red,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Text(
                message,
                fontSize = 14.sp
            )
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
            ) {
                Text("OK", color = Color.White)
            }
        },
        containerColor = Color.White
    )
}

@Composable
fun RegisterSuccessDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "Registration Successful!",
                color = Color.Green,
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
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF17AA00))
            ) {
                Text("Go to Login", color = Color.White)
            }
        },
        containerColor = Color.White
    )
}