package com.example.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ClothingDao {
    @Query("SELECT * FROM clothing_items ORDER BY dateAdded DESC")
    fun getAllClothing(): Flow<List<ClothingItemEntity>>

    @Query("SELECT * FROM clothing_items WHERE category = :category ORDER BY dateAdded DESC")
    fun getClothingByCategory(category: String): Flow<List<ClothingItemEntity>>

    @Query("SELECT * FROM clothing_items WHERE isFavorite = 1 ORDER BY dateAdded DESC")
    fun getFavoriteClothing(): Flow<List<ClothingItemEntity>>

    @Query("SELECT * FROM clothing_items WHERE id = :id LIMIT 1")
    suspend fun getClothingById(id: Long): ClothingItemEntity?

    @Query("SELECT * FROM clothing_items WHERE id IN (:ids)")
    suspend fun getClothingByIds(ids: List<Long>): List<ClothingItemEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClothing(item: ClothingItemEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllClothing(items: List<ClothingItemEntity>): List<Long>

    @Update
    suspend fun updateClothing(item: ClothingItemEntity)

    @Delete
    suspend fun deleteClothing(item: ClothingItemEntity)

    @Query("DELETE FROM clothing_items WHERE id = :id")
    suspend fun deleteClothingById(id: Long)

    @Query("UPDATE clothing_items SET isFavorite = NOT isFavorite WHERE id = :id")
    suspend fun toggleFavorite(id: Long)

    @Query("SELECT COUNT(*) FROM clothing_items")
    suspend fun getClothingCount(): Int
}

@Dao
interface OutfitDao {
    @Query("SELECT * FROM saved_outfits ORDER BY dateCreated DESC")
    fun getAllSavedOutfits(): Flow<List<SavedOutfitEntity>>

    @Query("SELECT * FROM saved_outfits WHERE isFavorite = 1 ORDER BY dateCreated DESC")
    fun getFavoriteOutfits(): Flow<List<SavedOutfitEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOutfit(outfit: SavedOutfitEntity): Long

    @Delete
    suspend fun deleteOutfit(outfit: SavedOutfitEntity)

    @Query("DELETE FROM saved_outfits WHERE id = :id")
    suspend fun deleteOutfitById(id: Long)

    @Query("UPDATE saved_outfits SET isFavorite = NOT isFavorite WHERE id = :id")
    suspend fun toggleFavoriteOutfit(id: Long)
}
