package com.example.sekmeszodynas

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

@Composable
fun ConstitutionHomeScreen(
    onBlockSelected: (ConstitutionBlockId) -> Unit,
    onDictionary: () -> Unit,
    onQuiz: () -> Unit,
    onBack: () -> Unit,
) {
    val repository = ConstitutionStore.repository()
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        ScreenHeader(title = "Конституция Литвы", onBack = onBack)
        Text(
            text = "Читайте литовский текст по смысловым частям, открывайте термины и закрепляйте их тестом.",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(vertical = 8.dp),
        )
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = onDictionary, modifier = Modifier.weight(1f)) { Text("Все термины") }
            Button(onClick = onQuiz, modifier = Modifier.weight(1f)) { Text("Итоговый тест") }
        }
        Spacer(Modifier.height(8.dp))
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(repository.blocks, key = ConstitutionBlock::id) { block ->
                Card(
                    onClick = { onBlockSelected(block.id) },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("${block.order}. ${block.title}", style = MaterialTheme.typography.titleMedium)
                        Text(block.description, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(top = 4.dp))
                        Text(
                            text = blockRangeLabel(block),
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(top = 8.dp),
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ConstitutionBlockScreen(
    blockId: ConstitutionBlockId,
    onContentSelected: (ConstitutionArticleId) -> Unit,
    onDictionary: () -> Unit,
    onQuiz: () -> Unit,
    onBack: () -> Unit,
) {
    val repository = ConstitutionStore.repository()
    val block = repository.blockById[blockId] ?: return
    val content = repository.contentForBlock(blockId)

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        ScreenHeader(title = block.title, onBack = onBack)
        Text(block.description, style = MaterialTheme.typography.bodyLarge)
        Text(
            blockRangeLabel(block),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(top = 4.dp),
        )
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = onDictionary, modifier = Modifier.weight(1f)) { Text("Словарь блока") }
            Button(onClick = onQuiz, modifier = Modifier.weight(1f)) { Text("Тест по блоку") }
        }
        Spacer(Modifier.height(8.dp))
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(content, key = ConstitutionContent::id) { item ->
                Card(
                    onClick = { onContentSelected(item.id) },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(contentLabel(item), style = MaterialTheme.typography.titleMedium)
                        Text(item.parts.first().lt, style = MaterialTheme.typography.bodyMedium, maxLines = 2)
                    }
                }
            }
        }
    }
}

@Composable
fun ConstitutionArticleScreen(
    blockId: ConstitutionBlockId,
    contentId: ConstitutionArticleId,
    onBack: () -> Unit,
) {
    val repository = ConstitutionStore.repository()
    val content = repository.content(contentId)
    if (content.blockId != blockId) return

    var showTranslation by rememberSaveable(contentId) { mutableStateOf(true) }
    var selectedWordId by rememberSaveable(contentId) { mutableStateOf<WordId?>(null) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        ScreenHeader(title = contentLabel(content), onBack = onBack)
        if (content is ConstitutionArticle) {
            Text(content.sectionTitle, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
        }
        TextButton(onClick = { showTranslation = !showTranslation }) {
            Text(if (showTranslation) "Скрыть русский перевод" else "Показать русский перевод")
        }
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(content.parts, key = ConstitutionPart::id) { part ->
                ConstitutionPartCard(
                    part = part,
                    showTranslation = showTranslation,
                    onTermSelected = { selectedWordId = it },
                )
            }
        }
    }

    selectedWordId?.let { wordId ->
        ConstitutionTermDialog(
            word = repository.dictionaryEntry(wordId),
            onDismiss = { selectedWordId = null },
        )
    }
}

@Composable
fun ConstitutionDictionaryScreen(blockId: ConstitutionBlockId?, onBack: () -> Unit) {
    val repository = ConstitutionStore.repository()
    val words = constitutionWords(blockId)
    val title = blockId?.let { repository.blockById[it]?.let { block -> "Словарь: ${block.title}" } }
        ?: "Термины Конституции"
    DictionaryWordsScreen(
        words = words,
        themeTitle = title,
        stateKey = "constitution:dictionary:${blockId ?: "all"}",
        onBack = onBack,
    )
}

@Composable
fun ConstitutionQuizScreen(
    blockId: ConstitutionBlockId?,
    onQuizFinished: (Int, Int, Map<String, Int>) -> Unit,
    onBack: () -> Unit,
) {
    QuizWordsScreen(
        sessionKey = "constitution:quiz:${blockId ?: "all"}",
        sourceWords = constitutionWords(blockId),
        onQuizFinished = onQuizFinished,
        onBack = onBack,
    )
}

@Composable
private fun ConstitutionPartCard(
    part: ConstitutionPart,
    showTranslation: Boolean,
    onTermSelected: (WordId) -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            if (part.itemNumber != null) {
                Text("Пункт ${part.itemNumber}", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
            }
            LinkedConstitutionText(
                text = part.lt,
                numericFragments = part.ltNumericFragments,
                termLinks = part.termLinks,
                onTermSelected = onTermSelected,
            )
            if (showTranslation) {
                Spacer(Modifier.height(8.dp))
                LinkedConstitutionText(
                    text = part.ru,
                    numericFragments = part.ruNumericFragments,
                    termLinks = emptyList(),
                    onTermSelected = onTermSelected,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun LinkedConstitutionText(
    text: String,
    numericFragments: List<NumericFragment>,
    termLinks: List<ConstitutionTermLink>,
    onTermSelected: (WordId) -> Unit,
    color: Color = MaterialTheme.colorScheme.onSurface,
) {
    val annotated = constitutionAnnotatedText(text, numericFragments, termLinks, color)
    ClickableText(
        text = annotated,
        style = MaterialTheme.typography.bodyLarge.copy(color = color),
        onClick = { offset ->
            annotated.getStringAnnotations(tag = "word", start = offset, end = offset)
                .firstOrNull()
                ?.let { onTermSelected(it.item) }
        },
    )
}

fun constitutionAnnotatedText(
    text: String,
    numericFragments: List<NumericFragment>,
    termLinks: List<ConstitutionTermLink>,
    baseColor: Color = Color.Unspecified,
): AnnotatedString = buildAnnotatedString {
    append(text)
    numericFragments.forEach { fragment ->
        addStyle(
            SpanStyle(
                background = Color(0xFFFFE8A3),
                fontWeight = FontWeight.Bold,
                color = baseColor,
            ),
            fragment.start,
            fragment.end,
        )
    }
    termLinks.forEach { link ->
        addStyle(
            SpanStyle(
                color = Color(0xFF1B5E20),
                fontWeight = FontWeight.SemiBold,
            ),
            link.start,
            link.end,
        )
        addStringAnnotation(tag = "word", annotation = link.wordId, start = link.start, end = link.end)
    }
}

@Composable
private fun ConstitutionTermDialog(word: DictionaryEntry, onDismiss: () -> Unit) {
    val progressByWordId by ProgressStore.repository().observeAll().collectAsState(initial = emptyMap())
    val scope = rememberCoroutineScope()
    val status = progressByWordId[word.id]?.status ?: WordLearningStatus.NEW

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(word.lt) },
        text = {
            Column {
                Text(word.ru, style = MaterialTheme.typography.bodyLarge)
                Text(statusLabel(status), style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(top = 8.dp))
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Закрыть") }
        },
        dismissButton = {
            Row {
                TextButton(onClick = { scope.launch { ProgressStore.repository().setStatus(word.id, WordLearningStatus.KNOWN) } }) {
                    Text("Знаю")
                }
                TextButton(onClick = { scope.launch { ProgressStore.repository().setStatus(word.id, WordLearningStatus.HARD) } }) {
                    Text("Повторять")
                }
            }
        },
    )
}

@Composable
private fun ScreenHeader(title: String, onBack: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = onBack) { Text("←", fontSize = 24.sp) }
        Text(title, style = MaterialTheme.typography.headlineSmall)
    }
}

private fun constitutionWords(blockId: ConstitutionBlockId?): List<Word> {
    val catalog = CatalogStore.repository()
    val lessonIds = blockId?.let(::listOf)
        ?: ConstitutionStore.repository().blocks.map(ConstitutionBlock::id)
    return lessonIds
        .flatMap(catalog::wordsForLesson)
        .map { entry -> Word(ru = entry.ru, lt = entry.lt, type = entry.type, id = entry.id) }
        .distinctBy(Word::id)
}

private fun blockRangeLabel(block: ConstitutionBlock): String =
    if (block.includesPreamble) "Преамбула, статьи ${block.articleStart}–${block.articleEnd}"
    else "Статьи ${block.articleStart}–${block.articleEnd}"

private fun contentLabel(content: ConstitutionContent): String = when (content) {
    is ConstitutionPreamble -> "Преамбула"
    is ConstitutionArticle -> "Статья ${content.number}"
}
