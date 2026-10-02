package com.example.choreapp.di

import android.content.Context
import com.example.choreapp.data.db.ChoreAppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun provideChoreAppDatabase(
        @ApplicationContext context: Context
    ): ChoreAppDatabase {
        return ChoreAppDatabase.getDatabase(context)
    }

    @Singleton
    @Provides
    fun provideCleanerDao(database: ChoreAppDatabase) = database.cleanerDao()

    @Singleton
    @Provides
    fun provideChoreDao(database: ChoreAppDatabase) = database.choreDao()

    @Singleton
    @Provides
    fun provideChoreInstanceDao(database: ChoreAppDatabase) = database.choreInstanceDao()

    @Singleton
    @Provides
    fun provideDailyScoreDao(database: ChoreAppDatabase) = database.dailyScoreDao()

    @Singleton
    @Provides
    fun provideAllTimeScoreDao(database: ChoreAppDatabase) = database.allTimeScoreDao()

    @Singleton
    @Provides
    fun provideSettingsDao(database: ChoreAppDatabase) = database.settingsDao()

    @Singleton
    @Provides
    fun provideCommentDao(database: ChoreAppDatabase) = database.commentDao()
}
