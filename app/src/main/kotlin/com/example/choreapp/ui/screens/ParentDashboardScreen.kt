package com.example.choreapp.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.choreapp.R
import com.example.choreapp.domain.model.Chore
import com.example.choreapp.domain.model.ChoreInstance
import com.example.choreapp.domain.model.Cleaner
import com.example.choreapp.ui.components.EmojiBadge
import com.example.choreapp.ui.components.ScreenHeader
import com.example.choreapp.utils.ColorUtils

@Composable
fun ParentDashboardScreen(
    submittedChores: List<ChoreInstance>,
    chores: List<Chore>,
    cleaners: List<Cleaner>,
    onApprove: (String) -> Unit,
    onReject: (String) -> Unit,
    onBack: () -> Unit
) {
    // Entries whose chore or hero was deleted can't be reviewed, so they must not be counted.
    val reviewable = submittedChores.filter { s -> chores.any { it.id == s.choreId } && cleaners.any { it.id == s.cleanerId } }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        ScreenHeader(title = stringResource(R.string.parent_corner), onBack = onBack)

        if (reviewable.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = stringResource(R.string.nothing_to_review),
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text(
                        text = stringResource(R.string.awaiting_review, reviewable.size),
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = stringResource(R.string.review_hint),
                        fontSize = 13.sp,
                        color = Color.Gray
                    )
                }
                items(reviewable, key = { it.id }) { instance ->
                    val chore = chores.find { it.id == instance.choreId }
                    val cleaner = cleaners.find { it.id == instance.cleanerId }
                    if (chore != null && cleaner != null) {
                        ParentApprovalCard(
                            chore = chore,
                            cleaner = cleaner,
                            onApprove = { onApprove(instance.id) },
                            onReject = { onReject(instance.id) },
                            modifier = Modifier.animateItem()
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ParentApprovalCard(
    chore: Chore,
    cleaner: Cleaner,
    onApprove: () -> Unit,
    onReject: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3B0))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                EmojiBadge(cleaner.avatar, ColorUtils.hexToColor(cleaner.color), size = 52, fontSize = 28)
                Column(modifier = Modifier.weight(1f)) {
                    Text(cleaner.name, style = MaterialTheme.typography.titleMedium)
                    Text("${chore.icon} ${chore.name}", fontSize = 15.sp)
                }
                Text(
                    text = "⭐ ${chore.points}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFFE08A00)
                )
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = onReject,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE57373))
                ) { Text(stringResource(R.string.reject)) }
                Button(
                    onClick = onApprove,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF43A047))
                ) { Text(stringResource(R.string.approve)) }
            }
        }
    }
}
