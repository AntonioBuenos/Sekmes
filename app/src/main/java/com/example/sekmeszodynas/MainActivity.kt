package com.example.sekmeszodynas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import kotlinx.coroutines.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.example.sekmeszodynas.ui.theme.SekmesZodynasTheme

sealed class Screen {
    object Dashboard : Screen()
    object ThemeSelectionForQuiz : Screen()
    object ThemeSelectionForDictionary : Screen()
    data class Quiz(val themeId: String) : Screen()
    data class Dictionary(val themeId: String) : Screen()
    data class Results(val score: Int, val total: Int, val mistakes: Map<String, Int>) : Screen()
    object AudioSelection : Screen()
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SekmesZodynasTheme {
                var currentScreen by remember { mutableStateOf<Screen>(Screen.Dashboard) }

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        when (val screen = currentScreen) {
                            is Screen.Dashboard -> MainDashboardScreen(
                                onNavigateToDictionary = { currentScreen = Screen.ThemeSelectionForDictionary },
                                onNavigateToQuiz = { currentScreen = Screen.ThemeSelectionForQuiz },
                                onNavigateToAudio = { currentScreen = Screen.AudioSelection }
                            )
                            is Screen.ThemeSelectionForDictionary -> ThemeSelectionScreen(
                                title = "Словарь: Выбор темы",
                                onThemeSelected = { id ->
                                    currentScreen = Screen.Dictionary(id)
                                },
                                onBack = { currentScreen = Screen.Dashboard }
                            )
                            is Screen.ThemeSelectionForQuiz -> ThemeSelectionScreen(
                                title = "Тест: Выбор темы",
                                onThemeSelected = { id ->
                                    currentScreen = Screen.Quiz(id)
                                },
                                onBack = { currentScreen = Screen.Dashboard }
                            )
                            is Screen.Dictionary -> DictionaryScreen(
                                themeId = screen.themeId,
                                onBack = { currentScreen = Screen.ThemeSelectionForDictionary }
                            )
                            is Screen.Quiz -> QuizScreen(
                                themeId = screen.themeId,
                                onQuizFinished = { score, total, mistakes ->
                                    currentScreen = Screen.Results(score, total, mistakes)
                                },
                                onBack = { currentScreen = Screen.ThemeSelectionForQuiz }
                            )
                            is Screen.Results -> ResultScreen(
                                score = screen.score,
                                total = screen.total,
                                mistakes = screen.mistakes,
                                onRestart = { currentScreen = Screen.Dashboard }
                            )
                            is Screen.AudioSelection -> AudioScreen(
                                onBack = { currentScreen = Screen.Dashboard }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MainDashboardScreen(
    onNavigateToDictionary: () -> Unit,
    onNavigateToQuiz: () -> Unit,
    onNavigateToAudio: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp).verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "🇱🇹 Sėkmės",
            style = MaterialTheme.typography.displayMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        Card(
            onClick = onNavigateToDictionary,
            modifier = Modifier.fillMaxWidth().height(120.dp).padding(vertical = 8.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("📖", fontSize = 32.sp)
                    Text("Словарь", style = MaterialTheme.typography.titleLarge)
                }
            }
        }

        Card(
            onClick = onNavigateToQuiz,
            modifier = Modifier.fillMaxWidth().height(120.dp).padding(vertical = 8.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("📝", fontSize = 32.sp)
                    Text("Пройти тест", style = MaterialTheme.typography.titleLarge)
                }
            }
        }

        Card(
            onClick = onNavigateToAudio,
            modifier = Modifier.fillMaxWidth().height(120.dp).padding(vertical = 8.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("🎧", fontSize = 32.sp)
                    Text("Аудио курс", style = MaterialTheme.typography.titleLarge)
                }
            }
        }
    }
}

@Composable
fun ThemeSelectionScreen(title: String, onThemeSelected: (String) -> Unit, onBack: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Text("←", fontSize = 24.sp)
            }
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = { onThemeSelected("all") },
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
        ) {
            Text("Все темы сразу")
        }

        Text(
            text = "Выберите тему:",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(THEMES_DATA.keys.toList().sortedBy { it.toIntOrNull() ?: 999 }) { id ->
                val theme = THEMES_DATA[id]!!
                Card(
                    onClick = { onThemeSelected(id) },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                ) {
                    Text(
                        text = "${theme.title} (${theme.words.size})",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
    }
}

@Composable
fun DictionaryScreen(themeId: String, onBack: () -> Unit) {
    val words = if (themeId == "all") GLOBAL_POOL else THEMES_DATA[themeId]?.words ?: emptyList()
    val themeTitle = if (themeId == "all") "Все слова" else THEMES_DATA[themeId]?.title ?: ""

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Text("←", fontSize = 24.sp)
            }
            Text(
                text = themeTitle,
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.weight(1f)
            )
            Text("${words.size}", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(words) { word ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = word.lt,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = word.ru,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.End
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun QuizScreen(
    themeId: String,
    onQuizFinished: (Int, Int, Map<String, Int>) -> Unit,
    onBack: () -> Unit
) {
    val quizWords = remember {
        if (themeId == "all") GLOBAL_POOL.shuffled()
        else THEMES_DATA[themeId]?.words?.shuffled() ?: emptyList()
    }
    
    var answeredCorrectlyIds by remember { mutableStateOf(setOf<String>()) }
    val mistakes = remember { mutableStateMapOf<String, Int>() }
    var currentWord by remember { mutableStateOf<Word?>(null) }
    var selectedOption by remember { mutableStateOf<String?>(null) }
    var isCorrect by remember { mutableStateOf<Boolean?>(null) }
    
    // Функция для подбора следующего слова
    val pickNextWord = {
        val pending = quizWords.filter { !answeredCorrectlyIds.contains(it.id) }
        if (pending.isEmpty() && quizWords.isNotEmpty()) {
            onQuizFinished(quizWords.size, quizWords.size, mistakes.toMap())
        } else {
            currentWord = pending.random()
            selectedOption = null
            isCorrect = null
        }
    }

    // Инициализация первого слова
    LaunchedEffect(Unit) {
        pickNextWord()
    }

    if (currentWord == null) return

    val options = remember(currentWord) {
        generateOptions(currentWord!!, quizWords)
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = onBack) {
                Text("← Назад")
            }
            Text(
                text = "${answeredCorrectlyIds.size} / ${quizWords.size} изучено",
                style = MaterialTheme.typography.bodyMedium
            )
        }

        LinearProgressIndicator(
            progress = { if (quizWords.isNotEmpty()) answeredCorrectlyIds.size.toFloat() / quizWords.size else 0f },
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = currentWord!!.ru,
            style = MaterialTheme.typography.headlineLarge,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.heightIn(min = 100.dp).wrapContentHeight()
        )

        Spacer(modifier = Modifier.height(32.dp))

        options.forEach { option ->
            val isThisCorrectOption = option == currentWord!!.lt
            val isThisSelected = selectedOption == option
            
            val containerColor = when {
                selectedOption == null -> MaterialTheme.colorScheme.primary
                isThisCorrectOption -> Color(0xFF4CAF50) // Всегда зеленый для правильного после выбора
                isThisSelected && !isCorrect!! -> Color(0xFFF44336) // Красный если выбрали неправильно
                else -> MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
            }

            Button(
                onClick = {
                    if (selectedOption == null) {
                        selectedOption = option
                        if (option == currentWord!!.lt) {
                            isCorrect = true
                        } else {
                            isCorrect = false
                            mistakes[currentWord!!.lt] = (mistakes[currentWord!!.lt] ?: 0) + 1
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                shape = MaterialTheme.shapes.medium,
                colors = ButtonDefaults.buttonColors(
                    containerColor = containerColor,
                    disabledContainerColor = containerColor // Важно для сохранения подсветки
                ),
                enabled = selectedOption == null
            ) {
                Text(
                    text = option,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White
                )
            }
        }
        
    // Обработка перехода
    if (selectedOption != null) {
        LaunchedEffect(selectedOption) {
            delay(if (isCorrect == true) 800 else 1500)
            if (isCorrect == true) {
                answeredCorrectlyIds = answeredCorrectlyIds + currentWord!!.id
            }
            pickNextWord()
        }
    }
    }
}

@Composable
fun ResultScreen(
    score: Int,
    total: Int,
    mistakes: Map<String, Int>,
    onRestart: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "🎉 Тренировка завершена!",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(top = 32.dp),
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Отличная работа. Статистика ошибок:",
            style = MaterialTheme.typography.bodyLarge,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (mistakes.isNotEmpty()) {
            val sortedMistakes = mistakes.toList().sortedByDescending { it.second }
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(sortedMistakes) { (lt, count) ->
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp).fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = lt, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            Text(text = "× $count", color = Color(0xFFF44336), fontWeight = FontWeight.ExtraBold)
                        }
                    }
                }
            }
        } else {
            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                Text(text = "Ошибок нет! Идеальный результат. 🌟", style = MaterialTheme.typography.bodyLarge)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onRestart,
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
        ) {
            Text("Вернуться к темам")
        }
    }
}

@Composable
fun AudioScreen(onBack: () -> Unit) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val scope = rememberCoroutineScope()
    val exoPlayer = remember { ExoPlayer.Builder(context).build() }
    var playingTrackId by remember { mutableStateOf<Int?>(null) }
    var isExoPlaying by remember { mutableStateOf(false) }

    // Слушатель состояния плеера
    DisposableEffect(exoPlayer) {
        val listener = object : androidx.media3.common.Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                isExoPlaying = isPlaying
            }
        }
        exoPlayer.addListener(listener)
        onDispose {
            exoPlayer.removeListener(listener)
            exoPlayer.release()
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(onClick = onBack) {
                Text("←", fontSize = 24.sp)
            }
            Text("Аудио-курс (оффлайн)", style = MaterialTheme.typography.headlineSmall)
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "Все аудиофайлы встроены в приложение. Интернет не требуется.",
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray,
            modifier = Modifier.padding(start = 48.dp)
        )

        var expandedChapters by remember { mutableStateOf(setOf<String>()) }

        LazyColumn(modifier = Modifier.weight(1f)) {
            AUDIO_BOOKS.forEach { book ->
                item {
                    Text(
                        "Книга ${book.number}",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                    )
                }
                book.chapters.forEach { chapter ->
                    val chapterKey = "${book.number}_${chapter.number}"
                    val isExpanded = expandedChapters.contains(chapterKey)
                    item {
                        Card(
                            onClick = {
                                expandedChapters = if (isExpanded) {
                                    expandedChapters - chapterKey
                                } else {
                                    expandedChapters + chapterKey
                                }
                            },
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isExpanded) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(if (isExpanded) "▼" else "▶", modifier = Modifier.width(24.dp))
                                Text(
                                    "Глава ${chapter.number}: ${chapter.title}",
                                    style = MaterialTheme.typography.titleMedium,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }

                    if (isExpanded) {
                        items(chapter.tracks) { track ->
                            Card(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp, horizontal = 16.dp),
                                onClick = {
                                    if (playingTrackId == track.id) {
                                        if (exoPlayer.isPlaying) {
                                            exoPlayer.pause()
                                        } else {
                                            exoPlayer.play()
                                        }
                                    } else {
                                        exoPlayer.stop()
                                        exoPlayer.clearMediaItems()
                                        
                                        playingTrackId = track.id
                                        
                                        // Ищем ресурс по имени audio_{id}
                                        val resName = "audio_${track.id}"
                                        val resId = context.resources.getIdentifier(resName, "raw", context.packageName)
                                        
                                        if (resId != 0) {
                                            val uri = android.net.Uri.parse("android.resource://${context.packageName}/$resId")
                                            val mediaItem = MediaItem.fromUri(uri)
                                            exoPlayer.setMediaItem(mediaItem)
                                            exoPlayer.prepare()
                                            exoPlayer.play()
                                        } else {
                                            android.util.Log.e("AudioDebug", "Resource not found: $resName")
                                        }
                                    }
                                }
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    val isCurrentPlaying = playingTrackId == track.id && isExoPlaying
                                    Text(
                                        text = if (isCurrentPlaying) "⏸" else "▶",
                                        fontSize = 18.sp,
                                        modifier = Modifier.width(24.dp),
                                        color = if (playingTrackId == track.id) Color(0xFF4CAF50) else MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(track.title, style = MaterialTheme.typography.bodyMedium)
                                        Text(track.type.name.lowercase(), style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


fun generateOptions(correctWord: Word, themeWords: List<Word>): List<String> {
    // 1. Пытаемся найти дистракторы в текущей теме (того же типа)
    val themeDistractors = themeWords
        .asSequence()
        .filter { it.type == correctWord.type && it.lt != correctWord.lt }
        .map { it.lt }
        .distinct()
        .shuffled()
        .take(3)
        .toMutableList()

    // 2. Если в теме мало слов того же типа, берем любые другие из этой же темы
    if (themeDistractors.size < 3) {
        val extraFromTheme = themeWords
            .asSequence()
            .filter { it.lt != correctWord.lt && !themeDistractors.contains(it.lt) }
            .map { it.lt }
            .distinct()
            .shuffled()
            .take(3 - themeDistractors.size)
        themeDistractors.addAll(extraFromTheme)
    }

    // 3. Если всё еще не хватает (очень маленькая тема), добираем из глобального пула
    if (themeDistractors.size < 3) {
        val extraGlobal = GLOBAL_POOL
            .asSequence()
            .filter { it.lt != correctWord.lt && !themeDistractors.contains(it.lt) }
            .map { it.lt }
            .distinct()
            .shuffled()
            .take(3 - themeDistractors.size)
        themeDistractors.addAll(extraGlobal)
    }

    return (themeDistractors + correctWord.lt).shuffled()
}

fun translateType(type: String): String {
    return when (type) {
        "v" -> "глагол"
        "n" -> "сущ."
        "adj" -> "прилаг."
        "adv" -> "наречие"
        "other" -> "др."
        else -> type
    }
}
