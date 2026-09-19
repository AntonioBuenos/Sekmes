package com.example.sekmeszodynas.feature.splash

import android.app.Activity
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import kotlinx.coroutines.delay

private val SplashGreen = Color(0xFF063821)
private val SplashGreenLight = Color(0xFF176B43)
private val FlagYellow = Color(0xFFFFB81C)
private val FlagGreen = Color(0xFF046A38)
private val FlagRed = Color(0xFFBE3A34)

@Composable
fun SekmesSplashScreen(onFinished: () -> Unit) {
    val progress = remember { Animatable(0f) }

    KeepSystemBarIconsLight()
    LaunchedEffect(Unit) {
        progress.animateTo(1f, tween(durationMillis = 760, easing = FastOutSlowInEasing))
        delay(420)
        onFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.linearGradient(listOf(SplashGreen, SplashGreenLight)))
            .pointerInput(Unit) {
                awaitPointerEventScope {
                    while (true) awaitPointerEvent().changes.forEach { it.consume() }
                }
            },
    ) {
        SplashBackdrop(progress.value)

        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .graphicsLayer {
                    alpha = progress.value
                    scaleX = 0.84f + progress.value * 0.16f
                    scaleY = 0.84f + progress.value * 0.16f
                    translationY = (1f - progress.value) * 42f
                    rotationZ = (1f - progress.value) * -3f
                },
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            FlagMonogram()
            Spacer(Modifier.height(28.dp))
            Text(
                text = "Sėkmės",
                color = Color.White,
                fontSize = 46.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = (-0.8).sp,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "ЛИТОВСКИЙ • КАЖДЫЙ ДЕНЬ",
                color = Color.White.copy(alpha = 0.72f),
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 1.8.sp,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun SplashBackdrop(progress: Float) {
    Canvas(Modifier.fillMaxSize()) {
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(FlagYellow.copy(alpha = 0.24f), Color.Transparent),
                center = Offset(size.width * 0.92f, size.height * 0.12f),
                radius = size.minDimension * 0.72f,
            ),
            radius = size.minDimension * 0.72f,
            center = Offset(size.width * 0.92f, size.height * 0.12f),
        )
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(FlagRed.copy(alpha = 0.15f), Color.Transparent),
                center = Offset(size.width * 0.05f, size.height * 0.88f),
                radius = size.minDimension * 0.62f,
            ),
            radius = size.minDimension * 0.62f,
            center = Offset(size.width * 0.05f, size.height * 0.88f),
        )

        val ringRadius = size.minDimension * (0.23f + progress * 0.09f)
        drawCircle(
            color = Color.White.copy(alpha = 0.09f * progress),
            radius = ringRadius,
            center = center,
            style = Stroke(width = 1.dp.toPx()),
        )
        drawCircle(
            color = Color.White.copy(alpha = 0.045f * progress),
            radius = ringRadius + 28.dp.toPx(),
            center = center,
            style = Stroke(width = 1.dp.toPx()),
        )

        rotate(-14f) {
            drawRoundRect(
                color = Color.White.copy(alpha = 0.04f),
                topLeft = Offset(size.width * 0.7f, -size.height * 0.08f),
                size = Size(size.width * 0.34f, size.height * 1.15f),
                cornerRadius = CornerRadius(80.dp.toPx()),
            )
        }
    }
}

@Composable
private fun FlagMonogram() {
    val shape = RoundedCornerShape(32.dp)
    Box(
        modifier = Modifier
            .size(116.dp)
            .shadow(24.dp, shape, ambientColor = Color.Black.copy(alpha = 0.24f), spotColor = Color.Black.copy(alpha = 0.32f))
            .clip(shape)
            .border(1.dp, Color.White.copy(alpha = 0.34f), shape),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(Modifier.fillMaxSize()) {
            val stripeHeight = size.height / 3f
            drawRect(FlagYellow, size = Size(size.width, stripeHeight))
            drawRect(FlagGreen, topLeft = Offset(0f, stripeHeight), size = Size(size.width, stripeHeight))
            drawRect(FlagRed, topLeft = Offset(0f, stripeHeight * 2f), size = Size(size.width, stripeHeight + 1f))
            drawLine(
                color = Color.White.copy(alpha = 0.18f),
                start = Offset(size.width * 0.2f, size.height * 0.08f),
                end = Offset(size.width * 0.78f, size.height * 0.08f),
                strokeWidth = 2.dp.toPx(),
                cap = StrokeCap.Round,
            )
        }
        Text(
            text = "S",
            color = Color.White,
            fontSize = 64.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = (-3).sp,
        )
    }
}

@Composable
private fun KeepSystemBarIconsLight() {
    val view = LocalView.current
    val darkTheme = isSystemInDarkTheme()

    DisposableEffect(view, darkTheme) {
        val window = (view.context as Activity).window
        val controller = WindowCompat.getInsetsController(window, view)
        controller.isAppearanceLightStatusBars = false
        controller.isAppearanceLightNavigationBars = false
        onDispose {
            controller.isAppearanceLightStatusBars = !darkTheme
            controller.isAppearanceLightNavigationBars = !darkTheme
        }
    }
}
