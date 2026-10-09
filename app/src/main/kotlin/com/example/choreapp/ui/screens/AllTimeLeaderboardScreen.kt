package com.example.choreapp.ui.screens

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.choreapp.R
import com.example.choreapp.ui.components.EmojiBadge
import com.example.choreapp.ui.components.ScreenHeader
import com.example.choreapp.utils.ColorUtils
import com.example.choreapp.utils.Leaderboard
import com.example.choreapp.utils.ScoreEntry

@Composable
fun AllTimeLeaderboardScreen(
    entries: List<ScoreEntry>,
    onBack: () -> Unit
) {
    val crowned = Leaderboard.crowned(entries)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        ScreenHeader(title = stringResource(R.string.all_time_title), onBack = onBack)

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(entries, key = { it.cleaner.id }) { entry ->
                AllTimeScoreCard(
                    rank = Leaderboard.rank(entries, entry),
                    entry = entry,
                    hasCrown = entry.cleaner.id in crowned,
                    modifier = Modifier.animateItem()
                )
            }
        }
    }
}

@Composable
fun AllTimeScoreCard(
    rank: Int,
    entry: ScoreEntry,
    hasCrown: Boolean,
    modifier: Modifier = Modifier
) {
    val color = ColorUtils.hexToColor(entry.cleaner.color)
    val gold = Color(0xFFFFC107)

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = if (hasCrown) gold.copy(alpha = 0.25f) else color.copy(alpha = 0.18f)
        ),
        border = if (hasCrown) BorderStroke(3.dp, gold) else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = "#$rank",
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.width(40.dp)
            )
            EmojiBadge(entry.cleaner.avatar, color, size = 56, fontSize = 30)
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = entry.cleaner.name,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    if (hasCrown) {
                        BobbingCrown(modifier = Modifier.padding(start = 8.dp))
                    }
                }
                Text(
                    text = "${entry.points} ${stringResource(R.string.total_points)}",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun BobbingCrown(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "crown")
    val offset by transition.animateFloat(
        initialValue = 0f,
        targetValue = -6f,
        animationSpec = infiniteRepeatable(tween(600), RepeatMode.Reverse),
        label = "crownOffset"
    )
    Text("👑", fontSize = 28.sp, modifier = modifier.offset(y = offset.dp))
}
