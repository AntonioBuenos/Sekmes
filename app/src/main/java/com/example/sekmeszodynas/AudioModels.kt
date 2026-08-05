package com.example.sekmeszodynas

import android.content.res.AssetManager
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

data class AudioTrack(
    val id: Int,
    val title: String,
    val type: AudioType,
    val resourceName: String = "audio_$id",
)

enum class AudioType { POKALBIS, SKAITYMAS, KLAUSYMAS }

data class AudioChapter(val number: Int, val title: String, val tracks: List<AudioTrack>)

data class AudioBook(val number: Int, val chapters: List<AudioChapter>)

data class AudioCourse(val courseId: String, val books: List<AudioBook>)

/** Content catalogue is intentionally loaded from assets so a new course does not need UI code. */
object AudioCatalogStore {
    private var courses: List<AudioCourse> = emptyList()

    fun initialize(assets: AssetManager) {
        val source = assets.open("content/audio-catalog.json").bufferedReader().use { it.readText() }
        courses = parseAudioCourses(source)
    }

    fun booksForCourse(courseId: String): List<AudioBook> =
        courses.firstOrNull { it.courseId == courseId }?.books.orEmpty()

    internal fun installForTests(value: List<AudioCourse>) {
        courses = value
    }
}

fun audioBooksForCourse(courseId: String): List<AudioBook> = AudioCatalogStore.booksForCourse(courseId)

internal fun parseAudioCourses(source: String): List<AudioCourse> {
    val root = Json { ignoreUnknownKeys = false }.parseToJsonElement(source).jsonObject
    return root.getValue("courses").jsonArray.map { courseElement ->
        val course = courseElement.jsonObject
        AudioCourse(
            courseId = course.string("courseId"),
            books = course.getValue("books").jsonArray.map { bookElement ->
                val book = bookElement.jsonObject
                AudioBook(
                    number = book.integer("number"),
                    chapters = book.getValue("chapters").jsonArray.map { chapterElement ->
                        val chapter = chapterElement.jsonObject
                        AudioChapter(
                            number = chapter.integer("number"),
                            title = chapter.string("title"),
                            tracks = chapter.getValue("trackGroups").jsonArray.flatMap { groupElement ->
                                val group = groupElement.jsonObject
                                val from = group.integer("from")
                                val to = group.integer("to")
                                val type = AudioType.valueOf(group.string("type"))
                                val resourcePrefix = group.optionalString("resourcePrefix") ?: "audio_"
                                (from..to).map { id ->
                                    AudioTrack(
                                        id = id,
                                        title = group.trackTitle(id, from),
                                        type = type,
                                        resourceName = if (resourcePrefix == "ne_dienos_") {
                                            "$resourcePrefix${id.toString().padStart(3, '0')}"
                                        } else {
                                            "$resourcePrefix$id"
                                        },
                                    )
                                }
                            },
                        )
                    },
                )
            },
        )
    }
}

private fun JsonObject.trackTitle(id: Int, from: Int): String = when (string("label")) {
    "intro" -> "$id | Įžanga (Titulinis)"
    "dialogue" -> "$id | Pokalbis ${id - from + 1}"
    "reading" -> "$id | Skaitymas"
    "listening" -> "$id | Klausymas"
    "listeningExercise" -> "$id | Klausymo užduotis ${id - from + 1}"
    "readingExercise" -> "$id | Skaitymo užduotis ${id - from + 1}"
    else -> error("Unknown audio track label: ${string("label")}")
}

private fun JsonObject.string(key: String): String = getValue(key).jsonPrimitive.content

private fun JsonObject.optionalString(key: String): String? = get(key)?.jsonPrimitive?.content

private fun JsonObject.integer(key: String): Int = getValue(key).jsonPrimitive.int
