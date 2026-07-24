package com.example.sekmeszodynas

import androidx.media3.common.Player
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class QuizLogicTest {

    @Test
    fun allThemesPoolContainsOnlyUniqueWordIds() {
        val pool = quizPoolForTheme("all")

        assertEquals(GLOBAL_POOL.distinctBy { it.id }.size, pool.size)
        assertEquals(pool.size, pool.map { it.id }.distinct().size)
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
