package com.example.sekmeszodynas.feature.vocabulary

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.sekmeszodynas.VocabularyScope
import com.example.sekmeszodynas.ui.components.SekmesEmptyState
import com.example.sekmeszodynas.ui.components.SekmesTopAppBar
import com.example.sekmeszodynas.ui.theme.SekmesSpacing
import com.example.sekmeszodynas.visualVocabularyCards

@Composable
fun VisualVocabularyCardsScreen(
    scope: VocabularyScope,
    onBack: () -> Unit,
) {
    val cards = remember(scope) { visualVocabularyCards(scope) }
    var index by rememberSaveable(scope.partOfSpeech, scope.sourceId) { mutableIntStateOf(0) }

    Column(Modifier.fillMaxSize()) {
        SekmesTopAppBar(
            title = "Визуальные карточки",
            subtitle = if (cards.isEmpty()) null else "${index + 1} из ${cards.size}",
            onBack = onBack,
        )

        if (cards.isEmpty()) {
            SekmesEmptyState(
                title = "Для этой подборки пока нет карточек",
                description = "Измените фильтры общего словаря.",
                modifier = Modifier.weight(1f),
            )
            return@Column
        }

        val card = cards[index]
        BoxWithConstraints(
            modifier = Modifier.weight(1f).fillMaxWidth().padding(SekmesSpacing.Medium),
            contentAlignment = Alignment.Center,
        ) {
            val cardWidth = minOf(maxWidth, maxHeight - 72.dp).coerceIn(180.dp, 560.dp)
            Card(
                modifier = Modifier.width(cardWidth),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            ) {
                Image(
                    painter = painterResource(card.imageResId),
                    contentDescription = card.word.lt,
                    modifier = Modifier.fillMaxWidth().aspectRatio(1f).testTag("visual-card-image"),
                    contentScale = ContentScale.Crop,
                )
                Text(
                    text = card.word.lt,
                    modifier = Modifier.fillMaxWidth().padding(SekmesSpacing.Medium).testTag("visual-card-word"),
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = SekmesSpacing.Medium, vertical = SekmesSpacing.XSmall),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(
                onClick = { index = (index - 1 + cards.size) % cards.size },
                enabled = cards.size > 1,
                modifier = Modifier.size(56.dp).testTag("visual-card-previous"),
            ) {
                Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Предыдущая карточка")
            }
            IconButton(
                onClick = { index = (index + 1) % cards.size },
                enabled = cards.size > 1,
                modifier = Modifier.size(56.dp).testTag("visual-card-next"),
            ) {
                Icon(Icons.AutoMirrored.Rounded.ArrowForward, contentDescription = "Следующая карточка")
            }
        }
    }
}
