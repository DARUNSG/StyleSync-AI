package com.example.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.remote.GeminiFashionClient
import com.example.data.repository.OutfitPlannerRepository
import com.example.data.repository.WardrobeRepository
import com.example.data.repository.WeatherRepository
import com.example.domain.model.ClothingItem
import com.example.domain.model.Occasion
import com.example.domain.model.RecommendedOutfit
import com.example.domain.model.WeatherInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface OutfitGenerationState {
    data object Idle : OutfitGenerationState
    data object Loading : OutfitGenerationState
    data class Success(val outfit: RecommendedOutfit) : OutfitGenerationState
    data class Error(val message: String) : OutfitGenerationState
}

class OutfitPlannerViewModel(
    private val wardrobeRepository: WardrobeRepository,
    private val weatherRepository: WeatherRepository,
    private val outfitPlannerRepository: OutfitPlannerRepository,
    private val geminiClient: GeminiFashionClient
) : ViewModel() {

    private val _selectedOccasion = MutableStateFlow(Occasion.COLLEGE)
    val selectedOccasion: StateFlow<Occasion> = _selectedOccasion.asStateFlow()

    private val _selectedStyle = MutableStateFlow("Casual")
    val selectedStyle: StateFlow<String> = _selectedStyle.asStateFlow()

    private val _weather = MutableStateFlow(WeatherInfo(26.0, "Sunny", 26.5, 40, 0.0, 8.0, "Current Location", true))
    val weather: StateFlow<WeatherInfo> = _weather.asStateFlow()

    private val _customTemp = MutableStateFlow(24)
    val customTemp: StateFlow<Int> = _customTemp.asStateFlow()

    private val _isCustomWeather = MutableStateFlow(false)
    val isCustomWeather: StateFlow<Boolean> = _isCustomWeather.asStateFlow()

    private val _generationState = MutableStateFlow<OutfitGenerationState>(OutfitGenerationState.Idle)
    val generationState: StateFlow<OutfitGenerationState> = _generationState.asStateFlow()

    private val _outfitSaved = MutableStateFlow(false)
    val outfitSaved: StateFlow<Boolean> = _outfitSaved.asStateFlow()

    val wardrobeItems: StateFlow<List<ClothingItem>> = wardrobeRepository.allClothing
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        loadWeather()
    }

    private fun loadWeather() {
        viewModelScope.launch {
            val live = weatherRepository.getCurrentLocationWeather()
            _weather.value = live
            _customTemp.value = live.temperatureCelsius.toInt()
        }
    }

    fun selectOccasion(occasion: Occasion) {
        _selectedOccasion.value = occasion
    }

    fun selectStyle(style: String) {
        _selectedStyle.value = style
    }

    fun setCustomTemp(temp: Int) {
        _customTemp.value = temp
        _isCustomWeather.value = true
        _weather.value = _weather.value.copy(
            temperatureCelsius = temp.toDouble(),
            apparentTemperature = temp.toDouble()
        )
    }

    fun toggleUseLiveWeather() {
        _isCustomWeather.value = false
        loadWeather()
    }

    fun setLocationCoords(lat: Double, lon: Double, name: String) {
        viewModelScope.launch {
            val w = weatherRepository.getWeatherForLocation(lat, lon, name)
            _weather.value = w
            _customTemp.value = w.temperatureCelsius.toInt()
            _isCustomWeather.value = false
        }
    }

    fun generateOutfit() {
        viewModelScope.launch {
            _generationState.value = OutfitGenerationState.Loading
            _outfitSaved.value = false

            val currentWardrobe = wardrobeItems.value
            if (currentWardrobe.isEmpty()) {
                _generationState.value = OutfitGenerationState.Error(
                    "Wardrobe is currently empty. Please add items or tap 'Load Sample Wardrobe' on the Wardrobe screen."
                )
                return@launch
            }

            val currentWeather = _weather.value
            val result = outfitPlannerRepository.generateOutfit(
                wardrobe = currentWardrobe,
                occasion = _selectedOccasion.value,
                weather = currentWeather,
                preferredStyle = _selectedStyle.value
            )

            result.onSuccess { outfit ->
                _generationState.value = OutfitGenerationState.Success(outfit)
            }.onFailure { error ->
                _generationState.value = OutfitGenerationState.Error(
                    error.message ?: "Failed to generate outfit recommendation."
                )
            }
        }
    }

    fun saveCurrentOutfit() {
        val state = _generationState.value
        if (state is OutfitGenerationState.Success) {
            viewModelScope.launch {
                outfitPlannerRepository.saveOutfit(state.outfit)
                _outfitSaved.value = true
            }
        }
    }

    fun clearResult() {
        _generationState.value = OutfitGenerationState.Idle
        _outfitSaved.value = false
    }
}
