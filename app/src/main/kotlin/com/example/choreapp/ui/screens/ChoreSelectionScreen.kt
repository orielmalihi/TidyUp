package com.example.choreapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import com.example.choreapp.domain.model.Chore
import com.example.choreapp.domain.model.ChoreInstance
import com.example.choreapp.domain.model.ChoreStatus
import com.example.choreapp.utils.ColorUtils

@Composable
fun ChoreSelectionScreen(
    chores: List<Chore>,
    instances: List<ChoreInstance>,
    onSelectChore: (ChoreInstance) -> Unit,
    onSubmitChore: (String) -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.chore_selection_title),
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
            IconButton(onClick = onBack) {
                Text("←", fontSize = 24.sp)
            }
        }

        // Available Chores
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            val groupedInstances = instances.groupBy { it.status }
            
            // Pending chores
            val available = groupedInstances[ChoreStatus.AVAILABLE] ?: emptyList()
            if (available.isNotEmpty()) {
                item {
                    Text("Available Chores", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
                items(available) { instance ->
                    val chore = chores.find { it.id == instance.choreId }
                    chore?.let {
                        ChoreCard(
                            chore = it,
                            status = instance.status,
                            onClick = { onSelectChore(instance) }
                        )
                    }
                }
            }

            // Submitted chores awaiting approval
            val submitted = groupedInstances[ChoreStatus.SUBMITTED] ?: emptyList()
            if (submitted.isNotEmpty()) {
                item {
                    Text("Submitted (Awaiting Approval)", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
                items(submitted) { instance ->
                    val chore = chores.find { it.id == instance.choreId }
                    chore?.let {
                        ChoreCard(
                            chore = it,
                            status = instance.status,
                            onClick = { }
                        )
                    }
                }
            }

            // Completed chores
            val completed = groupedInstances[ChoreStatus.APPROVED] ?: emptyList()
            if (completed.isNotEmpty()) {
                item {
                    Text("Completed Today ✓", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
                items(completed) { instance ->
                    val chore = chores.find { it.id == instance.choreId }
                    chore?.let {
                        ChoreCard(
                            chore = it,
                            status = instance.status,
                            onClick = { }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ChoreCard(
    chore: Chore,
    status: ChoreStatus,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .clickable(enabled = status == ChoreStatus.AVAILABLE) { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = when (status) {
                ChoreStatus.AVAILABLE -> Color.White
                ChoreStatus.SUBMITTED -> Color(0xFFFFF9C4)
                ChoreStatus.APPROVED -> Color(0xFFC8E6C9)
                else -> Color.White
            }
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = chore.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = chore.description.ifEmpty { "Complete this task" },
                    fontSize = 14.sp,
                    color = Color.Gray,
                    maxLines = 1
                )
                Text(
                    text = "${chore.points} ${stringResource(R.string.points)}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF4CAF50)
                )
            }
            Text(
                text = chore.icon,
                fontSize = 32.sp,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}
