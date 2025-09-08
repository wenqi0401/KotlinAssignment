package com.example.myapplication

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.myapplication.orderData.OrderRepository
import java.text.SimpleDateFormat
import java.util.*

// 主要的 TrackOrderScreen 函数（带 repository 参数）
@Composable
fun TrackOrderScreen(
    navController: NavController,
    orderId: String,
    repository: OrderRepository
) {
    var order by remember { mutableStateOf(repository.getOrderById(orderId)) }

    // 如果 admin 改 status，这里会刷新
    LaunchedEffect(orderId) {
        order = repository.getOrderById(orderId)
    }

    if (order == null) {
        Text("Order not found")
        return
    }

    val calendar = Calendar.getInstance()
    calendar.add(Calendar.MINUTE, 10)
    val estimatedTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(calendar.time)

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 订单号
        Text(text = "#${order!!.orderId}", fontSize = 22.sp)

        Spacer(modifier = Modifier.height(12.dp))

        // LOGO - 如果图片不存在会出错，可以先注释掉
        Image(
            painter = painterResource(id = R.drawable.delivery_logo),
            contentDescription = "Mixue Logo",
            modifier = Modifier.size(180.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 状态进度
        Text(text = "Status: ${order!!.status}", fontSize = 18.sp)

        Spacer(modifier = Modifier.height(12.dp))

        Text(text = "Estimated delivery around $estimatedTime", fontSize = 14.sp)

        Spacer(modifier = Modifier.height(24.dp))

        // 地址信息
        Text(text = "🏠 ${order!!.deliveryAddress}")
        Text(text = "📞 ${order!!.phoneNumber}")

        Spacer(modifier = Modifier.height(32.dp))

        Button(onClick = { navController.navigate("menu_main") }) {
            Text("Back to Home")
        }
    }
}

// 重载函数 - 兼容旧的调用方式（带 address 和 phone 参数）
@Composable
fun TrackOrderScreen(
    navController: NavController,
    orderId: String,
    address: String,
    phone: String
) {
    val context = LocalContext.current
    val repository = remember { OrderRepository(context) }

    // 调用主要的 TrackOrderScreen 函数
    TrackOrderScreen(navController, orderId, repository)
}