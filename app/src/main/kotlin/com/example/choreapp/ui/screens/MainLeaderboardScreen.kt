package com.example.choreapp.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.choreapp.R
import com.example.choreapp.ui.components.Bouncing
import com.example.choreapp.ui.components.EmojiBadge
import com.example.choreapp.ui.components.ScreenHeader
import com.example.choreapp.utils.ColorUtils
import com.example.choreapp.utils.ScoreEntry

private val medals = listOf("🥇", "🥈", "🥉")

@Composable
fun MainLeaderboardScreen(
    entries: List<ScoreEntry>,
    onNavigateToChores: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToAllTime: () -> Unit,
    onNavigateToParentDashboard: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        ScreenHeader(title = stringResource(R.string.main_screen_title)) {
            IconButton(onClick = onNavigateToAllTime) { Text("👑", fontSize = 26.sp) }
            IconButton(onClick = onNavigateToParentDashboard) { Text("👨‍👩‍👧", fontSize = 26.sp) }
            IconButton(onClick = onNavigateToSettings) { Text("⚙️", fontSize = 26.sp) }
        }

        if (entries.isEmpty()) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Bouncing { Text("🧹", fontSize = 72.sp) }
                Text(
                    text = stringResource(R.string.no_kids_yet),
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }
        } else {
            val best = entries.maxOf { it.points }
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(entries, key = { it.cleaner.id }) { entry ->
                    val rank = entries.indexOf(entry)
                    CleanerScoreCard(
                        entry = entry,
                        medal = if (entry.points > 0) medals.getOrNull(rank) else null,
                        best = best,
                        modifier = Modifier.animateItem()
                    )
                }
            }
        }

        Button(
            onClick = onNavigateToChores,
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .padding(top = 8.dp),
            shape = RoundedCornerShape(30.dp)
        ) {
            Text("🧽  " + stringResource(R.string.pick_a_chore), fontSize = 20.sp)
        }
    }
}

@Composable
fun CleanerScoreCard(
    entry: ScoreEntry,
    medal: String?,
    best: Int,
    modifier: Modifier = Modifier
) {
    val color = ColorUtils.hexToColor(entry.cleaner.color)
    val animatedPoints by animateIntAsState(entry.points, tween(700), label = "points")
    val progress by animateFloatAsState(
        targetValue = if (best > 0) entry.points.toFloat() / best else 0f,
        animationSpec = tween(700),
        label = "progress"
    )

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.22f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            EmojiBadge(entry.cleaner.avatar, color, size = 60, fontSize = 34)
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = entry.cleaner.name,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    if (medal != null) {
                        Bouncing(modifier = Modifier.padding(start = 8.dp), amount = 1.2f) {
                            Text(medal, fontSize = 26.sp)
                        }
                    }
                }
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .clip(RoundedCornerShape(5.dp)),
                    color = color,
                    trackColor = color.copy(alpha = 0.2f)
                )
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "$animatedPoints",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = stringResource(R.string.points),
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }
    }
}
