package com.example.myapplication.registerData

import androidx.room.Entity
import androidx.room.PrimaryKey

// Updated DATABASE entity with name and gender
@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val phoneNumber: String,
    val password: String,
    val name: String = "User", // Default name
    val gender: String = "Male", // Default gender
    val profilePicturePath: String? = null // For storing profile picture path
)

// UI STATE for login
data class loginUiState(
    val phoneNumber: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isLoggedIn: Boolean = false,
    val currentUser: User? = null // Store current logged-in user
)