package com.example.choreapp.ui

import android.content.res.Configuration
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.choreapp.domain.model.ChoreStatus
import com.example.choreapp.ui.components.CelebrationDialog
import com.example.choreapp.ui.screens.AllTimeLeaderboardScreen
import com.example.choreapp.ui.screens.ChoreManagementScreen
import com.example.choreapp.ui.screens.ChoreSelectionScreen
import com.example.choreapp.ui.screens.CleanerManagementScreen
import com.example.choreapp.ui.screens.MainLeaderboardScreen
import com.example.choreapp.ui.screens.ParentDashboardScreen
import com.example.choreapp.ui.screens.SettingsScreen
import com.example.choreapp.viewmodel.ChoreAppViewModel
import java.util.Locale

sealed class Screen(val route: String) {
    object Main : Screen("main")
    object Chores : Screen("chores")
    object AllTime : Screen("all_time")
    object Settings : Screen("settings")
    object ParentDashboard : Screen("parent_dashboard")
    object CleanerManagement : Screen("cleaner_management")
    object ChoreManagement : Screen("chore_management")
}

@Composable
fun ChoreAppNavigation() {
    val viewModel: ChoreAppViewModel = hiltViewModel()
    val settings by viewModel.settings.collectAsState()

    // Wait for the saved language so the first frame is never in the wrong one.
    val language = settings?.language ?: return
    LocalizedContent(language) {
        AppNavHost(viewModel, language)
    }
}

@Composable
private fun LocalizedContent(language: String, content: @Composable () -> Unit) {
    val base = LocalContext.current
    val localized = remember(base, language) {
        val locale = Locale.forLanguageTag(language)
        val config = Configuration(base.resources.configuration).apply {
            setLocale(locale)
            setLayoutDirection(locale)
        }
        base.createConfigurationContext(config)
    }
    CompositionLocalProvider(
        LocalContext provides localized,
        LocalConfiguration provides localized.resources.configuration,
        LocalLayoutDirection provides if (language == "he") LayoutDirection.Rtl else LayoutDirection.Ltr
    ) {
        content()
    }
}

@Composable
private fun AppNavHost(viewModel: ChoreAppViewModel, language: String) {
    val navController = rememberNavController()

    val cleaners by viewModel.allCleaners.collectAsState()
    val chores by viewModel.allChores.collectAsState()
    val todayBoard by viewModel.todayLeaderboard.collectAsState()
    val allTimeBoard by viewModel.allTimeLeaderboard.collectAsState()
    val instances by viewModel.choreInstances.collectAsState()
    val celebration by viewModel.celebration.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        NavHost(
            navController = navController,
            startDestination = Screen.Main.route,
            enterTransition = { fadeIn(tween(250)) + scaleIn(tween(250), initialScale = 0.94f) },
            exitTransition = { fadeOut(tween(150)) },
            popEnterTransition = { fadeIn(tween(250)) + scaleIn(tween(250), initialScale = 0.94f) },
            popExitTransition = { fadeOut(tween(150)) }
        ) {
            composable(Screen.Main.route) {
                MainLeaderboardScreen(
                    entries = todayBoard,
                    onNavigateToChores = { navController.navigate(Screen.Chores.route) },
                    onNavigateToSettings = { navController.navigate(Screen.Settings.route) },
                    onNavigateToAllTime = { navController.navigate(Screen.AllTime.route) },
                    onNavigateToParentDashboard = { navController.navigate(Screen.ParentDashboard.route) }
                )
            }

            composable(Screen.Chores.route) {
                ChoreSelectionScreen(
                    cleaners = cleaners,
                    chores = chores,
                    instances = instances,
                    onClaim = viewModel::claimChore,
                    onDone = viewModel::submitChore,
                    onPutBack = viewModel::unclaimChore,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Screen.AllTime.route) {
                AllTimeLeaderboardScreen(
                    entries = allTimeBoard,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Screen.Settings.route) {
                SettingsScreen(
                    currentLanguage = language,
                    onLanguageChange = viewModel::updateLanguage,
                    onNavigateToCleanerManagement = { navController.navigate(Screen.CleanerManagement.route) },
                    onNavigateToChoreManagement = { navController.navigate(Screen.ChoreManagement.route) },
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Screen.ParentDashboard.route) {
                ParentDashboardScreen(
                    submittedChores = instances.filter { it.status == ChoreStatus.SUBMITTED },
                    chores = chores,
                    cleaners = cleaners,
                    onApprove = viewModel::approveChore,
                    onReject = viewModel::rejectChore,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Screen.CleanerManagement.route) {
                CleanerManagementScreen(
                    cleaners = cleaners,
                    onAddCleaner = viewModel::addCleaner,
                    onDeleteCleaner = viewModel::deleteCleaner,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Screen.ChoreManagement.route) {
                ChoreManagementScreen(
                    chores = chores,
                    onAddChore = viewModel::addChore,
                    onDeleteChore = viewModel::deleteChore,
                    onBack = { navController.popBackStack() }
                )
            }
        }

        celebration?.let {
            CelebrationDialog(celebration = it, onDismiss = viewModel::dismissCelebration)
        }
    }
}
