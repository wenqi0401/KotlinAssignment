// UserRepository.kt
package com.example.myapplication

class UserRepository(private val userDao: UserDao) {

    suspend fun insertUser(user: loginUiState) {
        userDao.insertUser(user)
    }

    suspend fun getUserByCredentials(username: String, password: String): loginUiState? {
        return userDao.getUserByUsernameAndPassword(username, password)
    }
}