package com.example.presentation.favorites

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.SavedOutfitEntity
import com.example.presentation.viewmodel.FavoritesViewModel
import com.example.ui.components.LuxuryCard
import com.example.ui.theme.*

@Composable
fun FavoritesScreen(
    viewModel: FavoritesViewModel,
    onNavigateToPlanner: () -> Unit,
    modifier: Modifier = Modifier
) {
    val savedOutfits by viewModel.savedOutfits.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MidnightNavy)
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Screen Header
        Column {
            Text(
                text = "SAVED STYLING",
                color = SlateMuted,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp
            )
            Text(
                text = "Favorite Outfits",
                color = WarmCream,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "${savedOutfits.size} custom looks saved for easy dressing",
                color = SlateMuted,
                fontSize = 13.sp
            )
        }

        if (savedOutfits.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 60.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(24.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(DarkNavyBlue)
                            .border(1.5.dp, RoyalSlate, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.BookmarkBorder,
                            contentDescription = null,
                            tint = WarmCream,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No saved outfits yet",
                        color = WarmCream,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Generate stylish recommendations in the Outfit Planner and tap 'Save Outfit'.",
                        color = SlateMuted,
                        fontSize = 13.sp,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Button(
                        onClick = onNavigateToPlanner,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = WarmCream, contentColor = MidnightNavy),
                        modifier = Modifier.testTag("plan_outfit_from_favorites_button")
                    ) {
                        Text("Plan an Outfit Now", fontWeight = FontWeight.Bold)
                    }
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(14.dp),
                contentPadding = PaddingValues(bottom = 100.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(savedOutfits, key = { it.id }) { outfit ->
                    SavedOutfitCard(
                        outfit = outfit,
                        onDelete = { viewModel.deleteOutfit(outfit.id) },
                        onToggleFavorite = { viewModel.toggleFavorite(outfit.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun SavedOutfitCard(
    outfit: SavedOutfitEntity,
    onDelete: () -> Unit,
    onToggleFavorite: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = DarkNavyBlue),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.verticalGradient(listOf(RoyalSlate, DarkNavySurface))),
        modifier = modifier
            .fillMaxWidth()
            .testTag("saved_outfit_${outfit.id}")
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(RoyalSlate)
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = outfit.occasion.uppercase(),
                        color = WarmCream,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
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

                    Spacer(modifier = Modifier.width(6.dp))

                    IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
                        Icon(imageVector = Icons.Default.DeleteOutline, contentDescription = "Delete", tint = SlateMuted, modifier = Modifier.size(16.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = outfit.title,
                color = WarmCream,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = outfit.itemNames,
                color = WarmCreamLight,
                fontSize = 13.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${outfit.weatherDescription} • ${outfit.style}",
                    color = SlateMuted,
                    fontSize = 11.sp
                )
            }

            if (outfit.aiExplanation.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = outfit.aiExplanation,
                    color = SlateMuted,
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )
            }
        }
    }
}
