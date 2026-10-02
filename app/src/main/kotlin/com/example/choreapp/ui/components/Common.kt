package com.example.choreapp.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.choreapp.R
import com.example.choreapp.ui.theme.Sunshine
import com.example.choreapp.utils.Celebration
import kotlinx.coroutines.delay

@Composable
fun ScreenHeader(
    title: String,
    onBack: (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (onBack != null) {
            IconButton(onClick = onBack) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.back),
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
        }
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 4.dp)
        )
        actions()
    }
}

@Composable
fun EmojiBadge(emoji: String, color: Color, size: Int = 52, fontSize: Int = 28) {
    Box(
        modifier = Modifier
            .size(size.dp)
            .clip(CircleShape)
            .background(color.copy(alpha = 0.35f)),
        contentAlignment = Alignment.Center
    ) {
        Text(text = emoji, fontSize = fontSize.sp)
    }
}

@Composable
fun Bouncing(
    modifier: Modifier = Modifier,
    amount: Float = 1.15f,
    durationMillis: Int = 700,
    content: @Composable () -> Unit
) {
    val transition = rememberInfiniteTransition(label = "bounce")
    val scale by transition.animateFloat(
        initialValue = 1f,
        targetValue = amount,
        animationSpec = infiniteRepeatable(tween(durationMillis), RepeatMode.Reverse),
        label = "bounceScale"
    )
    Box(modifier = modifier.scale(scale)) { content() }
}

@Composable
fun ConfirmDeleteDialog(name: String, onConfirm: () -> Unit, onCancel: () -> Unit) {
    AlertDialog(
        onDismissRequest = onCancel,
        title = { Text(stringResource(R.string.delete_confirm, name)) },
        confirmButton = {
            Button(onClick = onConfirm) { Text(stringResource(R.string.delete)) }
        },
        dismissButton = {
            TextButton(onClick = onCancel) { Text(stringResource(R.string.cancel)) }
        }
    )
}

@Composable
fun CelebrationDialog(celebration: Celebration, onDismiss: () -> Unit) {
    LaunchedEffect(celebration) {
        delay(4000)
        onDismiss()
    }

    var shown by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { shown = true }
    val popScale by animateFloatAsState(
        targetValue = if (shown) 1f else 0.2f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioHighBouncy, stiffness = Spring.StiffnessLow),
        label = "pop"
    )

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .scale(popScale),
            shape = MaterialTheme.shapes.large,
            colors = CardDefaults.cardColors(containerColor = Sunshine)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Bouncing(amount = 1.25f, durationMillis = 500) {
                    Text("🎉", fontSize = 72.sp)
                }
                Text("⭐ ✨ 🌟 ✨ ⭐", fontSize = 22.sp)
                Text(
                    text = celebration.message,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center,
                    color = Color(0xFF2D2A4A)
                )
                if (celebration.kidName.isNotBlank()) {
                    Text(
                        text = celebration.kidName,
                        style = MaterialTheme.typography.titleLarge,
                        color = Color(0xFF2D2A4A)
                    )
                }
                if (celebration.points > 0) {
                    Text(
                        text = stringResource(R.string.plus_points, celebration.points),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2E7D32)
                    )
                }
                Text(
                    text = stringResource(R.string.points_after_approval),
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center,
                    color = Color(0xFF2D2A4A)
                )
                Button(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) {
                    Text(stringResource(R.string.great))
                }
            }
        }
    }
}
