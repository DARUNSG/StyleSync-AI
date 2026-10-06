package com.example.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.remote.FirebaseFashionBridge
import com.example.data.remote.GeminiFashionClient
import com.example.data.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ProfileUiState(
    val name: String = "Fashion Explorer",
    val gender: String = "Unisex",
    val preferredStyles: List<String> = listOf("Casual", "Minimalist", "Smart Chic"),
    val preferredColors: List<String> = listOf("Cream Ivory", "Powder Blue", "Lavender", "Rosewood"),
    val size: String = "Medium (M)",
    val firebaseStatus: String = "Local Secure Storage",
    val isGeminiReady: Boolean = false
)

class ProfileViewModel(
    private val firebaseBridge: FirebaseFashionBridge,
    private val geminiClient: GeminiFashionClient,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        ProfileUiState(
            name = userPreferencesRepository.userSession.value.name.ifBlank { "Fashion Explorer" },
            gender = userPreferencesRepository.userSession.value.gender.ifBlank { "Unisex" },
            firebaseStatus = firebaseBridge.syncWardrobeStatus(),
            isGeminiReady = geminiClient.isApiKeyConfigured
        )
    )
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            userPreferencesRepository.userSession.collect { session ->
                _uiState.value = _uiState.value.copy(
                    name = session.name.ifBlank { "Fashion Explorer" },
                    gender = session.gender.ifBlank { "Unisex" }
                )
            }
        }
    }

    fun updateNameAndGender(name: String, gender: String) {
        userPreferencesRepository.updateProfile(name, gender)
    }

    fun resetOnboarding() {
        userPreferencesRepository.resetOnboarding()
    }

    fun updateStyle(style: String) {
        val current = _uiState.value.preferredStyles.toMutableList()
        if (current.contains(style)) {
            if (current.size > 1) current.remove(style)
        } else {
            current.add(style)
        }
        _uiState.value = _uiState.value.copy(preferredStyles = current)
    }

    fun updateColor(color: String) {
        val current = _uiState.value.preferredColors.toMutableList()
        if (current.contains(color)) {
            if (current.size > 1) current.remove(color)
        } else {
            current.add(color)
        }
        _uiState.value = _uiState.value.copy(preferredColors = current)
    }

    fun updateSize(size: String) {
        _uiState.value = _uiState.value.copy(size = size)
    }
}
