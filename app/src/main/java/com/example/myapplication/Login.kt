package com.example.myapplication

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.ui.theme.MyApplicationTheme
import kotlinx.coroutines.delay

@Composable
fun LoginScreen(
    navController: NavHostController,
    viewModel: AuthViewModel = viewModel()  // Use AuthViewModel instead of LoginViewModel
) {
    var showErrorDialog by remember { mutableStateOf(false) }
    val uiState = viewModel.uiState.collectAsState()

    // Handle successful login - Don't clear credentials immediately
    LaunchedEffect(uiState.value.isLoggedIn) {
        if (uiState.value.isLoggedIn) {
            navController.navigate("login_success")
            // Don't clear credentials here - let them persist for the session
        }
    }

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
                text = "Login",
                fontStyle = FontStyle.Italic,
                fontFamily = FontFamily.Serif,
                fontSize = 40.sp,
                color = Color.White
            )

            TextField(
                value = uiState.value.phoneNumber,
                onValueChange = { viewModel.setPhoneNumber(it) },
                label = { Text("Phone Number") },
                placeholder = {Text("e.g. 012345678901")},
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
                label = { Text("Password") },
                placeholder = {Text("At least 8 chars, letters & numbers")},
                visualTransformation = PasswordVisualTransformation(),
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
                onClick = { viewModel.loginUser() }, // Use database login
                modifier = Modifier.fillMaxWidth(),
                // Enable button only when not loading and fields are not empty(optional)
                enabled = !uiState.value.isLoading &&
                        uiState.value.phoneNumber.isNotBlank() &&
                        uiState.value.password.isNotBlank(),
                //set enabled button color to white and disabled to gray
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (!uiState.value.isLoading &&
                        uiState.value.phoneNumber.isNotBlank() &&
                        uiState.value.password.isNotBlank()
                    ) Color.White else Color.Gray
                )

            ) {
                if (uiState.value.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        color = Color.Red
                    )
                } else {
                    Text("Login", color = Color.Red, fontSize = 30.sp)
                }
            }

            TextButton(
                onClick = { navController.navigate("admin_login") }
            ) {
                Text("Admin Login", fontSize = 16.sp, color = Color.Gray)
            }

            // Show error dialog
            if (showErrorDialog) {
                LoginErrorDialog(
                    message = uiState.value.errorMessage ?: "Login failed",
                    onDismiss = {
                        showErrorDialog = false
                        viewModel.clearErrorMessage()
                    }
                )
            }

            TextButton(
                onClick = { navController.navigate("register") }
            ) {
                Text("Register", fontSize = 16.sp, color = Color.Gray)
            }

            TextButton(onClick = { /* TODO */ }) {
                Text("Forgot Password?", fontSize = 16.sp, color = Color.Gray)
            }
        }
    }
}

@Composable
fun LoginSuccessScreen(navController: NavHostController) {
    //delay with 1 second to navigate to menu_main
    LaunchedEffect(Unit) {
        delay(1000)
        //no need pop up to login_success
        navController.navigate("menu_main")
    }
    LaunchedEffect(Unit) {
        delay(1000)
        navController.navigate("menu_main") {
            popUpTo("menu_main") { inclusive = true }
        }
    }
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.Red
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.logomixue),
                contentDescription = null,
                modifier = Modifier.size(300.dp),            // 宽高
                contentScale = ContentScale.Crop // 裁剪填充方式
            )
            // 标题
            Text(
                text = "Login Successful!",
                fontStyle = FontStyle.Italic,
                fontFamily = FontFamily.SansSerif,
                fontSize = 40.sp,
                color = Color.White
            )

            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "Success",
                modifier = Modifier.size(100.dp),
                tint = Color.White
            )
        }

    }
}

@Preview(showBackground = true)
@Composable
fun LoginSuccessPreview() {
    MyApplicationTheme {
        val navController = rememberNavController()
        LoginScreen(navController = navController)
    }
}

@Composable
fun LoginErrorDialog(message: String, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Login Failed") },
        text = { Text(message) },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("OK")
            }
        }
    )
}