package com.example.choreapp.data.backup

import android.content.Context
import android.util.Log
import com.example.choreapp.data.db.BackupDao
import com.example.choreapp.data.db.ChoreAppDatabase
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BackupManager @Inject constructor(
    @ApplicationContext context: Context,
    private val database: ChoreAppDatabase,
    private val dao: BackupDao
) {
    private val store = BackupStore(context)

    private val _pendingRestore = MutableStateFlow<BackupSnapshot?>(null)
    val pendingRestore: StateFlow<BackupSnapshot?> = _pendingRestore.asStateFlow()

    // Until the user answers the restore prompt, an empty database must not overwrite the saved backup.
    @Volatile
    private var armed = false

    @OptIn(FlowPreview::class)
    fun start(scope: CoroutineScope) {
        if (!store.isSupported) return
        scope.launch(Dispatchers.IO) {
            val saved = store.read()?.let { runCatching { BackupJson.decode(it) }.getOrNull() }
            if (dao.cleanerCount() == 0 && saved != null && saved.cleaners.isNotEmpty()) {
                _pendingRestore.value = saved
            } else {
                armed = true
            }
            Log.d(TAG, "start: saved=${saved?.cleaners?.size} pending=${_pendingRestore.value != null}")

            database.invalidationTracker
                .createFlow("cleaners", "chores", "chore_instances", "daily_scores", "all_time_scores")
                .debounce(1500)
                .collect { if (armed) save() }
        }
    }

    private suspend fun save() {
        val snapshot = dao.snapshot()
        if (snapshot.cleaners.isEmpty()) return
        val ok = store.write(BackupJson.encode(snapshot))
        Log.d(TAG, "save ok=$ok")
    }

    suspend fun restore() = withContext(Dispatchers.IO) {
        val snapshot = _pendingRestore.value ?: return@withContext
        dao.replaceAll(snapshot)
        _pendingRestore.value = null
        armed = true
    }

    // Used when the user picks a backup file manually (MediaStore hides files left by a previous install).
    suspend fun restoreFromText(text: String): Boolean = withContext(Dispatchers.IO) {
        val snapshot = runCatching { BackupJson.decode(text) }.getOrNull()
        if (snapshot == null || snapshot.cleaners.isEmpty()) return@withContext false
        dao.replaceAll(snapshot)
        _pendingRestore.value = null
        armed = true
        true
    }

    fun skipRestore() {
        _pendingRestore.value = null
        armed = true
    }

    private companion object {
        const val TAG = "TidyUpBackup"
    }
}