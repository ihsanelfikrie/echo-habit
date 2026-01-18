package com.echohabit.app.ui.screens.auth

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.echohabit.app.data.model.Result
import com.echohabit.app.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    companion object {
        private const val TAG = "AuthViewModel"
    }

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Initial)
    val uiState = _uiState.asStateFlow()

    sealed class AuthUiState {
        object Initial : AuthUiState()
        object Loading : AuthUiState()
        object Success : AuthUiState()
        data class Error(val message: String) : AuthUiState()
    }

    /**
     * Sign in with Google ID Token
     */
    fun signInWithGoogle(idToken: String) {
        viewModelScope.launch {
            try {
                _uiState.value = AuthUiState.Loading
                Log.d(TAG, "Starting Google Sign-In flow...")

                when (val result = authRepository.signInWithGoogle(idToken)) {
                    is Result.Success -> {
                        Log.d(TAG, "Sign-in successful: ${result.data.username}")
                        _uiState.value = AuthUiState.Success
                    }
                    is Result.Error -> {
                        val errorMessage = result.exception.message ?: "Login failed"
                        Log.e(TAG, "Sign-in failed: $errorMessage", result.exception)
                        _uiState.value = AuthUiState.Error(errorMessage)
                    }
                    else -> {
                        Log.e(TAG, "Unknown result type from sign-in")
                        _uiState.value = AuthUiState.Error("Unknown error occurred")
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception during sign-in", e)
                _uiState.value = AuthUiState.Error(e.message ?: "An error occurred")
            }
        }
    }

    /**
     * Sign in with username only (simple mode)
     */
    fun signInWithUsername(username: String) {
        viewModelScope.launch {
            try {
                _uiState.value = AuthUiState.Loading
                Log.d(TAG, "Starting username-only sign-in for: $username")

                // Small delay for UX
                kotlinx.coroutines.delay(500)

                when (val result = authRepository.signInWithUsername(username)) {
                    is Result.Success -> {
                        Log.d(TAG, "Username sign-in successful")
                        _uiState.value = AuthUiState.Success
                    }
                    is Result.Error -> {
                        val errorMessage = result.exception.message ?: "Login failed"
                        Log.e(TAG, "Username sign-in failed: $errorMessage", result.exception)
                        _uiState.value = AuthUiState.Error(errorMessage)
                    }
                    else -> {
                        Log.e(TAG, "Unknown result type from username sign-in")
                        _uiState.value = AuthUiState.Error("Unknown error occurred")
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception during username sign-in", e)
                _uiState.value = AuthUiState.Error(e.message ?: "An error occurred")
            }
        }
    }

    /**
     * Reset state
     */
    fun resetState() {
        _uiState.value = AuthUiState.Initial
    }
}