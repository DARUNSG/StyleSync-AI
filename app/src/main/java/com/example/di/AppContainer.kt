package com.example.di

import android.content.Context
import com.example.data.local.AppDatabase
import com.example.data.remote.FirebaseFashionBridge
import com.example.data.remote.GeminiFashionClient
import com.example.data.repository.LocationHelper
import com.example.data.repository.OutfitPlannerRepository
import com.example.data.repository.UserPreferencesRepository
import com.example.data.repository.WardrobeRepository
import com.example.data.repository.WeatherRepository

/**
 * Clean architectural dependency injection container.
 * Encapsulates singleton lifetime management, repository decoupling,
 * and service provision with zero compile-time annotation processor overhead.
 */
interface AppContainer {
    val database: AppDatabase
    val wardrobeRepository: WardrobeRepository
    val weatherRepository: WeatherRepository
    val outfitPlannerRepository: OutfitPlannerRepository
    val geminiClient: GeminiFashionClient
    val locationHelper: LocationHelper
    val firebaseBridge: FirebaseFashionBridge
    val userPreferencesRepository: UserPreferencesRepository
}

class DefaultAppContainer(private val context: Context) : AppContainer {
    override val database: AppDatabase by lazy {
        AppDatabase.getInstance(context)
    }

    override val geminiClient: GeminiFashionClient by lazy {
        GeminiFashionClient()
    }

    override val locationHelper: LocationHelper by lazy {
        LocationHelper(context)
    }

    override val wardrobeRepository: WardrobeRepository by lazy {
        WardrobeRepository(context, database.clothingDao())
    }

    override val weatherRepository: WeatherRepository by lazy {
        WeatherRepository(locationHelper)
    }

    override val outfitPlannerRepository: OutfitPlannerRepository by lazy {
        OutfitPlannerRepository(
            outfitDao = database.outfitDao(),
            wardrobeRepository = wardrobeRepository,
            geminiClient = geminiClient
        )
    }

    override val firebaseBridge: FirebaseFashionBridge by lazy {
        FirebaseFashionBridge(context)
    }

    override val userPreferencesRepository: UserPreferencesRepository by lazy {
        UserPreferencesRepository(context)
    }
}
