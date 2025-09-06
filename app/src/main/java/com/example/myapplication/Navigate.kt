package com.example.myapplication

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun MyApp() {
    val navController = rememberNavController()
    val menuManager = MilkTeaMenuManager()  // Create instance here

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
                menuManager = menuManager  // Pass the instance
            )
        }
        composable("menu_category/{category}") { backStackEntry ->
            val category = backStackEntry.arguments?.getString("category") ?: ""
            MenuCategoryScreen(
                navController = navController,
                menuManager = menuManager,  // Pass the instance
                category = category
            )
        }
        composable("menu_full") {
            MenuFullScreen(
                navController = navController,
                menuManager = menuManager  // Pass the instance
            )
        }
        composable("menu_search") {
            MenuSearchScreen(
                navController = navController,
                menuManager = menuManager  // Pass the instance
            )
        }
        composable("register") {
            Register(navController = navController)
        }

        composable("register_success") {
            RegisterSuccess(navController = navController)
        }

    }
}