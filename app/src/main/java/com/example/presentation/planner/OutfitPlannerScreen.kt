package com.example.presentation.planner

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.Occasion
import com.example.presentation.viewmodel.OutfitGenerationState
import com.example.presentation.viewmodel.OutfitPlannerViewModel
import com.example.ui.components.LuxuryCard
import com.example.ui.components.SectionHeader
import com.example.ui.theme.*

@Composable
fun OutfitPlannerScreen(
    viewModel: OutfitPlannerViewModel,
    onNavigateToResult: () -> Unit,
    modifier: Modifier = Modifier
) {
    val selectedOccasion by viewModel.selectedOccasion.collectAsState()
    val selectedStyle by viewModel.selectedStyle.collectAsState()
    val weather by viewModel.weather.collectAsState()
    val customTemp by viewModel.customTemp.collectAsState()
    val isCustomWeather by viewModel.isCustomWeather.collectAsState()
    val generationState by viewModel.generationState.collectAsState()
    val wardrobeItems by viewModel.wardrobeItems.collectAsState()

    val styles = listOf("Casual", "Minimalist", "Chic", "Streetwear", "Elegant", "Athleisure", "Preppy")

    LaunchedEffect(generationState) {
        if (generationState is OutfitGenerationState.Success) {
            onNavigateToResult()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MidnightNavy)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(22.dp)
    ) {
        // Screen Header
        Column {
            Text(
                text = "OUTFIT CURATOR",
                color = SlateMuted,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp
            )
            Text(
                text = "Plan An Outfit",
                color = WarmCream,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Select your occasion and weather context to generate tailored AI styling.",
                color = SlateMuted,
                fontSize = 13.sp
            )
        }

        // Section 1: Choose Occasion
        SectionHeader(
            title = "1. Select Occasion",
            subtitle = "Where are you heading today?"
        )

        // Occasion grid/chips
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            val occasions = Occasion.entries.toList()
            val chunked = occasions.chunked(2)
            chunked.forEach { rowOccasions ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    rowOccasions.forEach { occasion ->
                        val isSelected = occasion == selectedOccasion
                        Card(
                            onClick = { viewModel.selectOccasion(occasion) },
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) WarmCream else DarkNavyBlue
                            ),
                            border = CardDefaults.outlinedCardBorder().copy(
                                brush = if (isSelected) Brush.linearGradient(listOf(WarmCream, SoftGoldHover))
                                else Brush.verticalGradient(listOf(RoyalSlate, DarkNavySurface))
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("occasion_card_${occasion.name.lowercase()}")
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = occasion.emoji,
                                    fontSize = 22.sp
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = occasion.title,
                                        color = if (isSelected) MidnightNavy else WarmCream,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = occasion.subtitle,
                                        color = if (isSelected) MidnightNavy.copy(alpha = 0.7f) else SlateMuted,
                                        fontSize = 10.sp,
                                        maxLines = 1,
                                        lineHeight = 12.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Section 2: Weather Context
        SectionHeader(
            title = "2. Weather & Context",
            subtitle = if (isCustomWeather) "Customized forecast override" else "Synced with current weather"
        )

        LuxuryCard(modifier = Modifier.fillMaxWidth().testTag("planner_weather_card")) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Thermostat,
                            contentDescription = null,
                            tint = WarmCream,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isCustomWeather) "Custom Setting" else weather.locationName,
                            color = WarmCream,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    TextButton(onClick = { viewModel.toggleUseLiveWeather() }) {
                        Text(
                            text = if (isCustomWeather) "Reset to Live" else "Live GPS",
                            color = WarmCream,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${customTemp}°C",
                        color = WarmCream,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (customTemp > 28) "Warm / Summer" else if (customTemp < 18) "Cool / Layering" else "Pleasant / Mild",
                        color = SlateMuted,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Slider(
                    value = customTemp.toFloat(),
                    onValueChange = { viewModel.setCustomTemp(it.toInt()) },
                    valueRange = 0f..45f,
                    colors = SliderDefaults.colors(
                        thumbColor = WarmCream,
                        activeTrackColor = WarmCream,
                        inactiveTrackColor = RoyalSlate
                    ),
                    modifier = Modifier.testTag("temp_slider")
                )
            }
        }

        // Section 3: Preferred Style Vibe
        SectionHeader(
            title = "3. Preferred Style",
            subtitle = "Guiding aesthetic for recommendation"
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(styles) { style ->
                val isSelected = style.equals(selectedStyle, ignoreCase = true)
                FilterChip(
                    selected = isSelected,
                    onClick = { viewModel.selectStyle(style) },
                    label = {
                        Text(
                            text = style,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = WarmCream,
                        selectedLabelColor = MidnightNavy,
                        containerColor = DarkNavyBlue,
                        labelColor = WarmCream
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        borderColor = if (isSelected) WarmCream else RoyalSlate,
                        enabled = true,
                        selected = isSelected
                    ),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.testTag("style_chip_${style.lowercase()}")
                )
            }
        }

        // Error message if any
        if (generationState is OutfitGenerationState.Error) {
            val error = (generationState as OutfitGenerationState.Error).message
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = DarkNavyBlue),
                border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(ErrorRose, WarningAmber))),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.Warning, contentDescription = null, tint = ErrorRose)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(text = error, color = WarmCream, fontSize = 13.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Big Primary CTA Button: Generate AI Outfit
        val isLoading = generationState is OutfitGenerationState.Loading
        Button(
            onClick = { viewModel.generateOutfit() },
            enabled = !isLoading,
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = WarmCream,
                contentColor = MidnightNavy,
                disabledContainerColor = RoyalSlate,
                disabledContentColor = SlateMuted
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp)
                .testTag("generate_outfit_cta_button")
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MidnightNavy,
                    strokeWidth = 2.5.dp
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text("Analyzing Wardrobe with Gemini AI...", fontWeight = FontWeight.Bold, fontSize = 15.sp)
            } else {
                Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(22.dp))
                Spacer(modifier = Modifier.width(10.dp))
                Text("Curate AI Outfit", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }

        Spacer(modifier = Modifier.height(90.dp))
    }
}
