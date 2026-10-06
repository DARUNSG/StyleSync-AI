package com.example.domain.model

data class ClothingItem(
    val id: Long = 0,
    val name: String,
    val category: String,
    val color: String,
    val pattern: String = "Solid",
    val material: String = "Cotton",
    val style: String = "Casual",
    val season: String = "All Season",
    val tags: List<String> = emptyList(),
    val imageUri: String,
    val isFavorite: Boolean = false,
    val dateAdded: Long = System.currentTimeMillis()
)

enum class ClothingCategory(val displayName: String, val iconName: String) {
    ALL("All", "ic_all"),
    T_SHIRTS("T-Shirts", "ic_tshirt"),
    SHIRTS("Shirts", "ic_shirt"),
    JEANS("Jeans", "ic_jeans"),
    TROUSERS("Trousers", "ic_trousers"),
    SHORTS("Shorts", "ic_shorts"),
    JACKETS("Jackets", "ic_jacket"),
    HOODIES("Hoodies", "ic_hoodie"),
    SHOES("Shoes", "ic_shoes"),
    ACCESSORIES("Accessories", "ic_accessories");

    companion object {
        fun fromDisplayName(name: String): ClothingCategory {
            return entries.find { it.displayName.equals(name, ignoreCase = true) } ?: ALL
        }
    }
}

enum class Occasion(val title: String, val subtitle: String, val emoji: String) {
    COLLEGE("College", "Comfortable, trendy & casual daily wear", "🎓"),
    CASUAL("Casual", "Relaxed & stylish weekend outfits", "☕"),
    PARTY("Party", "Vibrant, sharp & eye-catching styles", "🎉"),
    INTERVIEW("Interview", "Polished, confident & professional attire", "💼"),
    WEDDING("Wedding", "Grand, sophisticated & celebratory look", "💍"),
    FORMAL("Formal", "Elegant, tailored & boardroom-ready", "👔"),
    TRAVEL("Travel", "Versatile, breathable & layered comfort", "✈️"),
    SPORTS("Sports", "Active, flexible & athletic performance", "🏃");

    companion object {
        fun fromTitle(title: String): Occasion {
            return entries.find { it.title.equals(title, ignoreCase = true) } ?: CASUAL
        }
    }
}

data class WeatherInfo(
    val temperatureCelsius: Double,
    val condition: String,
    val apparentTemperature: Double = temperatureCelsius,
    val humidityPercentage: Int = 50,
    val precipitationMm: Double = 0.0,
    val windSpeedKmh: Double = 10.0,
    val locationName: String = "Current Location",
    val isDay: Boolean = true
) {
    val isRaining: Boolean get() = precipitationMm > 0.1 || condition.contains("Rain", ignoreCase = true)
    val isCold: Boolean get() = temperatureCelsius < 16.0
    val isHot: Boolean get() = temperatureCelsius > 28.0
    val isMild: Boolean get() = temperatureCelsius in 16.0..28.0
}

data class RecommendedOutfit(
    val id: Long = 0,
    val title: String,
    val occasion: Occasion,
    val items: List<ClothingItem>,
    val style: String,
    val weatherDescription: String,
    val temperature: Double,
    val overallScore: Int,
    val colorMatchScore: Int,
    val weatherMatchScore: Int,
    val occasionMatchScore: Int,
    val styleMatchScore: Int,
    val aiExplanation: String,
    val isSaved: Boolean = false,
    val dateCreated: Long = System.currentTimeMillis()
)

data class UserProfile(
    val name: String = "Fashion Explorer",
    val preferredStyles: List<String> = listOf("Casual", "Chic", "Minimalist"),
    val preferredColors: List<String> = listOf("Navy", "White", "Cream", "Black", "Earthy"),
    val size: String = "M",
    val favoriteOccasions: List<String> = listOf("Casual", "College", "Party")
)
