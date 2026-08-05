package com.example.sekmeszodynas.feature.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sekmeszodynas.*
import com.example.sekmeszodynas.feature.words.asWord
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(onStatusManager: () -> Unit, onBack: () -> Unit) {
    val settings by SettingsStore.repository().settings.collectAsState(initial = AppSettings())
    val scope = rememberCoroutineScope()
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Header("Настройки", onBack)
        Text("Тест", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 12.dp))
        TextButton(onClick = { scope.launch { SettingsStore.repository().update { it.copy(quizDirection = if (it.quizDirection == QuizDirection.RUSSIAN_TO_LITHUANIAN) QuizDirection.LITHUANIAN_TO_RUSSIAN else QuizDirection.RUSSIAN_TO_LITHUANIAN) } } }) { Text(if (settings.quizDirection == QuizDirection.RUSSIAN_TO_LITHUANIAN) "Направление: русский → литовский" else "Направление: литовский → русский") }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) { listOf(10, 20, 50, 0).forEach { size -> FilterChip(settings.quizSize == size, { scope.launch { SettingsStore.repository().update { it.copy(quizSize = size) } } }, label = { Text(if (size == 0) "Все" else "$size") }) } }
        Text("Словарь", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 16.dp))
        Row(verticalAlignment = Alignment.CenterVertically) { Checkbox(settings.showKnownWords, { checked -> scope.launch { SettingsStore.repository().update { it.copy(showKnownWords = checked) } } }); Text("Показывать известные слова по умолчанию") }
        Button(onClick = onStatusManager, modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) { Text("Управление статусами слов") }
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
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Header("Статусы слов", onBack)
        OutlinedTextField(query, { query = it }, label = { Text("Поиск") }, modifier = Modifier.fillMaxWidth())
        Text("Всего ${allWords.size}; новое ${allWords.count { (progress[it.id]?.status ?: WordLearningStatus.NEW) == WordLearningStatus.NEW }}, изучаю ${allWords.count { progress[it.id]?.status == WordLearningStatus.LEARNING }}, трудное ${allWords.count { progress[it.id]?.status == WordLearningStatus.HARD }}, знаю ${allWords.count { progress[it.id]?.status == WordLearningStatus.KNOWN }}", style = MaterialTheme.typography.bodySmall)
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
            FilterChip(selectedStatus == null, { selectedStatus = null }, label = { Text("Все") })
            WordLearningStatus.entries.forEach { target -> FilterChip(selectedStatus == target, { selectedStatus = target }, label = { Text(target.name) }) }
        }
        TextButton(onClick = { confirmReset = words.isNotEmpty() }) { Text("Сбросить статусы у найденных (${words.size})") }
        LazyColumn(Modifier.weight(1f)) { items(words, key = Word::id) { word ->
            val status = progress[word.id]?.status ?: WordLearningStatus.NEW
            Card(Modifier.fillMaxWidth().padding(vertical = 4.dp)) { Column(Modifier.padding(12.dp)) {
                Text("${word.lt} — ${word.ru}", style = MaterialTheme.typography.titleMedium)
                Text(statusLabel(status), style = MaterialTheme.typography.bodySmall)
                Row { listOf(WordLearningStatus.NEW, WordLearningStatus.LEARNING, WordLearningStatus.HARD, WordLearningStatus.KNOWN).forEach { target -> TextButton(onClick = { scope.launch { if (target == WordLearningStatus.NEW) ProgressStore.repository().reset(word.id) else ProgressStore.repository().setStatus(word.id, target) } }) { Text(statusLabel(target)) } } }
            } }
        } }
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

@Composable private fun Header(title: String, onBack: () -> Unit) { Row(verticalAlignment = Alignment.CenterVertically) { IconButton(onClick = onBack) { Text("←", fontSize = 24.sp) }; Text(title, style = MaterialTheme.typography.headlineSmall) } }
