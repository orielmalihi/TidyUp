package com.example.choreapp

import com.example.choreapp.domain.model.AllTimeScore
import com.example.choreapp.domain.model.Chore
import com.example.choreapp.domain.model.ChoreInstance
import com.example.choreapp.domain.model.ChoreStatus
import com.example.choreapp.domain.model.Cleaner
import com.example.choreapp.domain.model.DailyScore
import com.example.choreapp.utils.ChoreBoard
import com.example.choreapp.utils.DateUtils
import com.example.choreapp.utils.Leaderboard
import com.example.choreapp.utils.ScoreEntry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class LeaderboardTest {
    private val dana = Cleaner(id = "d", name = "Dana", color = "#FF6B6B")
    private val omer = Cleaner(id = "o", name = "Omer", color = "#4ECDC4")
    private val noa = Cleaner(id = "n", name = "Noa", color = "#45B7D1")

    @Test
    fun daily_includesKidsWithNoScoreAsZero() {
        val board = Leaderboard.daily(
            listOf(dana, omer),
            listOf(DailyScore(cleanerId = "d", date = "2026-01-01", points = 30))
        )
        assertEquals(listOf("Dana" to 30, "Omer" to 0), board.map { it.cleaner.name to it.points })
    }

    @Test
    fun daily_sortsByPointsDescending() {
        val board = Leaderboard.daily(
            listOf(dana, omer, noa),
            listOf(
                DailyScore(cleanerId = "d", date = "2026-01-01", points = 10),
                DailyScore(cleanerId = "o", date = "2026-01-01", points = 50),
                DailyScore(cleanerId = "n", date = "2026-01-01", points = 20)
            )
        )
        assertEquals(listOf("Omer", "Noa", "Dana"), board.map { it.cleaner.name })
    }

    @Test
    fun allTime_sumsAndSorts() {
        val board = Leaderboard.allTime(
            listOf(dana, omer),
            listOf(AllTimeScore(cleanerId = "o", totalPoints = 120))
        )
        assertEquals("Omer", board.first().cleaner.name)
        assertEquals(120, board.first().points)
        assertEquals(0, board.last().points)
    }

    @Test
    fun ties_areSortedByNameForStableOrder() {
        val board = Leaderboard.daily(listOf(omer, dana), emptyList())
        assertEquals(listOf("Dana", "Omer"), board.map { it.cleaner.name })
    }

    @Test
    fun crown_goesToTopScorer() {
        val board = listOf(ScoreEntry(omer, 90), ScoreEntry(dana, 40))
        assertEquals(setOf("o"), Leaderboard.crowned(board))
    }

    @Test
    fun crown_isSharedOnATie() {
        val board = listOf(ScoreEntry(omer, 60), ScoreEntry(dana, 60), ScoreEntry(noa, 10))
        assertEquals(setOf("o", "d"), Leaderboard.crowned(board))
    }

    @Test
    fun crown_isNotAwardedWhenNobodyScored() {
        assertTrue(Leaderboard.crowned(listOf(ScoreEntry(omer, 0), ScoreEntry(dana, 0))).isEmpty())
        assertTrue(Leaderboard.crowned(emptyList()).isEmpty())
    }
}

class ChoreBoardTest {
    private val sweep = Chore(id = "c1", name = "Sweep", points = 20)
    private val dishes = Chore(id = "c2", name = "Dishes", points = 50)
    private val toys = Chore(id = "c3", name = "Toys", points = 10)

    private fun instance(choreId: String, kid: String, status: ChoreStatus) =
        ChoreInstance(choreId = choreId, cleanerId = kid, date = "2026-01-01", status = status)

    @Test
    fun available_hidesChoresThatAreClaimedSubmittedOrApproved() {
        val instances = listOf(
            instance("c1", "d", ChoreStatus.SELECTED),
            instance("c2", "o", ChoreStatus.SUBMITTED),
            instance("c3", "n", ChoreStatus.APPROVED)
        )
        assertTrue(ChoreBoard.available(listOf(sweep, dishes, toys), instances).isEmpty())
    }

    @Test
    fun available_keepsUntouchedChores() {
        val result = ChoreBoard.available(listOf(sweep, dishes), listOf(instance("c1", "d", ChoreStatus.SELECTED)))
        assertEquals(listOf(dishes), result)
    }

    @Test
    fun available_neverHidesChoreWithOnlyAvailableOrRejectedInstance() {
        val instances = listOf(
            instance("c1", "d", ChoreStatus.AVAILABLE),
            instance("c2", "d", ChoreStatus.REJECTED)
        )
        assertEquals(listOf(sweep, dishes), ChoreBoard.available(listOf(sweep, dishes), instances))
    }

    @Test
    fun forKid_filtersByKidAndStatus() {
        val mine = instance("c1", "d", ChoreStatus.SELECTED)
        val instances = listOf(mine, instance("c2", "o", ChoreStatus.SELECTED), instance("c3", "d", ChoreStatus.APPROVED))
        assertEquals(listOf(mine), ChoreBoard.forKid(instances, "d", ChoreStatus.SELECTED))
    }
}

class DateUtilsTest {
    @Test
    fun today_hasIsoFormat() {
        assertTrue(DateUtils.getTodayDate().matches(Regex("""\d{4}-\d{2}-\d{2}""")))
    }

    @Test
    fun isToday_trueOnlyForToday() {
        assertTrue(DateUtils.isToday(DateUtils.getTodayDate()))
        assertFalse(DateUtils.isToday("2000-01-01"))
    }

    @Test
    fun parseDate_rejectsGarbage() {
        assertNull(DateUtils.parseDate("not a date"))
    }

    @Test
    fun daysDifference_countsWholeDays() {
        assertEquals(3L, DateUtils.getDaysDifference("2026-03-01", "2026-03-04"))
        assertEquals(0L, DateUtils.getDaysDifference("2026-03-01", "garbage"))
    }
}
