package com.example.myapplication

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import com.example.myapplication.di.ServiceLocator
import com.example.myapplication.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ServiceLocator.init(applicationContext)

        //initialize local database with existing data from firebase
        val authViewModel = AuthViewModel()
        authViewModel.initializeLocalDatabase()
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                CompositionLocalProvider(
                    LocalConfiguration provides Configuration().apply {
                        setTo(resources.configuration)
                    }
                ) {
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        containerColor = Color.White
                    ) {
                        MyApp()
                    }
                }
            }
        }
    }
}