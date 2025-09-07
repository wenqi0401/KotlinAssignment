package com.example.myapplication

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun TrackOrderScreen(
    navController: NavController,
    orderId: String,
    address: String,
    phone: String
) {
    // 模拟预计送达时间（例如 10 分钟后）
    val calendar = Calendar.getInstance()
    calendar.add(Calendar.MINUTE, 10)
    val estimatedTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(calendar.time)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 订单号
        Text(text = "#$orderId", fontSize = 22.sp, style = MaterialTheme.typography.titleLarge)

        Spacer(modifier = Modifier.height(12.dp))

        // LOGO
        Image(
            painter = painterResource(id = R.drawable.delivery_logo), // 你自己的 logo drawable
            contentDescription = "Mixue Logo",
            modifier = Modifier.size(180.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 四个进度条图标
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(painter = painterResource(id = R.drawable.ic_order_placed), contentDescription = "Order Placed")
            Image(painter = painterResource(id = R.drawable.ic_preparing), contentDescription = "Preparing")
            Image(painter = painterResource(id = R.drawable.ic_on_the_way), contentDescription = "On the Way")
            Image(painter = painterResource(id = R.drawable.ic_delivered), contentDescription = "Delivered")
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 状态信息
        Text(text = "Driver Arriving in 10 mins…", fontSize = 18.sp)
        Text(text = "Estimated delivery around $estimatedTime", fontSize = 14.sp)

        Spacer(modifier = Modifier.height(24.dp))

        // 送货信息
        Text(text = "Delivery Details:", fontSize = 18.sp, style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "🏠 $address")
        Text(text = "📞 $phone")

        Spacer(modifier = Modifier.height(32.dp))

        // 回到首页按钮
        ClickableText(
            text = AnnotatedString("Back to Home"),
            onClick = { navController.navigate("menu_main") }
        )
    }
}
