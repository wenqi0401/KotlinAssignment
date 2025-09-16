// app/src/main/java/com/example/myapplication/registerData/UserDao.kt
package com.example.myapplication.registerData

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Update
    suspend fun updateUser(user: UserEntity)

    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    suspend fun getUserById(userId: String): UserEntity?

    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    fun observeUserById(userId: String): Flow<UserEntity?>

    @Query("SELECT * FROM users WHERE phoneNumber = :phoneNumber LIMIT 1")
    suspend fun getUserByPhone(phoneNumber: String): UserEntity?

    @Query("UPDATE users SET address = :address WHERE id = :userId")
    suspend fun updateUserAddress(userId: String, address: String)

    @Query("UPDATE users SET password = :password WHERE id = :userId")
    suspend fun updateUserPassword(userId: String, password: String)

    @Query("UPDATE users SET name = :name WHERE id = :userId")
    suspend fun updateUserName(userId: String, name: String)

    @Query("UPDATE users SET gender = :gender WHERE id = :userId")
    suspend fun updateUserGender(userId: String, gender: String)

    @Query("UPDATE users SET profilePicturePath = :path WHERE id = :userId")
    suspend fun updateUserProfilePicture(userId: String, path: String?)
}
