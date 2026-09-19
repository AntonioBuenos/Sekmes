package com.example.sekmeszodynas.feature.vocabulary

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.sekmeszodynas.*
import com.example.sekmeszodynas.feature.words.asWord

@Composable
fun GlobalVocabularyScreen(
    onDictionary: (VocabularyScope) -> Unit,
    onQuiz: (VocabularyScope) -> Unit,
    onVisualCards: (VocabularyScope) -> Unit,
    onBack: () -> Unit,
) {
    var part by rememberSaveable { mutableStateOf<PartOfSpeech?>(null) }
    var sourceId by rememberSaveable { mutableStateOf<String?>(null) }
    val customWords by CustomWordsStore.repository().observeAll().collectAsState(initial = emptyList())
    val scope = VocabularyScope(part, sourceId)
    val allWords = (GLOBAL_POOL + customWords.map { it.asWord() }).distinctBy(Word::id)
    val count = allWords.count(scope::contains)
    val visualCardCount = visualVocabularyCards(scope).size
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Назад") }
            Text("Общий словарь", style = MaterialTheme.typography.headlineSmall)
        }
        Text("Соберите выборку из всех курсов и своих слов.", modifier = Modifier.padding(vertical = 8.dp))
        Text("Часть речи", style = MaterialTheme.typography.labelLarge)
        FilterRow { 
            FilterChip(part == null, { part = null }, label = { Text("Все") })
            PartOfSpeech.entries.forEach { value -> FilterChip(part == value, { part = value }, label = { Text(value.label) }) }
        }
        Text("Источник", style = MaterialTheme.typography.labelLarge, modifier = Modifier.padding(top = 8.dp))
        FilterRow {
            FilterChip(sourceId == null, { sourceId = null }, label = { Text("Все") })
            vocabularySources().forEach { source -> FilterChip(sourceId == source.id, { sourceId = source.id }, label = { Text(source.title) }) }
        }
        Text("Доступно слов: $count", modifier = Modifier.padding(vertical = 16.dp), style = MaterialTheme.typography.titleMedium)
        Button(onClick = { onDictionary(scope) }, modifier = Modifier.fillMaxWidth(), enabled = count > 0) { Text("Открыть словарь") }
        Button(onClick = { onVisualCards(scope) }, modifier = Modifier.fillMaxWidth().padding(top = 8.dp), enabled = visualCardCount > 0) { Text("Смотреть карточки ($visualCardCount)") }
        Button(onClick = { onQuiz(scope) }, modifier = Modifier.fillMaxWidth().padding(top = 8.dp), enabled = count > 0) { Text("Начать тест") }
    }
}

@Composable
fun GlobalDictionaryScreen(scope: VocabularyScope, onBack: () -> Unit) {
    val customWords by CustomWordsStore.repository().observeAll().collectAsState(initial = emptyList())
    val words = (GLOBAL_POOL + customWords.map { it.asWord() }).distinctBy(Word::id).filter(scope::contains)
    DictionaryWordsScreen(words, "Общий словарь", "global:${scope.partOfSpeech}:${scope.sourceId}", onBack)
}

@Composable
fun GlobalQuizScreen(scope: VocabularyScope, onQuizFinished: (Int, Int, Map<String, Int>) -> Unit, onBack: () -> Unit) {
    val customWords by CustomWordsStore.repository().observeAll().collectAsState(initial = emptyList())
    val words = (GLOBAL_POOL + customWords.map { it.asWord() }).distinctBy(Word::id).filter(scope::contains)
    QuizWordsScreen("global:${scope.partOfSpeech}:${scope.sourceId}", words, onQuizFinished, onBack)
}

@Composable
private fun FilterRow(content: @Composable RowScope.() -> Unit) {
    Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp), content = content)
}
