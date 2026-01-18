package com.echohabit.app.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.echohabit.app.data.model.Result
import com.echohabit.app.data.model.User
import com.echohabit.app.data.repository.AuthRepository
import com.echohabit.app.data.repository.UserRepository
import com.echohabit.app.util.Constants
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Loading)
    val uiState = _uiState.asStateFlow()

    sealed class ProfileUiState {
        object Loading : ProfileUiState()
        data class Success(
            val user: User,
            val badges: List<BadgeDisplay>
        ) : ProfileUiState()
        data class Error(val message: String) : ProfileUiState()
    }

    data class BadgeDisplay(
        val id: String,
        val name: String,
        val emoji: String,
        val description: String,
        val isUnlocked: Boolean
    )

    init {
        loadProfile()
    }

    /**
     * Load user profile data
     */
    fun loadProfile() {
        viewModelScope.launch {
            _uiState.value = ProfileUiState.Loading

            val userId = authRepository.getCurrentUserId()
            if (userId == null) {
                _uiState.value = ProfileUiState.Error("User not logged in")
                return@launch
            }

            when (val result = userRepository.getUser(userId)) {
                is Result.Success -> {
                    val user = result.data
                    val badges = getBadgesDisplay(user)
                    _uiState.value = ProfileUiState.Success(user, badges)
                }
                is Result.Error -> {
                    _uiState.value = ProfileUiState.Error(
                        result.exception.message ?: "Failed to load profile"
                    )
                }
                else -> {
                    _uiState.value = ProfileUiState.Error("Unknown error")
                }
            }
        }
    }

    /**
     * Get badges display with unlock status
     */
    private fun getBadgesDisplay(user: User): List<BadgeDisplay> {
        return Constants.BADGES.map { badge ->
            BadgeDisplay(
                id = badge.id,
                name = badge.name,
                emoji = badge.emoji,
                description = badge.description,
                isUnlocked = user.badges.contains(badge.id)
            )
        }
    }

    /**
     * Sign out
     */
    fun signOut() {
        authRepository.signOut()
    }
}