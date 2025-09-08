package com.example.myapplication.registerData

class UserRepository(private val userDao: UserDao) {

    suspend fun insertUser(user: User) {
        userDao.insertUser(user)
    }

    suspend fun updateUser(user: User) {
        userDao.updateUser(user)
    }

    suspend fun getUserByCredentials(phoneNumber: String, password: String): User? {
        return userDao.getUserByCredentials(phoneNumber, password)
    }

    suspend fun checkIfUserExists(phoneNumber: String): User? {
        return userDao.getUserByPhoneNumber(phoneNumber)
    }

    suspend fun getUserById(userId: Int): User? {
        return userDao.getUserById(userId)
    }

    suspend fun updateUserName(userId: Int, name: String) {
        userDao.updateUserName(userId, name)
    }

    suspend fun updateUserGender(userId: Int, gender: String) {
        userDao.updateUserGender(userId, gender)
    }

    suspend fun updateUserProfilePicture(userId: Int, profilePicturePath: String?) {
        userDao.updateUserProfilePicture(userId, profilePicturePath)
    }
}