package com.example.myapplication.registerData

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface UserDao {

    @Insert
    suspend fun insertUser(user: User)  // Use User, not loginUiState

    @Query("SELECT * FROM users WHERE phoneNumber = :username AND password = :password LIMIT 1")
    suspend fun getUserByUsernameAndPassword(username: String, password: String): User?

    @Query("SELECT * FROM users WHERE phoneNumber = :username LIMIT 1")
    suspend fun getUserByUsername(username: String): User?
}