package com.example.myapplication

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.orderData.UserSession
import com.example.myapplication.registerData.User
import com.example.myapplication.registerData.UserRepository
import com.example.myapplication.registerData.loginUiState
import com.google.firebase.auth.FirebaseAuth
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
                Log.d("AuthViewModel", "User registered successfully with ID: $userId")

                _uiState.value = _uiState.value.copy(
                    isLoading = true,
                    isLoggedIn = true,
                    errorMessage = null,
                    currentUser = newUser.copy(id = userId)
                )

            } catch (e: Exception) {
                Log.e("AuthViewModel", "Registration failed", e)
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
                    Log.d("AuthViewModel", "User logged in successfully: ${user.name}")
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
                Log.e("AuthViewModel", "Login failed", e)
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
        Log.d("AuthViewModel", "User logged out")
        _uiState.value = loginUiState()
    }

    fun updateUserName(newName: String) {
        viewModelScope.launch {
            try {
                val currentUser = _uiState.value.currentUser
                if (currentUser != null && currentUser.id.isNotEmpty()) {
                    // Clear any existing errors
                    _uiState.value = _uiState.value.copy(errorMessage = null)

                    repository.updateUserName(currentUser.id, newName)
                    Log.d("AuthViewModel", "Name updated successfully to: $newName")

                    // Update local state
                    _uiState.value = _uiState.value.copy(
                        currentUser = currentUser.copy(name = newName)
                    )
                } else {
                    Log.e("AuthViewModel", "Cannot update name: User not logged in")
                }
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Failed to update name", e)
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
                    // Clear any existing errors
                    _uiState.value = _uiState.value.copy(errorMessage = null)

                    repository.updateUserGender(currentUser.id, newGender)
                    Log.d("AuthViewModel", "Gender updated successfully to: $newGender")

                    // Update local state
                    _uiState.value = _uiState.value.copy(
                        currentUser = currentUser.copy(gender = newGender)
                    )
                } else {
                    Log.e("AuthViewModel", "Cannot update gender: User not logged in")
                }
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Failed to update gender", e)
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
                    // Clear any existing errors
                    _uiState.value = _uiState.value.copy(errorMessage = null)

                    repository.updateUserProfilePicture(currentUser.id, profilePicturePath)
                    Log.d("AuthViewModel", "Profile picture updated successfully: $profilePicturePath")

                    // Update local state
                    _uiState.value = _uiState.value.copy(
                        currentUser = currentUser.copy(profilePicturePath = profilePicturePath)
                    )
                } else {
                    Log.e("AuthViewModel", "Cannot update profile picture: User not logged in")
                }
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Failed to update profile picture", e)
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
                        Log.d("AuthViewModel", "User data refreshed successfully")
                        _uiState.value = _uiState.value.copy(
                            currentUser = updatedUser
                        )
                    } else {
                        Log.w("AuthViewModel", "User not found during refresh")
                    }
                }
            } catch (e: Exception) {
                // Handle error quietly (don't show error message for refresh)
                Log.e("AuthViewModel", "Failed to refresh user data", e)
            }
        }
    }

    // Helper function to check if user is logged in
    fun isUserLoggedIn(): Boolean {
        return _uiState.value.currentUser != null && _uiState.value.isLoggedIn
    }

    // Helper function to get current user safely
    fun getCurrentUser(): User? {
        return _uiState.value.currentUser
    }

    // Function to clear profile picture
    fun removeProfilePicture() {
        viewModelScope.launch {
            try {
                val currentUser = _uiState.value.currentUser
                if (currentUser != null && currentUser.id.isNotEmpty()) {
                    repository.updateUserProfilePicture(currentUser.id, null)
                    Log.d("AuthViewModel", "Profile picture removed successfully")

                    // Update local state
                    _uiState.value = _uiState.value.copy(
                        currentUser = currentUser.copy(profilePicturePath = null)
                    )
                }
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Failed to remove profile picture", e)
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Failed to remove profile picture: ${e.message}"
                )
            }
        }
    }
}