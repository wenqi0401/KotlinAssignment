package com.example.myapplication.registerData

import androidx.lifecycle.ViewModel

// UserViewModel.kt
class UserViewModel(
    private val userRepository: UserRepository,
    private val userDao: UserDao
) : ViewModel() {

    suspend fun syncUserFromFirebaseToRoom(user: User) {
        userDao.insertUser(UserEntity.fromUser(user))
    }

    suspend fun updateUserAddress(userId: String, newAddress: String) {
        userRepository.updateUserAddress(userId, newAddress)
        userDao.updateUserAddress(userId, newAddress)
    }
}
