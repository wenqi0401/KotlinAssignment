package com.example.myapplication

import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert

interface UserData {
    @Upsert
    suspend fun insertUser(user: loginUiState)
    @Delete
    suspend fun deleteUser(user: loginUiState)

    @Query("SELECT * FROM loginUiState ORDER BY id ASC")
    suspend fun getAllUsers(): List<loginUiState>
}