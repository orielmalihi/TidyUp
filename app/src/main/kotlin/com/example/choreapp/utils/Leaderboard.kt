package com.example.choreapp.utils

import com.example.choreapp.domain.model.AllTimeScore
import com.example.choreapp.domain.model.Chore
import com.example.choreapp.domain.model.ChoreInstance
import com.example.choreapp.domain.model.ChoreStatus
import com.example.choreapp.domain.model.Cleaner
import com.example.choreapp.domain.model.DailyScore

data class ScoreEntry(val cleaner: Cleaner, val points: Int)

data class Celebration(val kidName: String, val message: String, val points: Int)

object Leaderboard {
    private val order = compareByDescending<ScoreEntry> { it.points }.thenBy { it.cleaner.name.lowercase() }

    // Competition ranking: equal points share a rank, so ties get the same medal.
    fun rank(entries: List<ScoreEntry>, entry: ScoreEntry): Int = 1 + entries.count { it.points > entry.points }

    fun daily(cleaners: List<Cleaner>, scores: List<DailyScore>): List<ScoreEntry> =
        cleaners
            .map { c -> ScoreEntry(c, scores.filter { it.cleanerId == c.id }.sumOf { it.points }) }
            .sortedWith(order)

    fun allTime(cleaners: List<Cleaner>, scores: List<AllTimeScore>): List<ScoreEntry> =
        cleaners
            .map { c -> ScoreEntry(c, scores.filter { it.cleanerId == c.id }.sumOf { it.totalPoints }) }
            .sortedWith(order)

    /** Ids of kids holding the crown: the top score, only if somebody actually scored. */
    fun crowned(entries: List<ScoreEntry>): Set<String> {
        val best = entries.maxOfOrNull { it.points } ?: return emptySet()
        if (best <= 0) return emptySet()
        return entries.filter { it.points == best }.map { it.cleaner.id }.toSet()
    }
}

object ChoreBoard {
    private val blocking = setOf(ChoreStatus.SELECTED, ChoreStatus.SUBMITTED, ChoreStatus.APPROVED)

    /** A chore can be claimed once per day, as long as nobody has it in progress or done. */
    fun available(chores: List<Chore>, instances: List<ChoreInstance>): List<Chore> {
        val taken = instances.filter { it.status in blocking }.map { it.choreId }.toSet()
        return chores.filter { it.id !in taken }
    }

    fun forKid(instances: List<ChoreInstance>, cleanerId: String, status: ChoreStatus): List<ChoreInstance> =
        instances.filter { it.cleanerId == cleanerId && it.status == status }
}
