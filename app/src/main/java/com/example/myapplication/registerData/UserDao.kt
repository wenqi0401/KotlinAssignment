package com.example.myapplication.registerData

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface UserDao {

    @Insert
    suspend fun insertUser(user: User)

    @Update
    suspend fun updateUser(user: User)

    @Query("SELECT * FROM users WHERE phoneNumber = :phoneNumber AND password = :password LIMIT 1")
    suspend fun getUserByCredentials(phoneNumber: String, password: String): User?

    @Query("SELECT * FROM users WHERE phoneNumber = :phoneNumber LIMIT 1")
    suspend fun getUserByPhoneNumber(phoneNumber: String): User?

    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    suspend fun getUserById(userId: Int): User?

    @Query("UPDATE users SET name = :name WHERE id = :userId")
    suspend fun updateUserName(userId: Int, name: String)

    @Query("UPDATE users SET gender = :gender WHERE id = :userId")
    suspend fun updateUserGender(userId: Int, gender: String)

    @Query("UPDATE users SET profilePicturePath = :profilePicturePath WHERE id = :userId")
    suspend fun updateUserProfilePicture(userId: Int, profilePicturePath: String?)
}