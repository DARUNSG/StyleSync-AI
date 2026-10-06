package com.example.presentation.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import com.example.presentation.viewmodel.ProfileViewModel
import com.example.ui.components.LuxuryCard
import com.example.ui.components.SectionHeader
import com.example.ui.theme.*

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    var showEditDialog by remember { mutableStateOf(false) }
    var editName by remember { mutableStateOf(uiState.name) }
    var editGender by remember { mutableStateOf(uiState.gender) }

    val availableStyles = listOf("Minimalist", "Casual", "Smart Chic", "Streetwear", "Classic", "Athleisure", "Preppy")
    val availableColors = listOf("Cream Ivory", "Sky Powder Blue", "Lavender Mauve", "Rosewood", "White", "Navy", "Earth Tones")
    val availableSizes = listOf("XS", "S", "M", "L", "XL", "XXL")

    if (showEditDialog) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = {
                Text("Edit Profile Details", color = CreamIvory, fontWeight = FontWeight.Bold)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    OutlinedTextField(
                        value = editName,
                        onValueChange = { editName = it },
                        label = { Text("Your Name") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CreamIvory,
                            unfocusedBorderColor = BorderLavender,
                            focusedTextColor = CreamIvory,
                            unfocusedTextColor = CreamIvory
                        )
                    )

                    Text("Gender Preference", color = TextMutedRose, fontSize = 12.sp)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("Female", "Male", "Unisex").forEach { g ->
                            FilterChip(
                                selected = editGender == g,
                                onClick = { editGender = g },
                                label = { Text(g) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = CreamIvory,
                                    selectedLabelColor = DeepCharcoalRose,
                                    containerColor = CardElevatedRose,
                                    labelColor = CreamIvory
                                )
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (editName.isNotBlank()) {
                            viewModel.updateNameAndGender(editName.trim(), editGender)
                            showEditDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CreamIvory, contentColor = DeepCharcoalRose)
                ) {
                    Text("Save", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) {
                    Text("Cancel", color = TextMutedRose)
                }
            },
            containerColor = DarkSurfaceRose
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DeepCharcoalRose)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(22.dp)
    ) {
        // Screen Header & Avatar
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .size(68.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(listOf(DarkSurfaceRose, Rosewood))
                    )
                    .border(2.dp, CreamIvory, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Avatar",
                    tint = CreamIvory,
                    modifier = Modifier.size(34.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = uiState.name,
                    color = CreamIvory,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${uiState.gender} • Personal Style Profile",
                    color = TextMutedRose,
                    fontSize = 13.sp
                )
            }

            IconButton(
                onClick = {
                    editName = uiState.name
                    editGender = uiState.gender
                    showEditDialog = true
                },
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(DarkSurfaceRose)
                    .border(1.dp, LavenderMauve, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit Profile",
                    tint = CreamIvory,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        // Style Preferences Section
        SectionHeader(
            title = "Style Aesthetic",
            subtitle = "Preferences guiding outfit recommendations"
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(availableStyles) { style ->
                val isSelected = uiState.preferredStyles.contains(style)
                FilterChip(
                    selected = isSelected,
                    onClick = { viewModel.updateStyle(style) },
                    label = {
                        Text(
                            text = style,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = CreamIvory,
                        selectedLabelColor = DeepCharcoalRose,
                        containerColor = DarkSurfaceRose,
                        labelColor = CreamIvory
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        borderColor = if (isSelected) CreamIvory else BorderLavender,
                        enabled = true,
                        selected = isSelected
                    ),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.testTag("pref_style_${style.lowercase()}")
                )
            }
        }

        // Color Palette Preferences
        SectionHeader(
            title = "Favorite Palettes",
            subtitle = "Preferred clothing color tones"
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(availableColors) { color ->
                val isSelected = uiState.preferredColors.contains(color)
                FilterChip(
                    selected = isSelected,
                    onClick = { viewModel.updateColor(color) },
                    label = {
                        Text(
                            text = color,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = CreamIvory,
                        selectedLabelColor = DeepCharcoalRose,
                        containerColor = DarkSurfaceRose,
                        labelColor = CreamIvory
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        borderColor = if (isSelected) CreamIvory else BorderLavender,
                        enabled = true,
                        selected = isSelected
                    ),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.testTag("pref_color_${color.lowercase().replace(" ", "_")}")
                )
            }
        }

        // Sizing
        SectionHeader(
            title = "Default Sizing",
            subtitle = "Standard fit category"
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            availableSizes.forEach { sz ->
                val isSelected = uiState.size.contains(sz)
                FilterChip(
                    selected = isSelected,
                    onClick = { viewModel.updateSize(sz) },
                    label = {
                        Text(
                            text = sz,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = CreamIvory,
                        selectedLabelColor = DeepCharcoalRose,
                        containerColor = DarkSurfaceRose,
                        labelColor = CreamIvory
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        borderColor = if (isSelected) CreamIvory else BorderLavender,
                        enabled = true,
                        selected = isSelected
                    ),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.testTag("pref_size_$sz")
                )
            }
        }

        // Technology & Cloud Infrastructure Card
        SectionHeader(
            title = "Technology & Aesthetics",
            subtitle = "Integrated Google & Fashion Tech Services"
        )

        LuxuryCard(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Gemini API Status
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Google Gemini AI", color = CreamIvory, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Text("Vision & Recommendation (gemini-2.5-flash)", color = TextMutedRose, fontSize = 11.sp)
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(CardElevatedRose)
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = if (uiState.isGeminiReady) "Active" else "Hybrid Engine Ready",
                            color = SuccessGreen,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                HorizontalDivider(color = BorderLavender.copy(alpha = 0.5f))

                // Firebase Status
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Cloud Persistence", color = CreamIvory, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Text(uiState.firebaseStatus, color = TextMutedRose, fontSize = 11.sp)
                    }
                    Icon(imageVector = Icons.Default.CloudDone, contentDescription = null, tint = CreamIvory, modifier = Modifier.size(20.dp))
                }

                HorizontalDivider(color = BorderLavender.copy(alpha = 0.5f))

                // Brand Color Scheme
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Brand Color Palette", color = CreamIvory, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Text("#FDF4D2 • #B0CDE6 • #A290B7 • #946D6D", color = TextMutedRose, fontSize = 11.sp)
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Box(modifier = Modifier.size(18.dp).clip(CircleShape).background(CreamIvory).border(1.dp, Rosewood, CircleShape))
                        Box(modifier = Modifier.size(18.dp).clip(CircleShape).background(SkyPowder).border(1.dp, Rosewood, CircleShape))
                        Box(modifier = Modifier.size(18.dp).clip(CircleShape).background(LavenderMauve).border(1.dp, Rosewood, CircleShape))
                        Box(modifier = Modifier.size(18.dp).clip(CircleShape).background(Rosewood))
                    }
                }
            }
        }

        // Reset Onboarding Option
        OutlinedButton(
            onClick = { viewModel.resetOnboarding() },
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = TextMutedRose),
            border = ButtonDefaults.outlinedButtonBorder().copy(brush = Brush.linearGradient(listOf(BorderLavender, Rosewood))),
            modifier = Modifier.fillMaxWidth().height(48.dp)
        ) {
            Icon(imageVector = Icons.Default.RestartAlt, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Restart Name & Gender Onboarding", fontSize = 13.sp)
        }

        Spacer(modifier = Modifier.height(80.dp))
    }
}
