package com.example.choreapp.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

val Cream = Color(0xFFFFF8E7)
val Bubblegum = Color(0xFFFF6B9D)
val SkyBlue = Color(0xFF45B7D1)
val Sunshine = Color(0xFFFFD166)
val Mint = Color(0xFF4ECDC4)
val Grape = Color(0xFF7C5CFF)

private val KidColors = lightColorScheme(
    primary = Grape,
    onPrimary = Color.White,
    secondary = Bubblegum,
    onSecondary = Color.White,
    tertiary = Mint,
    background = Cream,
    onBackground = Color(0xFF2D2A4A),
    surface = Color.White,
    onSurface = Color(0xFF2D2A4A),
    surfaceVariant = Color(0xFFFFEFC2),
    error = Color(0xFFE53935)
)

private val KidShapes = Shapes(
    small = RoundedCornerShape(14.dp),
    medium = RoundedCornerShape(20.dp),
    large = RoundedCornerShape(28.dp)
)

private val rounded = FontFamily.SansSerif

private val KidTypography = Typography(
    headlineMedium = TextStyle(fontFamily = rounded, fontWeight = FontWeight.ExtraBold, fontSize = 28.sp),
    titleLarge = TextStyle(fontFamily = rounded, fontWeight = FontWeight.Bold, fontSize = 22.sp),
    titleMedium = TextStyle(fontFamily = rounded, fontWeight = FontWeight.Bold, fontSize = 18.sp),
    bodyLarge = TextStyle(fontFamily = rounded, fontSize = 16.sp),
    bodyMedium = TextStyle(fontFamily = rounded, fontSize = 14.sp),
    labelLarge = TextStyle(fontFamily = rounded, fontWeight = FontWeight.Bold, fontSize = 16.sp)
)

@Composable
fun ChoreAppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = KidColors,
        shapes = KidShapes,
        typography = KidTypography,
        content = content
    )
}
