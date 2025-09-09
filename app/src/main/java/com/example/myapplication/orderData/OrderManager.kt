package com.example.myapplication.orderData

import android.util.Log


// UserSession remains the same
object UserSession {
    var currentUserPhone: String? = null

    fun setCurrentUser(phoneNumber: String) {
        currentUserPhone = phoneNumber
        Log.d("UserSession", "User session set: $phoneNumber")
    }

    fun getCurrentUser(): String? {
        Log.d("UserSession", "Current user: $currentUserPhone")
        return currentUserPhone
    }

    fun clearSession() {
        Log.d("UserSession", "User session cleared")
        currentUserPhone = null
    }
}
