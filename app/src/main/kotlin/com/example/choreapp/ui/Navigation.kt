package com.example.choreapp.ui

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import com.example.choreapp.R
import kotlinx.coroutines.launch
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.TextButton
import androidx.compose.runtime.remember
import java.text.DateFormat
import java.util.Date
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
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
    object AllTime : Screen("all_time")
    object Settings : Screen("settings")
    object CleanerManagement : Screen("cleaner_management")
    object ChoreManagement : Screen("chore_management")
}

@Composable
fun ChoreAppNavigation() {
    val viewModel: ChoreAppViewModel = hiltViewModel()
    val settings by viewModel.settings.collectAsState()
    val context = LocalContext.current
    val activity = context.findActivity()

    // Wait for the saved language so the first frame is never in the wrong one.
    val language = settings?.language ?: return
    val applied = LocalConfiguration.current.locales[0].language
    if (applied != Locale.forLanguageTag(language).language) {
        // The activity's own resources must be rebuilt for the new language.
        LaunchedEffect(language) {
            AppLanguage.save(context, language)
            activity?.recreate()
        }
        return
    }
    AppNavHost(viewModel, language)
}

private tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

private const val TAB_CHORES = 0
private const val TAB_POINTS = 1
private const val TAB_PARENTS = 2

// Chores, today's points and the parent corner sit side by side; swiping or tapping a tab moves between them.
@Composable
private fun MainTabs(
    chorePage: @Composable () -> Unit,
    pointsPage: @Composable () -> Unit,
    parentPage: @Composable () -> Unit
) {
    val pagerState = rememberPagerState(initialPage = TAB_POINTS) { 3 }
    val scope = rememberCoroutineScope()
    val tabs = listOf(
        TAB_CHORES to "🧽  " + stringResource(R.string.manage_chores),
        TAB_POINTS to "🏆  " + stringResource(R.string.points),
        TAB_PARENTS to "👍  " + stringResource(R.string.tab_parents)
    )

    Column(modifier = Modifier.fillMaxSize()) {
        HorizontalPager(state = pagerState, modifier = Modifier.weight(1f)) { page ->
            when (page) {
                TAB_CHORES -> chorePage()
                TAB_POINTS -> pointsPage()
                else -> parentPage()
            }
        }
        TabRow(
            selectedTabIndex = pagerState.currentPage,
            containerColor = Color.Transparent
        ) {
            tabs.forEach { (index, label) ->
                Tab(
                    selected = pagerState.currentPage == index,
                    onClick = { scope.launch { pagerState.animateScrollToPage(index) } },
                    text = { Text(label, maxLines = 1, fontSize = 14.sp) }
                )
            }
        }
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
                MainTabs(
                    chorePage = {
                        ChoreSelectionScreen(
                            cleaners = cleaners,
                            chores = chores,
                            instances = instances,
                            onClaim = viewModel::claimChore,
                            onDone = viewModel::submitChore,
                            onPutBack = viewModel::unclaimChore,
                            onAddChore = viewModel::addChore,
                            onDeleteAll = viewModel::deleteAvailableChores
                        )
                    },
                    pointsPage = {
                        MainLeaderboardScreen(
                            entries = todayBoard,
                            onNavigateToSettings = { navController.navigate(Screen.Settings.route) },
                            onNavigateToAllTime = { navController.navigate(Screen.AllTime.route) }
                        )
                    },
                    parentPage = {
                        ParentDashboardScreen(
                            submittedChores = instances.filter { it.status == ChoreStatus.SUBMITTED },
                            chores = chores,
                            cleaners = cleaners,
                            onApprove = viewModel::approveChore,
                            onReject = viewModel::rejectChore
                        )
                    }
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
                    onUpdateChore = viewModel::updateChore,
                    onDeleteAll = viewModel::deleteAllChores,
                    onDeleteChore = viewModel::deleteChore,
                    onBack = { navController.popBackStack() }
                )
            }
        }

        val pendingRestore by viewModel.pendingRestore.collectAsState()
        pendingRestore?.let { backup ->
            val saved = remember(backup) { DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT).format(Date(backup.savedAt)) }
            AlertDialog(
                onDismissRequest = {},
                title = { Text(stringResource(R.string.backup_found_title)) },
                text = { Text(stringResource(R.string.backup_found_message, backup.cleaners.size, backup.chores.size, saved)) },
                confirmButton = { Button(onClick = viewModel::restoreBackup) { Text(stringResource(R.string.restore)) } },
                dismissButton = { TextButton(onClick = viewModel::skipRestore) { Text(stringResource(R.string.no_thanks)) } }
            )
        }

        celebration?.let {
            CelebrationDialog(celebration = it, onDismiss = viewModel::dismissCelebration)
        }
    }
}
