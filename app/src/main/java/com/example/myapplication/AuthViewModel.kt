package com.example.myapplication

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.registerData.User
import com.example.myapplication.registerData.UserDatabase
import com.example.myapplication.registerData.UserRepository
import com.example.myapplication.registerData.loginUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val database = UserDatabase.getDatabase(application)
    private val repository = UserRepository(database.userDao())

    private val _uiState = MutableStateFlow(loginUiState())
    val uiState: StateFlow<loginUiState> = _uiState

    fun setUsername(username: String) {
        _uiState.update { it.copy(username = username) }
    }

    fun setPassword(password: String) {
        _uiState.update { it.copy(password = password) }
    }

    // LOGIN WITH DATABASE
    fun loginUser() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            try {
                val user = repository.getUserByCredentials(
                    _uiState.value.username,
                    _uiState.value.password
                )

                if (user != null) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isLoggedIn = true,
                            errorMessage = null
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "Invalid username or password"
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Login failed: ${e.message}"
                    )
                }
            }
        }
    }

    // REGISTER WITH DATABASE
    fun registerUser() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            try {
                // Check if user already exists
                val existingUser = repository.checkIfUserExists(_uiState.value.username)

                if (existingUser != null) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "Username already exists"
                        )
                    }
                } else {
                    // Create new user
                    val newUser = User(
                        username = _uiState.value.username,
                        password = _uiState.value.password
                    )

                    repository.insertUser(newUser)

                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = null
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Registration failed: ${e.message}"
                    )
                }
            }
        }
    }

    fun passwordsMatch(confirmPassword: String): Boolean {
        return _uiState.value.password == confirmPassword
    }

    fun clearCredentials() {
        _uiState.value = loginUiState()
    }

    fun clearErrorMessage() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}