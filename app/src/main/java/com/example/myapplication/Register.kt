package com.example.myapplication

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

@Composable
fun Register(
    navController: NavHostController,
    viewModel: AuthViewModel = viewModel()  // Use AuthViewModel instead of LoginViewModel
) {
    var confirmPassword by remember { mutableStateOf("") }
    var showErrorDialog by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }

    val uiState = viewModel.uiState.collectAsState()

    // Show error dialog when there's an error message
    LaunchedEffect(uiState.value.errorMessage) {
        showErrorDialog = uiState.value.errorMessage != null
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

            TextField(
                value = uiState.value.username,
                onValueChange = { viewModel.setUsername(it) },
                label = { Text("Create a Username") },
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.value.isLoading,
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Red,
                    unfocusedIndicatorColor = Color.Gray,
                    focusedLabelColor = Color.Red,
                    cursorColor = Color.Red
                )
            )

            TextField(
                value = uiState.value.password,
                onValueChange = { viewModel.setPassword(it) },
                label = { Text("Create a Password") },
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.value.isLoading,
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
                label = { Text("Confirm Password") },
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.value.isLoading,
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Red,
                    unfocusedIndicatorColor = Color.Gray,
                    focusedLabelColor = Color.Red,
                    cursorColor = Color.Red
                )
            )

            Button(
                onClick = {
                    if (viewModel.passwordsMatch(confirmPassword)) {
                        viewModel.registerUser() // Register in database
                        showSuccessDialog = true
                    } else {
                        showErrorDialog = true
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.value.isLoading &&
                        uiState.value.username.isNotBlank() &&
                        uiState.value.password.isNotBlank() &&
                        confirmPassword.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White)
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
                    message = uiState.value.errorMessage ?: "Passwords do not match",
                    onDismiss = {
                        showErrorDialog = false
                        viewModel.clearErrorMessage()
                    }
                )
            }

            // Success dialog
            if (showSuccessDialog && uiState.value.errorMessage == null && !uiState.value.isLoading) {
                RegisterSuccessDialog(
                    onDismiss = {
                        showSuccessDialog = false
                        viewModel.clearCredentials()
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

// Update RegisterErrorDialog
@Composable
fun RegisterErrorDialog(message: String, onDismiss: () -> Unit) {
    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Registration Error") },
        text = { Text(message) },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("OK")
            }
        }
    )
}

// Add RegisterSuccessDialog
@Composable
fun RegisterSuccessDialog(onDismiss: () -> Unit) {
    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Registration Successful") },
        text = { Text("Account created successfully! You can now login.") },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("OK")
            }
        }
    )
}