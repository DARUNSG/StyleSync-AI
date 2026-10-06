package com.example.presentation.wardrobe

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.ClothingCategory
import com.example.domain.model.ClothingItem
import com.example.presentation.viewmodel.WardrobeViewModel
import com.example.ui.components.ClothingImageThumbnail
import com.example.ui.components.LuxuryCard
import com.example.ui.theme.*

@Composable
fun WardrobeScreen(
    viewModel: WardrobeViewModel,
    onNavigateToAddClothing: () -> Unit,
    modifier: Modifier = Modifier
) {
    val items by viewModel.filteredClothing.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val onlyFavorites by viewModel.onlyFavorites.collectAsState()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MidnightNavy,
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAddClothing,
                containerColor = WarmCream,
                contentColor = MidnightNavy,
                shape = CircleShape,
                modifier = Modifier.testTag("fab_add_clothing")
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Clothing")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Screen Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "DIGITAL WARDROBE",
                        color = SlateMuted,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp
                    )
                    Text(
                        text = "Your Collection",
                        color = WarmCream,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                IconButton(
                    onClick = { viewModel.toggleFavoritesFilter() },
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(if (onlyFavorites) WarmCream else DarkNavyBlue)
                        .border(1.dp, RoyalSlate, CircleShape)
                        .testTag("filter_favorites_button")
                ) {
                    Icon(
                        imageVector = if (onlyFavorites) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorites Only",
                        tint = if (onlyFavorites) MidnightNavy else WarmCream,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.updateSearchQuery(it) },
                placeholder = { Text("Search by name, color, style...", color = SlateMuted, fontSize = 14.sp) },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Search, contentDescription = "Search", tint = SlateMuted)
                },
                trailingIcon = {
                    if (searchQuery.isNotBlank()) {
                        IconButton(onClick = { viewModel.updateSearchQuery("") }) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Clear", tint = SlateMuted)
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = WarmCream,
                    unfocusedBorderColor = RoyalSlate,
                    focusedTextColor = WarmCream,
                    unfocusedTextColor = WarmCream,
                    focusedContainerColor = DarkNavyBlue,
                    unfocusedContainerColor = DarkNavyBlue
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("search_clothing_input")
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Category Filter Pills
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(ClothingCategory.entries.toTypedArray()) { cat ->
                    val isSelected = cat == selectedCategory
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.selectCategory(cat) },
                        label = {
                            Text(
                                text = cat.displayName,
                                fontSize = 13.sp,
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
                        modifier = Modifier.testTag("category_chip_${cat.name.lowercase()}")
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Clothing Grid or Empty State
            if (items.isEmpty()) {
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
                                imageVector = Icons.Default.Checkroom,
                                contentDescription = null,
                                tint = WarmCream,
                                modifier = Modifier.size(36.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = if (searchQuery.isNotBlank() || onlyFavorites) "No matching clothes found" else "Wardrobe is Empty",
                            color = WarmCream,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = if (searchQuery.isNotBlank() || onlyFavorites)
                                "Try resetting filters or searching for something else."
                            else "Add your apparel photos to let Gemini AI style them.",
                            color = SlateMuted,
                            fontSize = 13.sp,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(20.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            OutlinedButton(
                                onClick = { viewModel.populateSampleWardrobe() },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = WarmCream),
                                border = ButtonDefaults.outlinedButtonBorder().copy(brush = Brush.linearGradient(listOf(RoyalSlate, WarmCream))),
                                modifier = Modifier.testTag("load_sample_wardrobe_button")
                            ) {
                                Text("Load Sample Wardrobe")
                            }

                            Button(
                                onClick = onNavigateToAddClothing,
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = WarmCream, contentColor = MidnightNavy),
                                modifier = Modifier.testTag("add_item_empty_state_button")
                            ) {
                                Text("Add Item", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    contentPadding = PaddingValues(bottom = 100.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(items, key = { it.id }) { item ->
                        ClothingGridCard(
                            item = item,
                            onToggleFavorite = { viewModel.toggleItemFavorite(item.id) },
                            onDelete = { viewModel.deleteItem(item.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ClothingGridCard(
    item: ClothingItem,
    onToggleFavorite: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = DarkNavyBlue),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.verticalGradient(listOf(RoyalSlate, DarkNavySurface))),
        modifier = modifier
            .fillMaxWidth()
            .testTag("clothing_card_${item.id}")
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Thumbnail with Favorite overlay
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
            ) {
                ClothingImageThumbnail(
                    imageUri = item.imageUri,
                    category = item.category,
                    modifier = Modifier.fillMaxSize()
                )

                // Favorite Heart Badge
                IconButton(
                    onClick = onToggleFavorite,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(6.dp)
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(MidnightNavy.copy(alpha = 0.75f))
                ) {
                    Icon(
                        imageVector = if (item.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (item.isFavorite) ErrorRose else WarmCream,
                        modifier = Modifier.size(16.dp)
                    )
                }

                // Delete Button
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(6.dp)
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(MidnightNavy.copy(alpha = 0.75f))
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = "Delete",
                        tint = SlateMuted,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = item.name,
                color = WarmCream,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = item.color,
                    color = SlateMuted,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(RoyalSlate.copy(alpha = 0.5f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = item.category,
                        color = WarmCreamLight,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}
