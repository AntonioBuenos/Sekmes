package com.example.sekmeszodynas

data class AudioTrack(
    val id: Int,
    val title: String,
    val type: AudioType,
    val url: String
)

enum class AudioType {
    POKALBIS, SKAITYMAS, KLAUSYMAS
}

data class AudioChapter(
    val number: Int,
    val title: String,
    val tracks: List<AudioTrack>
)

data class AudioBook(
    val number: Int,
    val chapters: List<AudioChapter>
)

val AUDIO_BOOKS = listOf(
    AudioBook(1, listOf(
        AudioChapter(0, "Pradžia", 
            listOf(AudioTrack(1, "1 | Įžanga (Titulinis)", AudioType.POKALBIS, ""))
        ),
        AudioChapter(1, "Koks jūsų vardas?", 
            (2..9).map { AudioTrack(it, "$it | Pokalbis ${it-1}", AudioType.POKALBIS, "") }),
        AudioChapter(2, "Čia mano draugas", 
            (10..18).map { AudioTrack(it, "$it | Pokalbis ${it-9}", AudioType.POKALBIS, "") } +
            listOf(AudioTrack(19, "19 | Skaitymas", AudioType.SKAITYMAS, ""))
        ),
        AudioChapter(3, "Koks tavo adresas?", 
            (20..24).map { AudioTrack(it, "$it | Pokalbis ${it-19}", AudioType.POKALBIS, "") } +
            listOf(AudioTrack(25, "25 | Skaitymas", AudioType.SKAITYMAS, "")) +
            (26..28).map { AudioTrack(it, "$it | Klausymas", AudioType.KLAUSYMAS, "") }
        ),
        AudioChapter(4, "Kada ir kur susitinkame?", 
            (29..39).map { AudioTrack(it, "$it | Pokalbis ${it-28}", AudioType.POKALBIS, "") } +
            (40..45).map { AudioTrack(it, "$it | Klausymas", AudioType.KLAUSYMAS, "") }
        ),
        AudioChapter(5, "Čia mano šeima", 
            (46..54).map { AudioTrack(it, "$it | Pokalbis ${it-45}", AudioType.POKALBIS, "") } +
            listOf(AudioTrack(55, "55 | Skaitymas", AudioType.SKAITYMAS, "")) +
            (56..59).map { AudioTrack(it, "$it | Klausymas", AudioType.KLAUSYMAS, "") }
        ),
        AudioChapter(6, "Mano sesers vardas yra Lina", 
            (60..65).map { AudioTrack(it, "$it | Pokalbis ${it-59}", AudioType.POKALBIS, "") } +
            (66..70).map { AudioTrack(it, "$it | Klausymas", AudioType.KLAUSYMAS, "") }
        ),
        AudioChapter(7, "Labai skanu!", 
            (71..77).map { AudioTrack(it, "$it | Pokalbis ${it-70}", AudioType.POKALBIS, "") } +
            listOf(AudioTrack(78, "78 | Skaitymas", AudioType.SKAITYMAS, "")) +
            (79..84).map { AudioTrack(it, "$it | Klausymas", AudioType.KLAUSYMAS, "") }
        ),
        AudioChapter(8, "Šiandien eisiu į turgų", 
            (85..90).map { AudioTrack(it, "$it | Pokalbis ${it-84}", AudioType.POKALBIS, "") } +
            (91..94).map { AudioTrack(it, "$it | Klausymas", AudioType.KLAUSYMAS, "") }
        ),
        AudioChapter(9, "Norėčiau kavos", 
            (95..102).map { AudioTrack(it, "$it | Pokalbis ${it-94}", AudioType.POKALBIS, "") } +
            listOf(AudioTrack(103, "103 | Skaitymas", AudioType.SKAITYMAS, "")) +
            (104..109).map { AudioTrack(it, "$it | Klausymas", AudioType.KLAUSYMAS, "") }
        )
    )),
    AudioBook(2, listOf(
        AudioChapter(0, "Pradžia", 
            listOf(AudioTrack(110, "110 | Įžanga (Titulinis)", AudioType.POKALBIS, ""))
        ),
        AudioChapter(10, "Kaip nuvažiuoti į universitetą?", 
            (111..124).map { AudioTrack(it, "$it | Pokalbis ${it-110}", AudioType.POKALBIS, "") } +
            (125..129).map { AudioTrack(it, "$it | Klausymas", AudioType.KLAUSYMAS, "") }
        ),
        AudioChapter(11, "Geros kelionės!", 
            (130..137).map { AudioTrack(it, "$it | Pokalbis ${it-129}", AudioType.POKALBIS, "") } +
            listOf(AudioTrack(138, "138 | Skaitymas", AudioType.SKAITYMAS, "")) +
            (139..143).map { AudioTrack(it, "$it | Klausymas", AudioType.KLAUSYMAS, "") }
        ),
        AudioChapter(12, "Koks jaukus butas!", 
            (144..150).map { AudioTrack(it, "$it | Pokalbis ${it-143}", AudioType.POKALBIS, "") } +
            (151..155).map { AudioTrack(it, "$it | Klausymas", AudioType.KLAUSYMAS, "") }
        ),
        AudioChapter(13, "Reikia meistro!", 
            (156..162).map { AudioTrack(it, "$it | Pokalbis ${it-155}", AudioType.POKALBIS, "") } +
            listOf(AudioTrack(163, "163 | Skaitymas", AudioType.SKAITYMAS, "")) +
            (164..168).map { AudioTrack(it, "$it | Klausymas", AudioType.KLAUSYMAS, "") }
        ),
        AudioChapter(14, "Važiuojame apsipirkti?", 
            (169..177).map { AudioTrack(it, "$it | Pokalbis ${it-168}", AudioType.POKALBIS, "") } +
            listOf(AudioTrack(178, "178 | Skaitymas", AudioType.SKAITYMAS, "")) +
            (179..183).map { AudioTrack(it, "$it | Klausymas", AudioType.KLAUSYMAS, "") }
        ),
        AudioChapter(15, "Kada galėsiu atsiimti?", 
            (184..190).map { AudioTrack(it, "$it | Pokalbis ${it-183}", AudioType.POKALBIS, "") } +
            (191..196).map { AudioTrack(it, "$it | Klausymas", AudioType.KLAUSYMAS, "") }
        ),
        AudioChapter(16, "Kada dirba šeimos gydytojas?", 
            (197..205).map { AudioTrack(it, "$it | Pokalbis ${it-196}", AudioType.POKALBIS, "") } +
            listOf(AudioTrack(206, "206 | Skaitymas", AudioType.SKAITYMAS, "")) +
            (207..212).map { AudioTrack(it, "$it | Klausymas", AudioType.KLAUSYMAS, "") }
        ),
        AudioChapter(17, "Prašom duoti vaistų nuo skausmo!", 
            (213..219).map { AudioTrack(it, "$it | Pokalbis ${it-212}", AudioType.POKALBIS, "") } +
            (220..226).map { AudioTrack(it, "$it | Klausymas", AudioType.KLAUSYMAS, "") }
        ),
        AudioChapter(18, "Kur jūs dirbate?", 
            (227..235).map { AudioTrack(it, "$it | Pokalbis ${it-226}", AudioType.POKALBIS, "") } +
            (236..240).map { AudioTrack(it, "$it | Klausymas", AudioType.KLAUSYMAS, "") }
        ),
        AudioChapter(19, "Ką tu studijuoji?", 
            (241..248).map { AudioTrack(it, "$it | Pokalbis ${it-240}", AudioType.POKALBIS, "") } +
            listOf(AudioTrack(249, "249 | Skaitymas", AudioType.SKAITYMAS, "")) +
            (250..253).map { AudioTrack(it, "$it | Klausymas", AudioType.KLAUSYMAS, "") }
        ),
        AudioChapter(20, "Ką mėgsti veikti laisvalaikiu?", 
            (254..260).map { AudioTrack(it, "$it | Pokalbis ${it-253}", AudioType.POKALBIS, "") } +
            listOf(AudioTrack(261, "261 | Skaitymas", AudioType.SKAITYMAS, "")) +
            listOf(AudioTrack(262, "262 | Klausymas", AudioType.KLAUSYMAS, ""))
        ),
        AudioChapter(21, "Su gimtadieniu!", 
            (263..268).map { AudioTrack(it, "$it | Pokalbis ${it-262}", AudioType.POKALBIS, "") } +
            listOf(AudioTrack(269, "269 | Skaitymas", AudioType.SKAITYMAS, "")) +
            (270..275).map { AudioTrack(it, "$it | Klausymas", AudioType.KLAUSYMAS, "") }
        )
    ))
)
