package com.example.choreapp.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.choreapp.ui.screens.AllTimeLeaderboardScreen
import com.example.choreapp.ui.screens.ChoreManagementScreen
import com.example.choreapp.ui.screens.ChoreSelectionScreen
import com.example.choreapp.ui.screens.CleanerManagementScreen
import com.example.choreapp.ui.screens.MainLeaderboardScreen
import com.example.choreapp.ui.screens.ParentDashboardScreen
import com.example.choreapp.ui.screens.SettingsScreen
import com.example.choreapp.viewmodel.ChoreAppViewModel

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
    val navController = rememberNavController()
    val viewModel: ChoreAppViewModel = hiltViewModel()

    val allCleaners by viewModel.allCleaners.collectAsState()
    val allChores by viewModel.allChores.collectAsState()
    val todayScores by viewModel.todayScores.collectAsState()
    val allTimeScores by viewModel.allTimeScores.collectAsState()
    val settings by viewModel.settings.collectAsState()
    val choreInstances by viewModel.choreInstances.collectAsState()

    NavHost(navController = navController, startDestination = Screen.Main.route) {
        composable(Screen.Main.route) {
            MainLeaderboardScreen(
                cleaners = allCleaners,
                todayScores = todayScores,
                onNavigateToChores = { navController.navigate(Screen.Chores.route) },
                onNavigateToSettings = { navController.navigate(Screen.Settings.route) },
                onNavigateToAllTime = { navController.navigate(Screen.AllTime.route) },
                onNavigateToParentDashboard = { navController.navigate(Screen.ParentDashboard.route) }
            )
        }

        composable(Screen.Chores.route) {
            ChoreSelectionScreen(
                chores = allChores,
                instances = choreInstances,
                onSelectChore = { instance ->
                    viewModel.selectChore(instance.choreId, instance.cleanerId)
                },
                onSubmitChore = { instanceId ->
                    viewModel.submitChore(instanceId)
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.AllTime.route) {
            AllTimeLeaderboardScreen(
                scores = allTimeScores,
                cleaners = allCleaners,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                currentLanguage = settings?.language ?: "en",
                onLanguageChange = { viewModel.updateLanguage(it) },
                onNavigateToCleanerManagement = { navController.navigate(Screen.CleanerManagement.route) },
                onNavigateToChoreManagement = { navController.navigate(Screen.ChoreManagement.route) },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.ParentDashboard.route) {
            val submittedChores = choreInstances.filter { it.status == com.example.choreapp.domain.model.ChoreStatus.SUBMITTED }
            ParentDashboardScreen(
                submittedChores = submittedChores,
                chores = allChores,
                cleaners = allCleaners,
                onApprove = { choreInstanceId ->
                    viewModel.approveChore(choreInstanceId)
                },
                onReject = { choreInstanceId ->
                    viewModel.rejectChore(choreInstanceId)
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.CleanerManagement.route) {
            CleanerManagementScreen(
                cleaners = allCleaners,
                onAddCleaner = { cleaner ->
                    viewModel.addCleaner(cleaner)
                },
                onDeleteCleaner = { cleaner ->
                    // Will implement delete functionality
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.ChoreManagement.route) {
            ChoreManagementScreen(
                chores = allChores,
                onAddChore = { chore ->
                    viewModel.addChore(chore)
                },
                onDeleteChore = { chore ->
                    // Will implement delete functionality
                },
                onBack = { navController.popBackStack() }
            )
        }
    }
}

