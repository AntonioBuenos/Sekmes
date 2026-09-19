package com.example.sekmeszodynas

data class VisualCardAsset(
    val wordId: WordId,
    val imageResId: Int,
)

data class VisualVocabularyCard(
    val word: Word,
    val imageResId: Int,
)

val VISUAL_CARD_ASSETS = listOf(
    VisualCardAsset("word_000145", R.drawable.visual_card_word_000145), // katė
    VisualCardAsset("word_000146", R.drawable.visual_card_word_000146), // šuo
    VisualCardAsset("word_000190", R.drawable.visual_card_word_000190), // pienas
    VisualCardAsset("word_000194", R.drawable.visual_card_word_000194), // sūris
    VisualCardAsset("word_000197", R.drawable.visual_card_word_000197), // duona
    VisualCardAsset("word_000209", R.drawable.visual_card_word_000209), // žuvis
    VisualCardAsset("word_000213", R.drawable.visual_card_word_000213), // arbata
    VisualCardAsset("word_000214", R.drawable.visual_card_word_000214), // kava
    VisualCardAsset("word_000218", R.drawable.visual_card_word_000218), // medus
    VisualCardAsset("word_000221", R.drawable.visual_card_word_000221), // spurga
    VisualCardAsset("word_000224", R.drawable.visual_card_word_000224), // kiaušinis
    VisualCardAsset("word_000259", R.drawable.visual_card_word_000259), // agurkas
    VisualCardAsset("word_000260", R.drawable.visual_card_word_000260), // baklažanas
    VisualCardAsset("word_000262", R.drawable.visual_card_word_000262), // bulvė
    VisualCardAsset("word_000264", R.drawable.visual_card_word_000264), // česnakas
    VisualCardAsset("word_000265", R.drawable.visual_card_word_000265), // kopūstas
    VisualCardAsset("word_000266", R.drawable.visual_card_word_000266), // moliūgas
    VisualCardAsset("word_000267", R.drawable.visual_card_word_000267), // morka
    VisualCardAsset("word_000268", R.drawable.visual_card_word_000268), // pomidoras
    VisualCardAsset("word_000273", R.drawable.visual_card_word_000273), // apelsinas
    VisualCardAsset("word_000274", R.drawable.visual_card_word_000274), // bananas
    VisualCardAsset("word_000275", R.drawable.visual_card_word_000275), // citrina
    VisualCardAsset("word_000277", R.drawable.visual_card_word_000277), // obuolys
    VisualCardAsset("word_000389", R.drawable.visual_card_word_000389), // automobilis
    VisualCardAsset("word_000391", R.drawable.visual_card_word_000391), // dviratis
    VisualCardAsset("word_000392", R.drawable.visual_card_word_000392), // paspirtukas
    VisualCardAsset("word_000393", R.drawable.visual_card_word_000393), // autobusas
    VisualCardAsset("word_000396", R.drawable.visual_card_word_000396), // traukinys
    VisualCardAsset("word_000401", R.drawable.visual_card_word_000401), // lėktuvas
    VisualCardAsset("word_000405", R.drawable.visual_card_word_000405), // troleibusas
    VisualCardAsset("word_000503", R.drawable.visual_card_word_000503), // laiptai
    VisualCardAsset("word_000505", R.drawable.visual_card_word_000505), // liftas
    VisualCardAsset("word_000506", R.drawable.visual_card_word_000506), // balkonas
    VisualCardAsset("word_000510", R.drawable.visual_card_word_000510), // durys
    VisualCardAsset("word_000513", R.drawable.visual_card_word_000513), // langas
    VisualCardAsset("word_000528", R.drawable.visual_card_word_000528), // stalas
    VisualCardAsset("word_000529", R.drawable.visual_card_word_000529), // kėdė
    VisualCardAsset("word_000530", R.drawable.visual_card_word_000530), // lova
    VisualCardAsset("word_000531", R.drawable.visual_card_word_000531), // sofa
    VisualCardAsset("word_000552", R.drawable.visual_card_word_000552), // šaldytuvas
    VisualCardAsset("word_000559", R.drawable.visual_card_word_000559), // virdulys
    VisualCardAsset("word_000561", R.drawable.visual_card_word_000561), // dulkių siurblys
    VisualCardAsset("word_000602", R.drawable.visual_card_word_000602), // megztinis
    VisualCardAsset("word_000603", R.drawable.visual_card_word_000603), // kelnės
    VisualCardAsset("word_000604", R.drawable.visual_card_word_000604), // sijonas
    VisualCardAsset("word_000605", R.drawable.visual_card_word_000605), // suknelė
    VisualCardAsset("word_000610", R.drawable.visual_card_word_000610), // marškinėliai
    VisualCardAsset("word_000613", R.drawable.visual_card_word_000613), // striukė
    VisualCardAsset("word_000615", R.drawable.visual_card_word_000615), // kojinės
    VisualCardAsset("word_000618", R.drawable.visual_card_word_000618), // šalikas
    VisualCardAsset("word_000667", R.drawable.visual_card_word_000667), // kuprinė
    VisualCardAsset("word_000668", R.drawable.visual_card_word_000668), // skėtis
    VisualCardAsset("word_000670", R.drawable.visual_card_word_000670), // laikrodis
    VisualCardAsset("word_000671", R.drawable.visual_card_word_000671), // akiniai
)

fun visualVocabularyCards(scope: VocabularyScope): List<VisualVocabularyCard> =
    selectVisualVocabularyCards(GLOBAL_POOL, scope)

fun selectVisualVocabularyCards(
    words: List<Word>,
    scope: VocabularyScope,
    assets: List<VisualCardAsset> = VISUAL_CARD_ASSETS,
): List<VisualVocabularyCard> {
    val wordsById = words.associateBy(Word::id)
    return assets.mapNotNull { asset ->
        wordsById[asset.wordId]
            ?.takeIf(scope::contains)
            ?.let { word -> VisualVocabularyCard(word, asset.imageResId) }
    }
}
