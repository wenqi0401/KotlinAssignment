package com.example.myapplication.ui.theme.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

// UserDao.kt
@Dao
interface UserDao {
    @Insert
    suspend fun insert(user: User)

    @Query("SELECT * FROM users WHERE username = :username AND password = :password")
    suspend fun getUser(username: String, password: String): User?

    @Query("SELECT * FROM users WHERE username = :username")
    suspend fun getUserByUsername(username: String): User?
}