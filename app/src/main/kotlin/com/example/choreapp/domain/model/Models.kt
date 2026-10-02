package com.example.choreapp.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "cleaners")
data class Cleaner(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val color: String,
    val avatar: String = "😊",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "chores")
data class Chore(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val description: String = "",
    val points: Int,
    val icon: String = "✓",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "chore_instances")
data class ChoreInstance(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val choreId: String,
    val cleanerId: String,
    val date: String,
    val status: ChoreStatus = ChoreStatus.AVAILABLE,
    val createdAt: Long = System.currentTimeMillis(),
    val submittedAt: Long? = null,
    val completedAt: Long? = null
)

enum class ChoreStatus {
    AVAILABLE,
    SELECTED,
    IN_PROGRESS,
    SUBMITTED,
    APPROVED,
    REJECTED
}

@Entity(tableName = "daily_scores")
data class DailyScore(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val cleanerId: String,
    val date: String,
    val points: Int = 0
)

@Entity(tableName = "all_time_scores")
data class AllTimeScore(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val cleanerId: String,
    val totalPoints: Int = 0
)

@Entity(tableName = "app_settings")
data class AppSettings(
    @PrimaryKey
    val id: String = "settings",
    val language: String = "en",
    val lastDailyReset: Long = System.currentTimeMillis()
)

@Entity(tableName = "comments")
data class Comment(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val textEn: String,
    val textHe: String,
    val category: String = "good_job"
)
