package com.example.sekmeszodynas.ui.theme

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@Preview(name = "Light", showBackground = true)
@Composable
private fun SekmesLightThemePreview() {
    SekmesZodynasTheme(darkTheme = false) { ThemePreviewContent() }
}

@Preview(name = "Dark", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
private fun SekmesDarkThemePreview() {
    SekmesZodynasTheme(darkTheme = true) { ThemePreviewContent() }
}

@Composable
private fun ThemePreviewContent() {
    Surface {
        Column(
            modifier = Modifier.padding(SekmesSpacing.Medium),
            verticalArrangement = Arrangement.spacedBy(SekmesSpacing.XSmall),
        ) {
            Text("Sėkmės", style = MaterialTheme.typography.headlineMedium)
            Text(
                "Литовский каждый день",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(SekmesSpacing.XSmall))
            Button(onClick = {}, modifier = Modifier.fillMaxWidth()) {
                Text("Продолжить обучение")
            }
        }
    }
}
