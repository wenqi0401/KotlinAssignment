package com.example.myapplication

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

@Composable
fun RegisterSuccess(navController: NavHostController) {

    //delay for 2.5 seconds and navigate to login screen
    LaunchedEffect(Unit) {

        kotlinx.coroutines.delay(2500)}
    navController.navigate("login") {
        popUpTo("register_success") { inclusive = true }
    }
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.Red
    ) { paddingValues ->


        Column(
            modifier = Modifier.padding(paddingValues).fillMaxSize(),
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
                text = "Register Successful!",
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
fun RegisterSuccessPreview() {
    RegisterSuccess(navController = rememberNavController())

}