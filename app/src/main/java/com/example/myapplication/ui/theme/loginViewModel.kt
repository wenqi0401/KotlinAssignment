package com.example.myapplication.ui.theme

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


data class loginUiState (
    var username: String="",
    var password: String =""
    )

class LoginViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(loginUiState())
    val uiState: StateFlow<loginUiState> = _uiState


    fun setUsername(username: String) {
        _uiState.value.username = username
    }

    fun setPassword(password: String) {
        _uiState.value.password = password
    }

    fun validateCredentials(): Boolean {
        // 简单的验证逻辑，可以根据需要进行扩展
        return uiState.username == "admin" && uiState.password == "password"
    }
}