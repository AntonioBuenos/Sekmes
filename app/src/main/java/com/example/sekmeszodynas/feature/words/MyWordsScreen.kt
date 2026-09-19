package com.example.sekmeszodynas.feature.words

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
import com.example.sekmeszodynas.ui.components.SekmesEmptyState
import com.example.sekmeszodynas.ui.components.SekmesTopAppBar
import com.example.sekmeszodynas.ui.theme.SekmesSpacing
import kotlinx.coroutines.launch

@Composable
fun MyWordsScreen(onBack: () -> Unit) {
    val words by CustomWordsStore.repository().observeAll().collectAsState(initial = emptyList())
    val scope = rememberCoroutineScope()
    var lt by rememberSaveable { mutableStateOf("") }
    var ru by rememberSaveable { mutableStateOf("") }
    var type by rememberSaveable { mutableStateOf("n") }
    var note by rememberSaveable { mutableStateOf("") }
    var editingId by rememberSaveable { mutableStateOf<String?>(null) }
    var deletingId by rememberSaveable { mutableStateOf<String?>(null) }
    val duplicate = words.firstOrNull { it.id != editingId && it.lt.equals(lt.trim(), true) && it.ru.equals(ru.trim(), true) }
    fun clearForm() { lt = ""; ru = ""; type = "n"; note = ""; editingId = null }

    Column(Modifier.fillMaxSize()) {
        SekmesTopAppBar(if (editingId == null) "Мои слова" else "Редактирование слова", subtitle = "${words.size} слов", onBack = onBack)
        Column(Modifier.padding(horizontal = SekmesSpacing.Medium), verticalArrangement = Arrangement.spacedBy(SekmesSpacing.XSmall)) {
        OutlinedTextField(lt, { lt = it }, label = { Text("Слово по-литовски") }, singleLine = true, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(ru, { ru = it }, label = { Text("Перевод") }, singleLine = true, modifier = Modifier.fillMaxWidth())
        Row(horizontalArrangement = Arrangement.spacedBy(SekmesSpacing.XSmall)) {
            PartOfSpeech.entries.forEach { part -> FilterChip(type == part.typeCode, { type = part.typeCode }, label = { Text(part.typeCode) }) }
        }
        OutlinedTextField(note, { note = it }, label = { Text("Заметка или пример") }, modifier = Modifier.fillMaxWidth())
        if (duplicate != null) Text("Такое слово уже есть: ${duplicate.lt} — ${duplicate.ru}", color = MaterialTheme.colorScheme.error)
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(SekmesSpacing.XSmall)) {
            Button(
                onClick = {
                    scope.launch {
                        if (lt.isBlank() || ru.isBlank() || duplicate != null) return@launch
                        val existing = words.firstOrNull { it.id == editingId }
                        if (existing == null) CustomWordsStore.repository().create(lt, ru, type, note)
                        else CustomWordsStore.repository().save(existing.copy(lt = lt.trim(), ru = ru.trim(), type = type, note = note.trim(), updatedAtEpochMillis = System.currentTimeMillis()))
                        clearForm()
                    }
                },
                enabled = lt.isNotBlank() && ru.isNotBlank() && duplicate == null,
                modifier = Modifier.weight(1f),
            ) { Text(if (editingId == null) "Добавить слово" else "Сохранить") }
            if (editingId != null) TextButton(onClick = ::clearForm) { Text("Отмена") }
        }
        }
        LazyColumn(Modifier.weight(1f), contentPadding = PaddingValues(SekmesSpacing.Medium), verticalArrangement = Arrangement.spacedBy(SekmesSpacing.XSmall)) {
            if (words.isEmpty()) item { SekmesEmptyState("Личный список пуст", "Добавьте своё первое слово выше.") }
            items(words, key = CustomWord::id) { word ->
                OutlinedCard(Modifier.fillMaxWidth()) {
                    Row(Modifier.padding(SekmesSpacing.Small), verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text("${word.lt} — ${word.ru}", style = MaterialTheme.typography.titleMedium)
                            Text(PartOfSpeech.fromTypeCode(word.type).label, style = MaterialTheme.typography.bodySmall)
                            if (word.note.isNotBlank()) Text(word.note, style = MaterialTheme.typography.bodySmall)
                        }
                        TextButton(onClick = { lt = word.lt; ru = word.ru; type = word.type; note = word.note; editingId = word.id }) { Text("Изменить") }
                        TextButton(onClick = { deletingId = word.id }) { Text("Удалить") }
                    }
                }
            }
        }
    }
    deletingId?.let { id ->
        AlertDialog(
            onDismissRequest = { deletingId = null },
            title = { Text("Удалить слово?") },
            text = { Text("Это действие нельзя отменить.") },
            confirmButton = { TextButton(onClick = { scope.launch { CustomWordsStore.repository().delete(id); if (editingId == id) clearForm(); deletingId = null } }) { Text("Удалить") } },
            dismissButton = { TextButton(onClick = { deletingId = null }) { Text("Отмена") } },
        )
    }
}

fun CustomWord.asWord() = Word(ru, lt, type, id, sourceIds = setOf(USER_WORDS_SOURCE_ID))
