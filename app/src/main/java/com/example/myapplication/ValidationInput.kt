package com.example.myapplication

object ValidationInput {


    // Phone number validation - only numbers, 10-15 digits (Malaysian format)
    fun isValidPhoneNumber(phoneNumber: String): Boolean {
        val phonePattern = "^[0-9]{10,15}$".toRegex()
        return phoneNumber.matches(phonePattern)
    }

    // Password validation - at least 8 characters, must contain letters and numbers
    fun isValidPassword(password: String): Boolean {
        if (password.length < 8) return false

        val hasLetter = password.any { it.isLetter() }
        val hasDigit = password.any { it.isDigit() }

        return hasLetter && hasDigit
    }

    // Get phone number error message
    fun getPhoneNumberError(phoneNumber: String): String? {
        return when {
            phoneNumber.isBlank() -> "Phone number cannot be empty"
            !phoneNumber.all { it.isDigit() } -> "Phone number can only contain numbers"
            phoneNumber.length < 10 -> "Phone number must be at least 10 digits"
            phoneNumber.length > 15 -> "Phone number cannot exceed 15 digits"
            else -> null
        }
    }

    // Get password error message
    fun getPasswordError(password: String): String? {
        return when {
            password.isBlank() -> "Password cannot be empty"
            password.length < 8 -> "Password must be at least 8 characters"
            !password.any { it.isLetter() } -> "Password must contain at least one letter"
            !password.any { it.isDigit() } -> "Password must contain at least one number"
            else -> null
        }
    }
}

