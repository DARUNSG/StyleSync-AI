package com.example.presentation.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.OutfitPlannerApp
import com.example.presentation.add_clothing.AddClothingScreen
import com.example.presentation.favorites.FavoritesScreen
import com.example.presentation.home.HomeScreen
import com.example.presentation.onboarding.WelcomeOnboardingScreen
import com.example.presentation.planner.OutfitPlannerScreen
import com.example.presentation.profile.ProfileScreen
import com.example.presentation.wardrobe.WardrobeScreen
import com.example.presentation.recommendation.RecommendationResultScreen
import com.example.presentation.viewmodel.*
import com.example.ui.theme.*

enum class AppTab(val title: String, val icon: ImageVector) {
    HOME("Home", Icons.Default.Home),
    WARDROBE("Wardrobe", Icons.Default.Checkroom),
    PLANNER("Planner", Icons.Default.AutoAwesome),
    SAVED("Saved", Icons.Default.Bookmark),
    PROFILE("Profile", Icons.Default.Person)
}

enum class SubScreen {
    NONE,
    ADD_CLOTHING,
    RECOMMENDATION_RESULT
}

@Composable
fun AppNavigation(
    app: OutfitPlannerApp,
    modifier: Modifier = Modifier
) {
    val factory = remember { AppViewModelFactory(app.container) }
    val userSession by app.container.userPreferencesRepository.userSession.collectAsState()

    val homeViewModel: HomeViewModel = viewModel(factory = factory)
    val wardrobeViewModel: WardrobeViewModel = viewModel(factory = factory)
    val addClothingViewModel: AddClothingViewModel = viewModel(factory = factory)
    val outfitPlannerViewModel: OutfitPlannerViewModel = viewModel(factory = factory)
    val favoritesViewModel: FavoritesViewModel = viewModel(factory = factory)
    val profileViewModel: ProfileViewModel = viewModel(factory = factory)

    var currentTab by remember { mutableStateOf(AppTab.HOME) }
    var currentSubScreen by remember { mutableStateOf(SubScreen.NONE) }

    // Handle back button for sub-screens
    BackHandler(enabled = currentSubScreen != SubScreen.NONE) {
        currentSubScreen = SubScreen.NONE
    }

    AnimatedContent(
        targetState = userSession.hasCompletedOnboarding,
        transitionSpec = {
            fadeIn(animationSpec = tween(500)) + slideInVertically(animationSpec = tween(500)) { height -> height / 4 } togetherWith
                    fadeOut(animationSpec = tween(400)) + slideOutVertically(animationSpec = tween(400)) { height -> -height / 4 }
        },
        label = "onboarding_transition"
    ) { hasCompleted ->
        if (!hasCompleted) {
            WelcomeOnboardingScreen(
                onComplete = { name, gender ->
                    app.container.userPreferencesRepository.saveUserOnboarding(name, gender)
                }
            )
        } else {
            Scaffold(
                modifier = modifier.fillMaxSize(),
                containerColor = DeepCharcoalRose,
                bottomBar = {
                    AnimatedVisibility(
                        visible = currentSubScreen == SubScreen.NONE,
                        enter = slideInVertically(animationSpec = tween(300)) { it } + fadeIn(),
                        exit = slideOutVertically(animationSpec = tween(300)) { it } + fadeOut()
                    ) {
                        NavigationBar(
                            containerColor = DarkSurfaceRose,
                            contentColor = CreamIvory,
                            tonalElevation = 8.dp,
                            modifier = Modifier
                                .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                                .border(
                                    1.dp,
                                    Brush.verticalGradient(listOf(BorderLavender, DarkSurfaceRose)),
                                    RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                                )
                        ) {
                            AppTab.entries.forEach { tab ->
                                val isSelected = currentTab == tab
                                NavigationBarItem(
                                    selected = isSelected,
                                    onClick = { currentTab = tab },
                                    icon = {
                                        Icon(
                                            imageVector = tab.icon,
                                            contentDescription = tab.title,
                                            modifier = Modifier.size(22.dp)
                                        )
                                    },
                                    label = {
                                        Text(
                                            text = tab.title,
                                            fontSize = 11.sp,
                                            fontWeight = if (isSelected) androidx.compose.ui.text.font.FontWeight.Bold else androidx.compose.ui.text.font.FontWeight.Normal
                                        )
                                    },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = DeepCharcoalRose,
                                        selectedTextColor = CreamIvory,
                                        indicatorColor = CreamIvory,
                                        unselectedIconColor = TextMutedRose,
                                        unselectedTextColor = TextMutedRose
                                    ),
                                    modifier = Modifier.testTag("nav_item_${tab.name.lowercase()}")
                                )
                            }
                        }
                    }
                }
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    when (currentSubScreen) {
                        SubScreen.ADD_CLOTHING -> {
                            AddClothingScreen(
                                viewModel = addClothingViewModel,
                                onNavigateBack = { currentSubScreen = SubScreen.NONE }
                            )
                        }
                        SubScreen.RECOMMENDATION_RESULT -> {
                            RecommendationResultScreen(
                                viewModel = outfitPlannerViewModel,
                                onNavigateBack = { currentSubScreen = SubScreen.NONE }
                            )
                        }
                        SubScreen.NONE -> {
                            AnimatedContent(
                                targetState = currentTab,
                                transitionSpec = {
                                    fadeIn(animationSpec = tween(300)) togetherWith fadeOut(animationSpec = tween(200))
                                },
                                label = "tab_content_transition"
                            ) { tab ->
                                when (tab) {
                                    AppTab.HOME -> {
                                        HomeScreen(
                                            viewModel = homeViewModel,
                                            onNavigateToPlanner = { currentTab = AppTab.PLANNER },
                                            onNavigateToWardrobe = { currentTab = AppTab.WARDROBE },
                                            onNavigateToAddClothing = { currentSubScreen = SubScreen.ADD_CLOTHING },
                                            onNavigateToSaved = { currentTab = AppTab.SAVED }
                                        )
                                    }
                                    AppTab.WARDROBE -> {
                                        WardrobeScreen(
                                            viewModel = wardrobeViewModel,
                                            onNavigateToAddClothing = { currentSubScreen = SubScreen.ADD_CLOTHING }
                                        )
                                    }
                                    AppTab.PLANNER -> {
                                        OutfitPlannerScreen(
                                            viewModel = outfitPlannerViewModel,
                                            onNavigateToResult = { currentSubScreen = SubScreen.RECOMMENDATION_RESULT }
                                        )
                                    }
                                    AppTab.SAVED -> {
                                        FavoritesScreen(
                                            viewModel = favoritesViewModel,
                                            onNavigateToPlanner = { currentTab = AppTab.PLANNER }
                                        )
                                    }
                                    AppTab.PROFILE -> {
                                        ProfileScreen(
                                            viewModel = profileViewModel
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
