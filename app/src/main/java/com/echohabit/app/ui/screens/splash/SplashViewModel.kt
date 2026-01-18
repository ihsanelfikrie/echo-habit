package com.echohabit.app.ui.screens.splash

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.echohabit.app.data.repository.AuthRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SplashViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    companion object {
        private const val TAG = "SplashViewModel"
    }

    private val _navigationState = MutableStateFlow<NavigationState>(NavigationState.Loading)
    val navigationState = _navigationState.asStateFlow()

    sealed class NavigationState {
        object Loading : NavigationState()
        object NavigateToHome : NavigationState()
        object NavigateToLogin : NavigationState()
    }

    init {
        Log.d(TAG, "=== SplashViewModel INIT START ===")
        try {
            checkAuthState()
        } catch (e: Exception) {
            Log.e(TAG, "❌ CRITICAL ERROR in init", e)
            e.printStackTrace()
            // Force navigate to login on error
            _navigationState.value = NavigationState.NavigateToLogin
        }
    }

    private fun checkAuthState() {
        viewModelScope.launch {
            try {
                Log.d(TAG, "⏳ Starting splash delay (2s)...")
                delay(2000)
                Log.d(TAG, "✅ Splash delay complete")

                Log.d(TAG, "🔍 Checking auth state...")

                val isLoggedIn = try {
                    val result = authRepository.isLoggedIn()
                    Log.d(TAG, "📌 isLoggedIn() returned: $result")
                    result
                } catch (e: Exception) {
                    Log.e(TAG, "❌ Error checking isLoggedIn", e)
                    false
                }

                val userId = try {
                    val id = authRepository.getCurrentUserId()
                    Log.d(TAG, "📌 getCurrentUserId() returned: $id")
                    id
                } catch (e: Exception) {
                    Log.e(TAG, "❌ Error getting userId", e)
                    null
                }

                Log.d(TAG, "📊 Auth State Summary:")
                Log.d(TAG, "   - isLoggedIn: $isLoggedIn")
                Log.d(TAG, "   - userId: $userId")

                if (isLoggedIn && userId != null) {
                    Log.d(TAG, "✅ User is logged in")
                    Log.d(TAG, "🚀 → NAVIGATING TO HOME")
                    _navigationState.value = NavigationState.NavigateToHome
                    Log.d(TAG, "✅ Navigation state set to HOME")
                } else {
                    Log.d(TAG, "⚠️ User is NOT logged in")

                    if (isLoggedIn && userId == null) {
                        Log.w(TAG, "⚠️ Inconsistent state: logged in but no userId")
                        Log.d(TAG, "🔄 Signing out to fix state...")
                        try {
                            authRepository.signOut()
                            Log.d(TAG, "✅ Signed out successfully")
                        } catch (e: Exception) {
                            Log.e(TAG, "❌ Error signing out", e)
                        }
                    }

                    Log.d(TAG, "🚀 → NAVIGATING TO LOGIN")
                    _navigationState.value = NavigationState.NavigateToLogin
                    Log.d(TAG, "✅ Navigation state set to LOGIN")
                }

                Log.d(TAG, "📌 Final navigationState value: ${_navigationState.value}")

            } catch (e: Exception) {
                Log.e(TAG, "❌ CRITICAL ERROR in checkAuthState", e)
                e.printStackTrace()
                Log.d(TAG, "🚨 Forcing navigation to LOGIN due to error")
                _navigationState.value = NavigationState.NavigateToLogin
            } finally {
                Log.d(TAG, "=== SplashViewModel checkAuthState COMPLETE ===")
            }
        }
    }
}