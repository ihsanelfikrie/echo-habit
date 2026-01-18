package com.echohabit.app.ui.screens.cardgen

import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.echohabit.app.data.model.Result
import com.echohabit.app.data.repository.PhotoRepository
import com.echohabit.app.domain.usecase.GenerateCardUseCase
import com.echohabit.app.domain.usecase.ShareCardUseCase
import com.echohabit.app.util.Constants
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CardViewModel(
    private val generateCardUseCase: GenerateCardUseCase,
    private val shareCardUseCase: ShareCardUseCase,
    private val photoRepository: PhotoRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<CardUiState>(CardUiState.Initial)
    val uiState = _uiState.asStateFlow()

    private val _selectedStyle = MutableStateFlow(Constants.CARD_STYLE_GLASSMORPHISM)
    val selectedStyle = _selectedStyle.asStateFlow()

    private var currentBitmap: Bitmap? = null

    sealed class CardUiState {
        object Initial : CardUiState()
        object Generating : CardUiState()
        data class Success(val bitmap: Bitmap) : CardUiState()
        data class Error(val message: String) : CardUiState()
        object Sharing : CardUiState()
        object ShareSuccess : CardUiState()
    }

    /**
     * Generate card with current style
     */
    fun generateCard(
        photoUri: Uri,
        activityType: String,
        co2SavedKg: Double,
        streak: Int,
        username: String
    ) {
        viewModelScope.launch {
            _uiState.value = CardUiState.Generating

            val request = GenerateCardUseCase.CardRequest(
                photoUri = photoUri,
                activityType = activityType,
                co2SavedKg = co2SavedKg,
                streak = streak,
                username = username,
                cardStyle = _selectedStyle.value
            )

            when (val result = generateCardUseCase(request)) {
                is Result.Success -> {
                    currentBitmap = result.data
                    _uiState.value = CardUiState.Success(result.data)
                }
                is Result.Error -> {
                    _uiState.value = CardUiState.Error(
                        result.exception.message ?: "Failed to generate card"
                    )
                }
                else -> {
                    _uiState.value = CardUiState.Error("Unknown error")
                }
            }
        }
    }

    /**
     * Change card style and regenerate
     */
    fun changeStyle(
        newStyle: String,
        photoUri: Uri,
        activityType: String,
        co2SavedKg: Double,
        streak: Int,
        username: String
    ) {
        _selectedStyle.value = newStyle
        generateCard(photoUri, activityType, co2SavedKg, streak, username)
    }

    /**
     * Share card to platform
     */
    fun shareCard(platform: String) {
        viewModelScope.launch {
            val bitmap = currentBitmap
            if (bitmap == null) {
                _uiState.value = CardUiState.Error("No card to share")
                return@launch
            }

            _uiState.value = CardUiState.Sharing

            when (val result = shareCardUseCase(bitmap, platform)) {
                is Result.Success -> {
                    _uiState.value = CardUiState.ShareSuccess
                    // Reset to success state after brief delay
                    kotlinx.coroutines.delay(1000)
                    _uiState.value = CardUiState.Success(bitmap)
                }
                is Result.Error -> {
                    _uiState.value = CardUiState.Error(
                        result.exception.message ?: "Failed to share"
                    )
                    // Reset to success state
                    kotlinx.coroutines.delay(2000)
                    _uiState.value = CardUiState.Success(bitmap)
                }
                else -> {
                    _uiState.value = CardUiState.Error("Unknown error")
                }
            }
        }
    }

    /**
     * Save card to Firebase Storage (DISABLED - requires Blaze plan)
     * For testing, cards are only saved in memory and shared directly
     */
    fun saveCardToStorage(userId: String) {
        // Disabled untuk testing - Storage butuh billing account
        // Card tetap bisa di-share tanpa upload ke Storage
        viewModelScope.launch {
            // Skip upload, card sudah di memory
            // Sharing akan menggunakan cache lokal
        }
    }

    /**
     * Reset state
     */
    fun resetState() {
        _uiState.value = CardUiState.Initial
        currentBitmap = null
    }
}