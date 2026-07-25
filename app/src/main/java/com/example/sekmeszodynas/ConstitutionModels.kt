package com.example.sekmeszodynas

typealias ConstitutionBlockId = String
typealias ConstitutionArticleId = String
typealias ConstitutionPartId = String

data class ConstitutionDocument(
    val title: String,
    val preamble: ConstitutionPreamble,
    val blocks: List<ConstitutionBlock>,
    val articles: List<ConstitutionArticle>,
)

data class ConstitutionBlock(
    val id: ConstitutionBlockId,
    val order: Int,
    val title: String,
    val description: String,
    val articleStart: Int,
    val articleEnd: Int,
    val includesPreamble: Boolean,
    val articleIds: List<ConstitutionArticleId>,
)

sealed interface ConstitutionContent {
    val id: ConstitutionArticleId
    val blockId: ConstitutionBlockId
    val titleLt: String
    val titleRu: String
    val parts: List<ConstitutionPart>
}

data class ConstitutionPreamble(
    override val id: ConstitutionArticleId,
    override val blockId: ConstitutionBlockId,
    override val titleLt: String,
    override val titleRu: String,
    override val parts: List<ConstitutionPart>,
) : ConstitutionContent

data class ConstitutionArticle(
    override val id: ConstitutionArticleId,
    val number: Int,
    override val blockId: ConstitutionBlockId,
    val sectionTitle: String,
    override val titleLt: String,
    override val titleRu: String,
    override val parts: List<ConstitutionPart>,
) : ConstitutionContent

data class ConstitutionPart(
    val id: ConstitutionPartId,
    val sourcePartId: String,
    val itemNumber: Int?,
    val lt: String,
    val ru: String,
    val ltNumericFragments: List<NumericFragment>,
    val ruNumericFragments: List<NumericFragment>,
    val termLinks: List<ConstitutionTermLink> = emptyList(),
)

data class NumericFragment(
    val start: Int,
    val end: Int,
    val type: NumericFragmentType,
)

enum class NumericFragmentType {
    NUMBER,
    FRACTION,
}

data class ConstitutionTermLink(
    val wordId: WordId,
    val start: Int,
    val end: Int,
)
