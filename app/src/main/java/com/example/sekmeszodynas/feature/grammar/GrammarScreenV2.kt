package com.example.sekmeszodynas.feature.grammar

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.NavigateBefore
import androidx.compose.material.icons.automirrored.rounded.NavigateNext
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.sekmeszodynas.ui.components.SekmesEmptyState
import com.example.sekmeszodynas.ui.components.SekmesTopAppBar
import com.example.sekmeszodynas.ui.theme.SekmesSpacing

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

    Column(Modifier.fillMaxSize()) {
        SekmesTopAppBar("Грамматические карточки", subtitle = "${visibleCards.size} правил", onBack = onBack)
        OutlinedTextField(query, { query = it }, label = { Text("Поиск правила") }, singleLine = true, modifier = Modifier.fillMaxWidth().padding(horizontal = SekmesSpacing.Medium))
        Row(Modifier.horizontalScroll(rememberScrollState()).padding(horizontal = SekmesSpacing.Medium, vertical = SekmesSpacing.XSmall), horizontalArrangement = Arrangement.spacedBy(SekmesSpacing.XSmall)) {
            FilterChip(category == null, { category = null }, label = { Text("Все") })
            cards.map(GrammarCard::category).distinct().forEach { value ->
                FilterChip(category == value, { category = value }, label = { Text(value) })
            }
            FilterChip(favoritesOnly, { favoritesOnly = !favoritesOnly }, label = { Text("Избранное") }, leadingIcon = { Icon(if (favoritesOnly) Icons.Rounded.Star else Icons.Rounded.StarBorder, null) })
        }
        if (visibleCards.isEmpty()) {
            SekmesEmptyState("Карточки не найдены", "Измените запрос или фильтр.", modifier = Modifier.weight(1f))
        } else {
            LazyColumn(Modifier.weight(1f), contentPadding = PaddingValues(horizontal = SekmesSpacing.Medium), verticalArrangement = Arrangement.spacedBy(SekmesSpacing.XSmall)) {
                items(visibleCards, key = GrammarCard::id) { card ->
                    OutlinedCard(onClick = { onCard(card.id) }, modifier = Modifier.fillMaxWidth()) {
                        Row(Modifier.padding(SekmesSpacing.Small), verticalAlignment = Alignment.CenterVertically) {
                            Column(Modifier.weight(1f)) { Text(card.category, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary); Text(card.title, style = MaterialTheme.typography.titleMedium); Text(card.rule, style = MaterialTheme.typography.bodySmall, maxLines = 2, modifier = Modifier.padding(top = SekmesSpacing.XxxSmall)) }
                            IconButton(onClick = { favorites = if (card.id in favorites) favorites - card.id else favorites + card.id }) { Icon(if (card.id in favorites) Icons.Rounded.Star else Icons.Rounded.StarBorder, contentDescription = if (card.id in favorites) "Убрать из избранного" else "В избранное") }
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
    Column(Modifier.fillMaxSize()) {
        SekmesTopAppBar(card.title, subtitle = card.category, onBack = onBack)
        Column(Modifier.padding(horizontal = SekmesSpacing.Medium)) {
        Text(card.rule, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = SekmesSpacing.Small))
        card.table?.let { table -> GrammarTableView(table) }
        card.examples.forEach { Text("• $it", modifier = Modifier.padding(vertical = 3.dp), style = MaterialTheme.typography.bodyLarge) }
        card.exceptions.forEach { Text("Важно: $it", modifier = Modifier.padding(top = 8.dp), color = MaterialTheme.colorScheme.error) }
        card.hint?.let { Text("Подсказка: $it", modifier = Modifier.padding(top = 8.dp), color = MaterialTheme.colorScheme.primary) }
        Row(Modifier.fillMaxWidth().padding(top = SekmesSpacing.Small), horizontalArrangement = Arrangement.SpaceBetween) {
            TextButton(onClick = { cards.getOrNull(index - 1)?.let { onCard(it.id) } }, enabled = index > 0) { Icon(Icons.AutoMirrored.Rounded.NavigateBefore, null); Text("Предыдущая") }
            TextButton(onClick = { cards.getOrNull(index + 1)?.let { onCard(it.id) } }, enabled = index < cards.lastIndex) { Text("Следующая"); Icon(Icons.AutoMirrored.Rounded.NavigateNext, null) }
        }
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
