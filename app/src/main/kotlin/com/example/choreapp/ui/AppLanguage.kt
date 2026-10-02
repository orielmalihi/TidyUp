package com.example.choreapp.ui

import android.content.Context
import android.content.res.Configuration
import java.util.Locale

object AppLanguage {
    private const val PREFS = "app_language"
    private const val KEY = "language"

    fun saved(context: Context): String? =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString(KEY, null)

    fun save(context: Context, language: String) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putString(KEY, language).commit()
    }

    fun wrap(base: Context): Context {
        val language = saved(base) ?: return base
        val locale = Locale(if (language == "he") "iw" else language)
        Locale.setDefault(locale)
        val config = Configuration(base.resources.configuration).apply {
            setLocale(locale)
            setLayoutDirection(locale)
        }
        return base.createConfigurationContext(config)
    }
}