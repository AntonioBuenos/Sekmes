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
    VisualCardAsset("word_000726", R.drawable.visual_card_word_000726), // galva
    VisualCardAsset("word_000727", R.drawable.visual_card_word_000727), // veidas
    VisualCardAsset("word_000728", R.drawable.visual_card_word_000728), // akis
    VisualCardAsset("word_000729", R.drawable.visual_card_word_000729), // ausis
    VisualCardAsset("word_000730", R.drawable.visual_card_word_000730), // nosis
    VisualCardAsset("word_000731", R.drawable.visual_card_word_000731), // dantis
    VisualCardAsset("word_000733", R.drawable.visual_card_word_000733), // ranka
    VisualCardAsset("word_000734", R.drawable.visual_card_word_000734), // pirštas
    VisualCardAsset("word_000735", R.drawable.visual_card_word_000735), // koja
    VisualCardAsset("word_000738", R.drawable.visual_card_word_000738), // nugara
    VisualCardAsset("word_000532", R.drawable.visual_card_word_000532), // fotelis
    VisualCardAsset("word_000533", R.drawable.visual_card_word_000533), // spinta
    VisualCardAsset("word_000534", R.drawable.visual_card_word_000534), // lentyna
    VisualCardAsset("word_000553", R.drawable.visual_card_word_000553), // orkaitė
    VisualCardAsset("word_000554", R.drawable.visual_card_word_000554), // viryklė
    VisualCardAsset("word_000555", R.drawable.visual_card_word_000555), // mikrobangų krosnelė
    VisualCardAsset("word_000556", R.drawable.visual_card_word_000556), // indaplovė
    VisualCardAsset("word_000557", R.drawable.visual_card_word_000557), // skalbyklė
    VisualCardAsset("word_000562", R.drawable.visual_card_word_000562), // lygintuvas
    VisualCardAsset("word_000564", R.drawable.visual_card_word_000564), // televizorius
    VisualCardAsset("word_000565", R.drawable.visual_card_word_000565), // lempa
    VisualCardAsset("word_000566", R.drawable.visual_card_word_000566), // veidrodis
    VisualCardAsset("word_000606", R.drawable.visual_card_word_000606), // palaidinė
    VisualCardAsset("word_000607", R.drawable.visual_card_word_000607), // marškiniai
    VisualCardAsset("word_000608", R.drawable.visual_card_word_000608), // švarkas
    VisualCardAsset("word_000611", R.drawable.visual_card_word_000611), // šortai
    VisualCardAsset("word_000612", R.drawable.visual_card_word_000612), // džinsai
    VisualCardAsset("word_000614", R.drawable.visual_card_word_000614), // paltas
    VisualCardAsset("word_000616", R.drawable.visual_card_word_000616), // pirštinės
    VisualCardAsset("word_000617", R.drawable.visual_card_word_000617), // kepurė
    VisualCardAsset("word_000625", R.drawable.visual_card_word_000625), // batai
    VisualCardAsset("word_000627", R.drawable.visual_card_word_000627), // sportbačiai
    VisualCardAsset("word_000665", R.drawable.visual_card_word_000665), // piniginė
    VisualCardAsset("word_000674", R.drawable.visual_card_word_000674), // žiedas
    VisualCardAsset("word_000851", R.drawable.visual_card_word_000851), // kompiuteris
    VisualCardAsset("word_000852", R.drawable.visual_card_word_000852), // spausdintuvas
    VisualCardAsset("word_000928", R.drawable.visual_card_word_000928), // knyga
    VisualCardAsset("word_000929", R.drawable.visual_card_word_000929), // vadovėlis
    VisualCardAsset("word_000930", R.drawable.visual_card_word_000930), // žodynas
    VisualCardAsset("word_000931", R.drawable.visual_card_word_000931), // rašiklis
    VisualCardAsset("word_000932", R.drawable.visual_card_word_000932), // pieštukas
    VisualCardAsset("word_000933", R.drawable.visual_card_word_000933), // trintukas
    VisualCardAsset("word_000934", R.drawable.visual_card_word_000934), // sąsiuvinis
    VisualCardAsset("word_000943", R.drawable.visual_card_word_000943), // gitara
    VisualCardAsset("word_000568", R.drawable.visual_card_word_000568), // gėlė
    VisualCardAsset("word_000979", R.drawable.visual_card_word_000979), // kalnai
    VisualCardAsset("word_001003", R.drawable.visual_card_word_001003), // dovana
    VisualCardAsset("word_001007", R.drawable.visual_card_word_001007), // žvakutė
    VisualCardAsset("word_001015", R.drawable.visual_card_word_001015), // eglė / eglutė
    VisualCardAsset("word_001020", R.drawable.visual_card_word_001020), // fejerverkai
    VisualCardAsset("word_000217", R.drawable.visual_card_word_000217), // bandelė
    VisualCardAsset("word_000220", R.drawable.visual_card_word_000220), // šokoladas
    VisualCardAsset("word_000222", R.drawable.visual_card_word_000222), // tortas
    VisualCardAsset("word_000225", R.drawable.visual_card_word_000225), // sumuštinis
    VisualCardAsset("word_000276", R.drawable.visual_card_word_000276), // kriaušė
    VisualCardAsset("word_000280", R.drawable.visual_card_word_000280), // braškės
    VisualCardAsset("word_000282", R.drawable.visual_card_word_000282), // vynuogės
    VisualCardAsset("word_000283", R.drawable.visual_card_word_000283), // vyšnios
    VisualCardAsset("word_000284", R.drawable.visual_card_word_000284), // riešutai
    VisualCardAsset("word_000339", R.drawable.visual_card_word_000339), // pica
    VisualCardAsset("word_000340", R.drawable.visual_card_word_000340), // blynai
    VisualCardAsset("word_000353", R.drawable.visual_card_word_000353), // ledai
    VisualCardAsset("word_000034", R.drawable.visual_card_word_000034), // namas
    VisualCardAsset("word_000046", R.drawable.visual_card_word_000046), // mokykla
    VisualCardAsset("word_000048", R.drawable.visual_card_word_000048), // biblioteka
    VisualCardAsset("word_000050", R.drawable.visual_card_word_000050), // restoranas
    VisualCardAsset("word_000060", R.drawable.visual_card_word_000060), // kino teatras
    VisualCardAsset("word_000062", R.drawable.visual_card_word_000062), // muziejus
    VisualCardAsset("word_000063", R.drawable.visual_card_word_000063), // pilis
    VisualCardAsset("word_000072", R.drawable.visual_card_word_000072), // oro uostas
    VisualCardAsset("word_000079", R.drawable.visual_card_word_000079), // tiltas
    VisualCardAsset("word_000083", R.drawable.visual_card_word_000083), // miškas
    VisualCardAsset("word_000390", R.drawable.visual_card_word_000390), // automobilių stovėjimo aikštelė
    VisualCardAsset("word_000398", R.drawable.visual_card_word_000398), // geležinkelio stotis
    VisualCardAsset("word_000409", R.drawable.visual_card_word_000409), // bilietas
    VisualCardAsset("word_000418", R.drawable.visual_card_word_000418), // degalinė
    VisualCardAsset("word_000423", R.drawable.visual_card_word_000423), // kolonėlė
    VisualCardAsset("word_000460", R.drawable.visual_card_word_000460), // vairas
    VisualCardAsset("word_000461", R.drawable.visual_card_word_000461), // raktai
    VisualCardAsset("word_000463", R.drawable.visual_card_word_000463), // padangos
    VisualCardAsset("word_000464", R.drawable.visual_card_word_000464), // valytuvai
    VisualCardAsset("word_000977", R.drawable.visual_card_word_000977), // bagažas
    VisualCardAsset("word_000517", R.drawable.visual_card_word_000517), // kilimas
    VisualCardAsset("word_000523", R.drawable.visual_card_word_000523), // dušas
    VisualCardAsset("word_000524", R.drawable.visual_card_word_000524), // tualetas
    VisualCardAsset("word_000567", R.drawable.visual_card_word_000567), // paveikslas
    VisualCardAsset("word_000571", R.drawable.visual_card_word_000571), // šiukšlių dėžė
    VisualCardAsset("word_000578", R.drawable.visual_card_word_000578), // vamzdis
    VisualCardAsset("word_000579", R.drawable.visual_card_word_000579), // čiaupas
    VisualCardAsset("word_000676", R.drawable.visual_card_word_000676), // šukos
    VisualCardAsset("word_000609", R.drawable.visual_card_word_000609), // kostiumas
    VisualCardAsset("word_000620", R.drawable.visual_card_word_000620), // maudymosi kostiumėlis
    VisualCardAsset("word_000621", R.drawable.visual_card_word_000621), // glaudės
    VisualCardAsset("word_000626", R.drawable.visual_card_word_000626), // bateliai
    VisualCardAsset("word_000628", R.drawable.visual_card_word_000628), // basutės
    VisualCardAsset("word_000666", R.drawable.visual_card_word_000666), // rankinė
    VisualCardAsset("word_000669", R.drawable.visual_card_word_000669), // diržas
    VisualCardAsset("word_000672", R.drawable.visual_card_word_000672), // akiniai nuo saulės
    VisualCardAsset("word_000673", R.drawable.visual_card_word_000673), // auskarai
    VisualCardAsset("word_000675", R.drawable.visual_card_word_000675), // grandinėlė
    VisualCardAsset("word_000507", R.drawable.visual_card_word_000507), // garažas
    VisualCardAsset("word_000508", R.drawable.visual_card_word_000508), // kiemas
    VisualCardAsset("word_000509", R.drawable.visual_card_word_000509), // vartai
    VisualCardAsset("word_000519", R.drawable.visual_card_word_000519), // virtuvė
    VisualCardAsset("word_000520", R.drawable.visual_card_word_000520), // svetainė
    VisualCardAsset("word_000521", R.drawable.visual_card_word_000521), // koridorius
    VisualCardAsset("word_000522", R.drawable.visual_card_word_000522), // vonia
    VisualCardAsset("word_000525", R.drawable.visual_card_word_000525), // miegamasis
    VisualCardAsset("word_000526", R.drawable.visual_card_word_000526), // rūsys
    VisualCardAsset("word_000560", R.drawable.visual_card_word_000560), // kavos aparatas
    VisualCardAsset("word_000752", R.drawable.visual_card_word_000752), // gydytojas
    VisualCardAsset("word_000754", R.drawable.visual_card_word_000754), // odontologas
    VisualCardAsset("word_000755", R.drawable.visual_card_word_000755), // slaugytojas
    VisualCardAsset("word_000756", R.drawable.visual_card_word_000756), // veterinaras
    VisualCardAsset("word_000769", R.drawable.visual_card_word_000769), // vaistai
    VisualCardAsset("word_000780", R.drawable.visual_card_word_000780), // tabletė
    VisualCardAsset("word_000782", R.drawable.visual_card_word_000782), // sirupas nuo kosulio
    VisualCardAsset("word_000783", R.drawable.visual_card_word_000783), // purškalas
    VisualCardAsset("word_000785", R.drawable.visual_card_word_000785), // lašai
    VisualCardAsset("word_000794", R.drawable.visual_card_word_000794), // greitoji pagalba
    VisualCardAsset("word_000315", R.drawable.visual_card_word_000315), // padavėjas
    VisualCardAsset("word_000856", R.drawable.visual_card_word_000856), // mokytojas
    VisualCardAsset("word_000862", R.drawable.visual_card_word_000862), // inžinierius
    VisualCardAsset("word_000863", R.drawable.visual_card_word_000863), // žurnalistas
    VisualCardAsset("word_000864", R.drawable.visual_card_word_000864), // architektas
    VisualCardAsset("word_000866", R.drawable.visual_card_word_000866), // aktorius
    VisualCardAsset("word_000867", R.drawable.visual_card_word_000867), // dainininkas
    VisualCardAsset("word_000868", R.drawable.visual_card_word_000868), // muzikantas
    VisualCardAsset("word_000869", R.drawable.visual_card_word_000869), // statybininkas
    VisualCardAsset("word_000870", R.drawable.visual_card_word_000870), // kirpėjas
    VisualCardAsset("word_000940", R.drawable.visual_card_word_000940), // koncertas
    VisualCardAsset("word_000944", R.drawable.visual_card_word_000944), // pianinas
    VisualCardAsset("word_000952", R.drawable.visual_card_word_000952), // skulptūra
    VisualCardAsset("word_000953", R.drawable.visual_card_word_000953), // stadionas
    VisualCardAsset("word_000954", R.drawable.visual_card_word_000954), // baseinas
    VisualCardAsset("word_000955", R.drawable.visual_card_word_000955), // krepšinis
    VisualCardAsset("word_000956", R.drawable.visual_card_word_000956), // futbolas
    VisualCardAsset("word_000957", R.drawable.visual_card_word_000957), // tenisas
    VisualCardAsset("word_000959", R.drawable.visual_card_word_000959), // šaškės
    VisualCardAsset("word_000960", R.drawable.visual_card_word_000960), // šachmatai
    VisualCardAsset("word_000080", R.drawable.visual_card_word_000080), // ežeras
    VisualCardAsset("word_000081", R.drawable.visual_card_word_000081), // jūra
    VisualCardAsset("word_000082", R.drawable.visual_card_word_000082), // parkas
    VisualCardAsset("word_000980", R.drawable.visual_card_word_000980), // kaimas
    VisualCardAsset("word_000981", R.drawable.visual_card_word_000981), // sodyba
    VisualCardAsset("word_000984", R.drawable.visual_card_word_000984), // vasara
    VisualCardAsset("word_000985", R.drawable.visual_card_word_000985), // ruduo
    VisualCardAsset("word_000986", R.drawable.visual_card_word_000986), // žiema
    VisualCardAsset("word_000987", R.drawable.visual_card_word_000987), // pavasaris
    VisualCardAsset("word_001030", R.drawable.visual_card_word_001030), // vėliava
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
