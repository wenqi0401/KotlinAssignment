package com.example.myapplication.registerData

class UserRepository(private val userDao: UserDao) {

    suspend fun insertUser(user: User) {
        userDao.insertUser(user)
    }

    suspend fun getUserByCredentials(phoneNumber: String, password: String): User? {
        return userDao.getUserByUsernameAndPassword(phoneNumber, password)
    }

    suspend fun checkIfUserExists(phoneNumber: String): User? {
        return userDao.getUserByUsername(phoneNumber)
    }
}