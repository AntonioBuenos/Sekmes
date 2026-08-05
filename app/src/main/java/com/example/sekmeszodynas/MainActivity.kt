package com.example.sekmeszodynas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
                SekmesAppNavigation()
            }
        }
    }
}
