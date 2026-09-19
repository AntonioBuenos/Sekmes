package com.example.sekmeszodynas.feature.grammar

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

private data class LegacyGrammarCard(val id: String, val category: String, val title: String, val blocks: List<String>)
private val cards = listOf(
    LegacyGrammarCard("verb-o", "Глаголы", "Спряжение: -o tipas", listOf("ragauti, ragauja, ragavo", "aš — ragavau    mes — ragavome", "tu — ragavai    jūs — ragavote", "jis, ji — ragavo    jie, jos — ragavo")),
    LegacyGrammarCard("su-instrumental", "Предлоги", "su + Įn. (Inst.) kuo?", listOf("Предлог su требует творительного падежа.", "Ar tu mėgsti ledus su uogiene?", "Ты любишь мороженое с вареньем?")),
    LegacyGrammarCard("pronouns", "Местоимения", "Личные местоимения: падежи", listOf("Vienaskaita", "V. aš / tu / jis / ji", "K. manęs / tavęs / jo / jos", "N. man / tau / jam / jai", "Įn. manimi / tavimi / juo / ja", "Daugiskaita", "V. mes / jūs / jie / jos", "K. mūsų / jūsų / jų / jų", "N. mums / jums / jiems / joms", "Įn. mumis / jumis / jais / jomis")),
)

@Composable private fun LegacyGrammarCardsScreen(onCard: (String) -> Unit, onBack: () -> Unit) { Column(Modifier.fillMaxSize().padding(16.dp)) { Header("Грамматические карточки", onBack); LazyColumn { items(cards, key = LegacyGrammarCard::id) { card -> Card(onClick = { onCard(card.id) }, modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) { Column(Modifier.padding(16.dp)) { Text(card.category, style = MaterialTheme.typography.labelLarge); Text(card.title, style = MaterialTheme.typography.titleMedium) } } } } } }
@Composable private fun LegacyGrammarCardScreen(id: String, onBack: () -> Unit) { val card = cards.firstOrNull { it.id == id } ?: return; Column(Modifier.fillMaxSize().padding(16.dp)) { Header(card.title, onBack); card.blocks.forEach { Text(it, modifier = Modifier.padding(vertical = 4.dp), style = MaterialTheme.typography.bodyLarge, fontWeight = if (it == "Vienaskaita" || it == "Daugiskaita") FontWeight.Bold else null) } } }
@Composable private fun Header(title: String, onBack: () -> Unit) { Row(verticalAlignment = Alignment.CenterVertically) { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Назад") }; Text(title, style = MaterialTheme.typography.headlineSmall) } }
