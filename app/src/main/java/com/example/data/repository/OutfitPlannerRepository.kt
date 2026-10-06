package com.example.data.repository

import com.example.data.local.OutfitDao
import com.example.data.local.SavedOutfitEntity
import com.example.data.remote.GeminiFashionClient
import com.example.domain.model.ClothingItem
import com.example.domain.model.Occasion
import com.example.domain.model.RecommendedOutfit
import com.example.domain.model.WeatherInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class OutfitPlannerRepository(
    private val outfitDao: OutfitDao,
    private val wardrobeRepository: WardrobeRepository,
    private val geminiClient: GeminiFashionClient
) {
    val allSavedOutfits: Flow<List<SavedOutfitEntity>> = outfitDao.getAllSavedOutfits()
    val favoriteOutfits: Flow<List<SavedOutfitEntity>> = outfitDao.getFavoriteOutfits()

    suspend fun generateOutfit(
        wardrobe: List<ClothingItem>,
        occasion: Occasion,
        weather: WeatherInfo,
        preferredStyle: String
    ): Result<RecommendedOutfit> = withContext(Dispatchers.IO) {
        val result = geminiClient.generateOutfitRecommendation(
            wardrobeItems = wardrobe,
            occasion = occasion.title,
            temperature = weather.temperatureCelsius,
            weatherCondition = weather.condition,
            preferredStyle = preferredStyle
        )

        result.map { geminiOut ->
            val selectedItems = wardrobe.filter { it.id in geminiOut.selectedItemIds }
            val finalItems = if (selectedItems.isNotEmpty()) selectedItems else wardrobe.take(3)

            RecommendedOutfit(
                id = System.currentTimeMillis(),
                title = geminiOut.title,
                occasion = occasion,
                items = finalItems,
                style = geminiOut.style,
                weatherDescription = "${weather.temperatureCelsius.toInt()}°C, ${weather.condition}",
                temperature = weather.temperatureCelsius,
                overallScore = geminiOut.overallScore,
                colorMatchScore = geminiOut.colorMatchScore,
                weatherMatchScore = geminiOut.weatherMatchScore,
                occasionMatchScore = geminiOut.occasionMatchScore,
                styleMatchScore = geminiOut.styleMatchScore,
                aiExplanation = geminiOut.aiExplanation,
                isSaved = false
            )
        }
    }

    suspend fun saveOutfit(outfit: RecommendedOutfit): Long = withContext(Dispatchers.IO) {
        val entity = SavedOutfitEntity(
            title = outfit.title,
            occasion = outfit.occasion.title,
            itemIds = outfit.items.map { it.id }.joinToString(","),
            itemNames = outfit.items.map { it.name }.joinToString(", "),
            imageUris = outfit.items.map { it.imageUri }.joinToString(","),
            style = outfit.style,
            weatherDescription = outfit.weatherDescription,
            temperature = outfit.temperature,
            score = outfit.overallScore,
            colorMatchScore = outfit.colorMatchScore,
            weatherMatchScore = outfit.weatherMatchScore,
            occasionMatchScore = outfit.occasionMatchScore,
            styleMatchScore = outfit.styleMatchScore,
            aiExplanation = outfit.aiExplanation,
            dateCreated = System.currentTimeMillis(),
            isFavorite = true
        )
        outfitDao.insertOutfit(entity)
    }

    suspend fun deleteOutfit(id: Long) = withContext(Dispatchers.IO) {
        outfitDao.deleteOutfitById(id)
    }

    suspend fun toggleFavorite(id: Long) = withContext(Dispatchers.IO) {
        outfitDao.toggleFavoriteOutfit(id)
    }
}
