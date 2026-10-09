package com.example.choreapp.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.choreapp.data.backup.BackupSnapshot
import com.example.choreapp.domain.model.AllTimeScore
import com.example.choreapp.domain.model.AppSettings
import com.example.choreapp.domain.model.Chore
import com.example.choreapp.domain.model.ChoreInstance
import com.example.choreapp.domain.model.Cleaner
import com.example.choreapp.domain.model.DailyScore

@Dao
abstract class BackupDao {
    @Query("SELECT * FROM cleaners") abstract suspend fun cleaners(): List<Cleaner>
    @Query("SELECT * FROM chores") abstract suspend fun chores(): List<Chore>
    @Query("SELECT * FROM chore_instances") abstract suspend fun instances(): List<ChoreInstance>
    @Query("SELECT * FROM daily_scores") abstract suspend fun dailyScores(): List<DailyScore>
    @Query("SELECT * FROM all_time_scores") abstract suspend fun allTimeScores(): List<AllTimeScore>
    @Query("SELECT * FROM app_settings LIMIT 1") abstract suspend fun settings(): AppSettings?
    @Query("SELECT COUNT(*) FROM cleaners") abstract suspend fun cleanerCount(): Int

    @Query("DELETE FROM cleaners") abstract suspend fun clearCleaners()
    @Query("DELETE FROM chores") abstract suspend fun clearChores()
    @Query("DELETE FROM chore_instances") abstract suspend fun clearInstances()
    @Query("DELETE FROM daily_scores") abstract suspend fun clearDailyScores()
    @Query("DELETE FROM all_time_scores") abstract suspend fun clearAllTimeScores()

    @Insert(onConflict = OnConflictStrategy.REPLACE) abstract suspend fun insertCleaners(items: List<Cleaner>)
    @Insert(onConflict = OnConflictStrategy.REPLACE) abstract suspend fun insertChores(items: List<Chore>)
    @Insert(onConflict = OnConflictStrategy.REPLACE) abstract suspend fun insertInstances(items: List<ChoreInstance>)
    @Insert(onConflict = OnConflictStrategy.REPLACE) abstract suspend fun insertDailyScores(items: List<DailyScore>)
    @Insert(onConflict = OnConflictStrategy.REPLACE) abstract suspend fun insertAllTimeScores(items: List<AllTimeScore>)
    @Insert(onConflict = OnConflictStrategy.REPLACE) abstract suspend fun insertSettings(settings: AppSettings)

    @Transaction
    open suspend fun snapshot() = BackupSnapshot(
        savedAt = System.currentTimeMillis(),
        cleaners = cleaners(),
        chores = chores(),
        instances = instances(),
        dailyScores = dailyScores(),
        allTimeScores = allTimeScores(),
        settings = settings()
    )

    @Transaction
    open suspend fun replaceAll(s: BackupSnapshot) {
        clearCleaners(); clearChores(); clearInstances(); clearDailyScores(); clearAllTimeScores()
        insertCleaners(s.cleaners)
        insertChores(s.chores)
        insertInstances(s.instances)
        insertDailyScores(s.dailyScores)
        insertAllTimeScores(s.allTimeScores)
        s.settings?.let { insertSettings(it) }
    }
}