package com.example.myapplication

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun MyApp() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "login"  // 简化名称
    ) {
        composable("login") {
            LoginScreen(navController = navController)
        }
        composable("login_success") {  // ✅ 匹配导航中的名称
            LoginSuccessScreen(navController = navController)
        }
    }
}