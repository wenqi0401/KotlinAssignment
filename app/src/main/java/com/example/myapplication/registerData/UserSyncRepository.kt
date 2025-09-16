package com.example.myapplication.registerData

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class UserSyncRepository(
    private val remote: UserRepository,
    private val userDao: UserDao
) {
    private val tag = "UserSyncRepository"
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private var registration: ListenerRegistration? = null

    suspend fun seedLocalFromRemote(userId: String) {
        val remoteUser = remote.getUserById(userId)
        if (remoteUser != null) {
            userDao.insertUser(UserEntity.fromUser(remoteUser))
        }
    }

    fun observeUser(userId: String): Flow<User?> {
        return userDao.observeUserById(userId).map { it?.toUser() }
    }

    fun startSync(userId: String) {
        stopSync()
        registration = remote.listenUserById(
            userId = userId,
            onUser = { user ->
                if (user != null) {
                    scope.launch {
                        userDao.insertUser(UserEntity.fromUser(user))
                    }
                }
            },
            onError = { e: FirebaseFirestoreException ->
                Log.e(tag, "Snapshot listener error", e)
            }
        )
    }

    fun stopSync() {
        registration?.remove()
        registration = null
        scope.coroutineContext.cancelChildren()
    }

    // Option A: write-through (remote then local) — what you have now.
    // Option B: write-remote-only and let snapshot update Room (avoid double writes).
    // Below keeps your write-through behavior.
    suspend fun syncUserFromFirebaseToRoom(user: User) {
        userDao.insertUser(UserEntity.fromUser(user))
    }

    suspend fun updateUserAddress(userId: String, newAddress: String) {
        remote.updateUserAddress(userId, newAddress)
        userDao.updateUserAddress(userId, newAddress)
    }
    suspend fun updateUserPassword(userId: String, newPassword: String) {
        remote.updateUserPassword(userId, newPassword)
        userDao.updateUserPassword(userId, newPassword)
    }

    suspend fun updateUserName(userId: String, name: String) {
        remote.updateUserName(userId, name)
        userDao.updateUserName(userId, name)
    }

    suspend fun updateUserGender(userId: String, gender: String) {
        remote.updateUserGender(userId, gender)
        userDao.updateUserGender(userId, gender)
    }

    suspend fun updateUserProfilePicture(userId: String, profilePicturePath: String?) {
        remote.updateUserProfilePicture(userId, profilePicturePath)
        userDao.updateUserProfilePicture(userId, profilePicturePath)
    }
    suspend fun syncAllUsersFromFirebaseToRoom() {
        try {
            Log.d(tag, "Starting sync of all users from Firebase to Room")
            val allFirebaseUsers = remote.getAllUsers() // You need to implement this

            allFirebaseUsers.forEach { user ->
                userDao.insertUser(UserEntity.fromUser(user))
                Log.d(tag, "Synced user to Room: ${user.name} (${user.phoneNumber})")
            }

            Log.d(tag, "Successfully synced ${allFirebaseUsers.size} users to Room")
        } catch (e: Exception) {
            Log.e(tag, "Failed to sync all users from Firebase to Room", e)
        }
    }

    suspend fun syncSpecificUserToRoom(userId: String) {
        try {
            val firebaseUser = remote.getUserById(userId)
            if (firebaseUser != null) {
                userDao.insertUser(UserEntity.fromUser(firebaseUser))
                Log.d(tag, "Synced specific user to Room: ${firebaseUser.name}")
            }
        } catch (e: Exception) {
            Log.e(tag, "Failed to sync user $userId to Room", e)
        }
    }

}
