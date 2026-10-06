package com.example.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.presentation.viewmodel.HomeViewModel
import com.example.ui.components.LuxuryCard
import com.example.ui.components.SectionHeader
import com.example.ui.theme.*

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigateToPlanner: () -> Unit,
    onNavigateToWardrobe: () -> Unit,
    onNavigateToAddClothing: () -> Unit,
    onNavigateToSaved: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val recentOutfits by viewModel.recentOutfits.collectAsState()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MidnightNavy)
            .padding(horizontal = 20.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(22.dp)
    ) {
        // Top App Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    androidx.compose.foundation.Image(
                        painter = androidx.compose.ui.res.painterResource(id = com.example.R.drawable.ic_stylesync_logo),
                        contentDescription = "StyleSync AI Logo",
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                    )
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text(
                            text = if (uiState.userName.isNotBlank()) "WELCOME BACK" else "STYLESYNC AI",
                            color = LavenderMauve,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.8.sp
                        )
                        Text(
                            text = if (uiState.userName.isNotBlank()) uiState.userName else "Fashion-Tech Assistant",
                            color = CreamIvory,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                val rotation by androidx.compose.animation.core.animateFloatAsState(
                    targetValue = if (uiState.isLoadingWeather) 360f else 0f,
                    animationSpec = androidx.compose.animation.core.tween(700),
                    label = "weatherRefreshSpin"
                )

                IconButton(
                    onClick = { viewModel.refreshWeather() },
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(DarkSurfaceRose)
                        .border(1.dp, LavenderMauve.copy(alpha = 0.5f), CircleShape)
                        .testTag("refresh_weather_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Refresh Weather",
                        tint = CreamIvory,
                        modifier = Modifier
                            .size(20.dp)
                            .rotate(rotation)
                    )
                }
            }
        }

        // Live Weather Card
        item {
            LuxuryCard(modifier = Modifier.fillMaxWidth().testTag("weather_card")) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = "Location",
                                tint = WarmCream,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = uiState.weather.locationName,
                                color = WarmCream,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(RoyalSlate)
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "Live Conditions",
                                color = WarmCreamLight,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.Top) {
                                Text(
                                    text = "${uiState.weather.temperatureCelsius.toInt()}",
                                    color = WarmCream,
                                    fontSize = 46.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "°C",
                                    color = WarmCream,
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Light,
                                    modifier = Modifier.padding(top = 6.dp)
                                )
                            }
                            Text(
                                text = uiState.weather.condition,
                                color = SlateMuted,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        // Weather specs
                        Column(
                            horizontalAlignment = Alignment.End,
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "Humidity: ${uiState.weather.humidityPercentage}%",
                                color = SlateMuted,
                                fontSize = 12.sp
                            )
                            Text(
                                text = "Wind: ${uiState.weather.windSpeedKmh.toInt()} km/h",
                                color = SlateMuted,
                                fontSize = 12.sp
                            )
                            Text(
                                text = if (uiState.weather.isRaining) "Wet Weather Gear Advised" else "Comfortable & Dry",
                                color = if (uiState.weather.isRaining) WarningAmber else SuccessGreen,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }

        // Hero CTA: Plan An Outfit
        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = WarmCream),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("plan_outfit_hero_card")
                    .clickable { onNavigateToPlanner() }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(22.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(MidnightNavy)
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "GEMINI POWERED",
                                color = WarmCream,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Plan Today's Outfit",
                            color = MidnightNavy,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Curate look matching weather, color harmony & your event.",
                            color = MidnightNavy.copy(alpha = 0.8f),
                            fontSize = 13.sp,
                            lineHeight = 18.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(MidnightNavy),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Start Planning",
                            tint = WarmCream,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }

        // Wardrobe Summary Row
        item {
            SectionHeader(
                title = "Wardrobe Inventory",
                subtitle = "${uiState.wardrobeCount} items indexed in personal collection",
                actionText = "View All",
                onActionClick = onNavigateToWardrobe
            )
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                WardrobeMetricPill(
                    label = "Tops",
                    count = uiState.topsCount,
                    icon = Icons.Default.DryCleaning,
                    modifier = Modifier.weight(1f)
                )
                WardrobeMetricPill(
                    label = "Bottoms",
                    count = uiState.bottomsCount,
                    icon = Icons.Default.Style,
                    modifier = Modifier.weight(1f)
                )
                WardrobeMetricPill(
                    label = "Shoes",
                    count = uiState.shoesCount,
                    icon = Icons.Default.RollerSkating,
                    modifier = Modifier.weight(1f)
                )
                WardrobeMetricPill(
                    label = "Layers",
                    count = uiState.layersCount,
                    icon = Icons.Default.Shield,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Recent Outfits or Empty CTA
        item {
            SectionHeader(
                title = "Recent Outfits",
                subtitle = "Saved styling combinations",
                actionText = if (recentOutfits.isNotEmpty()) "See Saved" else null,
                onActionClick = onNavigateToSaved
            )
            Spacer(modifier = Modifier.height(12.dp))

            if (recentOutfits.isEmpty()) {
                LuxuryCard(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = SlateMuted,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No saved outfits yet",
                            color = WarmCream,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Use the Outfit Planner to get your first AI-coordinated combination.",
                            color = SlateMuted,
                            fontSize = 13.sp,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = onNavigateToPlanner,
                            colors = ButtonDefaults.buttonColors(containerColor = WarmCream, contentColor = MidnightNavy),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.testTag("plan_first_outfit_button")
                        ) {
                            Text("Create My First Outfit", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(recentOutfits.take(5)) { outfit ->
                        Card(
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(containerColor = DarkNavyBlue),
                            border = CardDefaults.outlinedCardBorder().copy(brush = Brush.verticalGradient(listOf(RoyalSlate, DarkNavySurface))),
                            modifier = Modifier
                                .width(220.dp)
                                .clickable { onNavigateToSaved() }
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = outfit.occasion,
                                        color = WarmCream,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(CardElevated)
                                            .padding(horizontal = 8.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = "${outfit.score}% Match",
                                            color = WarmCream,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = outfit.title,
                                    color = WarmCreamLight,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = outfit.itemNames,
                                    color = SlateMuted,
                                    fontSize = 12.sp,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }
        }

        // Quick Actions
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onNavigateToAddClothing,
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = WarmCream),
                    border = ButtonDefaults.outlinedButtonBorder().copy(brush = Brush.linearGradient(listOf(RoyalSlate, WarmCream))),
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                        .testTag("add_clothing_button")
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Add Clothes", fontWeight = FontWeight.SemiBold)
                }

                Button(
                    onClick = onNavigateToPlanner,
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = RoyalSlate, contentColor = WarmCream),
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                        .testTag("style_me_button")
                ) {
                    Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Style Me", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun WardrobeMetricPill(
    label: String,
    count: Int,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkNavyBlue),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.verticalGradient(listOf(RoyalSlate, DarkNavySurface))),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = WarmCream,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "$count",
                color = WarmCream,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = label,
                color = SlateMuted,
                fontSize = 11.sp
            )
        }
    }
}
