package com.example.presentation.viewmodel

import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.remote.GeminiFashionClient
import com.example.data.repository.WardrobeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface AnalysisState {
    data object Idle : AnalysisState
    data object Analyzing : AnalysisState
    data class Success(val message: String) : AnalysisState
    data class Error(val error: String) : AnalysisState
}

data class AddClothingForm(
    val name: String = "",
    val category: String = "T-Shirts",
    val color: String = "Navy Blue",
    val pattern: String = "Solid",
    val material: String = "Cotton",
    val style: String = "Casual",
    val season: String = "All Season",
    val tags: String = "Versatile, Everyday",
    val imageUri: String = "",
    val bitmap: Bitmap? = null
)

class AddClothingViewModel(
    private val wardrobeRepository: WardrobeRepository,
    private val geminiClient: GeminiFashionClient
) : ViewModel() {

    private val _form = MutableStateFlow(AddClothingForm())
    val form: StateFlow<AddClothingForm> = _form.asStateFlow()

    private val _analysisState = MutableStateFlow<AnalysisState>(AnalysisState.Idle)
    val analysisState: StateFlow<AnalysisState> = _analysisState.asStateFlow()

    private val _saveSuccess = MutableStateFlow(false)
    val saveSuccess: StateFlow<Boolean> = _saveSuccess.asStateFlow()

    fun setImageUri(uri: Uri, bitmap: Bitmap?) {
        _form.value = _form.value.copy(
            imageUri = uri.toString(),
            bitmap = bitmap
        )
        // Automatically trigger AI analysis if bitmap is present
        if (bitmap != null) {
            analyzeImage(bitmap)
        }
    }

    fun setBitmap(bitmap: Bitmap) {
        _form.value = _form.value.copy(bitmap = bitmap)
        analyzeImage(bitmap)
    }

    fun updateName(name: String) { _form.value = _form.value.copy(name = name) }
    fun updateCategory(category: String) { _form.value = _form.value.copy(category = category) }
    fun updateColor(color: String) { _form.value = _form.value.copy(color = color) }
    fun updatePattern(pattern: String) { _form.value = _form.value.copy(pattern = pattern) }
    fun updateMaterial(material: String) { _form.value = _form.value.copy(material = material) }
    fun updateStyle(style: String) { _form.value = _form.value.copy(style = style) }
    fun updateSeason(season: String) { _form.value = _form.value.copy(season = season) }
    fun updateTags(tags: String) { _form.value = _form.value.copy(tags = tags) }

    fun analyzeImage(bitmap: Bitmap? = _form.value.bitmap) {
        if (bitmap == null) return

        viewModelScope.launch {
            _analysisState.value = AnalysisState.Analyzing
            val result = geminiClient.analyzeClothingImage(bitmap)
            result.onSuccess { analysis ->
                _form.value = _form.value.copy(
                    name = analysis.name,
                    category = analysis.category,
                    color = analysis.color,
                    pattern = analysis.pattern,
                    material = analysis.material,
                    style = analysis.style,
                    season = analysis.season,
                    tags = analysis.tags.joinToString(", ")
                )
                _analysisState.value = AnalysisState.Success("Analyzed with Gemini AI")
            }.onFailure {
                _analysisState.value = AnalysisState.Error("AI analysis unavailable, fields set to default.")
            }
        }
    }

    fun saveClothing() {
        val current = _form.value
        if (current.name.isBlank()) return

        viewModelScope.launch {
            val finalImageUri = if (current.bitmap != null) {
                wardrobeRepository.saveBitmapToInternalStorage(current.bitmap)
            } else if (current.imageUri.isNotBlank()) {
                current.imageUri
            } else {
                "sample_cream_tee"
            }

            val tagsList = current.tags.split(",").map { it.trim() }.filter { it.isNotBlank() }

            wardrobeRepository.addClothing(
                name = current.name,
                category = current.category,
                color = current.color,
                pattern = current.pattern,
                material = current.material,
                style = current.style,
                season = current.season,
                tags = tagsList,
                imageUri = finalImageUri
            )
            _saveSuccess.value = true
        }
    }

    fun resetState() {
        _form.value = AddClothingForm()
        _analysisState.value = AnalysisState.Idle
        _saveSuccess.value = false
    }
}
