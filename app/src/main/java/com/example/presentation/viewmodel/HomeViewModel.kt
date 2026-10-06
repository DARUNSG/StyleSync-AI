package com.example.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.SavedOutfitEntity
import com.example.data.repository.OutfitPlannerRepository
import com.example.data.repository.UserPreferencesRepository
import com.example.data.repository.WardrobeRepository
import com.example.data.repository.WeatherRepository
import com.example.domain.model.ClothingItem
import com.example.domain.model.WeatherInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class HomeUiState(
    val userName: String = "",
    val userGender: String = "",
    val weather: WeatherInfo = WeatherInfo(24.0, "Sunny", 24.5, 45, 0.0, 10.0, "Detecting...", true),
    val isLoadingWeather: Boolean = false,
    val wardrobeCount: Int = 0,
    val topsCount: Int = 0,
    val bottomsCount: Int = 0,
    val shoesCount: Int = 0,
    val layersCount: Int = 0,
    val accessoriesCount: Int = 0
)

class HomeViewModel(
    private val wardrobeRepository: WardrobeRepository,
    private val weatherRepository: WeatherRepository,
    private val outfitPlannerRepository: OutfitPlannerRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        HomeUiState(
            userName = userPreferencesRepository.userSession.value.name,
            userGender = userPreferencesRepository.userSession.value.gender
        )
    )
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    val wardrobeItems: StateFlow<List<ClothingItem>> = wardrobeRepository.allClothing
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val recentOutfits: StateFlow<List<SavedOutfitEntity>> = outfitPlannerRepository.allSavedOutfits
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            wardrobeRepository.seedSampleWardrobeIfEmpty()
            refreshWeather()
        }

        viewModelScope.launch {
            userPreferencesRepository.userSession.collect { session ->
                _uiState.value = _uiState.value.copy(
                    userName = session.name,
                    userGender = session.gender
                )
            }
        }

        viewModelScope.launch {
            wardrobeRepository.allClothing.collect { items ->
                _uiState.value = _uiState.value.copy(
                    wardrobeCount = items.size,
                    topsCount = items.count { it.category in listOf("T-Shirts", "Shirts", "Hoodies") },
                    bottomsCount = items.count { it.category in listOf("Jeans", "Trousers", "Shorts") },
                    shoesCount = items.count { it.category == "Shoes" },
                    layersCount = items.count { it.category == "Jackets" },
                    accessoriesCount = items.count { it.category == "Accessories" }
                )
            }
        }
    }

    fun refreshWeather() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoadingWeather = true)
            val weather = weatherRepository.getCurrentLocationWeather()
            _uiState.value = _uiState.value.copy(
                weather = weather,
                isLoadingWeather = false
            )
        }
    }

    fun setCustomLocation(city: String, lat: Double, lon: Double) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoadingWeather = true)
            val weather = weatherRepository.getWeatherForLocation(lat, lon, city)
            _uiState.value = _uiState.value.copy(
                weather = weather,
                isLoadingWeather = false
            )
        }
    }

    fun loadSampleWardrobe() {
        viewModelScope.launch {
            wardrobeRepository.seedSampleWardrobeIfEmpty()
        }
    }
}
