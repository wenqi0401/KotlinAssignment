// loginViewModel.kt
package com.example.myapplication.ui.theme

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.ui.theme.data.User
import com.example.myapplication.ui.theme.data.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LoginUiState(
    var username: String = "",
    var password: String = ""
)

class LoginViewModel(private val repository: UserRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState

    fun setUsername(username: String) {
        _uiState.update { it.copy(username = username) }
    }

    fun setPassword(password: String) {
        _uiState.update { it.copy(password = password) }
    }

    fun validateCredentials(callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            val user = repository.getUser(_uiState.value.username, _uiState.value.password)
            callback(user != null)
        }
    }

    fun registerUser(username: String, password: String, callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            val existingUser = repository.getUserByUsername(username)
            if (existingUser == null) {
                repository.insert(User(username = username, password = password))
                callback(true)
            } else {
                callback(false)
            }
        }
    }

    fun clearCredentials() {
        _uiState.value = LoginUiState()
    }

    fun isLoginButtonEnabled(): Boolean {
        return _uiState.value.username.isNotBlank() && _uiState.value.password.isNotBlank()
    }
}