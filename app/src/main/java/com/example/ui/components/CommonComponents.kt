package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.ui.theme.*
import java.io.File

@Composable
fun ClothingImageThumbnail(
    imageUri: String,
    category: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(DarkNavyBlue)
            .border(1.dp, RoyalSlate.copy(alpha = 0.5f), RoundedCornerShape(14.dp)),
        contentAlignment = Alignment.Center
    ) {
        val isLocalFile = imageUri.startsWith("/") || imageUri.startsWith("file://") || imageUri.startsWith("content://")
        if (isLocalFile) {
            val file = if (imageUri.startsWith("file://")) File(imageUri.removePrefix("file://")) else File(imageUri)
            val model = if (imageUri.startsWith("content://")) imageUri else file

            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(model)
                    .crossfade(true)
                    .build(),
                contentDescription = category,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            // Elegant category vector icon placeholder
            val icon = getCategoryIcon(category)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(12.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = category,
                    tint = WarmCream,
                    modifier = Modifier.size(36.dp)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = category,
                    color = SlateMuted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

fun getCategoryIcon(category: String): ImageVector {
    return when (category.lowercase()) {
        "t-shirts", "t-shirt" -> Icons.Default.Checkroom
        "shirts", "shirt" -> Icons.Default.DryCleaning
        "jeans", "trousers", "shorts" -> Icons.Default.Style
        "jackets", "jacket", "hoodies" -> Icons.Default.Shield
        "shoes", "footwear" -> Icons.Default.RollerSkating
        "accessories" -> Icons.Default.Watch
        else -> Icons.Default.Checkroom
    }
}

@Composable
fun LuxuryScoreBadge(
    score: Int,
    label: String,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardElevated),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.verticalGradient(listOf(RoyalSlate, DarkNavyBlue))),
        modifier = modifier
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Text(
                text = "$score%",
                color = WarmCream,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = label,
                color = SlateMuted,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun SectionHeader(
    title: String,
    subtitle: String? = null,
    actionText: String? = null,
    onActionClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = WarmCream,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.3.sp
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    color = SlateMuted,
                    fontSize = 13.sp
                )
            }
        }
        if (actionText != null && onActionClick != null) {
            TextButton(onClick = onActionClick) {
                Text(
                    text = actionText,
                    color = WarmCream,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp
                )
            }
        }
    }
}

@Composable
fun LuxuryCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    if (onClick != null) {
        Card(
            onClick = onClick,
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = DarkNavyBlue),
            border = CardDefaults.outlinedCardBorder().copy(brush = Brush.verticalGradient(listOf(RoyalSlate, DarkNavySurface))),
            modifier = modifier,
            content = content
        )
    } else {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = DarkNavyBlue),
            border = CardDefaults.outlinedCardBorder().copy(brush = Brush.verticalGradient(listOf(RoyalSlate, DarkNavySurface))),
            modifier = modifier,
            content = content
        )
    }
}
