package com.example.myapplication

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.di.ServiceLocator
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
    private val syncRepo get()= ServiceLocator.userSyncRepository
    // Call this after successful login
    fun onLoginSuccess(userId: String) {
        viewModelScope.launch {
            syncRepo.seedLocalFromRemote(userId)
            syncRepo.startSync(userId)
        }
    }

    fun updateUserAddress(newAddress: String) {
        val currentUser = _uiState.value.currentUser
        if (currentUser != null) {
            val updatedUser = currentUser.copy(address = newAddress)

            viewModelScope.launch {
                try {
                    _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

                    // Update in Firebase
                    repository.updateUserAddress(currentUser.id, newAddress)

                    // Update local state
                    _uiState.value = _uiState.value.copy(
                        currentUser = updatedUser,
                        isLoading = false
                    )

                    // Update session
                    UserSession.setCurrentUser(updatedUser.phoneNumber)

                    Log.d("AuthViewModel", "User address updated successfully")
                } catch (e: Exception) {
                    Log.e("AuthViewModel", "Failed to update user address", e)
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Failed to update address: ${e.message}"
                    )
                }
            }
        }
    }

    // Add this method to sync all existing Firebase data on app startup
    fun initializeLocalDatabase() {
        viewModelScope.launch {
            try {
                Log.d("AuthViewModel", "Initializing local database with Firebase data")
                syncRepo.syncAllUsersFromFirebaseToRoom()
                Log.d("AuthViewModel", "Local database initialization completed")
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Failed to initialize local database", e)
            }
        }
    }

    // Optional: Method to force refresh all data
    fun refreshAllDataFromFirebase() {
        viewModelScope.launch {
            try {
                Log.d("AuthViewModel", "Refreshing all data from Firebase")
                syncRepo.syncAllUsersFromFirebaseToRoom()
                Log.d("AuthViewModel", "Data refresh completed")
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Failed to refresh data from Firebase", e)
            }
        }
    }

    // Call this on logout
    fun onLogout() {
        syncRepo.stopSync()
        // clear local session/state as needed
    }
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
                val existingUser = repository.checkIfUserExists(_uiState.value.phoneNumber)
                if (existingUser != null) {
                    _uiState.value = _uiState.value.copy(
                        errorMessage = "Phone number already registered"
                    )
                    return@launch
                }

                val newUser = User(
                    phoneNumber = _uiState.value.phoneNumber,
                    password = _uiState.value.password
                )
                val userId = repository.insertUser(newUser)
                val savedUser = newUser.copy(id = userId)

                UserSession.setCurrentUser(savedUser.phoneNumber)
                onLoginSuccess(userId) // Start syncing after registration
                Log.d("AuthViewModel", "User registered successfully: ${savedUser.name}")
                _uiState.value = _uiState.value.copy(
                    isLoggedIn = true,
                    currentUser = savedUser,
                    errorMessage = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Registration failed: ${e.message}"
                )
            } finally {
                _uiState.value = _uiState.value.copy(isLoading = false)
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
                    onLoginSuccess(user.id) // Start syncing after login
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

    fun hydrateFromSession() {
        viewModelScope.launch {
            try {
                val phone = UserSession.getCurrentUser() // implement a getter if missing
                if (!phone.isNullOrEmpty()) {
                    val user = repository.getUserByPhone(phone)
                    if (user != null) {
                        _uiState.value = _uiState.value.copy(
                            isLoggedIn = true,
                            currentUser = user
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e("AuthViewModel", "hydrateFromSession failed", e)
            }
        }
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
                    _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
                    repository.updateUserGender(currentUser.id, newGender)
                    _uiState.value = _uiState.value.copy(
                        currentUser = currentUser.copy(gender = newGender)
                    )
                } else {
                    Log.e("AuthViewModel", "Cannot update gender: User not logged in")
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Failed to update gender: ${e.message}"
                )
            } finally {
                _uiState.value = _uiState.value.copy(isLoading = false)
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
    // In AuthViewModel.kt

    fun validatePhoneNumber(phone: String): String? {
        val phoneRegex = Regex("^01[0-9]{8,9}$")
        return when {
            phone.isBlank() -> "Phone number cannot be empty"
            !phoneRegex.matches(phone) -> "Invalid phone number format (should start with 01 and have 10 or 11 digits)"
            else -> null
        }
    }


    fun validatePassword(password: String): String? {
        return when {
            password.isEmpty() -> "Password is required"
            password.length < 8 -> "Password must be at least 8 characters"
            !password.any { it.isDigit() } || !password.any { it.isLetter() } ->
                "Password must contain letters and numbers"
            else -> null
        }
    }
    fun validateConfirmPassword(password: String, confirmPassword: String): String? {
        return when {
            confirmPassword.isEmpty() -> "Please confirm your password"
            password != confirmPassword -> "Passwords do not match"
            else -> null
        }
    }



}