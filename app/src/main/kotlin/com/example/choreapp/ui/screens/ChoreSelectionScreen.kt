package com.example.choreapp.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.choreapp.R
import com.example.choreapp.domain.model.Chore
import com.example.choreapp.domain.model.ChoreInstance
import com.example.choreapp.domain.model.ChoreStatus
import com.example.choreapp.domain.model.Cleaner
import com.example.choreapp.ui.components.Bouncing
import com.example.choreapp.ui.components.EmojiBadge
import com.example.choreapp.ui.components.ScreenHeader
import com.example.choreapp.ui.theme.Cream
import com.example.choreapp.utils.ChoreBoard
import com.example.choreapp.utils.ColorUtils

@Composable
fun ChoreSelectionScreen(
    cleaners: List<Cleaner>,
    chores: List<Chore>,
    instances: List<ChoreInstance>,
    onClaim: (choreId: String, cleanerId: String) -> Unit,
    onDone: (instanceId: String) -> Unit,
    onPutBack: (instanceId: String) -> Unit,
    onBack: () -> Unit
) {
    var selectedId by rememberSaveable { mutableStateOf<String?>(null) }
    val kid = cleaners.find { it.id == selectedId } ?: cleaners.firstOrNull()

    // The whole screen takes on the colour of the kid who is choosing.
    val kidColor = kid?.let { ColorUtils.hexToColor(it.color) } ?: Cream
    val background by animateColorAsState(lerp(Cream, kidColor, 0.4f), tween(400), label = "kidBackground")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(background)
            .padding(16.dp)
    ) {
        ScreenHeader(title = stringResource(R.string.chore_selection_title), onBack = onBack)

        if (kid == null) {
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
            return@Column
        }

        Text(
            text = stringResource(R.string.who_is_tidying),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(bottom = 12.dp)
        ) {
            items(cleaners, key = { it.id }) { c ->
                KidChip(cleaner = c, selected = c.id == kid.id, onClick = { selectedId = c.id })
            }
        }

        val mine = ChoreBoard.forKid(instances, kid.id, ChoreStatus.SELECTED)
        val waiting = ChoreBoard.forKid(instances, kid.id, ChoreStatus.SUBMITTED)
        val done = ChoreBoard.forKid(instances, kid.id, ChoreStatus.APPROVED)
        val available = ChoreBoard.available(chores, instances)
        val choreById = chores.associateBy { it.id }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (mine.isNotEmpty()) {
                item { SectionTitle(stringResource(R.string.working_on_it)) }
                items(mine, key = { "mine-${it.id}" }) { instance ->
                    choreById[instance.choreId]?.let { chore ->
                        ChoreCard(
                            chore = chore,
                            container = Color.White,
                            modifier = Modifier.animateItem(),
                            actions = {
                                TextButton(onClick = { onPutBack(instance.id) }) {
                                    Text(stringResource(R.string.put_back))
                                }
                                Button(onClick = { onDone(instance.id) }) {
                                    Text(stringResource(R.string.im_done))
                                }
                            }
                        )
                    }
                }
            }

            if (waiting.isNotEmpty()) {
                item { SectionTitle(stringResource(R.string.waiting_for_parent)) }
                items(waiting, key = { "wait-${it.id}" }) { instance ->
                    choreById[instance.choreId]?.let {
                        ChoreCard(chore = it, container = Color(0xFFFFF3B0), modifier = Modifier.animateItem(), trailing = "⏳")
                    }
                }
            }

            item { SectionTitle(stringResource(R.string.available_chores)) }
            if (available.isEmpty()) {
                item {
                    Text(
                        text = stringResource(R.string.nothing_available),
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    )
                }
            }
            items(available, key = { "free-${it.id}" }) { chore ->
                ChoreCard(
                    chore = chore,
                    container = Color.White,
                    modifier = Modifier
                        .animateItem()
                        .clickable { onClaim(chore.id, kid.id) }
                )
            }

            if (done.isNotEmpty()) {
                item { SectionTitle(stringResource(R.string.done_today)) }
                items(done, key = { "done-${it.id}" }) { instance ->
                    choreById[instance.choreId]?.let {
                        ChoreCard(chore = it, container = Color(0xFFC8E6C9), modifier = Modifier.animateItem(), trailing = "✅")
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.padding(top = 6.dp)
    )
}

@Composable
private fun KidChip(cleaner: Cleaner, selected: Boolean, onClick: () -> Unit) {
    val color = ColorUtils.hexToColor(cleaner.color)
    val scale by animateFloatAsState(if (selected) 1.12f else 1f, label = "chipScale")
    Column(
        modifier = Modifier
            .scale(scale)
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .background(color.copy(alpha = if (selected) 0.9f else 0.4f), RoundedCornerShape(50))
                .padding(4.dp)
        ) {
            EmojiBadge(cleaner.avatar, Color.White, size = 56, fontSize = 30)
        }
        Text(
            text = cleaner.name,
            fontWeight = if (selected) FontWeight.ExtraBold else FontWeight.Normal,
            fontSize = 14.sp,
            maxLines = 1
        )
    }
}

@Composable
private fun ChoreCard(
    chore: Chore,
    container: Color,
    modifier: Modifier = Modifier,
    trailing: String? = null,
    actions: (@Composable () -> Unit)? = null
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = container),
        border = BorderStroke(1.dp, Color.Black.copy(alpha = 0.06f))
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                EmojiBadge(chore.icon, Color(0xFFFFD166), size = 52, fontSize = 28)
                Column(modifier = Modifier.weight(1f)) {
                    Text(chore.name, style = MaterialTheme.typography.titleMedium)
                    if (chore.description.isNotBlank()) {
                        Text(chore.description, fontSize = 13.sp, color = Color.Gray, maxLines = 1)
                    }
                }
                Text(
                    text = "⭐ ${chore.points}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFFE08A00)
                )
                if (trailing != null) Text(trailing, fontSize = 24.sp)
            }
            if (actions != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
                    verticalAlignment = Alignment.CenterVertically
                ) { actions() }
            }
        }
    }
}
