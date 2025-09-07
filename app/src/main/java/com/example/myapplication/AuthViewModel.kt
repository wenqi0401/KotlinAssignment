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

    private val _uiState = MutableStateFlow(
        loginUiState(
            phoneNumber = "",
            password = "",
            isLoading = false,
            errorMessage = null,
            isLoggedIn = false
        )
    )
    val uiState: StateFlow<loginUiState> = _uiState

    fun setPhoneNumber(phoneNumber: String) {
        // Only allow numbers
        val filteredPhone = phoneNumber.filter { it.isDigit() }
        _uiState.update { it.copy(phoneNumber = filteredPhone) }
    }

    fun setPassword(password: String) {
        _uiState.update { it.copy(password = password) }
    }

    // VALIDATE AND LOGIN
    fun loginUser() {
        viewModelScope.launch {
            val phoneNumber = _uiState.value.phoneNumber
            val password = _uiState.value.password

            // Validate phone number
            val phoneError = ValidationInput.getPhoneNumberError(phoneNumber)
            if (phoneError != null) {
                _uiState.update { it.copy(errorMessage = phoneError) }
                return@launch
            }

            // Validate password
            val passwordError = ValidationInput.getPasswordError(password)
            if (passwordError != null) {
                _uiState.update { it.copy(errorMessage = passwordError) }
                return@launch
            }

            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            try {
                val user = repository.getUserByCredentials(phoneNumber, password)

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
                            errorMessage = "Invalid phone number or password"
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

    // VALIDATE AND REGISTER
    fun registerUser() {
        viewModelScope.launch {
            val phoneNumber = _uiState.value.phoneNumber
            val password = _uiState.value.password

            // Validate phone number
            val phoneError = ValidationInput.getPhoneNumberError(phoneNumber)
            if (phoneError != null) {
                _uiState.update { it.copy(errorMessage = phoneError) }
                return@launch
            }

            // Validate password
            val passwordError = ValidationInput.getPasswordError(password)
            if (passwordError != null) {
                _uiState.update { it.copy(errorMessage = passwordError) }
                return@launch
            }

            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            try {
                // Check if user already exists
                val existingUser = repository.checkIfUserExists(phoneNumber)

                if (existingUser != null) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "Phone number already registered"
                        )
                    }
                } else {
                    // Create new user
                    val newUser = User(
                        phoneNumber = phoneNumber,
                        password = password
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

    // VALIDATE CONFIRM PASSWORD
    fun validateConfirmPassword(confirmPassword: String): String? {
        return when {
            confirmPassword.isBlank() -> "Please confirm your password"
            confirmPassword != _uiState.value.password -> "Passwords do not match"
            else -> null
        }
    }

    fun passwordsMatch(confirmPassword: String): Boolean {
        return _uiState.value.password == confirmPassword
    }

    fun clearCredentials() {
        _uiState.value = loginUiState(
            phoneNumber = "",
            password = "",
            isLoading = false,
            errorMessage = null,
            isLoggedIn = false
        )
    }

    fun clearErrorMessage() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    // Check if form is valid for enabling buttons
    fun isLoginFormValid(): Boolean {
        return _uiState.value.phoneNumber.isNotBlank() &&
                _uiState.value.password.isNotBlank() &&
                ValidationInput.isValidPhoneNumber(_uiState.value.phoneNumber) &&
                ValidationInput.isValidPassword(_uiState.value.password)
    }

    fun isRegisterFormValid(confirmPassword: String): Boolean {
        return isLoginFormValid() &&
                confirmPassword.isNotBlank() &&
                passwordsMatch(confirmPassword)
    }
}
