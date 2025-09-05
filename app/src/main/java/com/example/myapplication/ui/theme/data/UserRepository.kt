package com.example.myapplication.ui.theme.data

// UserRepository.kt
class UserRepository(private val userDao: UserDao) {
    suspend fun insert(user: User) {
        userDao.insert(user)
    }

    suspend fun getUser(username: String, password: String): User? {
        return userDao.getUser(username, password)
    }

    suspend fun getUserByUsername(username: String): User? {
        return userDao.getUserByUsername(username)
    }
}