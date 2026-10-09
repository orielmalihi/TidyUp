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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
    onAddChore: (Chore) -> Unit,
    onDeleteAll: () -> Unit
) {
    var pendingChoreId by rememberSaveable { mutableStateOf<String?>(null) }
    var lastKidId by rememberSaveable { mutableStateOf<String?>(null) }
    var showAddChore by remember { mutableStateOf(false) }
    var showRemoveAll by remember { mutableStateOf(false) }

    // The screen takes on the colour of the kid who took a chore most recently.
    val kidColor = cleaners.find { it.id == lastKidId }?.let { ColorUtils.hexToColor(it.color) } ?: Cream
    val background by animateColorAsState(lerp(Cream, kidColor, 0.4f), tween(400), label = "kidBackground")

    val kidById = cleaners.associateBy { it.id }
    val choreById = chores.associateBy { it.id }
    val inProgress = instances.filter { it.status == ChoreStatus.SELECTED }
    val waiting = instances.filter { it.status == ChoreStatus.SUBMITTED }
    val done = instances.filter { it.status == ChoreStatus.APPROVED }
    val available = ChoreBoard.available(chores, instances)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(background)
            .padding(16.dp)
    ) {
        ScreenHeader(title = stringResource(R.string.chore_selection_title))

        if (cleaners.isEmpty()) {
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

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (inProgress.isNotEmpty()) {
                item { SectionTitle(stringResource(R.string.working_on_it)) }
                items(inProgress, key = { "mine-${it.id}" }) { instance ->
                    val chore = choreById[instance.choreId] ?: return@items
                    val kid = kidById[instance.cleanerId]
                    ChoreCard(
                        chore = chore,
                        owner = kid,
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

            if (waiting.isNotEmpty()) {
                item { SectionTitle(stringResource(R.string.waiting_for_parent)) }
                items(waiting, key = { "wait-${it.id}" }) { instance ->
                    choreById[instance.choreId]?.let {
                        ChoreCard(it, kidById[instance.cleanerId], Color(0xFFFFF3B0), Modifier.animateItem(), trailing = "⏳")
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
                    owner = null,
                    container = Color.White,
                    modifier = Modifier
                        .animateItem()
                        .clickable { pendingChoreId = chore.id }
                )
            }
            item {
                OutlinedButton(
                    onClick = { showAddChore = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                ) { Text("➕  " + stringResource(R.string.add_chore)) }
            }
            if (available.isNotEmpty()) {
                item {
                    OutlinedButton(
                        onClick = { showRemoveAll = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                    ) { Text("🗑️  " + stringResource(R.string.clear_available)) }
                }
            }

            if (done.isNotEmpty()) {
                item { SectionTitle(stringResource(R.string.done_today)) }
                items(done, key = { "done-${it.id}" }) { instance ->
                    choreById[instance.choreId]?.let {
                        ChoreCard(it, kidById[instance.cleanerId], Color(0xFFC8E6C9), Modifier.animateItem(), trailing = "✅")
                    }
                }
            }
        }
    }

    choreById[pendingChoreId]?.let { chore ->
        KidPickerDialog(
            chore = chore,
            cleaners = cleaners,
            onPick = { kid ->
                lastKidId = kid.id
                onClaim(chore.id, kid.id)
                pendingChoreId = null
            },
            onCancel = { pendingChoreId = null }
        )
    }
    if (showRemoveAll) {
        RemoveAllChoresDialog(
            onConfirm = {
                onDeleteAll()
                showRemoveAll = false
            },
            onCancel = { showRemoveAll = false },
            title = R.string.remove_available_confirm,
            confirmLabel = R.string.clear_available
        )
    }
    if (showAddChore) {
        ChoreDialog(
            initial = null,
            onConfirm = {
                it.forEach(onAddChore)
                showAddChore = false
            },
            onCancel = { showAddChore = false }
        )
    }
}

@Composable
private fun KidPickerDialog(chore: Chore, cleaners: List<Cleaner>, onPick: (Cleaner) -> Unit, onCancel: () -> Unit) {
    AlertDialog(
        onDismissRequest = onCancel,
        title = { Text("${chore.icon}  ${chore.name}") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(stringResource(R.string.who_takes_it), style = MaterialTheme.typography.titleMedium)
                LazyColumn(
                    modifier = Modifier.heightIn(max = 320.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(cleaners, key = { it.id }) { kid ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(ColorUtils.hexToColor(kid.color).copy(alpha = 0.35f), RoundedCornerShape(20.dp))
                                .clickable { onPick(kid) }
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            EmojiBadge(kid.avatar, Color.White, size = 44, fontSize = 24)
                            Text(kid.name, style = MaterialTheme.typography.titleMedium)
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = { TextButton(onClick = onCancel) { Text(stringResource(R.string.cancel)) } }
    )
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
private fun ChoreCard(
    chore: Chore,
    owner: Cleaner?,
    container: Color,
    modifier: Modifier = Modifier,
    trailing: String? = null,
    actions: (@Composable () -> Unit)? = null
) {
    val ownerColor = owner?.let { ColorUtils.hexToColor(it.color) }
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = container),
        border = BorderStroke(if (ownerColor != null) 3.dp else 1.dp, ownerColor ?: Color.Black.copy(alpha = 0.06f))
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                EmojiBadge(chore.icon, Color(0xFFFFD166), size = 52, fontSize = 28)
                Column(modifier = Modifier.weight(1f)) {
                    Text(chore.name, style = MaterialTheme.typography.titleMedium)
                    if (owner != null) {
                        Text(
                            "${owner.avatar} " + stringResource(R.string.chore_taken_by, owner.name),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    } else if (chore.description.isNotBlank()) {
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