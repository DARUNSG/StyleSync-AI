package com.example.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.SavedOutfitEntity
import com.example.data.repository.OutfitPlannerRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FavoritesViewModel(
    private val outfitPlannerRepository: OutfitPlannerRepository
) : ViewModel() {

    val savedOutfits: StateFlow<List<SavedOutfitEntity>> = outfitPlannerRepository.allSavedOutfits
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun deleteOutfit(id: Long) {
        viewModelScope.launch {
            outfitPlannerRepository.deleteOutfit(id)
        }
    }

    fun toggleFavorite(id: Long) {
        viewModelScope.launch {
            outfitPlannerRepository.toggleFavorite(id)
        }
    }
}
