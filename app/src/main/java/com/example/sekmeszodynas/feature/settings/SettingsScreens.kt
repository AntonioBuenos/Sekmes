package com.example.sekmeszodynas.feature.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sekmeszodynas.*
import com.example.sekmeszodynas.feature.words.asWord
import com.example.sekmeszodynas.ui.components.SekmesEmptyState
import com.example.sekmeszodynas.ui.components.SekmesActionRow
import com.example.sekmeszodynas.ui.components.SekmesStatusChip
import com.example.sekmeszodynas.ui.components.SekmesStatusOption
import com.example.sekmeszodynas.ui.components.SekmesStatusTone
import com.example.sekmeszodynas.ui.components.SekmesTopAppBar
import com.example.sekmeszodynas.ui.components.SekmesWordRow
import com.example.sekmeszodynas.ui.theme.SekmesSpacing
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(onStatusManager: () -> Unit, onBack: () -> Unit) {
    val settings by SettingsStore.repository().settings.collectAsState(initial = AppSettings())
    val scope = rememberCoroutineScope()
    Column(Modifier.fillMaxSize()) {
        SekmesTopAppBar("Настройки", onBack = onBack)
        Column(Modifier.padding(horizontal = SekmesSpacing.Medium), verticalArrangement = Arrangement.spacedBy(SekmesSpacing.XSmall)) {
            Text("Тест", style = MaterialTheme.typography.titleLarge)
            TextButton(onClick = { scope.launch { SettingsStore.repository().update { it.copy(quizDirection = if (it.quizDirection == QuizDirection.RUSSIAN_TO_LITHUANIAN) QuizDirection.LITHUANIAN_TO_RUSSIAN else QuizDirection.RUSSIAN_TO_LITHUANIAN) } } }) { Text(if (settings.quizDirection == QuizDirection.RUSSIAN_TO_LITHUANIAN) "Русский → литовский" else "Литовский → русский") }
            Row(horizontalArrangement = Arrangement.spacedBy(SekmesSpacing.XSmall), modifier = Modifier.horizontalScroll(rememberScrollState())) { listOf(10, 20, 50, 0).forEach { size -> FilterChip(settings.quizSize == size, { scope.launch { SettingsStore.repository().update { it.copy(quizSize = size) } } }, label = { Text(if (size == 0) "Все вопросы" else "$size вопросов") }) } }
            Text("Словарь", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(top = SekmesSpacing.Small))
            Row(verticalAlignment = Alignment.CenterVertically) { Checkbox(settings.showKnownWords, { checked -> scope.launch { SettingsStore.repository().update { it.copy(showKnownWords = checked) } } }); Text("Показывать известные слова") }
            SekmesActionRow("Управление статусами", "Поиск, фильтры и сброс прогресса", Icons.Rounded.Settings, onStatusManager)
        }
    }
}

@Composable
fun WordStatusManagerScreen(onBack: () -> Unit) {
    val progress by ProgressStore.repository().observeAll().collectAsState(initial = emptyMap())
    val customWords by CustomWordsStore.repository().observeAll().collectAsState(initial = emptyList())
    val scope = rememberCoroutineScope()
    var query by rememberSaveable { mutableStateOf("") }
    var selectedStatus by rememberSaveable { mutableStateOf<WordLearningStatus?>(null) }
    var confirmReset by rememberSaveable { mutableStateOf(false) }
    val allWords = (GLOBAL_POOL + customWords.map { it.asWord() }).distinctBy(Word::id)
    val words = allWords.filter { word ->
        (word.lt.contains(query, true) || word.ru.contains(query, true)) &&
            (selectedStatus == null || (progress[word.id]?.status ?: WordLearningStatus.NEW) == selectedStatus)
    }
    val statusOptions = WordLearningStatus.entries.map { it.asStatusOption() }
    Column(Modifier.fillMaxSize()) {
        SekmesTopAppBar(
            title = "Статусы слов",
            subtitle = "${words.size} из ${allWords.size} слов",
            onBack = onBack,
        )
        Column(
            modifier = Modifier.padding(horizontal = SekmesSpacing.Medium),
            verticalArrangement = Arrangement.spacedBy(SekmesSpacing.XSmall),
        ) {
            OutlinedTextField(query, { query = it }, label = { Text("Поиск") }, singleLine = true, modifier = Modifier.fillMaxWidth())
            Row(
                modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(SekmesSpacing.XSmall),
            ) {
                SekmesStatusChip("Все", SekmesStatusTone.Neutral, onClick = { selectedStatus = null }, selected = selectedStatus == null)
                WordLearningStatus.entries.forEach { target ->
                    val option = target.asStatusOption()
                    SekmesStatusChip(option.label, option.tone, onClick = { selectedStatus = target }, selected = selectedStatus == target)
                }
            }
            TextButton(onClick = { confirmReset = words.isNotEmpty() }) { Text("Сбросить статусы у найденных (${words.size})") }
        }
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(SekmesSpacing.Medium),
            verticalArrangement = Arrangement.spacedBy(SekmesSpacing.XSmall),
        ) {
            if (words.isEmpty()) {
                item { SekmesEmptyState("Ничего не найдено", "Измените запрос или фильтры, чтобы увидеть слова.") }
            } else {
                items(words, key = Word::id) { word ->
                    val status = progress[word.id]?.status ?: WordLearningStatus.NEW
                    SekmesWordRow(
                        word = word.lt,
                        translation = word.ru,
                        status = status.asStatusOption(),
                        statusOptions = statusOptions,
                        onStatusSelected = { option ->
                            val target = WordLearningStatus.valueOf(option.id)
                            scope.launch {
                                if (target == WordLearningStatus.NEW) ProgressStore.repository().reset(word.id)
                                else ProgressStore.repository().setStatus(word.id, target)
                            }
                        },
                    )
                }
            }
        }
    }
    if (confirmReset) {
        AlertDialog(
            onDismissRequest = { confirmReset = false },
            title = { Text("Восстановить слова?") },
            text = { Text("У ${words.size} найденных слов будет сброшен статус и статистика ответов.") },
            confirmButton = { TextButton(onClick = { scope.launch { words.forEach { ProgressStore.repository().reset(it.id) }; confirmReset = false } }) { Text("Сбросить") } },
            dismissButton = { TextButton(onClick = { confirmReset = false }) { Text("Отмена") } },
        )
    }
}

private fun WordLearningStatus.asStatusOption() = SekmesStatusOption(
    id = name,
    label = statusLabel(this),
    tone = when (this) {
        WordLearningStatus.NEW -> SekmesStatusTone.Neutral
        WordLearningStatus.LEARNING -> SekmesStatusTone.Learning
        WordLearningStatus.HARD -> SekmesStatusTone.Hard
        WordLearningStatus.KNOWN -> SekmesStatusTone.Known
    },
)
