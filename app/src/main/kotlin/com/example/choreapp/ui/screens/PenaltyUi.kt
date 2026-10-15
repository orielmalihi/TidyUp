package com.example.choreapp.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.offset
import com.example.choreapp.R
import com.example.choreapp.utils.ScoreEntry
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.sin

private const val MAX_STARS = 10

@Composable
fun PenaltyDialog(
    entries: List<ScoreEntry>,
    onApply: (fromId: String, toId: String?, amount: Int) -> Unit,
    onCancel: () -> Unit
) {
    var fromId by remember { mutableStateOf<String?>(null) }
    var amountText by remember { mutableStateOf("10") }
    var moveToOther by remember { mutableStateOf(false) }
    var toId by remember { mutableStateOf<String?>(null) }

    val from = entries.find { it.cleaner.id == fromId }
    val amount = amountText.toIntOrNull() ?: 0
    val valid = from != null && amount >= 1 && (!moveToOther || toId != null)

    AlertDialog(
        onDismissRequest = onCancel,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Image(painterResource(R.drawable.ic_stop_hand), contentDescription = null, modifier = Modifier.size(32.dp))
                Text(stringResource(R.string.penalty_title))
            }
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                Text(stringResource(R.string.penalty_who), fontWeight = FontWeight.Bold)
                HeroChips(entries, selected = fromId) {
                    fromId = it
                    if (toId == it) toId = null
                }

                OutlinedTextField(
                    value = amountText,
                    onValueChange = { v -> amountText = v.filter { it.isDigit() }.take(4) },
                    label = { Text(stringResource(R.string.penalty_amount)) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                if (entries.size > 1) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            stringResource(R.string.penalty_move),
                            modifier = Modifier.weight(1f)
                        )
                        Switch(checked = moveToOther, onCheckedChange = {
                            moveToOther = it
                            if (!it) toId = null
                        })
                    }
                    if (moveToOther) {
                        Text(stringResource(R.string.penalty_receiver), fontWeight = FontWeight.Bold)
                        HeroChips(entries.filter { it.cleaner.id != fromId }, selected = toId) { toId = it }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = { onApply(fromId!!, if (moveToOther) toId else null, amount) }, enabled = valid) {
                Text(stringResource(R.string.apply))
            }
        },
        dismissButton = {
            TextButton(onClick = onCancel) { Text("✖  " + stringResource(R.string.cancel)) }
        }
    )
}

@Composable
private fun HeroChips(entries: List<ScoreEntry>, selected: String?, onSelect: (String) -> Unit) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.horizontalScroll(rememberScrollState())
    ) {
        entries.forEach { entry ->
            FilterChip(
                selected = selected == entry.cleaner.id,
                onClick = { onSelect(entry.cleaner.id) },
                label = { Text("${entry.cleaner.avatar} ${entry.cleaner.name} (${entry.points})") },
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            )
        }
    }
}

/** Stars flying out of the penalised hero's card, either into the receiver's card or away. */
@Composable
fun PenaltyFlight(
    from: Offset,
    to: Offset?,
    points: Int,
    onFinished: () -> Unit
) {
    val count = (points / 10).coerceIn(3, MAX_STARS)
    val progress = remember { List(count) { Animatable(0f) } }
    val label = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        coroutineScope {
            progress.forEachIndexed { i, a ->
                async {
                    delay(i * 160L)
                    a.animateTo(1f, tween(1700, easing = LinearEasing))
                }
            }
            async { label.animateTo(1f, tween(2600, easing = LinearEasing)) }
        }
        onFinished()
    }

    Box {
        progress.forEachIndexed { i, a ->
            val p = a.value
            if (p > 0f && p < 1f) {
                val target = to ?: Offset(from.x, from.y + 260f)
                val sway = if (to != null) sin(PI * p).toFloat() * (120f + i * 12f) else (i - count / 2f) * 28f * p
                val x = from.x + (target.x - from.x) * p + if (to != null) 0f else sway
                val y = from.y + (target.y - from.y) * p - if (to != null) sway else 0f
                Text(
                    text = if (to != null) "⭐" else if (i % 2 == 0) "💔" else "⭐",
                    fontSize = 28.sp,
                    modifier = Modifier
                        .offset { IntOffset((x - 40f).toInt(), (y - 40f).toInt()) }
                        .alpha(if (to != null) 1f else 1f - p)
                )
            }
        }
        val l = label.value
        if (l in 0.001f..0.999f) {
            Text(
                text = "\u200E-$points",
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                color = androidx.compose.ui.graphics.Color(0xFFD32F2F),
                modifier = Modifier
                    .offset { IntOffset((from.x - 40f).toInt(), (from.y - 60f - 90f * l).toInt()) }
                    .alpha(1f - l)
            )
            if (to != null) {
                Text(
                    text = "\u200E+$points",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = androidx.compose.ui.graphics.Color(0xFF2E7D32),
                    modifier = Modifier
                        .offset { IntOffset((to.x - 40f).toInt(), (to.y - 60f - 90f * l).toInt()) }
                        .alpha(if (l > 0.4f) 1f - l else 0f)
                )
            }
        }
    }
}

fun Modifier.trackPosition(onPosition: (Offset) -> Unit): Modifier =
    this.onGloballyPositioned { c ->
        val topLeft = c.positionInRoot()
        onPosition(Offset(topLeft.x + c.size.width / 2f, topLeft.y + c.size.height / 2f))
    }
