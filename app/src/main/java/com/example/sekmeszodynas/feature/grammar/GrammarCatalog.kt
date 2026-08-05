package com.example.sekmeszodynas.feature.grammar

import android.content.res.AssetManager
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

data class GrammarTable(val headers: List<String>, val rows: List<List<String>>)

data class GrammarCard(
    val id: String,
    val category: String,
    val title: String,
    val rule: String,
    val hint: String?,
    val examples: List<String>,
    val exceptions: List<String>,
    val table: GrammarTable?,
)

object GrammarCatalogStore {
    private var cards: List<GrammarCard> = emptyList()

    fun initialize(assets: AssetManager) {
        val source = assets.open("content/grammar-cards.json").bufferedReader().use { it.readText() }
        cards = parseGrammarCards(source)
    }

    fun all(): List<GrammarCard> = cards
}

internal fun parseGrammarCards(source: String): List<GrammarCard> =
    Json { ignoreUnknownKeys = false }.parseToJsonElement(source).jsonObject
        .getValue("cards").jsonArray.map { element ->
            val card = element.jsonObject
            GrammarCard(
                id = card.string("id"),
                category = card.string("category"),
                title = card.string("title"),
                rule = card.string("rule"),
                hint = card["hint"]?.jsonPrimitive?.content,
                examples = card.strings("examples"),
                exceptions = card.strings("exceptions"),
                table = (card["table"] as? kotlinx.serialization.json.JsonObject)?.let { table ->
                    GrammarTable(
                        headers = table.strings("headers"),
                        rows = table.getValue("rows").jsonArray.map { row -> row.jsonArray.map { it.jsonPrimitive.content } },
                    )
                },
            )
        }

private fun kotlinx.serialization.json.JsonObject.string(key: String): String = getValue(key).jsonPrimitive.content

private fun kotlinx.serialization.json.JsonObject.strings(key: String): List<String> =
    getValue(key).jsonArray.map { it.jsonPrimitive.content }
