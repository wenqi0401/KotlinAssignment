// UserRepository.kt
package com.example.myapplication

class UserRepository(private val userDao: UserDao) {

    suspend fun insertUser(user: loginUiState) {
        userDao.insertUser(user)
    }

    suspend fun getAllUsers(): List<loginUiState> {
        return userDao.getAllUsers()
    }

    suspend fun validateUser(username: String, password: String): Boolean {
        val user = userDao.getUserByCredentials(username, password)
        return user != null
    }

    suspend fun isUsernameExists(username: String): Boolean {
        val user = userDao.getUserByUsername(username)
        return user != null
    }
}