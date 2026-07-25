package com.example.sekmeszodynas

import androidx.media3.common.Player
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import kotlin.random.Random

class QuizLogicTest {

    @Before
    fun installCatalogFixture() {
        val entries = listOf(
            DictionaryEntry("word_000001", "один", "vienas", "n"),
            DictionaryEntry("word_000002", "два", "du", "n"),
            DictionaryEntry("word_000003", "три", "trys", "n"),
            DictionaryEntry("word_000004", "четыре", "keturi", "n"),
        )
        CatalogStore.installForTests(
            DictionaryRepository(
                entries = entries,
                courses = listOf(Course(SEKMES_COURSE_ID, "Sekmes")),
                lessons = listOf(
                    Lesson(
                        id = "1",
                        courseId = SEKMES_COURSE_ID,
                        title = "Тестовая тема",
                        order = 1,
                        wordRefs = entries.map { LessonWordRef(it.id) },
                    ),
                ),
            ),
        )
    }

    @Test
    fun allThemesPoolContainsOnlyUniqueWordIds() {
        val pool = quizPoolForTheme("all")

        assertEquals(GLOBAL_POOL.distinctBy { it.id }.size, pool.size)
        assertEquals(pool.size, pool.map { it.id }.distinct().size)
    }

    @Test
    fun knownWordsAreExcludedFromQuizPool() {
        val pool = quizPoolForTheme(
            themeId = "all",
            progressByWordId = mapOf(
                "word_000001" to WordProgress("word_000001", WordLearningStatus.KNOWN),
            ),
        )

        assertTrue(pool.none { it.id == "word_000001" })
    }

    @Test
    fun hardWordsHaveHigherSelectionWeightWithoutImmediateRepeat() {
        val words = THEMES_DATA.getValue("1").words
        val hardId = words.first().id
        val random = Random(7)
        val selections = List(300) {
            selectNextQuizWord(words, setOf(hardId), previousWordId = null, random = random)!!.id
        }

        assertTrue(selections.count { it == hardId } > selections.count { it == words[1].id })
        assertTrue(
            selectNextQuizWord(words, emptySet(), previousWordId = words.first().id, random = Random(1))!!.id != words.first().id,
        )
    }

    @Test
    fun generatedOptionsContainCorrectAnswerAndUniqueDistractors() {
        val themeWords = THEMES_DATA.getValue("1").words
        val correctWord = themeWords.first()

        repeat(20) {
            val options = generateOptions(correctWord, themeWords)

            assertEquals(4, options.size)
            assertEquals(4, options.distinct().size)
            assertTrue(correctWord.lt in options)
        }
    }

    @Test
    fun scoreCountsWordsAnsweredWithoutMistakes() {
        val mistakes = mapOf(
            "first" to 3,
            "second" to 1
        )

        assertEquals(8, calculateQuizScore(total = 10, mistakes = mistakes))
    }

    @Test
    fun completedPlaybackRestartsFromBeginning() {
        assertEquals(0L, playbackStartPosition(Player.STATE_ENDED, 42_000L))
        assertEquals(12_000L, playbackStartPosition(Player.STATE_READY, 12_000L))
    }

    @Test
    fun everyAudioTrackHasMatchingRawResource() {
        val trackResourceNames = AUDIO_BOOKS
            .flatMap { it.chapters }
            .flatMap { it.tracks }
            .map { "audio_${it.id}" }
            .toSet()
        val packagedAudioResources = R.raw::class.java.fields
            .map { it.name }
            .filter { it.startsWith("audio_") }
            .toSet()

        assertEquals(275, trackResourceNames.size)
        assertEquals(trackResourceNames, packagedAudioResources)
    }
}
