package com.example.myapplication.registerData

data class User(
    val id: String = "", // Firebase uses String IDs
    val phoneNumber: String = "",
    val password: String = "",
    val name: String = "User",
    val gender: String = "Male",
    val profilePicturePath: String? = null,
    val address : String = ""
) {
    // Helper function to convert to Map for Firestore
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "phoneNumber" to phoneNumber,
            "password" to password,
            "name" to name,
            "gender" to gender,
            "profilePicturePath" to profilePicturePath,
            "address" to address
        )
    }

    companion object {
        // Helper function to create User from Firestore document
        fun fromMap(map: Map<String, Any>): User {
            return User(
                id = map["id"] as? String ?: "",
                phoneNumber = map["phoneNumber"] as? String ?: "",
                password = map["password"] as? String ?: "",
                name = map["name"] as? String ?: "User",
                gender = map["gender"] as? String ?: "Male",
                profilePicturePath = map["profilePicturePath"] as? String,
                address = map["address"] as? String ?: ""
            )
        }
    }
}

// UI STATE remains the same
data class loginUiState(
    val phoneNumber: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isLoggedIn: Boolean = false,
    val currentUser: User? = null
)