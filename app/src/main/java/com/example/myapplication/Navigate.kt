package com.example.myapplication

import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun MyApp() {
    val navController = rememberNavController()
    val menuManager = MilkTeaMenuManager()
    val snackbarHostState = remember { SnackbarHostState() }

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
//        composable("menu_category/{category}") { backStackEntry ->
//            val category = backStackEntry.arguments?.getString("category") ?: ""
//            CategoryDetailScreen(
//                navController = navController,
//                menuManager = menuManager,
//                category = category
//            )
//        }
//        composable("menu_full") {
//            MenuFullScreen(
//                navController = navController,
//                menuManager = menuManager
//            )
//        }
        composable("register") {
            Register(navController = navController)
        }
        composable("register_success") {
            RegisterSuccess(navController = navController)
        }
        composable("item_detail/{itemName}") { backStackEntry ->
            val itemName = backStackEntry.arguments?.getString("itemName") ?: ""
            val item = menuManager.getItemByName(itemName)
            if (item != null) {
                ItemDetailScreen(
                    navController = navController,
                    item = item,
                    snackbarHostState = snackbarHostState
                )
            } else {
                Text("Item not found")
            }
        }
        composable("cart") {
            CartScreen(navController = navController)
            CartPage(navController = navController)
        }
        composable("paymentPage") {
            PaymentPage(navController = navController)
        }
    }
}