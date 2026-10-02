package com.example.choreapp.utils

import androidx.compose.ui.graphics.Color

object ColorUtils {
    fun getColorFromHex(hex: String): Color {
        return try {
            Color(android.graphics.Color.parseColor(hex))
        } catch (e: Exception) {
            Color.Gray
        }
    }

    fun hexToColor(hex: String): Color {
        return getColorFromHex(hex)
    }

    val defaultColors = listOf(
        "#FF6B6B",
        "#4ECDC4",
        "#45B7D1",
        "#FFA07A",
        "#98D8C8",
        "#F7DC6F",
        "#BB8FCE",
        "#85C1E2"
    )

    fun getColorByIndex(index: Int): Color {
        val hex = defaultColors[index % defaultColors.size]
        return getColorFromHex(hex)
    }
}
