package com.example.myapplication

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.orderData.OrderRepository

@Composable
fun MyApp() {
    val navController = rememberNavController()
    val menuManager = remember { MilkTeaMenuManager() }
    val snackbarHostState = remember { SnackbarHostState() }

    val currentRoute = remember { mutableStateOf("login") }
    LaunchedEffect(navController) {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            currentRoute.value = destination.route ?: "login"
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.White,
        bottomBar = {
            if (currentRoute.value !in listOf(
                    "login",
                    "register",
                    "register_success",
                    "login_success",
                    "admin_login",
                    "admin_dashboard",
                    "admin_order_management",
                    "admin_order_detail/{orderId}",
                    "forget_password",
                    "admin_voucher_management",
                    "admin_ratings"
                )
            ) {
                BottomNavigationBar(navController = navController)
            }
        }
    ) { innerPadding ->
        OrientationAwareContent(
            portraitContent = {
                BuildNavHost(
                    navController = navController,
                    menuManager = menuManager,
                    snackbarHostState = snackbarHostState,
                    modifier = Modifier.padding(innerPadding)
                )
            },
            landscapeContent = {
                SimpleLandscapeWrapper {
                    BuildNavHost(
                        navController = navController,
                        menuManager = menuManager,
                        snackbarHostState = snackbarHostState,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        )
    }
}

@Composable
fun BuildNavHost(
    navController: NavHostController,
    menuManager: MilkTeaMenuManager,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = "login",
        modifier = modifier
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
            }
        }
        composable("cart") {
            CartPage(navController = navController)
        }
        composable("paymentPage") {
            PaymentPage(navController = navController)
        }
        composable("order_history") {
            OrderHistoryScreen(navController = navController)
        }
        composable("menu_full") {
            MenuFullScreen(navController = navController, menuManager = menuManager)
        }
        composable("menu_category/{category}") { backStackEntry ->
            val category = backStackEntry.arguments?.getString("category") ?: ""
            CategoryDetailScreen(
                navController = navController,
                menuManager = menuManager,
                category = category
            )
        }
        composable("profile") {
            ProfileScreen(navController = navController)
        }
        composable("language") {
            LanguageScreen(navController = navController)
        }
        composable("user_profile") {
            UserProfileScreen(navController = navController)
        }
        composable("admin_login") {
            AdminLoginScreen(navController = navController)
        }
        composable("admin_dashboard") {
            AdminDashboardScreen(navController = navController)
        }
        composable("admin_order_management") {
            AdminOrderListScreen(
                navController,
                repository = OrderRepository(LocalContext.current)
            )
        }
        composable("admin_order_detail/{orderId}") { backStackEntry ->
            val orderId = backStackEntry.arguments?.getString("orderId") ?: ""
            AdminOrderDetailScreen(
                navController,
                orderId,
                repository = OrderRepository(LocalContext.current)
            )
        }
        composable("help_center") {
            HelpCenterScreen(navController = navController)
        }
        composable("voucher_center") {
            VoucherCenterScreen(navController = navController)
        }
        composable("admin_voucher_management") {
            AdminVoucherManagementScreen(navController = navController)
        }
        composable("forget_password") {
            ForgetPasswordScreen(navController = navController)
        }
        composable("admin_ratings") {
            AdminRatingsScreen(navController = navController)
        }
    }
}

@Composable
fun WithOrientationSupport(content: @Composable () -> Unit) {
    OrientationAwareContent(
        portraitContent = content,
        landscapeContent = { SimpleLandscapeWrapper(content = content) }
    )
}

