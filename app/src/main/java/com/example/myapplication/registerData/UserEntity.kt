package com.example.myapplication.registerData

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val phoneNumber: String,
    val password: String,
    val name: String,
    val gender: String,
    val profilePicturePath: String?,
    val address: String ,
) {
    fun toUser(): User {
        return User(
            id = id,
            phoneNumber = phoneNumber,
            password = password,
            name = name,
            gender = gender,
            profilePicturePath = profilePicturePath,
            address = address
        )
    }

    companion object {
        fun fromUser(user: User): UserEntity {
            return UserEntity(
                id = user.id,
                phoneNumber = user.phoneNumber,
                password = user.password,
                name = user.name,
                gender = user.gender,
                profilePicturePath = user.profilePicturePath,
                address = user.address
            )
        }
    }
}