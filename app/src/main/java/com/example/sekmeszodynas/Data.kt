package com.example.sekmeszodynas

import android.content.res.AssetManager

const val SEKMES_COURSE_ID = "sekmes"

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
    get() {
        val repository = CatalogStore.repository()
        return repository.lessonsForCourse(SEKMES_COURSE_ID).associate { lesson ->
            lesson.id to Theme(
                id = lesson.id,
                title = lesson.title,
                words = repository.wordsForLesson(lesson.id).map { entry ->
                    Word(ru = entry.ru, lt = entry.lt, type = entry.type, id = entry.id)
                },
            )
        }
    }

val GLOBAL_POOL: List<Word>
    get() = CatalogStore.repository().entries.map { entry ->
        Word(ru = entry.ru, lt = entry.lt, type = entry.type, id = entry.id)
    }
