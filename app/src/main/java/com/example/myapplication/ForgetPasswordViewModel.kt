package com.example.myapplication

import android.app.Activity
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.registerData.UserRepository
import com.example.myapplication.registerData.UserSyncRepository
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

enum class ForgetPasswordStep {
    PHONE_INPUT,
    OTP_VERIFICATION,
    PASSWORD_RESET
}

data class ForgetPasswordUiState(
    val currentStep: ForgetPasswordStep = ForgetPasswordStep.PHONE_INPUT,
    val phoneNumber: String = "",
    val otpCode: String = "",
    val verificationId: String = "",
    val newPassword: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val passwordResetSuccess: Boolean = false
)

class ForgetPasswordViewModel(
    private val syncRepo: UserSyncRepository= com.example.myapplication.di.ServiceLocator.userSyncRepository,
) : ViewModel() {
    private val repository = UserRepository()
    private val _uiState = MutableStateFlow(ForgetPasswordUiState())
    val uiState: StateFlow<ForgetPasswordUiState> = _uiState.asStateFlow()
    private val auth = FirebaseAuth.getInstance()
    private var activity: Activity? = null

    fun setActivity(activity: Activity) {
        this.activity = activity
    }

    fun setPhoneNumber(phoneNumber: String) {
        _uiState.value = _uiState.value.copy(phoneNumber = phoneNumber)
    }

    fun setNewPassword(password: String) {
        _uiState.value = _uiState.value.copy(newPassword = password)
    }

    fun updateOTPCode(index: Int, digit: String) {
        var currentOTP = _uiState.value.otpCode.padEnd(6, ' ').toMutableList()

        if (digit.isEmpty()) {
            if (index < currentOTP.size) {
                currentOTP[index] = ' '
            }
        } else {
            if (index < 6) {
                currentOTP[index] = digit.first()
            }
        }

        val cleanOTP = currentOTP.joinToString("").replace(" ", "")
        _uiState.value = _uiState.value.copy(otpCode = cleanOTP)
    }

    private val phoneAuthCallbacks =
        object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                // Auto-verification completed
                Log.d("ForgetPasswordViewModel", "Verification completed automatically")
                val smsCode = credential.smsCode
                if (smsCode != null) {
                    _uiState.value = _uiState.value.copy(otpCode = smsCode)
                    verifyOTP()
                }
            }

            override fun onVerificationFailed(e: FirebaseException) {
                Log.e("ForgetPasswordViewModel", "Verification failed", e)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "SMS verification failed: ${e.message}"
                )
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                Log.d("ForgetPasswordViewModel", "OTP sent to ${_uiState.value.phoneNumber}")
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    verificationId = verificationId,
                    currentStep = ForgetPasswordStep.OTP_VERIFICATION,
                    otpCode = ""
                )
            }
        }

    fun sendOTP() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            try {
                // Check if user exists in your database
                val existingUser = repository.checkIfUserExists(_uiState.value.phoneNumber)
                if (existingUser == null) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Phone number not found. Please register first."
                    )
                    return@launch
                }

                // Format phone number with country code (Malaysia +6)
                val formattedPhone = "+6${_uiState.value.phoneNumber}"

                if (activity == null) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Activity not available for SMS verification"
                    )
                    return@launch
                }

                // Send OTP using Firebase Phone Authentication
                val options = PhoneAuthOptions.newBuilder(auth)
                    .setPhoneNumber(formattedPhone)
                    .setTimeout(60L, TimeUnit.SECONDS)
                    .setActivity(activity!!)
                    .setCallbacks(phoneAuthCallbacks)
                    .build()

                PhoneAuthProvider.verifyPhoneNumber(options)

            } catch (e: Exception) {
                Log.e("ForgetPasswordViewModel", "Failed to send OTP", e)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Failed to send OTP: ${e.message}"
                )
            }
        }
    }

    fun verifyOTP() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            try {
                if (_uiState.value.verificationId.isEmpty()) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Verification ID not found. Please resend OTP."
                    )
                    return@launch
                }

                // Create credential with verification ID and OTP code
                val credential = PhoneAuthProvider.getCredential(
                    _uiState.value.verificationId,
                    _uiState.value.otpCode
                )

                // Verify the credential
                auth.signInWithCredential(credential)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.d("ForgetPasswordViewModel", "OTP verification successful")
                            // Sign out immediately as we only needed verification
                            auth.signOut()

                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                currentStep = ForgetPasswordStep.PASSWORD_RESET
                            )
                        } else {
                            Log.e(
                                "ForgetPasswordViewModel",
                                "OTP verification failed",
                                task.exception
                            )
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                errorMessage = "Invalid OTP. Please try again."
                            )
                        }
                    }

            } catch (e: Exception) {
                Log.e("ForgetPasswordViewModel", "OTP verification error", e)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "OTP verification failed: ${e.message}"
                )
            }
        }
    }

    fun resetPassword() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            try {
                val user = repository.checkIfUserExists(_uiState.value.phoneNumber)
                if (user != null) {
                    // Write-through: Firebase -> Room
                    syncRepo.updateUserPassword(user.id, _uiState.value.newPassword)
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        passwordResetSuccess = true
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "User not found"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Password reset failed: ${e.message}"
                )
            }
        }
    }


    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    fun clearSuccessState() {
        _uiState.value = _uiState.value.copy(passwordResetSuccess = false)
    }

    fun validatePhoneNumber(phone: String): String? {
        return when {
            phone.isEmpty() -> "Phone number is required"
            !phone.matches(Regex("^\\d{10,15}$")) -> "Phone number must be 10-15 digits"
            else -> null
        }
    }

    fun validatePassword(password: String): String? {
        return when {
            password.isEmpty() -> "Password is required"
            password.length < 8 -> "Password must be at least 8 characters"
            !password.any { it.isDigit() } || !password.any { it.isLetter() } ->
                "Password must contain letters and numbers"

            else -> null
        }
    }

    fun validateConfirmPassword(password: String, confirmPassword: String): String? {
        return when {
            confirmPassword.isEmpty() -> "Please confirm your password"
            password != confirmPassword -> "Passwords do not match"
            else -> null
        }
    }

}

