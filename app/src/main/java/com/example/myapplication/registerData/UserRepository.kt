package com.example.myapplication.registerData

class UserRepository(private val userDao: UserDao) {

    suspend fun insertUser(user: User) {
        userDao.insertUser(user)
    }

    suspend fun getUserByCredentials(username: String, password: String): User? {
        return userDao.getUserByUsernameAndPassword(username, password)
    }

    suspend fun checkIfUserExists(username: String): User? {
        return userDao.getUserByUsername(username)
    }
}