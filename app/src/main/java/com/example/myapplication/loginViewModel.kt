package com.example.myapplication

import androidx.lifecycle.ViewModel
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

@Entity(tableName = "users")
data class loginUiState (
    @PrimaryKey(autoGenerate = true)
    var id:Int=0,
    var username: String="",
    var password: String =""
    //confirmPassword: String =""
    )

class LoginViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(loginUiState())
    val uiState: StateFlow<loginUiState> = _uiState


    // In your LoginViewModel class
    fun setUsername(username: String) {
        _uiState.update { it.copy(username = username) }
    }

    fun setPassword(password: String) {
        _uiState.update { it.copy(password = password) }
    }

fun getUsername(): String {
        return _uiState.value.username
    }
    fun getPassword(): String {
        return _uiState.value.password
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



    // Add other functions that confirm password match in register screen
    fun passwordsMatch(confirmPassword: String): Boolean {
        return _uiState.value.password == confirmPassword
    }

    // if register is successful and passwords match, store data in database
    // use Room database to store data

}