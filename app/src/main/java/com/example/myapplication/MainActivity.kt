package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MyApp()  // ✅ 调用 MyApp
            }
        }
    }
}

@Composable
fun LoginScreen(

    navController: NavHostController
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.logomixue),
                contentDescription = null,
                modifier = Modifier.size(300.dp),            // 宽高
                contentScale = ContentScale.Crop // 裁剪填充方式
            )
            // 标题
            Text(
                text = "Login",
                fontStyle = FontStyle.Italic,
                fontFamily = FontFamily.Serif,
                fontSize = 40.sp,
                color = Color.Red
            )

            // 用户名输入框
                TextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Username") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Red,  // 底部线条
                        unfocusedIndicatorColor = Color.Gray,
                        focusedLabelColor = Color.Red,
                        cursorColor = Color.Red
                    )
                )


            // 密码输入框
            TextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Red,
                    unfocusedIndicatorColor = Color.Gray,
                    focusedLabelColor = Color.Red,
                    cursorColor = Color.Red
                )
            )

            // 登录按钮
            Button(
                onClick = {navController.navigate("login_success")},
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
            ) {
                Text("Login", color = Color.White, fontSize = 40.sp)
            }

            // 额外按钮
            TextButton(onClick = { /* TODO: Handle register click */ }) {
                Text("Register", fontSize = 20.sp)
            }

            TextButton(onClick = { /* TODO: Handle forgot password click */ }) {
                Text("Forgot Password?", fontSize = 20.sp)
            }


        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    MyApplicationTheme {
        val navController = rememberNavController()
        LoginScreen(navController = navController)
    }
}

