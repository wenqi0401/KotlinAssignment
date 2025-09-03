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


    fun validCredentials(): Boolean {
        val validUsername = "admin"
        val validPassword = "password123"
        return _uiState.value.username == validUsername && _uiState.value.password == validPassword
    }

    //if the credentials are not valid, pop up a dialog to inform the user



    fun clearCredentials() {
        _uiState.value = loginUiState()
    }

    fun isLoginButtonEnabled(): Boolean {
        return _uiState.value.username.isNotBlank() && _uiState.value.password.isNotBlank()
    }


}