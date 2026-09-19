package com.example.sekmeszodynas.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

private val AppFont = FontFamily.Default

val Typography = Typography(
    displayLarge = textStyle(FontWeight.SemiBold, 36.sp, 44.sp, (-0.3).sp),
    displayMedium = textStyle(FontWeight.SemiBold, 32.sp, 40.sp, (-0.2).sp),
    displaySmall = textStyle(FontWeight.SemiBold, 28.sp, 36.sp),
    headlineLarge = textStyle(FontWeight.SemiBold, 28.sp, 34.sp),
    headlineMedium = textStyle(FontWeight.SemiBold, 24.sp, 30.sp),
    headlineSmall = textStyle(FontWeight.SemiBold, 22.sp, 28.sp),
    titleLarge = textStyle(FontWeight.SemiBold, 20.sp, 26.sp),
    titleMedium = textStyle(FontWeight.Medium, 16.sp, 22.sp),
    titleSmall = textStyle(FontWeight.Medium, 14.sp, 20.sp),
    bodyLarge = textStyle(FontWeight.Normal, 16.sp, 24.sp),
    bodyMedium = textStyle(FontWeight.Normal, 14.sp, 20.sp),
    bodySmall = textStyle(FontWeight.Normal, 12.sp, 16.sp),
    labelLarge = textStyle(FontWeight.Medium, 14.sp, 20.sp),
    labelMedium = textStyle(FontWeight.Medium, 12.sp, 16.sp, 0.1.sp),
    labelSmall = textStyle(FontWeight.Medium, 11.sp, 16.sp, 0.2.sp),
)

private fun textStyle(
    fontWeight: FontWeight,
    fontSize: TextUnit,
    lineHeight: TextUnit,
    letterSpacing: TextUnit = 0.sp,
) = TextStyle(
    fontFamily = AppFont,
    fontWeight = fontWeight,
    fontSize = fontSize,
    lineHeight = lineHeight,
    letterSpacing = letterSpacing,
)
