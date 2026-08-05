package com.example.sekmeszodynas

typealias WordId = String

enum class PartOfSpeech(val typeCode: String, val label: String) {
    NOUN("n", "Существительные"),
    VERB("v", "Глаголы"),
    ADJECTIVE("adj", "Прилагательные"),
    ADVERB("adv", "Наречия"),
    PREPOSITION("prep", "Предлоги"),
    OTHER("other", "Другие"),
    ;

    companion object {
        fun fromTypeCode(value: String): PartOfSpeech =
            entries.firstOrNull { it.typeCode == value } ?: OTHER
    }
}

data class DictionaryEntry(
    val id: WordId,
    val ru: String,
    val lt: String,
    val type: String,
)

data class Course(
    val id: String,
    val title: String,
    val description: String = "",
    val capabilities: Set<CourseCapability> = setOf(CourseCapability.DICTIONARY, CourseCapability.QUIZ),
    val visibility: CourseVisibility = CourseVisibility.LEARNING,
)

val DictionaryEntry.partOfSpeech: PartOfSpeech
    get() = PartOfSpeech.fromTypeCode(type)

enum class CourseCapability {
    DICTIONARY,
    QUIZ,
    AUDIO,
}

enum class CourseVisibility {
    LEARNING,
    SPECIAL,
    FIXTURE,
}

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
