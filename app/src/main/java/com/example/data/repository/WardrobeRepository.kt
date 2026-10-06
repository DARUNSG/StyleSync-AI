package com.example.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import com.example.data.local.ClothingDao
import com.example.data.local.ClothingItemEntity
import com.example.domain.model.ClothingCategory
import com.example.domain.model.ClothingItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

class WardrobeRepository(
    private val context: Context,
    private val clothingDao: ClothingDao
) {
    val allClothing: Flow<List<ClothingItem>> = clothingDao.getAllClothing().map { list ->
        list.map { it.toDomain() }
    }

    val favoriteClothing: Flow<List<ClothingItem>> = clothingDao.getFavoriteClothing().map { list ->
        list.map { it.toDomain() }
    }

    fun getClothingByCategory(category: String): Flow<List<ClothingItem>> {
        return if (category == "All" || category.isBlank()) {
            allClothing
        } else {
            clothingDao.getClothingByCategory(category).map { list ->
                list.map { it.toDomain() }
            }
        }
    }

    suspend fun getClothingById(id: Long): ClothingItem? = withContext(Dispatchers.IO) {
        clothingDao.getClothingById(id)?.toDomain()
    }

    suspend fun getClothingByIds(ids: List<Long>): List<ClothingItem> = withContext(Dispatchers.IO) {
        clothingDao.getClothingByIds(ids).map { it.toDomain() }
    }

    suspend fun addClothing(
        name: String,
        category: String,
        color: String,
        pattern: String,
        material: String,
        style: String,
        season: String,
        tags: List<String>,
        imageUri: String
    ): Long = withContext(Dispatchers.IO) {
        val entity = ClothingItemEntity(
            name = name,
            category = category,
            color = color,
            pattern = pattern,
            material = material,
            style = style,
            season = season,
            tags = tags.joinToString(","),
            imageUri = imageUri,
            isFavorite = false,
            dateAdded = System.currentTimeMillis()
        )
        clothingDao.insertClothing(entity)
    }

    suspend fun deleteClothing(id: Long) = withContext(Dispatchers.IO) {
        val item = clothingDao.getClothingById(id)
        if (item != null) {
            // Delete local file if stored internally
            try {
                if (item.imageUri.startsWith("file://") || item.imageUri.startsWith("/")) {
                    val file = File(item.imageUri.removePrefix("file://"))
                    if (file.exists()) file.delete()
                }
            } catch (_: Exception) {}
            clothingDao.deleteClothingById(id)
        }
    }

    suspend fun toggleFavorite(id: Long) = withContext(Dispatchers.IO) {
        clothingDao.toggleFavorite(id)
    }

    suspend fun saveImageToInternalStorage(uri: Uri): String = withContext(Dispatchers.IO) {
        try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()

            val imagesDir = File(context.filesDir, "wardrobe_images")
            if (!imagesDir.exists()) imagesDir.mkdirs()

            val fileName = "item_${UUID.randomUUID()}.jpg"
            val file = File(imagesDir, fileName)
            val out = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 85, out)
            out.flush()
            out.close()

            file.absolutePath
        } catch (e: Exception) {
            uri.toString()
        }
    }

    suspend fun saveBitmapToInternalStorage(bitmap: Bitmap): String = withContext(Dispatchers.IO) {
        val imagesDir = File(context.filesDir, "wardrobe_images")
        if (!imagesDir.exists()) imagesDir.mkdirs()

        val fileName = "item_${UUID.randomUUID()}.jpg"
        val file = File(imagesDir, fileName)
        val out = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 85, out)
        out.flush()
        out.close()

        file.absolutePath
    }

    suspend fun seedSampleWardrobeIfEmpty() = withContext(Dispatchers.IO) {
        if (clothingDao.getClothingCount() > 0) return@withContext

        val samples = listOf(
            ClothingItemEntity(
                name = "Classic Oxford White Shirt",
                category = "Shirts",
                color = "Pure White",
                pattern = "Solid",
                material = "Linen-Cotton",
                style = "Smart Casual",
                season = "All Season",
                tags = "Breathable,Button-down,Classic",
                imageUri = "sample_oxford_shirt"
            ),
            ClothingItemEntity(
                name = "Midnight Slim Denim Jeans",
                category = "Jeans",
                color = "Dark Navy",
                pattern = "Solid",
                material = "Stretch Denim",
                style = "Casual",
                season = "All Season",
                tags = "Comfort,Everyday,Versatile",
                imageUri = "sample_navy_jeans"
            ),
            ClothingItemEntity(
                name = "Tailored Charcoal Blazer",
                category = "Jackets",
                color = "Charcoal Grey",
                pattern = "Subtle Texture",
                material = "Wool Blend",
                style = "Formal",
                season = "Winter / Fall",
                tags = "Structured,Executive,Lapel",
                imageUri = "sample_charcoal_blazer"
            ),
            ClothingItemEntity(
                name = "Cream Minimalist Tee",
                category = "T-Shirts",
                color = "Warm Cream",
                pattern = "Solid",
                material = "Organic Cotton",
                style = "Minimalist",
                season = "Summer / Spring",
                tags = "Relaxed,Soft,Essential",
                imageUri = "sample_cream_tee"
            ),
            ClothingItemEntity(
                name = "Pleated Khaki Chinos",
                category = "Trousers",
                color = "Khaki Tan",
                pattern = "Solid",
                material = "Twill",
                style = "Preppy",
                season = "All Season",
                tags = "Smart,Tailored,College",
                imageUri = "sample_khaki_chinos"
            ),
            ClothingItemEntity(
                name = "Clean White Minimalist Sneakers",
                category = "Shoes",
                color = "White",
                pattern = "Leather",
                material = "Full-grain Leather",
                style = "Casual",
                season = "All Season",
                tags = "Low-top,Comfort,Everyday",
                imageUri = "sample_white_sneakers"
            ),
            ClothingItemEntity(
                name = "Burgundy Knit Sweater",
                category = "Jackets",
                color = "Burgundy",
                pattern = "Ribbed",
                material = "Merino Wool",
                style = "Chic",
                season = "Winter",
                tags = "Layering,Warmth,Cozy",
                imageUri = "sample_burgundy_sweater"
            ),
            ClothingItemEntity(
                name = "Heritage Leather Chronograph Watch",
                category = "Accessories",
                color = "Gold & Brown",
                pattern = "Metallic",
                material = "Leather / Brass",
                style = "Timeless",
                season = "All Season",
                tags = "Luxury,Refined,Wristwear",
                imageUri = "sample_watch"
            )
        )
        clothingDao.insertAllClothing(samples)
    }

    private fun ClothingItemEntity.toDomain() = ClothingItem(
        id = id,
        name = name,
        category = category,
        color = color,
        pattern = pattern,
        material = material,
        style = style,
        season = season,
        tags = if (tags.isBlank()) emptyList() else tags.split(",").map { it.trim() },
        imageUri = imageUri,
        isFavorite = isFavorite,
        dateAdded = dateAdded
    )
}
