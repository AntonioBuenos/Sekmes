package com.example.sekmeszodynas

import android.content.res.AssetManager
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.int
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

class ConstitutionRepository(
    val document: ConstitutionDocument,
    private val dictionary: DictionaryRepository,
) {
    val blocks: List<ConstitutionBlock> = document.blocks.sortedBy(ConstitutionBlock::order)
    val blockById: Map<ConstitutionBlockId, ConstitutionBlock> = blocks.associateBy(ConstitutionBlock::id)
    val articleById: Map<ConstitutionArticleId, ConstitutionArticle> =
        document.articles.associateBy(ConstitutionArticle::id)

    init {
        require(blockById.size == blocks.size) { "Constitution contains duplicate block IDs" }
        require(articleById.size == document.articles.size) { "Constitution contains duplicate article IDs" }
        require(document.preamble.parts.size == 10) { "The preamble must contain exactly 10 parts" }
        require(articleById.keys == (1..154).map { "article-$it" }.toSet()) {
            "Constitution must contain articles 1 through 154"
        }

        val referencedContentIds = blocks.flatMap(ConstitutionBlock::articleIds)
        require(referencedContentIds.size == referencedContentIds.toSet().size) {
            "Constitution blocks must not overlap"
        }
        require(referencedContentIds.toSet() == articleById.keys + document.preamble.id) {
            "Constitution blocks must cover every article and the preamble"
        }

        allContent().forEach(::validateContent)
    }

    fun contentForBlock(blockId: ConstitutionBlockId): List<ConstitutionContent> =
        requireNotNull(blockById[blockId]) { "Unknown Constitution block: $blockId" }
            .articleIds
            .map(::content)

    fun content(contentId: ConstitutionArticleId): ConstitutionContent = when (contentId) {
        document.preamble.id -> document.preamble
        else -> requireNotNull(articleById[contentId]) { "Unknown Constitution content: $contentId" }
    }

    fun dictionaryEntry(wordId: WordId): DictionaryEntry = dictionary.word(wordId)

    private fun allContent(): List<ConstitutionContent> = listOf(document.preamble) + document.articles

    private fun validateContent(content: ConstitutionContent) {
        require(blockById.containsKey(content.blockId)) {
            "Content ${content.id} references unknown block ${content.blockId}"
        }
        require(content.parts.isNotEmpty()) { "Content ${content.id} has no parts" }
        content.parts.forEach { part ->
            validateRanges(part.lt, part.ltNumericFragments, "Lithuanian numeric", part.id)
            validateRanges(part.ru, part.ruNumericFragments, "Russian numeric", part.id)
            validateTermLinks(part)
        }
    }

    private fun validateTermLinks(part: ConstitutionPart) {
        var previousEnd = 0
        part.termLinks.sortedBy(ConstitutionTermLink::start).forEach { link ->
            require(link.start >= previousEnd && link.end > link.start && link.end <= part.lt.length) {
                "Term link ${link.wordId} has invalid range in ${part.id}"
            }
            require(dictionary.wordById.containsKey(link.wordId)) {
                "Term link ${link.wordId} in ${part.id} is missing from dictionary"
            }
            previousEnd = link.end
        }
    }

    private fun validateRanges(
        text: String,
        fragments: List<NumericFragment>,
        label: String,
        partId: ConstitutionPartId,
    ) {
        var previousEnd = 0
        fragments.sortedBy(NumericFragment::start).forEach { fragment ->
            require(fragment.start >= previousEnd && fragment.end > fragment.start && fragment.end <= text.length) {
                "$label fragment has invalid range in $partId"
            }
            previousEnd = fragment.end
        }
    }
}

class ConstitutionAssetLoader(private val assets: AssetManager) {
    fun load(dictionary: DictionaryRepository): ConstitutionRepository =
        ConstitutionRepository(parseDocument(readAsset("content/constitution.json")), dictionary)

    private fun readAsset(path: String): String =
        assets.open(path).bufferedReader().use { it.readText() }
}

object ConstitutionStore {
    private var repository: ConstitutionRepository? = null

    fun initialize(assets: AssetManager, dictionary: DictionaryRepository) {
        repository = ConstitutionAssetLoader(assets).load(dictionary)
    }

    fun installForTests(value: ConstitutionRepository) {
        repository = value
    }

    fun repository(): ConstitutionRepository =
        requireNotNull(repository) { "ConstitutionStore must be initialized before use" }
}

private val constitutionJson = Json { ignoreUnknownKeys = false }

private fun parseDocument(source: String): ConstitutionDocument {
    val root = constitutionJson.parseToJsonElement(source).jsonObject
    require(root.requiredInt("schemaVersion") == 1) { "Unsupported Constitution schema" }
    return ConstitutionDocument(
        title = root.requiredString("title"),
        preamble = root.requiredObject("preamble").toPreamble(),
        blocks = root.requiredArray("blocks").map { it.jsonObject.toBlock() },
        articles = root.requiredArray("articles").map { it.jsonObject.toArticle() },
    )
}

private fun JsonObject.toBlock(): ConstitutionBlock = ConstitutionBlock(
    id = requiredString("id"),
    order = requiredInt("order"),
    title = requiredString("title"),
    description = requiredString("description"),
    articleStart = requiredInt("articleStart"),
    articleEnd = requiredInt("articleEnd"),
    includesPreamble = get("includesPreamble")?.jsonPrimitive?.booleanOrNull ?: false,
    articleIds = requiredArray("articleIds").map { it.jsonPrimitive.content },
)

private fun JsonObject.toPreamble(): ConstitutionPreamble = ConstitutionPreamble(
    id = requiredString("id"),
    blockId = requiredString("blockId"),
    titleLt = requiredString("titleLt"),
    titleRu = requiredString("titleRu"),
    parts = requiredArray("parts").map { it.jsonObject.toPart() },
)

private fun JsonObject.toArticle(): ConstitutionArticle = ConstitutionArticle(
    id = requiredString("id"),
    number = requiredInt("number"),
    blockId = requiredString("blockId"),
    sectionTitle = requiredString("sectionTitle"),
    titleLt = requiredString("titleLt"),
    titleRu = requiredString("titleRu"),
    parts = requiredArray("parts").map { it.jsonObject.toPart() },
)

private fun JsonObject.toPart(): ConstitutionPart = ConstitutionPart(
    id = requiredString("id"),
    sourcePartId = requiredString("sourcePartId"),
    itemNumber = get("itemNumber")?.jsonPrimitive?.intOrNull,
    lt = requiredString("lt"),
    ru = requiredString("ru"),
    ltNumericFragments = requiredArray("ltNumericFragments").map { it.jsonObject.toNumericFragment() },
    ruNumericFragments = requiredArray("ruNumericFragments").map { it.jsonObject.toNumericFragment() },
    termLinks = requiredArray("termLinks").map { it.jsonObject.toTermLink() },
)

private fun JsonObject.toNumericFragment(): NumericFragment = NumericFragment(
    start = requiredInt("start"),
    end = requiredInt("end"),
    type = NumericFragmentType.valueOf(requiredString("type").uppercase()),
)

private fun JsonObject.toTermLink(): ConstitutionTermLink = ConstitutionTermLink(
    wordId = requiredString("wordId"),
    start = requiredInt("start"),
    end = requiredInt("end"),
)

private fun JsonObject.requiredObject(key: String): JsonObject = getValue(key).jsonObject

private fun JsonObject.requiredArray(key: String): JsonArray = getValue(key).jsonArray

private fun JsonObject.requiredString(key: String): String = getValue(key).jsonPrimitive.content

private fun JsonObject.requiredInt(key: String): Int = getValue(key).jsonPrimitive.int
