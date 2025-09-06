package com.example.myapplication

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun MyApp() {
    val navController = rememberNavController()
    val menuManager = MilkTeaMenuManager()

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        composable("login") {
            LoginScreen(navController = navController)
        }
        composable("login_success") {
            LoginSuccessScreen(navController = navController)
        }
        composable("menu_main") {
            MenuMainScreen(
                navController = navController,
                menuManager = menuManager
            )
        }
        composable("menu_category/{category}") { backStackEntry ->
            val category = backStackEntry.arguments?.getString("category") ?: ""
            MenuCategoryScreen(
                navController = navController,
                menuManager = menuManager,
                category = category
            )
        }
        composable("menu_full") {
            MenuFullScreen(
                navController = navController,
                menuManager = menuManager
            )
        }
    }
}