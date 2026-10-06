package com.example.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.di.AppContainer

class AppViewModelFactory(
    private val container: AppContainer
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> {
                HomeViewModel(
                    wardrobeRepository = container.wardrobeRepository,
                    weatherRepository = container.weatherRepository,
                    outfitPlannerRepository = container.outfitPlannerRepository,
                    userPreferencesRepository = container.userPreferencesRepository
                ) as T
            }
            modelClass.isAssignableFrom(WardrobeViewModel::class.java) -> {
                WardrobeViewModel(
                    wardrobeRepository = container.wardrobeRepository
                ) as T
            }
            modelClass.isAssignableFrom(AddClothingViewModel::class.java) -> {
                AddClothingViewModel(
                    wardrobeRepository = container.wardrobeRepository,
                    geminiClient = container.geminiClient
                ) as T
            }
            modelClass.isAssignableFrom(OutfitPlannerViewModel::class.java) -> {
                OutfitPlannerViewModel(
                    wardrobeRepository = container.wardrobeRepository,
                    weatherRepository = container.weatherRepository,
                    outfitPlannerRepository = container.outfitPlannerRepository,
                    geminiClient = container.geminiClient
                ) as T
            }
            modelClass.isAssignableFrom(FavoritesViewModel::class.java) -> {
                FavoritesViewModel(
                    outfitPlannerRepository = container.outfitPlannerRepository
                ) as T
            }
            modelClass.isAssignableFrom(ProfileViewModel::class.java) -> {
                ProfileViewModel(
                    firebaseBridge = container.firebaseBridge,
                    geminiClient = container.geminiClient,
                    userPreferencesRepository = container.userPreferencesRepository
                ) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
