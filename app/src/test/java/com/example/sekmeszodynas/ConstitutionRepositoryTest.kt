package com.example.sekmeszodynas

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ConstitutionRepositoryTest {
    private val dictionary = DictionaryRepository(
        entries = listOf(DictionaryEntry("word_constitution", "Конституция", "Konstitucija", "n")),
        courses = emptyList(),
        lessons = emptyList(),
    )

    @Test
    fun blockProvidesPreambleAndArticlesInDeclaredOrder() {
        val repository = ConstitutionRepository(validDocument(), dictionary)

        val content = repository.contentForBlock("block")

        assertEquals("preamble", content.first().id)
        assertEquals(155, content.size)
        assertEquals("article-154", content.last().id)
        assertEquals("Konstitucija", repository.dictionaryEntry("word_constitution").lt)
    }

    @Test
    fun rejectsMissingDictionaryTermLinks() {
        val invalidArticle = article(number = 1).copy(
            parts = listOf(part(termWordId = "missing_word")),
        )
        val document = validDocument().copy(
            articles = listOf(invalidArticle) + (2..154).map(::article),
        )

        val error = runCatching { ConstitutionRepository(document, dictionary) }.exceptionOrNull()

        assertTrue(error is IllegalArgumentException)
        assertTrue(error?.message.orEmpty().contains("missing from dictionary"))
    }

    @Test
    fun rejectsNumericFragmentOutsideText() {
        val invalidArticle = article(number = 1).copy(
            parts = listOf(part(numericEnd = 99)),
        )
        val document = validDocument().copy(
            articles = listOf(invalidArticle) + (2..154).map(::article),
        )

        val error = runCatching { ConstitutionRepository(document, dictionary) }.exceptionOrNull()

        assertTrue(error is IllegalArgumentException)
        assertTrue(error?.message.orEmpty().contains("invalid range"))
    }

    private fun validDocument(): ConstitutionDocument {
        val articles = (1..154).map(::article)
        return ConstitutionDocument(
            title = "Test Constitution",
            preamble = ConstitutionPreamble(
                id = "preamble",
                blockId = "block",
                titleLt = "PREAMBULĖ",
                titleRu = "ПРЕАМБУЛА",
                parts = (1..10).map { index -> part(id = "p$index") },
            ),
            blocks = listOf(
                ConstitutionBlock(
                    id = "block",
                    order = 1,
                    title = "Test block",
                    description = "Test description",
                    articleStart = 1,
                    articleEnd = 154,
                    includesPreamble = true,
                    articleIds = listOf("preamble") + articles.map(ConstitutionArticle::id),
                ),
            ),
            articles = articles,
        )
    }

    private fun article(number: Int): ConstitutionArticle = ConstitutionArticle(
        id = "article-$number",
        number = number,
        blockId = "block",
        sectionTitle = "Section",
        titleLt = "$number straipsnis",
        titleRu = "Статья $number",
        parts = listOf(part(id = "$number.1")),
    )

    private fun part(
        id: String = "1.1",
        termWordId: WordId = "word_constitution",
        numericEnd: Int = 16,
    ): ConstitutionPart = ConstitutionPart(
        id = id,
        sourcePartId = id,
        itemNumber = null,
        lt = "Konstitucija 1/5",
        ru = "Конституция 1/5",
        ltNumericFragments = listOf(NumericFragment(start = 13, end = numericEnd, type = NumericFragmentType.FRACTION)),
        ruNumericFragments = listOf(
            NumericFragment(
                start = 12,
                end = if (numericEnd == 16) 15 else numericEnd,
                type = NumericFragmentType.FRACTION,
            ),
        ),
        termLinks = listOf(ConstitutionTermLink(wordId = termWordId, start = 0, end = 12)),
    )
}
