package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "clothing_items")
data class ClothingItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val category: String,
    val color: String,
    val pattern: String = "Solid",
    val material: String = "Cotton",
    val style: String = "Casual",
    val season: String = "All Season",
    val tags: String = "",
    val imageUri: String,
    val isFavorite: Boolean = false,
    val dateAdded: Long = System.currentTimeMillis()
)

@Entity(tableName = "saved_outfits")
data class SavedOutfitEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val occasion: String,
    val itemIds: String, // Comma-separated clothing item IDs
    val itemNames: String,
    val imageUris: String,
    val style: String,
    val weatherDescription: String,
    val temperature: Double,
    val score: Int,
    val colorMatchScore: Int,
    val weatherMatchScore: Int,
    val occasionMatchScore: Int,
    val styleMatchScore: Int,
    val aiExplanation: String,
    val dateCreated: Long = System.currentTimeMillis(),
    val isFavorite: Boolean = true
)
