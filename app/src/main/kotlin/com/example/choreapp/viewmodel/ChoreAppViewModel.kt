package com.example.choreapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.choreapp.data.repository.*
import com.example.choreapp.domain.model.*
import com.example.choreapp.utils.DateUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

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

    // UI State
    private val _allCleaners = MutableStateFlow<List<Cleaner>>(emptyList())
    val allCleaners: StateFlow<List<Cleaner>> = _allCleaners.asStateFlow()

    private val _allChores = MutableStateFlow<List<Chore>>(emptyList())
    val allChores: StateFlow<List<Chore>> = _allChores.asStateFlow()

    private val _todayScores = MutableStateFlow<List<DailyScore>>(emptyList())
    val todayScores: StateFlow<List<DailyScore>> = _todayScores.asStateFlow()

    private val _allTimeScores = MutableStateFlow<List<AllTimeScore>>(emptyList())
    val allTimeScores: StateFlow<List<AllTimeScore>> = _allTimeScores.asStateFlow()

    private val _settings = MutableStateFlow<AppSettings?>(null)
    val settings: StateFlow<AppSettings?> = _settings.asStateFlow()

    private val _choreInstances = MutableStateFlow<List<ChoreInstance>>(emptyList())
    val choreInstances: StateFlow<List<ChoreInstance>> = _choreInstances.asStateFlow()

    init {
        loadAllCleaners()
        loadAllChores()
        loadTodayScores()
        loadAllTimeScores()
        loadSettings()
        ensureDailyScoresExist()
        loadChoreInstances()
        seedDefaultComments()
    }

    private fun loadAllCleaners() {
        viewModelScope.launch {
            cleanerRepository.getAllCleaners().collect {
                _allCleaners.value = it
            }
        }
    }

    private fun loadAllChores() {
        viewModelScope.launch {
            choreRepository.getAllChores().collect {
                _allChores.value = it
            }
        }
    }

    private fun loadTodayScores() {
        viewModelScope.launch {
            dailyScoreRepository.getScoresForDate(DateUtils.getTodayDate()).collect {
                _todayScores.value = it.sortedByDescending { score -> score.points }
            }
        }
    }

    private fun loadAllTimeScores() {
        viewModelScope.launch {
            allTimeScoreRepository.getAllTimeScores().collect {
                _allTimeScores.value = it
            }
        }
    }

    private fun loadSettings() {
        viewModelScope.launch {
            settingsRepository.getSettings().collect {
                if (it == null) {
                    val defaultSettings = AppSettings()
                    settingsRepository.insertSettings(defaultSettings)
                    _settings.value = defaultSettings
                } else {
                    _settings.value = it
                }
            }
        }
    }

    private fun loadChoreInstances() {
        viewModelScope.launch {
            choreInstanceRepository.getInstancesByDate(DateUtils.getTodayDate()).collect {
                _choreInstances.value = it
            }
        }
    }

    private fun seedDefaultComments() {
        viewModelScope.launch {
            val count = commentRepository.getCommentCount("good_job")
            if (count == 0) {
                val defaultComments = listOf(
                    Comment(textEn = "Good job!", textHe = "עבודה נהדרת!", category = "good_job"),
                    Comment(textEn = "Awesome work!", textHe = "מדהים!", category = "good_job"),
                    Comment(textEn = "Keep it up!", textHe = "תמשיך ככה!", category = "good_job"),
                    Comment(textEn = "Fantastic!", textHe = "פנטסטי!", category = "good_job"),
                    Comment(textEn = "You're a star!", textHe = "אתה כוכב!", category = "good_job"),
                    Comment(textEn = "Amazing!", textHe = "מעולה!", category = "good_job"),
                    Comment(textEn = "Excellent work!", textHe = "עבודה מעולה!", category = "good_job"),
                    Comment(textEn = "You rock!", textHe = "אתה בטירוף!", category = "good_job"),
                    Comment(textEn = "Super job!", textHe = "עבודה סופר!", category = "good_job"),
                    Comment(textEn = "Well done!", textHe = "יפה מאוד!", category = "good_job")
                )
                for (comment in defaultComments) {
                    commentRepository.addComment(comment)
                }
            }
        }
    }

    private fun ensureDailyScoresExist() {
        viewModelScope.launch {
            val today = DateUtils.getTodayDate()
            for (cleaner in _allCleaners.value) {
                val existingScore = dailyScoreRepository.getScoreForCleanerOnDate(cleaner.id, today)
                if (existingScore == null) {
                    dailyScoreRepository.addScore(DailyScore(cleanerId = cleaner.id, date = today, points = 0))
                }
            }
        }
    }

    fun addCleaner(cleaner: Cleaner) {
        viewModelScope.launch {
            cleanerRepository.addCleaner(cleaner)
            ensureDailyScoresExist()
        }
    }

    fun addChore(chore: Chore) {
        viewModelScope.launch {
            choreRepository.addChore(chore)
        }
    }

    fun selectChore(choreId: String, cleanerId: String) {
        viewModelScope.launch {
            val instance = ChoreInstance(
                choreId = choreId,
                cleanerId = cleanerId,
                date = DateUtils.getTodayDate(),
                status = ChoreStatus.SELECTED
            )
            choreInstanceRepository.addInstance(instance)
        }
    }

    fun submitChore(choreInstanceId: String) {
        viewModelScope.launch {
            choreInstanceRepository.updateInstanceStatus(choreInstanceId, ChoreStatus.SUBMITTED)
        }
    }

    fun approveChore(choreInstanceId: String) {
        viewModelScope.launch {
            val instance = choreInstanceRepository.getInstanceById(choreInstanceId) ?: return@launch
            val chore = choreRepository.getChoreById(instance.choreId) ?: return@launch
            
            choreInstanceRepository.updateInstanceStatus(choreInstanceId, ChoreStatus.APPROVED)
            dailyScoreRepository.addPointsToCleanerOnDate(instance.cleanerId, DateUtils.getTodayDate(), chore.points)
            
            val existingScore = allTimeScoreRepository.getScoreForCleaner(instance.cleanerId)
            if (existingScore == null) {
                allTimeScoreRepository.addScore(AllTimeScore(cleanerId = instance.cleanerId, totalPoints = chore.points))
            } else {
                allTimeScoreRepository.addPointsToCleaner(instance.cleanerId, chore.points)
            }
        }
    }

    fun rejectChore(choreInstanceId: String) {
        viewModelScope.launch {
            choreInstanceRepository.updateInstanceStatus(choreInstanceId, ChoreStatus.AVAILABLE)
        }
    }

    suspend fun getRandomComment(): String {
        val comment = commentRepository.getRandomComment("good_job") ?: return "Good job!"
        return if (_settings.value?.language == "he") comment.textHe else comment.textEn
    }

    fun updateLanguage(language: String) {
        viewModelScope.launch {
            settingsRepository.updateLanguage(language)
        }
    }
}
