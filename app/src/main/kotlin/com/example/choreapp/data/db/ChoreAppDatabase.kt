package com.example.choreapp.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.choreapp.domain.model.AllTimeScore
import com.example.choreapp.domain.model.AppSettings
import com.example.choreapp.domain.model.Chore
import com.example.choreapp.domain.model.ChoreInstance
import com.example.choreapp.domain.model.Cleaner
import com.example.choreapp.domain.model.Comment
import com.example.choreapp.domain.model.DailyScore

@Database(
    entities = [
        Cleaner::class,
        Chore::class,
        ChoreInstance::class,
        DailyScore::class,
        AllTimeScore::class,
        AppSettings::class,
        Comment::class
    ],
    version = 1,
    exportSchema = false
)
abstract class ChoreAppDatabase : RoomDatabase() {
    abstract fun cleanerDao(): CleanerDao
    abstract fun choreDao(): ChoreDao
    abstract fun choreInstanceDao(): ChoreInstanceDao
    abstract fun dailyScoreDao(): DailyScoreDao
    abstract fun allTimeScoreDao(): AllTimeScoreDao
    abstract fun settingsDao(): SettingsDao
    abstract fun commentDao(): CommentDao
    abstract fun backupDao(): BackupDao

    companion object {
        @Volatile
        private var INSTANCE: ChoreAppDatabase? = null

        fun getDatabase(context: Context): ChoreAppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ChoreAppDatabase::class.java,
                    "chore_app_database"
                )
                    .addCallback(DatabaseCallback())
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class DatabaseCallback : RoomDatabase.Callback() {
        override fun onCreate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
            super.onCreate(db)
            // Seed default data if needed
        }
    }
}
