package com.example.choreapp.ui.screens

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import com.example.choreapp.utils.SoundEffects
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.choreapp.R
import com.example.choreapp.ui.components.ScreenHeader

@Composable
fun SettingsScreen(
    currentLanguage: String,
    onLanguageChange: (String) -> Unit,
    onNavigateToCleanerManagement: () -> Unit,
    onNavigateToChoreManagement: () -> Unit,
    onRestoreFile: (String, (Boolean) -> Unit) -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        ScreenHeader(title = stringResource(R.string.settings_title), onBack = onBack)

        Card(modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.large) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("🌍  " + stringResource(R.string.language_label), style = MaterialTheme.typography.titleMedium)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    LanguageButton(stringResource(R.string.english), currentLanguage == "en", { onLanguageChange("en") }, Modifier.weight(1f))
                    LanguageButton(stringResource(R.string.hebrew), currentLanguage == "he", { onLanguageChange("he") }, Modifier.weight(1f))
                }
            }
        }

        val context = LocalContext.current
        var soundOn by remember { mutableStateOf(SoundEffects.enabled) }
        Card(modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.large) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("🔔  " + stringResource(R.string.sounds), style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                Switch(checked = soundOn, onCheckedChange = {
                    soundOn = it
                    SoundEffects.setEnabled(context, it)
                })
            }
        }

        val picker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
            if (uri != null) {
                val text = runCatching {
                    context.contentResolver.openInputStream(uri)?.use { it.readBytes().toString(Charsets.UTF_8) }
                }.getOrNull()
                if (text == null) {
                    Toast.makeText(context, R.string.restore_failed, Toast.LENGTH_LONG).show()
                } else {
                    onRestoreFile(text) { ok ->
                        Toast.makeText(context, if (ok) R.string.restore_done else R.string.restore_failed, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
        OutlinedButton(
            onClick = { picker.launch(arrayOf("*/*")) },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) { Text("💾  " + stringResource(R.string.restore_from_file), fontSize = 18.sp) }

        Button(
            onClick = onNavigateToCleanerManagement,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) { Text("🧑‍🎤  " + stringResource(R.string.manage_cleaners), fontSize = 18.sp) }

        Button(
            onClick = onNavigateToChoreManagement,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
        ) { Text("🧽  " + stringResource(R.string.manage_chores), fontSize = 18.sp) }
    }
}

@Composable
private fun LanguageButton(label: String, selected: Boolean, onClick: () -> Unit, modifier: Modifier) {
    if (selected) {
        Button(onClick = onClick, modifier = modifier) { Text(label) }
    } else {
        OutlinedButton(onClick = onClick, modifier = modifier) { Text(label) }
    }
}
