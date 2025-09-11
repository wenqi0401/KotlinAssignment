package com.example.myapplication

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(navController: NavHostController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Your Cart", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = androidx.compose.material3.TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Red
                )
            )
        },
        bottomBar = {
            Button(
                onClick = { /* Proceed to checkout */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
            ) {
                Text(
                    "Proceed to Checkout - RM 0.00",
                    color = Color.White,
                    fontSize = 18.sp
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Your cart is empty",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Add some delicious drinks to your cart!",
                    fontSize = 16.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemDetailScreen(
    navController: NavHostController,
    item: MenuItem,
    snackbarHostState: SnackbarHostState
) {

    var quantity by remember { mutableStateOf(1) }
    var selectedSize by remember { mutableStateOf("Medium") }
    var selectedIceLevel by remember { mutableStateOf("Regular") }
    var selectedSugarLevel by remember { mutableStateOf("Regular") }

    val totalPrice = remember(quantity) {
        item.price * quantity
    }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(item.name, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Red
                )
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        bottomBar = {
            Button(
                onClick = {
                    // 创建购物车商品
                    val cartItem = CartItem(
                        item = item,
                        quantity = quantity,
                        size = selectedSize,
                        ice = selectedIceLevel,
                        sugar = selectedSugarLevel
                    )

                    // 添加到购物车
                    CartManager.addToCart(cartItem)

                    // 显示提示信息
                    scope.launch {
                        val result = snackbarHostState.showSnackbar(
                            message = "${item.name} added to cart",
                            actionLabel = "View Cart"
                        )
                        if (result == SnackbarResult.ActionPerformed) {
                            navController.navigate("cart")
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
            ) {
                Text(
                    "Add To Cart - RM ${"%.2f".format(totalPrice)}",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            item {
                // 商品图片
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp)
                ) {
                    Image(
                        painter = painterResource(id = item.imageResId),
                        contentDescription = item.name,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Fit,
                        alignment = Alignment.Center
                    )
                }

                // 商品信息
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = item.name,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Red
                    )
                    Text(
                        text = "RM ${"%.2f".format(item.price)}",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                // 数量选择器
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Quantity:", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // 减少按钮
                        Card(
                            onClick = { if (quantity > 1) quantity-- },
                            modifier = Modifier.size(48.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFCDD2)),
                            shape = CircleShape,
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("-", fontSize = 36.sp, fontWeight = FontWeight.Bold, color = Color.Red)
                            }
                        }

                        Spacer(modifier = Modifier.width(16.dp))
                        Text(quantity.toString(), fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(16.dp))

                        // 增加按钮
                        Card(
                            onClick = { quantity++ },
                            modifier = Modifier.size(48.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFCDD2)),
                            shape = CircleShape,
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("+", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.Red)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 尺寸选择
                Text(
                    text = "Choose your size:",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    listOf("Large", "Medium", "Small").forEach { size ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 4.dp)
                        ) {
                            RadioButton(
                                selected = selectedSize == size,
                                onClick = { selectedSize = size },
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = Color.Red,
                                    unselectedColor = Color.Gray
                                )
                            )
                            Text(text = size, modifier = Modifier.padding(start = 8.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 冰度选择
                Text(
                    text = "Choose your ice level:",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    listOf("No ice", "Less ice", "Regular").forEach { iceLevel ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 4.dp)
                        ) {
                            RadioButton(
                                selected = selectedIceLevel == iceLevel,
                                onClick = { selectedIceLevel = iceLevel },
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = Color.Red,
                                    unselectedColor = Color.Gray
                                )
                            )
                            Text(text = iceLevel, modifier = Modifier.padding(start = 8.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 糖度选择
                Text(
                    text = "Choose your sugar level:",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    listOf("No sugar", "Less sugar", "Regular").forEach { sugarLevel ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 4.dp)
                        ) {
                            RadioButton(
                                selected = selectedSugarLevel == sugarLevel,
                                onClick = { selectedSugarLevel = sugarLevel },
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = Color.Red,
                                    unselectedColor = Color.Gray
                                )
                            )
                            Text(text = sugarLevel, modifier = Modifier.padding(start = 8.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}