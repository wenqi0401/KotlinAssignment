package com.example.myapplication

interface UserDao {
    suspend fun insertUser(user: loginUiState)
    suspend fun getAllUsers(): List<loginUiState>
    suspend fun getUserByCredentials(username: String, password: String): loginUiState?
    suspend fun getUserByUsername(username: String): loginUiState?
}