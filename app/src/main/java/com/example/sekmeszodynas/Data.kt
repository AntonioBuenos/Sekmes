package com.example.sekmeszodynas

import android.content.res.AssetManager

const val SEKMES_COURSE_ID = "sekmes"
const val USER_WORDS_SOURCE_ID = "user"

data class VocabularySource(val id: String, val title: String)

data class VocabularyScope(
    val partOfSpeech: PartOfSpeech? = null,
    val sourceId: String? = null,
) {
    fun contains(word: Word): Boolean =
        (partOfSpeech == null || word.partOfSpeech == partOfSpeech) &&
            (sourceId == null || sourceId in word.sourceIds)
}

object CatalogStore {
    private var catalog: DictionaryRepository? = null

    fun initialize(assets: AssetManager) {
        catalog = AssetCatalogLoader(assets).load()
    }

    fun installForTests(repository: DictionaryRepository) {
        catalog = repository
    }

    fun repository(): DictionaryRepository =
        requireNotNull(catalog) { "CatalogStore must be initialized before use" }
}

// Transitional UI projections. The source of truth is DictionaryRepository;
// lessons retain only wordId references in the JSON course file.
val THEMES_DATA: Map<String, Theme>
    get() = themesForCourse(SEKMES_COURSE_ID)

fun themesForCourse(courseId: String): Map<String, Theme> {
    val repository = CatalogStore.repository()
    return repository.lessonsForCourse(courseId).associate { lesson ->
            lesson.id to Theme(
                id = lesson.id,
                title = lesson.title,
                words = repository.wordsForLesson(lesson.id).map { entry ->
                    Word(ru = entry.ru, lt = entry.lt, type = entry.type, id = entry.id)
                },
            )
        }
}

fun wordsForCourse(courseId: String): List<Word> =
    CatalogStore.repository().wordsForCourse(courseId).map { entry ->
        Word(ru = entry.ru, lt = entry.lt, type = entry.type, id = entry.id, sourceIds = entry.sourceIds)
    }

val GLOBAL_POOL: List<Word>
    get() = CatalogStore.repository().entries.map { entry ->
        Word(ru = entry.ru, lt = entry.lt, type = entry.type, id = entry.id, sourceIds = entry.sourceIds)
    }

val DictionaryEntry.sourceIds: Set<String>
    get() = CatalogStore.repository().lessons
        .asSequence()
        .filter { lesson -> lesson.wordRefs.any { it.wordId == id } }
        .map(Lesson::courseId)
        .toSet()

val Word.partOfSpeech: PartOfSpeech get() = PartOfSpeech.fromTypeCode(type)

fun vocabularySources(): List<VocabularySource> =
    CatalogStore.repository().courses
        .filter { it.visibility != CourseVisibility.FIXTURE }
        .map { VocabularySource(it.id, it.title) } + VocabularySource(USER_WORDS_SOURCE_ID, "Мои слова")
