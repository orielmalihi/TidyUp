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
import com.example.choreapp.domain.model.AllTimeScore
import com.example.choreapp.domain.model.Cleaner
import com.example.choreapp.utils.ColorUtils

@Composable
fun AllTimeLeaderboardScreen(
    scores: List<AllTimeScore>,
    cleaners: List<Cleaner>,
    onBack: () -> Unit
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
                text = stringResource(R.string.all_time_title),
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
            IconButton(onClick = onBack) {
                Text("←", fontSize = 24.sp)
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(scores.withIndex().toList()) { (index, score) ->
                val cleaner = cleaners.find { it.id == score.cleanerId }
                cleaner?.let {
                    AllTimeScoreCard(
                        rank = index + 1,
                        cleaner = it,
                        totalPoints = score.totalPoints,
                        isTopRanked = index == 0
                    )
                }
            }
        }
    }
}

@Composable
fun AllTimeScoreCard(
    rank: Int,
    cleaner: Cleaner,
    totalPoints: Int,
    isTopRanked: Boolean
) {
    val color = ColorUtils.hexToColor(cleaner.color)
    val backgroundColor = if (isTopRanked) Color(0xFFFFD700).copy(alpha = 0.2f) else color.copy(alpha = 0.1f)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(90.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isTopRanked) "👑" else "#$rank",
                    fontSize = if (isTopRanked) 32.sp else 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Column {
                    Text(
                        text = cleaner.name,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "$totalPoints ${stringResource(R.string.total_points)}",
                        fontSize = 14.sp,
                        color = color,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
            Text(text = cleaner.avatar, fontSize = 40.sp)
        }
    }
}
