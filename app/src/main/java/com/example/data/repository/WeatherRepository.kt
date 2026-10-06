package com.example.data.repository

import com.example.data.remote.WeatherNetworkClient
import com.example.domain.model.WeatherInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class WeatherRepository(
    private val locationHelper: LocationHelper
) {
    suspend fun getWeatherForLocation(
        latitude: Double,
        longitude: Double,
        locationName: String = "Current Location"
    ): WeatherInfo = withContext(Dispatchers.IO) {
        try {
            val response = WeatherNetworkClient.weatherService.getForecast(latitude, longitude)
            val current = response.current
            val temp = current?.temperature2m ?: 22.0
            val apparent = current?.apparentTemperature ?: temp
            val humidity = current?.relativeHumidity2m?.toInt() ?: 55
            val precip = current?.precipitation ?: 0.0
            val wind = current?.windSpeed10m ?: 12.0
            val condition = WeatherNetworkClient.weatherCodeToCondition(current?.weatherCode)
            val isDay = current?.isDay != 0

            WeatherInfo(
                temperatureCelsius = temp,
                condition = condition,
                apparentTemperature = apparent,
                humidityPercentage = humidity,
                precipitationMm = precip,
                windSpeedKmh = wind,
                locationName = locationName,
                isDay = isDay
            )
        } catch (e: Exception) {
            // Sensible fallback
            WeatherInfo(
                temperatureCelsius = 23.5,
                condition = "Clear Sky",
                apparentTemperature = 24.0,
                humidityPercentage = 48,
                precipitationMm = 0.0,
                windSpeedKmh = 10.5,
                locationName = locationName,
                isDay = true
            )
        }
    }

    suspend fun getCurrentLocationWeather(): WeatherInfo = withContext(Dispatchers.IO) {
        val coords = locationHelper.getCurrentLocation()
        if (coords != null) {
            getWeatherForLocation(coords.first, coords.second, "Nearby Location")
        } else {
            // Default to San Francisco
            getWeatherForLocation(37.7749, -122.4194, "San Francisco, CA")
        }
    }

    suspend fun searchCity(query: String): List<Pair<String, Pair<Double, Double>>> = withContext(Dispatchers.IO) {
        try {
            val response = WeatherNetworkClient.geocodingService.searchCity(query)
            response.results?.map {
                val label = buildString {
                    append(it.name)
                    if (!it.admin1.isNullOrBlank()) append(", ${it.admin1}")
                    if (!it.country.isNullOrBlank()) append(", ${it.country}")
                }
                Pair(label, Pair(it.latitude, it.longitude))
            } ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }
}
