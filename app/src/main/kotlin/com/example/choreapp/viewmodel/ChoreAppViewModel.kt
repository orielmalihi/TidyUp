package com.example.choreapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.choreapp.data.repository.AllTimeScoreRepository
import com.example.choreapp.data.repository.ChoreInstanceRepository
import com.example.choreapp.data.repository.ChoreRepository
import com.example.choreapp.data.repository.CleanerRepository
import com.example.choreapp.data.repository.CommentRepository
import com.example.choreapp.data.repository.DailyScoreRepository
import com.example.choreapp.data.repository.SettingsRepository
import com.example.choreapp.domain.model.AllTimeScore
import com.example.choreapp.domain.model.AppSettings
import com.example.choreapp.domain.model.Chore
import com.example.choreapp.domain.model.ChoreInstance
import com.example.choreapp.domain.model.ChoreStatus
import com.example.choreapp.domain.model.Cleaner
import com.example.choreapp.domain.model.Comment
import com.example.choreapp.domain.model.DailyScore
import com.example.choreapp.utils.Celebration
import com.example.choreapp.utils.DateUtils
import com.example.choreapp.utils.Leaderboard
import com.example.choreapp.utils.ScoreEntry
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ChoreAppViewModel @Inject constructor(
    private val cleanerRepository: CleanerRepository,
    private val choreRepository: ChoreRepository,
    private val choreInstanceRepository: ChoreInstanceRepository,
    private val dailyScoreRepository: DailyScoreRepository,
    private val allTimeScoreRepository: AllTimeScoreRepository,
    private val commentRepository: CommentRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    // Re-emits when midnight passes so every "today" screen starts from zero automatically.
    private val today: Flow<String> = flow {
        while (true) {
            emit(DateUtils.getTodayDate())
            delay(30_000)
        }
    }.distinctUntilChanged()

    val allCleaners: StateFlow<List<Cleaner>> = cleanerRepository.getAllCleaners()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val allChores: StateFlow<List<Chore>> = choreRepository.getAllChores()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val todayLeaderboard: StateFlow<List<ScoreEntry>> = combine(
        allCleaners,
        today.flatMapLatest { dailyScoreRepository.getScoresForDate(it) }
    ) { cleaners, scores -> Leaderboard.daily(cleaners, scores) }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val allTimeLeaderboard: StateFlow<List<ScoreEntry>> = combine(
        allCleaners,
        allTimeScoreRepository.getAllTimeScores()
    ) { cleaners, scores -> Leaderboard.allTime(cleaners, scores) }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val choreInstances: StateFlow<List<ChoreInstance>> = today
        .flatMapLatest { choreInstanceRepository.getInstancesByDate(it) }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val settings: StateFlow<AppSettings?> = settingsRepository.getSettings()
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    private val _celebration = MutableStateFlow<Celebration?>(null)
    val celebration: StateFlow<Celebration?> = _celebration.asStateFlow()

    init {
        viewModelScope.launch {
            ensureSettings()
            seedDefaultComments()
            seedDefaultChores()
        }
    }

    private suspend fun ensureSettings() {
        if (settingsRepository.getSettings().first() == null) {
            settingsRepository.insertSettings(AppSettings(language = deviceLanguage()))
        }
    }

    private fun deviceLanguage() = if (Locale.getDefault().language == "he") "he" else "en"

    private suspend fun seedDefaultComments() {
        if (commentRepository.getCommentCount(CATEGORY_GOOD_JOB) > 0) return
        DEFAULT_COMMENTS.forEach { (en, he) ->
            commentRepository.addComment(Comment(textEn = en, textHe = he, category = CATEGORY_GOOD_JOB))
        }
    }

    // Only on a fresh install, so deleted chores never come back.
    private suspend fun seedDefaultChores() {
        if (cleanerRepository.getAllCleaners().first().isNotEmpty()) return
        if (choreRepository.getAllChores().first().isNotEmpty()) return
        val hebrew = deviceLanguage() == "he"
        DEFAULT_CHORES.forEach { chore ->
            choreRepository.addChore(
                Chore(name = if (hebrew) chore.he else chore.en, points = chore.points, icon = chore.icon)
            )
        }
    }

    fun addCleaner(cleaner: Cleaner) {
        viewModelScope.launch { cleanerRepository.addCleaner(cleaner) }
    }

    fun deleteCleaner(cleaner: Cleaner) {
        viewModelScope.launch { cleanerRepository.deleteCleaner(cleaner) }
    }

    fun addChore(chore: Chore) {
        viewModelScope.launch { choreRepository.addChore(chore) }
    }

    fun deleteChore(chore: Chore) {
        viewModelScope.launch { choreRepository.deleteChore(chore) }
    }

    fun claimChore(choreId: String, cleanerId: String) {
        viewModelScope.launch {
            val alreadyTaken = choreInstances.value.any {
                it.choreId == choreId && it.status in TAKEN_STATUSES
            }
            if (alreadyTaken) return@launch
            choreInstanceRepository.addInstance(
                ChoreInstance(
                    choreId = choreId,
                    cleanerId = cleanerId,
                    date = DateUtils.getTodayDate(),
                    status = ChoreStatus.SELECTED
                )
            )
        }
    }

    fun unclaimChore(instanceId: String) {
        viewModelScope.launch {
            val instance = choreInstanceRepository.getInstanceById(instanceId) ?: return@launch
            if (instance.status == ChoreStatus.SELECTED) choreInstanceRepository.deleteInstance(instance)
        }
    }

    fun submitChore(instanceId: String) {
        viewModelScope.launch {
            val instance = choreInstanceRepository.getInstanceById(instanceId) ?: return@launch
            if (instance.status != ChoreStatus.SELECTED) return@launch
            choreInstanceRepository.updateInstanceStatus(instanceId, ChoreStatus.SUBMITTED)

            val chore = choreRepository.getChoreById(instance.choreId)
            val kid = cleanerRepository.getCleanerById(instance.cleanerId)
            val comment = commentRepository.getRandomComment(CATEGORY_GOOD_JOB)
            val hebrew = settings.value?.language == "he"
            val message = when {
                comment == null -> if (hebrew) "עבודה נהדרת!" else "Good job!"
                hebrew -> comment.textHe
                else -> comment.textEn
            }
            _celebration.value = Celebration(kid?.name.orEmpty(), message, chore?.points ?: 0)
        }
    }

    fun dismissCelebration() {
        _celebration.value = null
    }

    fun approveChore(instanceId: String) {
        viewModelScope.launch {
            val instance = choreInstanceRepository.getInstanceById(instanceId) ?: return@launch
            if (instance.status != ChoreStatus.SUBMITTED) return@launch
            val chore = choreRepository.getChoreById(instance.choreId) ?: return@launch

            choreInstanceRepository.updateInstanceStatus(instanceId, ChoreStatus.APPROVED)

            val date = DateUtils.getTodayDate()
            if (dailyScoreRepository.getScoreForCleanerOnDate(instance.cleanerId, date) == null) {
                dailyScoreRepository.addScore(DailyScore(cleanerId = instance.cleanerId, date = date, points = 0))
            }
            dailyScoreRepository.addPointsToCleanerOnDate(instance.cleanerId, date, chore.points)

            if (allTimeScoreRepository.getScoreForCleaner(instance.cleanerId) == null) {
                allTimeScoreRepository.addScore(AllTimeScore(cleanerId = instance.cleanerId, totalPoints = 0))
            }
            allTimeScoreRepository.addPointsToCleaner(instance.cleanerId, chore.points)
        }
    }

    fun rejectChore(instanceId: String) {
        viewModelScope.launch {
            choreInstanceRepository.updateInstanceStatus(instanceId, ChoreStatus.SELECTED)
        }
    }

    fun updateLanguage(language: String) {
        viewModelScope.launch { settingsRepository.updateLanguage(language) }
    }

    private data class DefaultChore(val en: String, val he: String, val points: Int, val icon: String)

    companion object {
        private const val CATEGORY_GOOD_JOB = "good_job"
        private val TAKEN_STATUSES = setOf(ChoreStatus.SELECTED, ChoreStatus.SUBMITTED, ChoreStatus.APPROVED)

        private val DEFAULT_COMMENTS = listOf(
            "Good job!" to "עבודה נהדרת!",
            "Awesome work!" to "מדהים!",
            "Keep it up!" to "תמשיכו ככה!",
            "Fantastic!" to "פנטסטי!",
            "You're a star!" to "אתם כוכבים!",
            "Amazing!" to "מעולה!",
            "Excellent work!" to "עבודה מצוינת!",
            "You rock!" to "אתם אלופים!",
            "Super job!" to "עבודת על!",
            "Well done!" to "כל הכבוד!"
        )

        private val DEFAULT_CHORES = listOf(
            DefaultChore("Make the bed", "לסדר את המיטה", 10, "🛏️"),
            DefaultChore("Tidy the toys", "לסדר את הצעצועים", 20, "🧸"),
            DefaultChore("Set the table", "לערוך את השולחן", 30, "🍽️"),
            DefaultChore("Do the dishes", "לשטוף כלים", 50, "🧼"),
            DefaultChore("Take out the trash", "להוציא את הזבל", 40, "🗑️"),
            DefaultChore("Vacuum the room", "לשאוב את החדר", 100, "🧹")
        )
    }
}
