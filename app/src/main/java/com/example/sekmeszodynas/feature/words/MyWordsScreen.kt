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
    val duplicate = words.firstOrNull { it.id != editingId && it.lt.equals(lt.trim(), true) && it.ru.equals(ru.trim(), true) }
    fun clearForm() { lt = ""; ru = ""; type = "n"; note = ""; editingId = null }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Text("←", fontSize = 24.sp) }
            Text(if (editingId == null) "Мои слова" else "Редактирование слова", style = MaterialTheme.typography.headlineSmall)
        }
        OutlinedTextField(lt, { lt = it }, label = { Text("Слово по-литовски") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(ru, { ru = it }, label = { Text("Перевод") }, modifier = Modifier.fillMaxWidth())
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            PartOfSpeech.entries.forEach { part -> FilterChip(type == part.typeCode, { type = part.typeCode }, label = { Text(part.typeCode) }) }
        }
        OutlinedTextField(note, { note = it }, label = { Text("Заметка или пример") }, modifier = Modifier.fillMaxWidth())
        if (duplicate != null) Text("Такое слово уже есть: ${duplicate.lt} — ${duplicate.ru}", color = MaterialTheme.colorScheme.error)
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
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
        LazyColumn(Modifier.weight(1f)) {
            items(words, key = CustomWord::id) { word ->
                Card(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text("${word.lt} — ${word.ru}", style = MaterialTheme.typography.titleMedium)
                            Text(PartOfSpeech.fromTypeCode(word.type).label, style = MaterialTheme.typography.bodySmall)
                            if (word.note.isNotBlank()) Text(word.note, style = MaterialTheme.typography.bodySmall)
                        }
                        TextButton(onClick = { lt = word.lt; ru = word.ru; type = word.type; note = word.note; editingId = word.id }) { Text("Изменить") }
                        TextButton(onClick = { scope.launch { CustomWordsStore.repository().delete(word.id); if (editingId == word.id) clearForm() } }) { Text("Удалить") }
                    }
                }
            }
        }
    }
}

fun CustomWord.asWord() = Word(ru, lt, type, id, sourceIds = setOf(USER_WORDS_SOURCE_ID))
