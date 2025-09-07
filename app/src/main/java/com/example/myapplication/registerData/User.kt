package com.example.myapplication.registerData

import androidx.room.Entity
import androidx.room.PrimaryKey

// This is for DATABASE - separate from UI state
@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val phoneNumber: String,
    val password: String
)

// This is for UI STATE - keep separate from database
data class loginUiState(
    val phoneNumber: String,
    val password: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isLoggedIn: Boolean = false
)