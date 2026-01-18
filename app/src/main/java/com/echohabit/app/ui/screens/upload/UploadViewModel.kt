package com.echohabit.app.ui.screens.upload

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.echohabit.app.data.model.Result
import com.echohabit.app.data.repository.AuthRepository
import com.echohabit.app.data.repository.LocalActivityRepository
import com.echohabit.app.domain.usecase.CalculateCO2UseCase
import com.echohabit.app.util.Constants
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class UploadViewModel(
    private val authRepository: AuthRepository,
    private val localActivityRepository: LocalActivityRepository, // USE LOCAL!
    private val calculateCO2UseCase: CalculateCO2UseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<UploadUiState>(UploadUiState.Initial)
    val uiState = _uiState.asStateFlow()

    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory = _selectedCategory.asStateFlow()

    private val _selectedActivityType = MutableStateFlow<String?>(null)
    val selectedActivityType = _selectedActivityType.asStateFlow()

    private val _photoUri = MutableStateFlow<Uri?>(null)
    val photoUri = _photoUri.asStateFlow()

    private val _caption = MutableStateFlow("")
    val caption = _caption.asStateFlow()

    sealed class UploadUiState {
        object Initial : UploadUiState()
        object Loading : UploadUiState()
        data class Success(val activityId: Long) : UploadUiState()
        data class Error(val message: String) : UploadUiState()
    }

    /**
     * Select category
     */
    fun selectCategory(category: String) {
        _selectedCategory.value = category
        _selectedActivityType.value = null // Reset activity type
    }

    /**
     * Select activity type
     */
    fun selectActivityType(activityType: String) {
        _selectedActivityType.value = activityType
    }

    /**
     * Set photo URI
     */
    fun setPhotoUri(uri: Uri?) {
        _photoUri.value = uri
    }

    /**
     * Update caption
     */
    fun updateCaption(text: String) {
        _caption.value = text
    }

    /**
     * Get available activity types for selected category
     */
    fun getActivityTypesForCategory(category: String): List<ActivityTypeOption> {
        return when (category) {
            Constants.CATEGORY_MOVE_GREEN -> listOf(
                ActivityTypeOption(Constants.ActivityType.BIKE, "Biked", "🚴"),
                ActivityTypeOption(Constants.ActivityType.WALK, "Walked", "🚶"),
                ActivityTypeOption(Constants.ActivityType.PUBLIC_TRANSPORT, "Public Transport", "🚌")
            )
            Constants.CATEGORY_EAT_CLEAN -> listOf(
                ActivityTypeOption(Constants.ActivityType.VEGAN_MEAL, "Vegan Meal", "🥗"),
                ActivityTypeOption(Constants.ActivityType.VEGETARIAN_MEAL, "Vegetarian Meal", "🥙"),
                ActivityTypeOption(Constants.ActivityType.LOCAL_FOOD, "Local Food", "🍽️")
            )
            Constants.CATEGORY_CUT_WASTE -> listOf(
                ActivityTypeOption(Constants.ActivityType.USE_TUMBLER, "Used Tumbler", "🥤"),
                ActivityTypeOption(Constants.ActivityType.TOTE_BAG, "Tote Bag", "🛍️"),
                ActivityTypeOption(Constants.ActivityType.NO_PLASTIC_STRAW, "No Plastic Straw", "🚫"),
                ActivityTypeOption(Constants.ActivityType.RECYCLING, "Recycled", "♻️")
            )
            Constants.CATEGORY_SAVE_ENERGY -> listOf(
                ActivityTypeOption(Constants.ActivityType.UNPLUG_DEVICES, "Unplugged Devices", "🔌"),
                ActivityTypeOption(Constants.ActivityType.LED_LIGHTS, "LED Lights", "💡"),
                ActivityTypeOption(Constants.ActivityType.AC_OFF, "AC Off", "❄️")
            )
            else -> emptyList()
        }
    }

    /**
     * Upload activity - LOCAL STORAGE ONLY!
     */
    fun uploadActivity() {
        viewModelScope.launch {
            val userId = authRepository.getCurrentUserId()
            val category = _selectedCategory.value
            val activityType = _selectedActivityType.value
            val uri = _photoUri.value

            // Validation
            if (userId == null) {
                _uiState.value = UploadUiState.Error("User not logged in")
                return@launch
            }

            if (category == null) {
                _uiState.value = UploadUiState.Error("Please select a category")
                return@launch
            }

            if (activityType == null) {
                _uiState.value = UploadUiState.Error("Please select an activity type")
                return@launch
            }

            if (uri == null) {
                _uiState.value = UploadUiState.Error("Please add a photo")
                return@launch
            }

            _uiState.value = UploadUiState.Loading

            // Calculate CO2 and points
            val co2Data = calculateCO2UseCase(activityType)

            // Save to LOCAL DATABASE
            when (val result = localActivityRepository.createActivity(
                userId = userId,
                category = category,
                activityType = activityType,
                photoUri = uri,
                caption = _caption.value,
                points = co2Data.points,
                co2SavedKg = co2Data.co2SavedKg
            )) {
                is Result.Success -> {
                    _uiState.value = UploadUiState.Success(result.data)
                }
                is Result.Error -> {
                    _uiState.value = UploadUiState.Error(
                        result.exception.message ?: "Upload failed"
                    )
                }
                else -> {
                    _uiState.value = UploadUiState.Error("Unknown error")
                }
            }
        }
    }

    /**
     * Reset state
     */
    fun resetState() {
        _uiState.value = UploadUiState.Initial
        _selectedCategory.value = null
        _selectedActivityType.value = null
        _photoUri.value = null
        _caption.value = ""
    }

    data class ActivityTypeOption(
        val value: String,
        val label: String,
        val emoji: String
    )
}