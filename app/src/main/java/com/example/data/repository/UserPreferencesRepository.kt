package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class UserSession(
    val name: String = "",
    val gender: String = "",
    val hasCompletedOnboarding: Boolean = false
)

class UserPreferencesRepository(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("stylesync_user_prefs", Context.MODE_PRIVATE)

    private val _userSession = MutableStateFlow(
        UserSession(
            name = prefs.getString("user_name", "") ?: "",
            gender = prefs.getString("user_gender", "") ?: "",
            hasCompletedOnboarding = prefs.getBoolean("has_completed_onboarding", false)
        )
    )
    val userSession: StateFlow<UserSession> = _userSession.asStateFlow()

    fun saveUserOnboarding(name: String, gender: String) {
        prefs.edit()
            .putString("user_name", name.trim())
            .putString("user_gender", gender)
            .putBoolean("has_completed_onboarding", true)
            .apply()

        _userSession.value = UserSession(
            name = name.trim(),
            gender = gender,
            hasCompletedOnboarding = true
        )
    }

    fun updateProfile(name: String, gender: String) {
        prefs.edit()
            .putString("user_name", name.trim())
            .putString("user_gender", gender)
            .apply()

        _userSession.value = _userSession.value.copy(
            name = name.trim(),
            gender = gender
        )
    }

    fun resetOnboarding() {
        prefs.edit().clear().apply()
        _userSession.value = UserSession()
    }
}
