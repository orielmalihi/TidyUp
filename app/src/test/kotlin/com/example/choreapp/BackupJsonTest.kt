package com.example.choreapp

import com.example.choreapp.data.backup.BackupJson
import com.example.choreapp.data.backup.BackupSnapshot
import com.example.choreapp.domain.model.AllTimeScore
import com.example.choreapp.domain.model.AppSettings
import com.example.choreapp.domain.model.Chore
import com.example.choreapp.domain.model.ChoreInstance
import com.example.choreapp.domain.model.ChoreStatus
import com.example.choreapp.domain.model.Cleaner
import com.example.choreapp.domain.model.DailyScore
import org.junit.Assert.assertEquals
import org.junit.Test

class BackupJsonTest {
    @Test
    fun roundTripKeepsEverything() {
        val snapshot = BackupSnapshot(
            savedAt = 1234L,
            cleaners = listOf(Cleaner("k1", "Dana", "#FF0000", "🦄", 5L)),
            chores = listOf(Chore("c1", "Make the bed", "", 10, "🛏️", 6L)),
            instances = listOf(
                ChoreInstance("i1", "c1", "k1", "2026-10-09", ChoreStatus.SUBMITTED, 7L, 8L, null),
                ChoreInstance("i2", "c1", "k1", "2026-10-09", ChoreStatus.SELECTED, 9L)
            ),
            dailyScores = listOf(DailyScore("d1", "k1", "2026-10-09", 30)),
            allTimeScores = listOf(AllTimeScore("a1", "k1", 120)),
            settings = AppSettings(language = "he")
        )

        val decoded = BackupJson.decode(BackupJson.encode(snapshot))

        assertEquals(snapshot.copy(settings = AppSettings(language = "he", lastDailyReset = decoded.settings!!.lastDailyReset)), decoded)
    }
}