package com.example.sekmeszodynas.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = SekmesGreen,
    onPrimary = OnSekmesGreen,
    primaryContainer = SekmesGreenContainer,
    onPrimaryContainer = OnSekmesGreenContainer,
    inversePrimary = SekmesGreenDark,
    secondary = SekmesAmber,
    onSecondary = OnSekmesAmber,
    secondaryContainer = SekmesAmberContainer,
    onSecondaryContainer = OnSekmesAmberContainer,
    tertiary = SekmesRose,
    onTertiary = OnSekmesRose,
    tertiaryContainer = SekmesRoseContainer,
    onTertiaryContainer = OnSekmesRoseContainer,
    background = AppBackgroundLight,
    onBackground = AppOnBackgroundLight,
    surface = AppSurfaceLight,
    onSurface = AppOnBackgroundLight,
    surfaceVariant = AppSurfaceVariantLight,
    onSurfaceVariant = AppOnSurfaceVariantLight,
    surfaceTint = SekmesGreen,
    inverseSurface = AppInverseSurfaceLight,
    inverseOnSurface = AppInverseOnSurfaceLight,
    error = AppErrorLight,
    onError = Color.White,
    errorContainer = AppErrorContainerLight,
    onErrorContainer = AppOnErrorContainerLight,
    outline = AppOutlineLight,
    outlineVariant = AppOutlineVariantLight,
    scrim = Color.Black,
)

private val DarkColorScheme = darkColorScheme(
    primary = SekmesGreenDark,
    onPrimary = OnSekmesGreenContainer,
    primaryContainer = SekmesGreenContainerDark,
    onPrimaryContainer = OnSekmesGreenContainerDark,
    inversePrimary = SekmesGreen,
    secondary = SekmesAmberDark,
    onSecondary = OnSekmesAmberContainer,
    secondaryContainer = SekmesAmberContainerDark,
    onSecondaryContainer = OnSekmesAmberContainerDark,
    tertiary = SekmesRoseDark,
    onTertiary = OnSekmesRoseContainer,
    tertiaryContainer = SekmesRoseContainerDark,
    onTertiaryContainer = OnSekmesRoseContainerDark,
    background = AppBackgroundDark,
    onBackground = AppOnBackgroundDark,
    surface = AppSurfaceDark,
    onSurface = AppOnBackgroundDark,
    surfaceVariant = AppSurfaceVariantDark,
    onSurfaceVariant = AppOnSurfaceVariantDark,
    surfaceTint = SekmesGreenDark,
    inverseSurface = AppInverseSurfaceDark,
    inverseOnSurface = AppInverseOnSurfaceDark,
    error = AppErrorDark,
    onError = OnSekmesRoseContainer,
    errorContainer = AppErrorContainerDark,
    onErrorContainer = AppOnErrorContainerDark,
    outline = AppOutlineDark,
    outlineVariant = AppOutlineVariantDark,
    scrim = Color.Black,
)

@Composable
fun SekmesZodynasTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Color.Transparent.toArgb()
            window.navigationBarColor = Color.Transparent.toArgb()
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = !darkTheme
                isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content,
    )
}
