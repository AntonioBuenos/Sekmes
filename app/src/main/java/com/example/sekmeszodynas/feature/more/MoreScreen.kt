package com.example.sekmeszodynas.feature.more

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Gavel
import androidx.compose.material.icons.rounded.MenuBook
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.sekmeszodynas.ui.components.SekmesActionRow
import com.example.sekmeszodynas.ui.components.SekmesSectionHeader
import com.example.sekmeszodynas.ui.theme.SekmesSpacing

@Composable
fun MoreScreen(
    onGrammar: () -> Unit,
    onConstitution: () -> Unit,
    onSettings: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(SekmesSpacing.Medium),
        verticalArrangement = Arrangement.spacedBy(SekmesSpacing.XSmall),
    ) {
        SekmesSectionHeader("Ещё")
        SekmesActionRow(
            title = "Грамматика",
            supportingText = "Карточки правил и примеров",
            icon = Icons.Rounded.MenuBook,
            onClick = onGrammar,
            highlighted = true,
        )
        SekmesActionRow(
            title = "Конституция Литвы",
            supportingText = "Параллельное чтение, термины и тесты",
            icon = Icons.Rounded.Gavel,
            onClick = onConstitution,
        )
        SekmesActionRow(
            title = "Настройки",
            supportingText = "Тесты, словарь и личные данные",
            icon = Icons.Rounded.Settings,
            onClick = onSettings,
        )
    }
}
