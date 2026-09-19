package com.example.sekmeszodynas

import org.junit.Assert.assertEquals
import org.junit.Test

class VisualCardCatalogTest {
    private val words = listOf(
        Word("кошка", "katė", "n", "cat", setOf("course-a")),
        Word("собака", "šuo", "n", "dog", setOf("course-b")),
    )
    private val assets = listOf(
        VisualCardAsset("cat", 1),
        VisualCardAsset("dog", 2),
        VisualCardAsset("missing", 3),
    )

    @Test
    fun selectsOnlyPackagedCardsMatchingVocabularyScope() {
        val cards = selectVisualVocabularyCards(words, VocabularyScope(sourceId = "course-a"), assets)

        assertEquals(listOf("katė"), cards.map { it.word.lt })
        assertEquals(listOf(1), cards.map { it.imageResId })
    }
}
