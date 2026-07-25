package com.example.sekmeszodynas

typealias WordId = String

data class DictionaryEntry(
    val id: WordId,
    val ru: String,
    val lt: String,
    val type: String,
)

data class Course(
    val id: String,
    val title: String,
)

data class Lesson(
    val id: String,
    val courseId: String,
    val title: String,
    val order: Int,
    val wordRefs: List<LessonWordRef>,
)

data class LessonWordRef(
    val wordId: WordId,
)
