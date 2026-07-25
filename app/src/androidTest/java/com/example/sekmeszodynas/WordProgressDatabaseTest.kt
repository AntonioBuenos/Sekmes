package com.example.sekmeszodynas

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class WordProgressDatabaseTest {
    private lateinit var database: SekmesDatabase
    private lateinit var repository: WordProgressRepository

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, SekmesDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        repository = RoomWordProgressRepository(database.wordProgressDao())
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun statusIsStoredObservedAndReset() = runBlocking {
        val wordId = "word_000001"

        assertEquals(WordLearningStatus.NEW, repository.get(wordId).status)

        repository.setStatus(
            wordId = wordId,
            status = WordLearningStatus.HARD,
            updatedAtEpochMillis = 123L,
        )

        assertEquals(WordLearningStatus.HARD, repository.get(wordId).status)
        assertEquals(123L, repository.observe(wordId).first().updatedAtEpochMillis)
        assertEquals(
            WordLearningStatus.HARD,
            repository.observeAll().first().getValue(wordId).status,
        )

        repository.reset(wordId)
        assertEquals(WordProgress(wordId), repository.get(wordId))
    }
}
