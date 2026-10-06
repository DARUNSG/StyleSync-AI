package com.example.presentation.recommendation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.ClothingItem
import com.example.presentation.viewmodel.OutfitGenerationState
import com.example.presentation.viewmodel.OutfitPlannerViewModel
import com.example.ui.components.ClothingImageThumbnail
import com.example.ui.components.LuxuryCard
import com.example.ui.components.LuxuryScoreBadge
import com.example.ui.components.SectionHeader
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecommendationResultScreen(
    viewModel: OutfitPlannerViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.generationState.collectAsState()
    val outfitSaved by viewModel.outfitSaved.collectAsState()

    val outfit = (state as? OutfitGenerationState.Success)?.outfit

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MidnightNavy,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "AI Recommendation",
                        color = WarmCream,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack, modifier = Modifier.testTag("result_back_button")) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = WarmCream)
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.saveCurrentOutfit() },
                        enabled = !outfitSaved,
                        modifier = Modifier.testTag("save_outfit_icon_button")
                    ) {
                        Icon(
                            imageVector = if (outfitSaved) Icons.Default.BookmarkAdded else Icons.Default.BookmarkBorder,
                            contentDescription = "Save Outfit",
                            tint = if (outfitSaved) SuccessGreen else WarmCream
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MidnightNavy)
            )
        }
    ) { innerPadding ->
        if (outfit == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text("No outfit generated yet.", color = SlateMuted)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 20.dp),
                contentPadding = PaddingValues(top = 10.dp, bottom = 80.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Main Header Score Banner
                item {
                    LuxuryCard(modifier = Modifier.fillMaxWidth().testTag("score_banner_card")) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(22.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(RoyalSlate)
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "${outfit.occasion.title.uppercase()} • ${outfit.style.uppercase()}",
                                    color = WarmCream,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.2.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Text(
                                text = outfit.title,
                                color = WarmCream,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Overall AI Score Ring / Badge
                            Box(
                                modifier = Modifier
                                    .size(96.dp)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.radialGradient(listOf(CardElevated, DarkNavyBlue))
                                    )
                                    .border(2.5.dp, WarmCream, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "${outfit.overallScore}%",
                                        color = WarmCream,
                                        fontSize = 26.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "AI MATCH",
                                        color = SlateMuted,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(18.dp))

                            // Sub-scores 4-grid
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                LuxuryScoreBadge(score = outfit.colorMatchScore, label = "Color")
                                LuxuryScoreBadge(score = outfit.occasionMatchScore, label = "Occasion")
                                LuxuryScoreBadge(score = outfit.weatherMatchScore, label = "Weather")
                                LuxuryScoreBadge(score = outfit.styleMatchScore, label = "Style")
                            }
                        }
                    }
                }

                // AI Explanation / Styling Rationale
                item {
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = CardElevated),
                        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.verticalGradient(listOf(RoyalSlate, DarkNavySurface))),
                        modifier = Modifier.fillMaxWidth().testTag("ai_rationale_card")
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = WarmCream,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Stylist Rationale",
                                    color = WarmCream,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = outfit.aiExplanation,
                                color = WarmCreamLight,
                                fontSize = 13.sp,
                                lineHeight = 19.sp
                            )
                        }
                    }
                }

                // Selected Garment Pieces
                item {
                    SectionHeader(
                        title = "Coordinated Pieces",
                        subtitle = "${outfit.items.size} items assembled from your wardrobe"
                    )
                }

                items(outfit.items) { item ->
                    CoordinatedItemRow(item = item)
                }

                // Save or Regenerate Action Buttons
                item {
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = { viewModel.generateOutfit() },
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = WarmCream),
                            border = ButtonDefaults.outlinedButtonBorder().copy(brush = Brush.linearGradient(listOf(RoyalSlate, WarmCream))),
                            modifier = Modifier
                                .weight(1f)
                                .height(52.dp)
                                .testTag("regenerate_button")
                        ) {
                            Icon(imageVector = Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Regenerate")
                        }

                        Button(
                            onClick = { viewModel.saveCurrentOutfit() },
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (outfitSaved) SuccessGreen else WarmCream,
                                contentColor = MidnightNavy
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .height(52.dp)
                                .testTag("save_outfit_button")
                        ) {
                            Icon(
                                imageVector = if (outfitSaved) Icons.Default.Check else Icons.Default.Bookmark,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (outfitSaved) "Saved to Outfits" else "Save Outfit",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CoordinatedItemRow(
    item: ClothingItem,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkNavyBlue),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.verticalGradient(listOf(RoyalSlate, DarkNavySurface))),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ClothingImageThumbnail(
                imageUri = item.imageUri,
                category = item.category,
                modifier = Modifier.size(68.dp)
            )

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.name,
                    color = WarmCream,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${item.category} • ${item.color}",
                    color = SlateMuted,
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(CardElevated)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = item.style,
                            color = WarmCreamLight,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(CardElevated)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = item.material,
                            color = SlateMuted,
                            fontSize = 10.sp
                        )
                    }
                }
            }
        }
    }
}
