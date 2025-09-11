package com.example.myapplication.registerData

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class UserRepository {
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val usersCollection = db.collection("users")
    private val TAG = "UserRepository"



    suspend fun updateUserName(userId: String, name: String) {
        usersCollection.document(userId).update("name", name).await()
    }

    suspend fun updateUserGender(userId: String, gender: String) {
        usersCollection.document(userId).update("gender", gender).await()
    }

    suspend fun updateUserProfilePicture(userId: String, profilePicturePath: String?) {
        usersCollection.document(userId).update("profilePicturePath", profilePicturePath).await()
    }
    suspend fun insertUser(user: User): String {
        return try {
            val documentRef = usersCollection.document()
            val userWithId = user.copy(id = documentRef.id)
            documentRef.set(userWithId.toMap()).await()
            Log.d(TAG, "User inserted successfully with ID: ${documentRef.id}")
            documentRef.id
        } catch (e: Exception) {
            Log.e(TAG, "Error inserting user: ${e.message}")
            throw e
        }
    }

    suspend fun getUserByCredentials(phoneNumber: String, password: String): User? {
        return try {
            val query = usersCollection
                .whereEqualTo("phoneNumber", phoneNumber)
                .whereEqualTo("password", password)
                .limit(1)
                .get()
                .await()

            if (!query.isEmpty) {
                val document = query.documents[0]
                Log.d(TAG, "User found with credentials: $phoneNumber")
                User.fromMap(document.data!!)
            } else {
                Log.d(TAG, "No user found with credentials: $phoneNumber")
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting user by credentials: ${e.message}")
            null
        }
    }

    suspend fun updateUserPassword(userId: String, newPassword: String) {
        usersCollection.document(userId).update("password", newPassword).await()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun checkIfUserExists(phoneNumber: String): User? {
        val query = usersCollection
            .whereEqualTo("phoneNumber", phoneNumber)
            .limit(1)
            .get()
            .await()

        return if (!query.isEmpty) {
            val document = query.documents[0]
            User.fromMap(document.data!!)
        } else {
            null
        }
    }

    suspend fun getUserById(userId: String): User? {
        return try {
            val document = usersCollection.document(userId).get().await()
            if (document.exists()) {
                User.fromMap(document.data!!)
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
    suspend fun getUserByPhone(phoneNumber: String): User? {
        return try {
            val query = usersCollection
                .whereEqualTo("phoneNumber", phoneNumber)
                .limit(1)
                .get()
                .await()

            if (!query.isEmpty) {
                User.fromMap(query.documents[0].data!!)
            } else null
        } catch (e: Exception) {
            Log.e(TAG, "Error getting user by phone: ${e.message}")
            null
        }
    }


}