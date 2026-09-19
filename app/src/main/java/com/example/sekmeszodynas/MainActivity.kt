package com.example.sekmeszodynas

import android.os.Bundle
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.sekmeszodynas.feature.splash.SekmesSplashScreen
import com.example.sekmeszodynas.navigation.SekmesAppNavigation
import com.example.sekmeszodynas.feature.grammar.GrammarCatalogStore
import com.example.sekmeszodynas.ui.theme.SekmesZodynasTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        CatalogStore.initialize(assets)
        AudioCatalogStore.initialize(assets)
        GrammarCatalogStore.initialize(assets)
        ConstitutionStore.initialize(assets, CatalogStore.repository())
        ProgressStore.initialize(applicationContext)
        SettingsStore.initialize(applicationContext)
        CustomWordsStore.initialize(applicationContext)
        enableEdgeToEdge()
        setContent {
            SekmesZodynasTheme {
                var showSplash by rememberSaveable { mutableStateOf(savedInstanceState == null) }

                Box(Modifier.fillMaxSize()) {
                    SekmesAppNavigation()
                    AnimatedVisibility(
                        visible = showSplash,
                        enter = EnterTransition.None,
                        exit = fadeOut(tween(260)) + scaleOut(targetScale = 1.025f, animationSpec = tween(260)),
                    ) {
                        SekmesSplashScreen(onFinished = { showSplash = false })
                    }
                }
            }
        }
    }
}
