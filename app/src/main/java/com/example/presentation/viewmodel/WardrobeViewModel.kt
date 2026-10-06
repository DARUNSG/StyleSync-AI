package com.example.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.repository.WardrobeRepository
import com.example.domain.model.ClothingCategory
import com.example.domain.model.ClothingItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class WardrobeViewModel(
    private val wardrobeRepository: WardrobeRepository
) : ViewModel() {

    private val _selectedCategory = MutableStateFlow(ClothingCategory.ALL)
    val selectedCategory: StateFlow<ClothingCategory> = _selectedCategory.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _onlyFavorites = MutableStateFlow(false)
    val onlyFavorites: StateFlow<Boolean> = _onlyFavorites.asStateFlow()

    val filteredClothing: StateFlow<List<ClothingItem>> = combine(
        wardrobeRepository.allClothing,
        _selectedCategory,
        _searchQuery,
        _onlyFavorites
    ) { allItems, category, query, favOnly ->
        allItems.filter { item ->
            val matchesCategory = category == ClothingCategory.ALL ||
                    item.category.equals(category.displayName, ignoreCase = true)
            val matchesQuery = query.isBlank() ||
                    item.name.contains(query, ignoreCase = true) ||
                    item.color.contains(query, ignoreCase = true) ||
                    item.style.contains(query, ignoreCase = true) ||
                    item.tags.any { it.contains(query, ignoreCase = true) }
            val matchesFav = !favOnly || item.isFavorite

            matchesCategory && matchesQuery && matchesFav
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun selectCategory(category: ClothingCategory) {
        _selectedCategory.value = category
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun toggleFavoritesFilter() {
        _onlyFavorites.value = !_onlyFavorites.value
    }

    fun toggleItemFavorite(id: Long) {
        viewModelScope.launch {
            wardrobeRepository.toggleFavorite(id)
        }
    }

    fun deleteItem(id: Long) {
        viewModelScope.launch {
            wardrobeRepository.deleteClothing(id)
        }
    }

    fun populateSampleWardrobe() {
        viewModelScope.launch {
            wardrobeRepository.seedSampleWardrobeIfEmpty()
        }
    }
}
