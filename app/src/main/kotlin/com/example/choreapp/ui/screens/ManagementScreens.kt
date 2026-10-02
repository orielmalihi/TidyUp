package com.example.choreapp.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.choreapp.R
import com.example.choreapp.domain.model.Chore
import com.example.choreapp.domain.model.Cleaner
import com.example.choreapp.ui.components.ConfirmDeleteDialog
import com.example.choreapp.ui.components.EmojiBadge
import com.example.choreapp.ui.components.ScreenHeader
import com.example.choreapp.utils.ColorUtils

private val avatars = listOf("😊", "🦸", "🦄", "🐱", "🐶", "🐼", "🦊", "🐸", "🚀", "🌈")
private val choreIcons = listOf("🧹", "🧸", "🛏️", "🍽️", "🧼", "🗑️", "👕", "🌱", "🐾", "📚")

@Composable
fun CleanerManagementScreen(
    cleaners: List<Cleaner>,
    onAddCleaner: (Cleaner) -> Unit,
    onDeleteCleaner: (Cleaner) -> Unit,
    onBack: () -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var toDelete by remember { mutableStateOf<Cleaner?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        ScreenHeader(title = stringResource(R.string.manage_cleaners), onBack = onBack)

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(cleaners, key = { it.id }) { cleaner ->
                Card(modifier = Modifier
                    .fillMaxWidth()
                    .animateItem(), shape = MaterialTheme.shapes.medium) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        EmojiBadge(cleaner.avatar, ColorUtils.hexToColor(cleaner.color), size = 48, fontSize = 26)
                        Text(cleaner.name, style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                        TextButton(onClick = { toDelete = cleaner }) {
                            Text(stringResource(R.string.delete), color = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }
            item { AddButton(stringResource(R.string.add_cleaner)) { showAddDialog = true } }
        }
    }

    if (showAddDialog) {
        AddCleanerDialog(
            onConfirm = { cleaner ->
                onAddCleaner(cleaner)
                showAddDialog = false
            },
            onCancel = { showAddDialog = false }
        )
    }
    toDelete?.let { cleaner ->
        ConfirmDeleteDialog(
            name = cleaner.name,
            onConfirm = {
                onDeleteCleaner(cleaner)
                toDelete = null
            },
            onCancel = { toDelete = null }
        )
    }
}

@Composable
fun ChoreManagementScreen(
    chores: List<Chore>,
    onAddChore: (Chore) -> Unit,
    onUpdateChore: (Chore) -> Unit,
    onDeleteChore: (Chore) -> Unit,
    onBack: () -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var toEdit by remember { mutableStateOf<Chore?>(null) }
    var toDelete by remember { mutableStateOf<Chore?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        ScreenHeader(title = stringResource(R.string.manage_chores), onBack = onBack)

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(chores, key = { it.id }) { chore ->
                Card(modifier = Modifier
                    .fillMaxWidth()
                    .animateItem(), shape = MaterialTheme.shapes.medium) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        EmojiBadge(chore.icon, Color(0xFFFFD166), size = 48, fontSize = 26)
                        Column(modifier = Modifier.weight(1f)) {
                            Text(chore.name, style = MaterialTheme.typography.titleMedium)
                            Text("⭐ ${chore.points}", fontWeight = FontWeight.SemiBold, color = Color(0xFFE08A00))
                        }
                        TextButton(onClick = { toEdit = chore }) {
                            Text(stringResource(R.string.edit))
                        }
                        TextButton(onClick = { toDelete = chore }) {
                            Text(stringResource(R.string.delete), color = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }
            item { AddButton(stringResource(R.string.add_chore)) { showAddDialog = true } }
        }
    }

    if (showAddDialog) {
        ChoreDialog(
            initial = null,
            onConfirm = { newChores ->
                newChores.forEach(onAddChore)
                showAddDialog = false
            },
            onCancel = { showAddDialog = false }
        )
    }
    toEdit?.let { chore ->
        ChoreDialog(
            initial = chore,
            onConfirm = {
                onUpdateChore(it.first())
                toEdit = null
            },
            onCancel = { toEdit = null }
        )
    }
    toDelete?.let { chore ->
        ConfirmDeleteDialog(
            name = chore.name,
            onConfirm = {
                onDeleteChore(chore)
                toDelete = null
            },
            onCancel = { toDelete = null }
        )
    }
}

@Composable
private fun AddButton(label: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .padding(top = 4.dp)
    ) {
        Icon(Icons.Filled.Add, contentDescription = null)
        Spacer(modifier = Modifier.width(8.dp))
        Text(label)
    }
}

@Composable
private fun EmojiPicker(options: List<String>, selected: String, onSelect: (String) -> Unit) {
    Row(
        modifier = Modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        options.forEach { emoji ->
            val isSelected = emoji == selected
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f) else Color.Transparent)
                    .border(
                        BorderStroke(2.dp, if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent),
                        CircleShape
                    )
                    .clickable { onSelect(emoji) },
                contentAlignment = Alignment.Center
            ) { Text(emoji, fontSize = 24.sp) }
        }
    }
}

@Composable
private fun AddCleanerDialog(onConfirm: (Cleaner) -> Unit, onCancel: () -> Unit) {
    var name by remember { mutableStateOf("") }
    var colorIndex by remember { mutableStateOf(0) }
    var avatar by remember { mutableStateOf(avatars.first()) }

    AlertDialog(
        onDismissRequest = onCancel,
        title = { Text(stringResource(R.string.add_cleaner)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(stringResource(R.string.cleaner_name)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Text(stringResource(R.string.select_avatar), fontWeight = FontWeight.Bold)
                EmojiPicker(avatars, avatar) { avatar = it }
                Text(stringResource(R.string.select_color), fontWeight = FontWeight.Bold)
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ColorUtils.defaultColors.forEachIndexed { index, hex ->
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(ColorUtils.hexToColor(hex))
                                .clickable { colorIndex = index },
                            contentAlignment = Alignment.Center
                        ) {
                            if (colorIndex == index) Text("✓", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                enabled = name.isNotBlank(),
                onClick = {
                    onConfirm(Cleaner(name = name.trim(), color = ColorUtils.defaultColors[colorIndex], avatar = avatar))
                }
            ) { Text(stringResource(R.string.save)) }
        },
        dismissButton = {
            TextButton(onClick = onCancel) { Text(stringResource(R.string.cancel)) }
        }
    )
}

@Composable
fun ChoreDialog(initial: Chore?, onConfirm: (List<Chore>) -> Unit, onCancel: () -> Unit) {
    var quantity by remember { mutableStateOf("1") }
    var name by remember { mutableStateOf(initial?.name.orEmpty()) }
    var description by remember { mutableStateOf(initial?.description.orEmpty()) }
    var points by remember { mutableStateOf(initial?.points?.toString().orEmpty()) }
    var icon by remember { mutableStateOf(initial?.icon ?: choreIcons.first()) }
    var showError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onCancel,
        title = { Text(stringResource(if (initial == null) R.string.add_chore else R.string.edit_chore)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(stringResource(R.string.chore_name)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text(stringResource(R.string.chore_description)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = points,
                    onValueChange = { if (it.length <= 3 && it.all(Char::isDigit)) points = it },
                    label = { Text(stringResource(R.string.points_value)) },
                    singleLine = true,
                    isError = showError,
                    modifier = Modifier.fillMaxWidth()
                )
                if (initial == null) {
                    OutlinedTextField(
                        value = quantity,
                        onValueChange = { if (it.length <= 2 && it.all(Char::isDigit)) quantity = it },
                        label = { Text(stringResource(R.string.how_many)) },
                        supportingText = { Text(stringResource(R.string.how_many_hint)) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                if (showError) {
                    Text(stringResource(R.string.points_error), color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                }
                Text(stringResource(R.string.select_icon), fontWeight = FontWeight.Bold)
                EmojiPicker(choreIcons, icon) { icon = it }
            }
        },
        confirmButton = {
            Button(
                enabled = name.isNotBlank(),
                onClick = {
                    val value = points.toIntOrNull() ?: 0
                    val count = (quantity.toIntOrNull() ?: 1).coerceIn(1, 10)
                    if (value in 10..100) {
                        if (initial != null) {
                            onConfirm(listOf(initial.copy(name = name.trim(), description = description.trim(), points = value, icon = icon)))
                        } else {
                            onConfirm(List(count) { i ->
                                Chore(
                                    name = if (count == 1) name.trim() else "${name.trim()} ${i + 1}",
                                    description = description.trim(), points = value, icon = icon
                                )
                            })
                        }
                    } else {
                        showError = true
                    }
                }
            ) { Text(stringResource(R.string.save)) }
        },
        dismissButton = {
            TextButton(onClick = onCancel) { Text(stringResource(R.string.cancel)) }
        }
    )
}
