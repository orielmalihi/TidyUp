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
import com.example.choreapp.domain.model.Chore
import com.example.choreapp.domain.model.ChoreInstance
import com.example.choreapp.domain.model.ChoreStatus
import com.example.choreapp.domain.model.Cleaner

@Composable
fun ParentDashboardScreen(
    submittedChores: List<ChoreInstance>,
    chores: List<Chore>,
    cleaners: List<Cleaner>,
    onApprove: (String) -> Unit,
    onReject: (String) -> Unit,
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
                text = "Parent Dashboard",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
            IconButton(onClick = onBack) {
                Text("←", fontSize = 24.sp)
            }
        }

        if (submittedChores.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No chores to review! Great job! 🎉",
                    fontSize = 18.sp,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text(
                        text = "Chores Awaiting Review (${submittedChores.size})",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                items(submittedChores) { instance ->
                    val chore = chores.find { it.id == instance.choreId }
                    val cleaner = cleaners.find { it.id == instance.cleanerId }
                    
                    if (chore != null && cleaner != null) {
                        ParentApprovalCard(
                            chore = chore,
                            cleaner = cleaner,
                            onApprove = { onApprove(instance.id) },
                            onReject = { onReject(instance.id) }
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
    onReject: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF9C4))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = cleaner.name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = chore.name,
                        fontSize = 14.sp
                    )
                }
                Text(
                    text = cleaner.avatar,
                    fontSize = 32.sp
                )
            }

            Text(
                text = chore.description.ifEmpty { "Review and approve this completed chore" },
                fontSize = 12.sp,
                color = Color.Gray
            )

            Text(
                text = "${chore.points} ${stringResource(R.string.points)}",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF4CAF50)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onReject,
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE57373))
                ) {
                    Text(stringResource(R.string.reject), fontSize = 12.sp)
                }
                Button(
                    onClick = onApprove,
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF81C784))
                ) {
                    Text(stringResource(R.string.approve), fontSize = 12.sp)
                }
            }
        }
    }
}
