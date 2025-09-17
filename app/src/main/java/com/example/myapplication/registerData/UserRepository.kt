package com.example.myapplication.registerData

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.tasks.await

class UserRepository {
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val usersCollection = db.collection("users")
    private val TAG = "UserRepository"

    suspend fun getAllUsers(): List<User> {
        return try {
            val snapshot = db.collection("users").get().await()
            snapshot.documents.mapNotNull { doc ->
                doc.toObject(User::class.java)?.copy(id = doc.id)
            }
        } catch (e: Exception) {
            Log.e("UserRepository", "Failed to get all users", e)
            emptyList()
        }
    }


    fun listenUserById(
        userId: String,
        onUser: (User?) -> Unit,
        onError: (FirebaseFirestoreException) -> Unit = {}
    ): ListenerRegistration {
        val docRef = usersCollection.document(userId)
        return docRef.addSnapshotListener { snap, e ->
            if (e != null) {
                onError(e)
                return@addSnapshotListener
            }
            if (snap != null && snap.exists()) {
                val data = snap.data
                onUser(if (data != null) User.fromMap(data) else null)
            } else {
                onUser(null)
            }
        }
    }

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
    suspend fun updateUserAddress(userId: String, address: String) {
        usersCollection.document(userId).update("address", address).await()
    }

    suspend fun checkUserExists(phoneNumber: String): Boolean {
        return try {
            // Use your existing getUserByPhone method
            val user = getUserByPhone(phoneNumber)
            user != null
        } catch (e: Exception) {
            Log.e(TAG, "Error checking if user exists: ${e.message}")
            false
        }
    }

}