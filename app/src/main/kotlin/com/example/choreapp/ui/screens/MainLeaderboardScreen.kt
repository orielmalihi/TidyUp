package com.example.choreapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.choreapp.R
import com.example.choreapp.domain.model.Cleaner
import com.example.choreapp.domain.model.DailyScore
import com.example.choreapp.utils.ColorUtils

@Composable
fun MainLeaderboardScreen(
    cleaners: List<Cleaner>,
    todayScores: List<DailyScore>,
    onNavigateToChores: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToAllTime: () -> Unit,
    onNavigateToParentDashboard: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.main_screen_title),
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
            Row {
                IconButton(onClick = onNavigateToAllTime) {
                    Text("👑", fontSize = 24.sp)
                }
                IconButton(onClick = onNavigateToParentDashboard) {
                    Text("👨‍👩‍👧‍👦", fontSize = 24.sp)
                }
                IconButton(onClick = onNavigateToSettings) {
                    Text("⚙️", fontSize = 24.sp)
                }
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(todayScores) { score ->
                val cleaner = cleaners.find { it.id == score.cleanerId }
                cleaner?.let {
                    CleanerScoreCard(cleaner = it, points = score.points)
                }
            }
        }

        Button(
            onClick = onNavigateToChores,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(top = 16.dp),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(text = stringResource(R.string.chore_selection_title), fontSize = 16.sp)
        }
    }
}

@Composable
fun CleanerScoreCard(
    cleaner: Cleaner,
    points: Int
) {
    val color = ColorUtils.hexToColor(cleaner.color)
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.2f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = cleaner.name, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text(text = "$points ${stringResource(R.string.points)}", fontSize = 16.sp, color = color)
            }
            Text(text = cleaner.avatar, fontSize = 40.sp)
        }
    }
}
