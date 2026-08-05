package com.example.sekmeszodynas.feature.grammar

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class GrammarCatalogTest {
    @Test
    fun acceptsCardWithoutTable() {
        val card = parseGrammarCards(
            """{"cards":[{"id":"test","category":"Тест","title":"Заголовок","rule":"Правило","hint":null,"examples":[],"exceptions":[],"table":null}]}""",
        ).single()

        assertEquals(null, card.table)
    }

    @Test
    fun parsesStructuredTableExamplesAndExceptions() {
        val cards = parseGrammarCards(
            """{"cards":[{"id":"test","category":"Тест","title":"Заголовок","rule":"Правило","hint":"Подсказка","examples":["Пример"],"exceptions":["Исключение"],"table":{"headers":["A"],"rows":[["B"]]}}]}""",
        )

        val card = cards.single()
        assertEquals("Тест", card.category)
        assertEquals(listOf("Пример"), card.examples)
        assertEquals(listOf("Исключение"), card.exceptions)
        assertNotNull(card.table)
        assertEquals("B", card.table!!.rows.single().single())
    }
}
