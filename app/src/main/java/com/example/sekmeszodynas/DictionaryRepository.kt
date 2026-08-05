package com.example.sekmeszodynas

import android.content.res.AssetManager
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

class DictionaryRepository(
    entries: List<DictionaryEntry>,
    courses: List<Course>,
    lessons: List<Lesson>,
) {
    val entries: List<DictionaryEntry> = entries.toList()
    val courses: List<Course> = courses.sortedBy { it.title }
    val lessons: List<Lesson> = lessons.sortedWith(compareBy(Lesson::courseId, Lesson::order))

    val wordById: Map<WordId, DictionaryEntry> = this.entries.associateBy(DictionaryEntry::id)
    val courseById: Map<String, Course> = this.courses.associateBy(Course::id)
    val lessonById: Map<String, Lesson> = this.lessons.associateBy(Lesson::id)
    val wordIdsByLithuanian: Map<String, List<WordId>> = indexByNormalizedText(DictionaryEntry::lt)
    val wordIdsByRussian: Map<String, List<WordId>> = indexByNormalizedText(DictionaryEntry::ru)

    init {
        require(wordById.size == this.entries.size) { "Dictionary contains duplicate word IDs" }
        require(courseById.size == this.courses.size) { "Catalog contains duplicate course IDs" }
        require(lessonById.size == this.lessons.size) { "Catalog contains duplicate lesson IDs" }

        this.lessons.forEach { lesson ->
            require(courseById.containsKey(lesson.courseId)) {
                "Lesson ${lesson.id} references missing course ${lesson.courseId}"
            }
            lesson.wordRefs.forEach { reference ->
                require(wordById.containsKey(reference.wordId)) {
                    "Lesson ${lesson.id} references missing word ${reference.wordId}"
                }
            }
        }
    }

    fun word(wordId: WordId): DictionaryEntry =
        requireNotNull(wordById[wordId]) { "Unknown word ID: $wordId" }

    fun lessonsForCourse(courseId: String): List<Lesson> =
        lessons.filter { it.courseId == courseId }

    fun wordsForCourse(courseId: String): List<DictionaryEntry> =
        lessonsForCourse(courseId)
            .flatMap(Lesson::wordRefs)
            .map { reference -> word(reference.wordId) }
            .distinctBy(DictionaryEntry::id)

    fun wordsForLesson(lessonId: String): List<DictionaryEntry> =
        requireNotNull(lessonById[lessonId]) { "Unknown lesson ID: $lessonId" }
            .wordRefs
            .map { reference -> word(reference.wordId) }

    fun findByLithuanian(query: String): List<DictionaryEntry> =
        wordIdsByLithuanian[normalizeForIndex(query)].orEmpty().map(::word)

    fun findByRussian(query: String): List<DictionaryEntry> =
        wordIdsByRussian[normalizeForIndex(query)].orEmpty().map(::word)

    private fun indexByNormalizedText(selector: (DictionaryEntry) -> String): Map<String, List<WordId>> =
        entries.groupBy { normalizeForIndex(selector(it)) }
            .mapValues { (_, entries) -> entries.map(DictionaryEntry::id) }

    private fun normalizeForIndex(value: String): String =
        value.trim().lowercase().replace(Regex("\\s+"), " ")
}

class AssetCatalogLoader(private val assets: AssetManager) {
    fun load(): DictionaryRepository {
        val index = parseObject(readAsset("content/index.json"))
        val importedCourse = parseNeDienosVocabulary(readAsset("content/ne_dienos_be_lietuviu_kalbos_zodynas_lt_ru.json"))
        val courseContents = index.requiredArray("coursePaths").map { path ->
            parseCourse(parseObject(readAsset(path.jsonPrimitive.content)))
        }.map { course -> if (course.course.id == NE_DIENOS_COURSE_ID) course.copy(lessons = importedCourse.lessons) else course }

        return DictionaryRepository(
            entries = parseDictionary(parseObject(readAsset("content/dictionary.json"))) + importedCourse.entries,
            courses = courseContents.map { it.course },
            lessons = courseContents.flatMap { it.lessons },
        )
    }

    private fun readAsset(path: String): String =
        assets.open(path).bufferedReader().use { it.readText() }
}

private data class CourseContent(
    val course: Course,
    val lessons: List<Lesson>,
)

private data class ImportedVocabulary(val entries: List<DictionaryEntry>, val lessons: List<Lesson>)

private const val NE_DIENOS_COURSE_ID = "ne-dienos-be-lietuviu-kalbos"

private val catalogJson = Json { ignoreUnknownKeys = false }

private fun parseObject(source: String): JsonObject = catalogJson.parseToJsonElement(source).jsonObject

private fun parseDictionary(root: JsonObject): List<DictionaryEntry> =
    root.requiredArray("entries").map { element ->
        val entry = element.jsonObject
        DictionaryEntry(
            id = entry.requiredString("id"),
            ru = entry.requiredString("ru"),
            lt = entry.requiredString("lt"),
            type = entry.requiredString("type"),
        )
    }

private fun parseCourse(root: JsonObject): CourseContent {
    val courseId = root.requiredString("id")
    return CourseContent(
        course = Course(
            id = courseId,
            title = root.requiredString("title"),
            description = root.optionalString("description").orEmpty(),
            capabilities = root.optionalArray("capabilities")
                ?.map { element -> CourseCapability.valueOf(element.jsonPrimitive.content) }
                ?.toSet()
                ?: setOf(CourseCapability.DICTIONARY, CourseCapability.QUIZ),
            visibility = root.optionalString("visibility")
                ?.let(CourseVisibility::valueOf)
                ?: CourseVisibility.LEARNING,
        ),
        lessons = root.requiredArray("lessons").map { element ->
            val lesson = element.jsonObject
            Lesson(
                id = lesson.requiredString("id"),
                courseId = courseId,
                title = lesson.requiredString("title"),
                order = lesson.requiredInt("order"),
                wordRefs = lesson.requiredArray("wordRefs").map { reference ->
                    LessonWordRef(reference.jsonObject.requiredString("wordId"))
                },
            )
        },
    )
}

/** User-provided course vocabulary remains in its compact authoring format in assets. */
private fun parseNeDienosVocabulary(source: String): ImportedVocabulary {
    val chapters = parseObject(source).requiredArray("chapters")
    val entriesByKey = linkedMapOf<String, DictionaryEntry>()
    val lessons = chapters.mapIndexed { chapterIndex, chapterElement ->
        val chapter = chapterElement.jsonObject
        val references = chapter.requiredArray("terms").map { termElement ->
            val term = termElement.jsonObject
            val lt = term.requiredString("lit")
            val ru = term.requiredString("ru")
            val type = term.requiredString("type")
            val key = "$lt\u0000$ru\u0000$type"
            val entry = entriesByKey.getOrPut(key) {
                DictionaryEntry(
                    id = "ne_dienos_%04d".format(entriesByKey.size + 1),
                    ru = ru,
                    lt = lt,
                    type = type,
                )
            }
            LessonWordRef(entry.id)
        }
        Lesson(
            id = chapter.requiredString("id"),
            courseId = NE_DIENOS_COURSE_ID,
            title = chapter.requiredString("titleRu"),
            order = chapterIndex + 1,
            wordRefs = references,
        )
    }
    return ImportedVocabulary(entriesByKey.values.toList(), lessons)
}

private fun JsonObject.requiredArray(key: String): JsonArray = getValue(key).jsonArray

private fun JsonObject.optionalArray(key: String): JsonArray? = get(key)?.jsonArray

private fun JsonObject.requiredString(key: String): String = getValue(key).jsonPrimitive.content

private fun JsonObject.optionalString(key: String): String? = get(key)?.jsonPrimitive?.content

private fun JsonObject.requiredInt(key: String): Int = getValue(key).jsonPrimitive.int
