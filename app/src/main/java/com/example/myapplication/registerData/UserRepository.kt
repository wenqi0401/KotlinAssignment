package com.example.myapplication.registerData

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class UserRepository {
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val usersCollection = db.collection("users")

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
        val documentRef = usersCollection.document() // Auto-generate ID
        val userWithId = user.copy(id = documentRef.id)

        documentRef.set(userWithId.toMap()).await()
        return documentRef.id
    }

    suspend fun updateUser(user: User) {
        require(user.id.isNotEmpty()) { "User must have an ID to update" }
        usersCollection.document(user.id).update(user.toMap()).await()
    }

    suspend fun getUserByCredentials(phoneNumber: String, password: String): User? {
        val query = usersCollection
            .whereEqualTo("phoneNumber", phoneNumber)
            .whereEqualTo("password", password)
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

}