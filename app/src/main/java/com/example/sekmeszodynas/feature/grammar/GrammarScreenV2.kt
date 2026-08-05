package com.example.sekmeszodynas.feature.grammar

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun GrammarCardsScreen(onCard: (String) -> Unit, onBack: () -> Unit) {
    val cards = GrammarCatalogStore.all()
    var query by rememberSaveable { mutableStateOf("") }
    var category by rememberSaveable { mutableStateOf<String?>(null) }
    var favoritesOnly by rememberSaveable { mutableStateOf(false) }
    var favorites by rememberSaveable { mutableStateOf(emptySet<String>()) }
    val visibleCards = cards.filter { card ->
        (category == null || card.category == category) &&
            (!favoritesOnly || card.id in favorites) &&
            (query.isBlank() || "${card.title} ${card.rule}".contains(query, ignoreCase = true))
    }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        GrammarHeader("Грамматические карточки", onBack)
        OutlinedTextField(query, { query = it }, label = { Text("Поиск правила") }, singleLine = true, modifier = Modifier.fillMaxWidth())
        Row(Modifier.horizontalScroll(rememberScrollState()).padding(vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(category == null, { category = null }, label = { Text("Все") })
            cards.map(GrammarCard::category).distinct().forEach { value ->
                FilterChip(category == value, { category = value }, label = { Text(value) })
            }
            FilterChip(favoritesOnly, { favoritesOnly = !favoritesOnly }, label = { Text("★") })
        }
        if (visibleCards.isEmpty()) {
            Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) { Text("Карточки по этому фильтру не найдены.") }
        } else {
            LazyColumn(Modifier.weight(1f)) {
                items(visibleCards, key = GrammarCard::id) { card ->
                    Card(onClick = { onCard(card.id) }, modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Column(Modifier.weight(1f)) { Text(card.category, style = MaterialTheme.typography.labelLarge); Text(card.title, style = MaterialTheme.typography.titleMedium) }
                            TextButton(onClick = { favorites = if (card.id in favorites) favorites - card.id else favorites + card.id }) { Text(if (card.id in favorites) "★" else "☆") }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GrammarCardScreen(id: String, onCard: (String) -> Unit, onBack: () -> Unit) {
    val cards = GrammarCatalogStore.all()
    val index = cards.indexOfFirst { it.id == id }
    val card = cards.getOrNull(index) ?: return
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        GrammarHeader(card.title, onBack)
        Text(card.category, style = MaterialTheme.typography.labelLarge)
        Text(card.rule, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = 12.dp))
        card.table?.let { table -> GrammarTableView(table) }
        card.examples.forEach { Text("• $it", modifier = Modifier.padding(vertical = 3.dp), style = MaterialTheme.typography.bodyLarge) }
        card.exceptions.forEach { Text("Важно: $it", modifier = Modifier.padding(top = 8.dp), color = MaterialTheme.colorScheme.error) }
        card.hint?.let { Text("Подсказка: $it", modifier = Modifier.padding(top = 8.dp), color = MaterialTheme.colorScheme.primary) }
        Row(Modifier.fillMaxWidth().padding(top = 12.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            TextButton(onClick = { cards.getOrNull(index - 1)?.let { onCard(it.id) } }, enabled = index > 0) { Text("← Предыдущая") }
            TextButton(onClick = { cards.getOrNull(index + 1)?.let { onCard(it.id) } }, enabled = index < cards.lastIndex) { Text("Следующая →") }
        }
    }
}

@Composable
private fun GrammarTableView(table: GrammarTable) {
    Column(Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
        Row(Modifier.fillMaxWidth()) { table.headers.forEach { Text(it, Modifier.weight(1f).padding(4.dp), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall) } }
        table.rows.forEach { row -> Row(Modifier.fillMaxWidth()) { row.forEach { Text(it, Modifier.weight(1f).padding(4.dp), style = MaterialTheme.typography.bodySmall) } } }
    }
}

@Composable
private fun GrammarHeader(title: String, onBack: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) { IconButton(onClick = onBack) { Text("←") }; Text(title, style = MaterialTheme.typography.headlineSmall) }
}
