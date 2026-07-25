package com.example.sekmeszodynas

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class WordProgressRepositoryTest {
    @Test
    fun newStatusIsDefaultAndExplicitStatusCanBeReset() = runBlocking {
        val repository = RoomWordProgressRepository(FakeWordProgressDao())
        val wordId = "word_000001"

        assertEquals(WordLearningStatus.NEW, repository.get(wordId).status)

        repository.setStatus(wordId, WordLearningStatus.KNOWN, updatedAtEpochMillis = 42L)
        assertEquals(WordLearningStatus.KNOWN, repository.get(wordId).status)
        assertEquals(42L, repository.get(wordId).updatedAtEpochMillis)

        repository.reset(wordId)
        assertEquals(WordProgress(wordId), repository.get(wordId))
    }

    @Test
    fun sharedWordIdKeepsOneProgressAcrossCourses() = runBlocking {
        val word = DictionaryEntry("word_000001", "быть", "būti", "v")
        val catalog = DictionaryRepository(
            entries = listOf(word),
            courses = listOf(Course("first", "Первый"), Course("second", "Второй")),
            lessons = listOf(
                Lesson("first-01", "first", "Урок 1", 1, listOf(LessonWordRef(word.id))),
                Lesson("second-01", "second", "Урок 1", 1, listOf(LessonWordRef(word.id))),
            ),
        )
        val progress = RoomWordProgressRepository(FakeWordProgressDao())

        progress.setStatus(catalog.wordsForLesson("first-01").single().id, WordLearningStatus.KNOWN)

        assertEquals("word_000001", catalog.wordsForLesson("second-01").single().id)
        assertEquals(WordLearningStatus.KNOWN, progress.get(catalog.wordsForLesson("second-01").single().id).status)
    }
}

private class FakeWordProgressDao : WordProgressDao {
    private val state = MutableStateFlow<Map<WordId, WordProgressEntity>>(emptyMap())

    override fun observeAll(): Flow<List<WordProgressEntity>> =
        state.map { entities -> entities.values.sortedBy(WordProgressEntity::wordId) }

    override fun observe(wordId: WordId): Flow<WordProgressEntity?> =
        state.map { entities -> entities[wordId] }

    override suspend fun get(wordId: WordId): WordProgressEntity? = state.value[wordId]

    override suspend fun upsert(progress: WordProgressEntity) {
        state.value = state.value + (progress.wordId to progress)
    }

    override suspend fun delete(wordId: WordId) {
        state.value = state.value - wordId
    }
}
