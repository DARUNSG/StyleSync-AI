package com.example.presentation.onboarding

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.*

data class GenderOption(
    val id: String,
    val title: String,
    val subtitle: String,
    val iconEmoji: String
)

@Composable
fun WelcomeOnboardingScreen(
    onComplete: (name: String, gender: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var name by remember { mutableStateOf("") }
    var selectedGender by remember { mutableStateOf("Female") }
    var hasAttemptedSubmit by remember { mutableStateOf(false) }

    val genderOptions = listOf(
        GenderOption("Female", "Female", "Chic, tailored & feminine curations", "🌸"),
        GenderOption("Male", "Male", "Sharp, structured & masculine styling", "👔"),
        GenderOption("Unisex", "Non-Binary / Unisex", "Fluid, modern & expressive fashion", "✨")
    )

    // Animated breathing/floating scale for the logo
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.85f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowAlpha"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        DeepCharcoalRose,
                        DarkSurfaceRose,
                        DeepCharcoalRose
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Animated Logo Emblem with Soft Glowing Aura
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(110.dp)
                    .scale(pulseScale)
            ) {
                // Outer subtle pastel glow using user palette: SkyPowder & LavenderMauve
                Box(
                    modifier = Modifier
                        .size(106.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                listOf(
                                    LavenderMauve.copy(alpha = glowAlpha * 0.5f),
                                    SkyPowder.copy(alpha = glowAlpha * 0.25f),
                                    Color.Transparent
                                )
                            )
                        )
                )

                // App Brand Emblem
                Image(
                    painter = painterResource(id = R.drawable.ic_stylesync_logo),
                    contentDescription = "StyleSync AI Emblem",
                    modifier = Modifier
                        .size(88.dp)
                        .clip(CircleShape)
                        .border(2.dp, CreamIvory.copy(alpha = 0.8f), CircleShape)
                )
            }

            // Welcome Headings with Chic Typography
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "WELCOME TO",
                    color = LavenderMauve,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.5.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "StyleSync AI",
                    color = CreamIvory,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Your personalized fashion assistant. Tell us about yourself to tailor your wardrobe & recommendations.",
                    color = TextMutedRose,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 18.sp,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Card 1: Name Input
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurfaceRose),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = Brush.verticalGradient(
                        listOf(LavenderMauve.copy(alpha = 0.6f), Rosewood.copy(alpha = 0.3f))
                    )
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(Rosewood.copy(alpha = 0.4f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = CreamIvory,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "What should we call you?",
                                color = CreamIvory,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Your name or styling alias",
                                color = TextMutedRose,
                                fontSize = 12.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    val isError = hasAttemptedSubmit && name.isBlank()

                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        placeholder = { Text("Enter your name...", color = TextMutedRose.copy(alpha = 0.7f), fontSize = 14.sp) },
                        singleLine = true,
                        isError = isError,
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CreamIvory,
                            unfocusedBorderColor = BorderLavender,
                            focusedTextColor = CreamIvory,
                            unfocusedTextColor = CreamIvory,
                            focusedContainerColor = CardElevatedRose,
                            unfocusedContainerColor = CardElevatedRose,
                            errorBorderColor = ErrorRose
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("onboarding_name_input")
                    )

                    if (isError) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Please enter your name to proceed",
                            color = ErrorRose,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            // Card 2: Gender Selection
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurfaceRose),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = Brush.verticalGradient(
                        listOf(LavenderMauve.copy(alpha = 0.6f), Rosewood.copy(alpha = 0.3f))
                    )
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Choose your styling preference",
                        color = CreamIvory,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Helps Gemini curate fitting silhouettes & categories",
                        color = TextMutedRose,
                        fontSize = 12.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        genderOptions.forEach { option ->
                            val isSelected = selectedGender == option.id

                            Card(
                                onClick = { selectedGender = option.id },
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected) Rosewood else CardElevatedRose
                                ),
                                border = CardDefaults.outlinedCardBorder().copy(
                                    brush = if (isSelected)
                                        Brush.horizontalGradient(listOf(CreamIvory, SkyPowder))
                                    else
                                        Brush.verticalGradient(listOf(BorderLavender, Color.Transparent))
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("gender_option_${option.id.lowercase()}")
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = option.iconEmoji,
                                        fontSize = 24.sp
                                    )
                                    Spacer(modifier = Modifier.width(14.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = option.title,
                                            color = CreamIvory,
                                            fontSize = 14.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold
                                        )
                                        Text(
                                            text = option.subtitle,
                                            color = if (isSelected) CreamIvory.copy(alpha = 0.85f) else TextMutedRose,
                                            fontSize = 11.sp
                                        )
                                    }

                                    if (isSelected) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = "Selected",
                                            tint = CreamIvory,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Enter Button with animated gradient highlight
            Button(
                onClick = {
                    hasAttemptedSubmit = true
                    if (name.isNotBlank()) {
                        onComplete(name.trim(), selectedGender)
                    }
                },
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = CreamIvory,
                    contentColor = DeepCharcoalRose
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .testTag("enter_app_button")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = DeepCharcoalRose,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Enter StyleSync AI",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = DeepCharcoalRose
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = DeepCharcoalRose,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Text(
                text = "Powered by Google Gemini 2.5 Flash & Material 3",
                color = TextMutedRose.copy(alpha = 0.7f),
                fontSize = 11.sp
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
