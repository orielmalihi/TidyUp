package com.example.choreapp.data.backup

import com.example.choreapp.domain.model.AllTimeScore
import com.example.choreapp.domain.model.AppSettings
import com.example.choreapp.domain.model.Chore
import com.example.choreapp.domain.model.ChoreInstance
import com.example.choreapp.domain.model.ChoreStatus
import com.example.choreapp.domain.model.Cleaner
import com.example.choreapp.domain.model.DailyScore
import org.json.JSONArray
import org.json.JSONObject

data class BackupSnapshot(
    val savedAt: Long,
    val cleaners: List<Cleaner>,
    val chores: List<Chore>,
    val instances: List<ChoreInstance>,
    val dailyScores: List<DailyScore>,
    val allTimeScores: List<AllTimeScore>,
    val settings: AppSettings?
)

object BackupJson {
    private const val FORMAT = 1

    fun encode(s: BackupSnapshot): String = JSONObject().apply {
        put("format", FORMAT)
        put("savedAt", s.savedAt)
        put("cleaners", JSONArray(s.cleaners.map {
            JSONObject().put("id", it.id).put("name", it.name).put("color", it.color)
                .put("avatar", it.avatar).put("createdAt", it.createdAt)
        }))
        put("chores", JSONArray(s.chores.map {
            JSONObject().put("id", it.id).put("name", it.name).put("description", it.description)
                .put("points", it.points).put("icon", it.icon).put("createdAt", it.createdAt)
        }))
        put("instances", JSONArray(s.instances.map {
            JSONObject().put("id", it.id).put("choreId", it.choreId).put("cleanerId", it.cleanerId)
                .put("date", it.date).put("status", it.status.name).put("createdAt", it.createdAt)
                .put("submittedAt", it.submittedAt ?: JSONObject.NULL)
                .put("completedAt", it.completedAt ?: JSONObject.NULL)
        }))
        put("dailyScores", JSONArray(s.dailyScores.map {
            JSONObject().put("id", it.id).put("cleanerId", it.cleanerId).put("date", it.date).put("points", it.points)
        }))
        put("allTimeScores", JSONArray(s.allTimeScores.map {
            JSONObject().put("id", it.id).put("cleanerId", it.cleanerId).put("totalPoints", it.totalPoints)
        }))
        s.settings?.let { put("language", it.language) }
    }.toString(2)

    fun decode(text: String): BackupSnapshot {
        val root = JSONObject(text)
        fun <T> JSONArray.map(transform: (JSONObject) -> T) = (0 until length()).map { transform(getJSONObject(it)) }
        fun JSONObject.longOrNull(key: String) = if (isNull(key)) null else getLong(key)

        return BackupSnapshot(
            savedAt = root.optLong("savedAt"),
            cleaners = root.getJSONArray("cleaners").map {
                Cleaner(it.getString("id"), it.getString("name"), it.getString("color"), it.getString("avatar"), it.getLong("createdAt"))
            },
            chores = root.getJSONArray("chores").map {
                Chore(it.getString("id"), it.getString("name"), it.getString("description"), it.getInt("points"), it.getString("icon"), it.getLong("createdAt"))
            },
            instances = root.getJSONArray("instances").map {
                ChoreInstance(
                    it.getString("id"), it.getString("choreId"), it.getString("cleanerId"), it.getString("date"),
                    ChoreStatus.valueOf(it.getString("status")), it.getLong("createdAt"),
                    it.longOrNull("submittedAt"), it.longOrNull("completedAt")
                )
            },
            dailyScores = root.getJSONArray("dailyScores").map {
                DailyScore(it.getString("id"), it.getString("cleanerId"), it.getString("date"), it.getInt("points"))
            },
            allTimeScores = root.getJSONArray("allTimeScores").map {
                AllTimeScore(it.getString("id"), it.getString("cleanerId"), it.getInt("totalPoints"))
            },
            settings = root.optString("language").takeIf { it.isNotEmpty() }?.let { AppSettings(language = it) }
        )
    }
}