package com.example.presentation.add_clothing

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.ClothingCategory
import com.example.presentation.viewmodel.AddClothingViewModel
import com.example.presentation.viewmodel.AnalysisState
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddClothingScreen(
    viewModel: AddClothingViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val form by viewModel.form.collectAsState()
    val analysisState by viewModel.analysisState.collectAsState()
    val saveSuccess by viewModel.saveSuccess.collectAsState()

    var categoryExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(saveSuccess) {
        if (saveSuccess) {
            viewModel.resetState()
            onNavigateBack()
        }
    }

    // Photo picker launcher (Zero storage permission required)
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            val bitmap = try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    val source = ImageDecoder.createSource(context.contentResolver, uri)
                    ImageDecoder.decodeBitmap(source)
                } else {
                    @Suppress("DEPRECATION")
                    MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
                }
            } catch (e: Exception) {
                null
            }
            viewModel.setImageUri(uri, bitmap)
        }
    }

    // Camera capture launcher
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        if (bitmap != null) {
            viewModel.setBitmap(bitmap)
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MidnightNavy,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Add Clothing Item",
                        color = WarmCream,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack, modifier = Modifier.testTag("back_button")) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = WarmCream
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MidnightNavy)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Image Selector Card
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = DarkNavyBlue),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.Brush.verticalGradient(listOf(RoyalSlate, DarkNavySurface))),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
            ) {
                if (form.bitmap != null) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Image(
                            bitmap = form.bitmap!!.asImageBitmap(),
                            contentDescription = "Selected Clothing",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )

                        // Retake / Change Row
                        Row(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    photoPickerLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = DarkNavyBlue.copy(alpha = 0.9f), contentColor = WarmCream),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text("Change Photo", fontSize = 12.sp)
                            }
                        }
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(CardElevated),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AddPhotoAlternate,
                                contentDescription = null,
                                tint = WarmCream,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Upload or Capture Clothing Photo",
                            color = WarmCream,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Gemini AI will automatically detect colors, styles & category",
                            color = SlateMuted,
                            fontSize = 12.sp,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            OutlinedButton(
                                onClick = {
                                    photoPickerLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = WarmCream),
                                border = ButtonDefaults.outlinedButtonBorder().copy(brush = androidx.compose.ui.graphics.Brush.linearGradient(listOf(RoyalSlate, WarmCream))),
                                modifier = Modifier.testTag("pick_gallery_button")
                            ) {
                                Icon(imageVector = Icons.Default.PhotoLibrary, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Gallery", fontSize = 13.sp)
                            }

                            Button(
                                onClick = { cameraLauncher.launch(null) },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = WarmCream, contentColor = MidnightNavy),
                                modifier = Modifier.testTag("capture_camera_button")
                            ) {
                                Icon(imageVector = Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Camera", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // AI Status indicator
            when (val state = analysisState) {
                is AnalysisState.Analyzing -> {
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = CardElevated),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = WarmCream,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Gemini AI analyzing fabric, color & cut...",
                                color = WarmCream,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
                is AnalysisState.Success -> {
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = DarkNavyBlue),
                        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.Brush.horizontalGradient(listOf(RoyalSlate, WarmCream))),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "AI Analysis Complete: Auto-filled garment attributes",
                                color = WarmCream,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
                is AnalysisState.Error -> {
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = CardElevated),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(imageVector = Icons.Default.Info, contentDescription = null, tint = WarningAmber, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = state.error,
                                color = WarmCream,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
                AnalysisState.Idle -> Unit
            }

            // Manual Fields / Tweak Details
            Text(
                text = "Item Information",
                color = WarmCream,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            // Name
            OutlinedTextField(
                value = form.name,
                onValueChange = { viewModel.updateName(it) },
                label = { Text("Item Name") },
                placeholder = { Text("e.g. Classic Linen Shirt") },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = luxuryTextFieldColors(),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("item_name_input")
            )

            // Category Dropdown
            ExposedDropdownMenuBox(
                expanded = categoryExpanded,
                onExpandedChange = { categoryExpanded = !categoryExpanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = form.category,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Category") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                    shape = RoundedCornerShape(14.dp),
                    colors = luxuryTextFieldColors(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                        .testTag("category_dropdown")
                )
                ExposedDropdownMenu(
                    expanded = categoryExpanded,
                    onDismissRequest = { categoryExpanded = false },
                    modifier = Modifier.background(DarkNavyBlue)
                ) {
                    ClothingCategory.entries.filter { it != ClothingCategory.ALL }.forEach { cat ->
                        DropdownMenuItem(
                            text = { Text(cat.displayName, color = WarmCream) },
                            onClick = {
                                viewModel.updateCategory(cat.displayName)
                                categoryExpanded = false
                            }
                        )
                    }
                }
            }

            // Color & Style Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = form.color,
                    onValueChange = { viewModel.updateColor(it) },
                    label = { Text("Color") },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = luxuryTextFieldColors(),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("color_input")
                )
                OutlinedTextField(
                    value = form.style,
                    onValueChange = { viewModel.updateStyle(it) },
                    label = { Text("Style") },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = luxuryTextFieldColors(),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("style_input")
                )
            }

            // Material & Season Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = form.material,
                    onValueChange = { viewModel.updateMaterial(it) },
                    label = { Text("Material") },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = luxuryTextFieldColors(),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("material_input")
                )
                OutlinedTextField(
                    value = form.season,
                    onValueChange = { viewModel.updateSeason(it) },
                    label = { Text("Season") },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = luxuryTextFieldColors(),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("season_input")
                )
            }

            // Pattern & Tags
            OutlinedTextField(
                value = form.pattern,
                onValueChange = { viewModel.updatePattern(it) },
                label = { Text("Pattern") },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = luxuryTextFieldColors(),
                modifier = Modifier.fillMaxWidth().testTag("pattern_input")
            )

            OutlinedTextField(
                value = form.tags,
                onValueChange = { viewModel.updateTags(it) },
                label = { Text("Tags (comma separated)") },
                placeholder = { Text("e.g. Trendy, Slim Fit, Minimalist") },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = luxuryTextFieldColors(),
                modifier = Modifier.fillMaxWidth().testTag("tags_input")
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Save Button
            Button(
                onClick = { viewModel.saveClothing() },
                enabled = form.name.isNotBlank(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = WarmCream,
                    contentColor = MidnightNavy,
                    disabledContainerColor = RoyalSlate,
                    disabledContentColor = SlateMuted
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("save_clothing_button")
            ) {
                Icon(imageVector = Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Save to Wardrobe", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun luxuryTextFieldColors(): TextFieldColors = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = WarmCream,
    unfocusedBorderColor = RoyalSlate,
    focusedLabelColor = WarmCream,
    unfocusedLabelColor = SlateMuted,
    focusedTextColor = WarmCream,
    unfocusedTextColor = WarmCream,
    focusedContainerColor = DarkNavyBlue,
    unfocusedContainerColor = DarkNavyBlue
)
