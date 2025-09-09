package com.example.myapplication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.orderData.UserSession
import com.example.myapplication.registerData.User
import com.example.myapplication.registerData.UserRepository
import com.example.myapplication.registerData.loginUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    private val repository = UserRepository() // This uses Firebase
    private val _uiState = MutableStateFlow(loginUiState())
    val uiState: StateFlow<loginUiState> = _uiState.asStateFlow()

    fun setPhoneNumber(phoneNumber: String) {
        _uiState.value = _uiState.value.copy(phoneNumber = phoneNumber)
    }

    fun setPassword(password: String) {
        _uiState.value = _uiState.value.copy(password = password)
    }

    fun registerUser() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            try {
                // Check if user already exists in Firebase
                val existingUser = repository.checkIfUserExists(_uiState.value.phoneNumber)
                if (existingUser != null) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Phone number already registered"
                    )
                    return@launch
                }

                // Create new user in Firebase
                val newUser = User(
                    phoneNumber = _uiState.value.phoneNumber,
                    password = _uiState.value.password
                )

                val userId = repository.insertUser(newUser)

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    currentUser = newUser.copy(id = userId)
                )

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Registration failed: ${e.message}"
                )
            }
        }
    }

    fun loginUser() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            try {
                // Login with Firebase
                val user = repository.getUserByCredentials(
                    _uiState.value.phoneNumber,
                    _uiState.value.password
                )

                if (user != null) {
                    UserSession.setCurrentUser(user.phoneNumber)
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isLoggedIn = true,
                        currentUser = user
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Invalid phone number or password"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Login failed: ${e.message}"
                )
            }
        }
    }

    fun validateConfirmPassword(confirmPassword: String): String? {
        return if (_uiState.value.password != confirmPassword) {
            "Passwords do not match"
        } else {
            null
        }
    }

    fun isRegisterFormValid(confirmPassword: String): Boolean {
        return _uiState.value.phoneNumber.length >= 10 &&
                _uiState.value.password.length >= 8 &&
                _uiState.value.password == confirmPassword
    }

    fun clearErrorMessage() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    fun clearCredentials() {
        _uiState.value = _uiState.value.copy(
            phoneNumber = "",
            password = "",
            errorMessage = null
        )
    }

    fun logout() {
        UserSession.clearSession()
        _uiState.value = loginUiState()
    }

    fun updateUserName(newName: String) {
        viewModelScope.launch {
            try {
                val currentUser = _uiState.value.currentUser
                if (currentUser != null && currentUser.id.isNotEmpty()) {
                    repository.updateUserName(currentUser.id, newName)

                    // Update local state
                    _uiState.value = _uiState.value.copy(
                        currentUser = currentUser.copy(name = newName)
                    )
                }
            } catch (e: Exception) {
                // Handle error
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Failed to update name: ${e.message}"
                )
            }
        }
    }

    fun updateUserGender(newGender: String) {
        viewModelScope.launch {
            try {
                val currentUser = _uiState.value.currentUser
                if (currentUser != null && currentUser.id.isNotEmpty()) {
                    repository.updateUserGender(currentUser.id, newGender)

                    // Update local state
                    _uiState.value = _uiState.value.copy(
                        currentUser = currentUser.copy(gender = newGender)
                    )
                }
            } catch (e: Exception) {
                // Handle error
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Failed to update gender: ${e.message}"
                )
            }
        }
    }

    fun updateProfilePicture(profilePicturePath: String?) {
        viewModelScope.launch {
            try {
                val currentUser = _uiState.value.currentUser
                if (currentUser != null && currentUser.id.isNotEmpty()) {
                    repository.updateUserProfilePicture(currentUser.id, profilePicturePath)

                    // Update local state
                    _uiState.value = _uiState.value.copy(
                        currentUser = currentUser.copy(profilePicturePath = profilePicturePath)
                    )
                }
            } catch (e: Exception) {
                // Handle error
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Failed to update profile picture: ${e.message}"
                )
            }
        }
    }

    fun refreshUserData() {
        viewModelScope.launch {
            try {
                val currentUser = _uiState.value.currentUser
                if (currentUser != null && currentUser.id.isNotEmpty()) {
                    // Fetch the latest user data from Firebase
                    val updatedUser = repository.getUserById(currentUser.id)
                    if (updatedUser != null) {
                        _uiState.value = _uiState.value.copy(
                            currentUser = updatedUser
                        )
                    }
                }
            } catch (e: Exception) {
                // Handle error quietly (don't show error message for refresh)
                println("Failed to refresh user data: ${e.message}")
            }
        }
    }
}