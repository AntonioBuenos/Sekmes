package com.example.sekmeszodynas

import android.annotation.SuppressLint
import android.content.res.Resources
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.runtime.collectAsState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
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

private val ScreenSaver = Saver<Screen, Bundle>(
    save = { screen ->
        Bundle().apply {
            when (screen) {
                Screen.Dashboard -> putString("type", "dashboard")
                Screen.ThemeSelectionForQuiz -> putString("type", "quiz_themes")
                Screen.ThemeSelectionForDictionary -> putString("type", "dictionary_themes")
                is Screen.Quiz -> {
                    putString("type", "quiz")
                    putString("theme_id", screen.themeId)
                }
                is Screen.Dictionary -> {
                    putString("type", "dictionary")
                    putString("theme_id", screen.themeId)
                }
                is Screen.Results -> {
                    putString("type", "results")
                    putInt("score", screen.score)
                    putInt("total", screen.total)
                    putStringArrayList("mistake_ids", ArrayList(screen.mistakes.keys))
                    putIntegerArrayList("mistake_counts", ArrayList(screen.mistakes.values))
                }
                Screen.AudioSelection -> putString("type", "audio")
            }
        }
    },
    restore = { state ->
        when (state.getString("type")) {
            "quiz_themes" -> Screen.ThemeSelectionForQuiz
            "dictionary_themes" -> Screen.ThemeSelectionForDictionary
            "quiz" -> Screen.Quiz(state.getString("theme_id").orEmpty())
            "dictionary" -> Screen.Dictionary(state.getString("theme_id").orEmpty())
            "results" -> {
                val ids = state.getStringArrayList("mistake_ids").orEmpty()
                val counts = state.getIntegerArrayList("mistake_counts").orEmpty()
                Screen.Results(
                    score = state.getInt("score"),
                    total = state.getInt("total"),
                    mistakes = ids.zip(counts).toMap()
                )
            }
            "audio" -> Screen.AudioSelection
            else -> Screen.Dashboard
        }
    }
)

private val StringSetSaver = Saver<Set<String>, ArrayList<String>>(
    save = { ArrayList(it) },
    restore = { it.toSet() }
)

private val StringListSaver = Saver<List<String>, ArrayList<String>>(
    save = { ArrayList(it) },
    restore = { it.toList() }
)

private val StringIntMapSaver = Saver<Map<String, Int>, Bundle>(
    save = { values ->
        Bundle().apply {
            putStringArrayList("keys", ArrayList(values.keys))
            putIntegerArrayList("values", ArrayList(values.values))
        }
    },
    restore = { state ->
        state.getStringArrayList("keys").orEmpty()
            .zip(state.getIntegerArrayList("values").orEmpty())
            .toMap()
    }
)

private fun previousScreen(screen: Screen): Screen = when (screen) {
    Screen.Dashboard -> Screen.Dashboard
    Screen.ThemeSelectionForQuiz,
    Screen.ThemeSelectionForDictionary,
    Screen.AudioSelection,
    is Screen.Results -> Screen.Dashboard
    is Screen.Quiz -> Screen.ThemeSelectionForQuiz
    is Screen.Dictionary -> Screen.ThemeSelectionForDictionary
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        CatalogStore.initialize(assets)
        ProgressStore.initialize(applicationContext)
        enableEdgeToEdge()
        setContent {
            SekmesZodynasTheme {
                var currentScreen by rememberSaveable(stateSaver = ScreenSaver) {
                    mutableStateOf<Screen>(Screen.Dashboard)
                }

                BackHandler(enabled = currentScreen != Screen.Dashboard) {
                    currentScreen = previousScreen(currentScreen)
                }

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
    val progressByWordId by ProgressStore.repository().observeAll().collectAsState(initial = emptyMap())
    val scope = rememberCoroutineScope()
    var showKnown by rememberSaveable(themeId) { mutableStateOf(false) }
    val visibleWords = words.filter { word ->
        showKnown || progressByWordId[word.id]?.status != WordLearningStatus.KNOWN
    }

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
            Text("${visibleWords.size}", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
        }

        TextButton(onClick = { showKnown = !showKnown }) {
            Text(if (showKnown) "Скрыть известные" else "Показать известные")
        }

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(visibleWords) { word ->
                val status = progressByWordId[word.id]?.status ?: WordLearningStatus.NEW
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = word.lt, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                            Text(text = word.ru, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f), textAlign = TextAlign.End)
                        }
                        Text(statusLabel(status), style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                        Row {
                            TextButton(onClick = { scope.launch { ProgressStore.repository().setStatus(word.id, WordLearningStatus.KNOWN) } }) { Text("Знаю") }
                            TextButton(onClick = { scope.launch { ProgressStore.repository().setStatus(word.id, WordLearningStatus.HARD) } }) { Text("Повторять") }
                            if (status != WordLearningStatus.LEARNING) {
                                TextButton(onClick = { scope.launch { ProgressStore.repository().setStatus(word.id, WordLearningStatus.LEARNING) } }) { Text("Изучаю") }
                            }
                        }
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
    val progressByWordId by ProgressStore.repository().observeAll().collectAsState(initial = emptyMap())
    val progressScope = rememberCoroutineScope()
    val quizWords = remember(themeId, progressByWordId) { quizPoolForTheme(themeId, progressByWordId).shuffled() }

    var answeredCorrectlyIds by rememberSaveable(themeId, stateSaver = StringSetSaver) {
        mutableStateOf(emptySet())
    }
    var mistakes by rememberSaveable(themeId, stateSaver = StringIntMapSaver) {
        mutableStateOf(emptyMap())
    }
    var currentWordId by rememberSaveable(themeId) { mutableStateOf<String?>(null) }
    var selectedOption by rememberSaveable(themeId) { mutableStateOf<String?>(null) }
    var isCorrect by rememberSaveable(themeId) { mutableStateOf<Boolean?>(null) }
    var options by rememberSaveable(themeId, stateSaver = StringListSaver) {
        mutableStateOf(emptyList())
    }

    val currentWord = quizWords.firstOrNull { it.id == currentWordId }

    val pickNextWord: (Set<String>) -> Unit = { completedIds ->
        val pending = quizWords.filter { it.id !in completedIds }
        if (pending.isEmpty() && quizWords.isNotEmpty()) {
            onQuizFinished(
                calculateQuizScore(quizWords.size, mistakes),
                quizWords.size,
                mistakes
            )
        } else {
            val nextWord = selectNextQuizWord(
                words = pending,
                hardWordIds = progressByWordId.filterValues { it.status == WordLearningStatus.HARD }.keys,
                previousWordId = currentWordId,
            )
            if (nextWord != null) {
                currentWordId = nextWord.id
                options = generateOptions(nextWord, quizWords)
                selectedOption = null
                isCorrect = null
            }
        }
    }

    LaunchedEffect(themeId, quizWords) {
        if (currentWord == null) {
            pickNextWord(answeredCorrectlyIds)
        } else if (options.isEmpty()) {
            options = generateOptions(currentWord, quizWords)
        }
    }

    if (currentWord == null) return

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
            text = currentWord.ru,
            style = MaterialTheme.typography.headlineLarge,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.heightIn(min = 100.dp).wrapContentHeight()
        )

        Text(statusLabel(progressByWordId[currentWord.id]?.status ?: WordLearningStatus.NEW), color = Color.Gray)
        Row {
            TextButton(onClick = { progressScope.launch { ProgressStore.repository().setStatus(currentWord.id, WordLearningStatus.KNOWN) } }) { Text("Знаю") }
            TextButton(onClick = { progressScope.launch { ProgressStore.repository().setStatus(currentWord.id, WordLearningStatus.HARD) } }) { Text("Трудное") }
        }

        Spacer(modifier = Modifier.height(32.dp))

        options.forEach { option ->
            val isThisCorrectOption = option == currentWord.lt
            val isThisSelected = selectedOption == option
            
            val containerColor = when {
                selectedOption == null -> MaterialTheme.colorScheme.primary
                isThisCorrectOption -> Color(0xFF4CAF50) // Всегда зеленый для правильного после выбора
                isThisSelected && isCorrect == false -> Color(0xFFF44336) // Красный если выбрали неправильно
                else -> MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
            }

            Button(
                onClick = {
                    if (selectedOption == null) {
                        selectedOption = option
                        if (option == currentWord.lt) {
                            isCorrect = true
                        } else {
                            isCorrect = false
                            mistakes = mistakes + (
                                currentWord.id to ((mistakes[currentWord.id] ?: 0) + 1)
                            )
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
            LaunchedEffect(selectedOption, currentWord.id) {
                delay(if (isCorrect == true) 800 else 1500)
                if (isCorrect == true) {
                    val completedIds = answeredCorrectlyIds + currentWord.id
                    answeredCorrectlyIds = completedIds
                    pickNextWord(completedIds)
                } else {
                    pickNextWord(answeredCorrectlyIds)
                }
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
            text = "Результат: $score из $total",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Отличная работа. Статистика ошибок:",
            style = MaterialTheme.typography.bodyLarge,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (mistakes.isNotEmpty()) {
            val sortedMistakes = mistakes.toList().sortedByDescending { it.second }
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(sortedMistakes) { (wordId, count) ->
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
                            Text(
                                text = mistakeWordLabel(wordId),
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
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
    val resources = androidx.compose.ui.platform.LocalResources.current
    val exoPlayer = remember { ExoPlayer.Builder(context).build() }
    var playingTrackId by rememberSaveable { mutableStateOf<Int?>(null) }
    var playbackPosition by rememberSaveable { mutableLongStateOf(0L) }
    var shouldResumePlaying by rememberSaveable { mutableStateOf(false) }
    var isExoPlaying by remember { mutableStateOf(false) }

    // Слушатель состояния плеера
    DisposableEffect(exoPlayer) {
        val listener = object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                isExoPlaying = isPlaying
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                if (playbackState == Player.STATE_ENDED) {
                    playbackPosition = 0L
                    shouldResumePlaying = false
                }
            }

            override fun onPlayerError(error: androidx.media3.common.PlaybackException) {
                shouldResumePlaying = false
                android.util.Log.e("AudioPlayer", "Playback failed", error)
            }
        }
        exoPlayer.addListener(listener)
        onDispose {
            exoPlayer.removeListener(listener)
            exoPlayer.release()
        }
    }

    LaunchedEffect(exoPlayer, playingTrackId) {
        val trackId = playingTrackId ?: return@LaunchedEffect
        val resId = audioResourceId(resources, context.packageName, trackId)
        if (resId == 0) {
            android.util.Log.e("AudioPlayer", "Resource not found: audio_$trackId")
            playingTrackId = null
            playbackPosition = 0L
            shouldResumePlaying = false
            return@LaunchedEffect
        }

        val uri = "android.resource://${context.packageName}/$resId".toUri()
        exoPlayer.setMediaItem(MediaItem.fromUri(uri))
        exoPlayer.prepare()
        exoPlayer.seekTo(playbackPosition)
        if (shouldResumePlaying) {
            exoPlayer.play()
        }
    }

    LaunchedEffect(exoPlayer, isExoPlaying) {
        while (isExoPlaying) {
            playbackPosition = exoPlayer.currentPosition.coerceAtLeast(0L)
            delay(250)
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

        var expandedChapters by rememberSaveable(stateSaver = StringSetSaver) {
            mutableStateOf(emptySet())
        }

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
                                            playbackPosition = exoPlayer.currentPosition
                                            shouldResumePlaying = false
                                            exoPlayer.pause()
                                        } else {
                                            val startPosition = playbackStartPosition(
                                                exoPlayer.playbackState,
                                                exoPlayer.currentPosition
                                            )
                                            playbackPosition = startPosition
                                            if (startPosition != exoPlayer.currentPosition) {
                                                exoPlayer.seekTo(startPosition)
                                            }
                                            shouldResumePlaying = true
                                            exoPlayer.play()
                                        }
                                    } else {
                                        exoPlayer.stop()
                                        exoPlayer.clearMediaItems()
                                        playbackPosition = 0L
                                        shouldResumePlaying = true
                                        playingTrackId = track.id
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

fun quizPoolForTheme(
    themeId: String,
    progressByWordId: Map<WordId, WordProgress> = emptyMap(),
): List<Word> {
    val words = if (themeId == "all") {
        GLOBAL_POOL
    } else {
        THEMES_DATA[themeId]?.words.orEmpty()
    }
    return words.distinctBy { it.id }
        .filter { word -> progressByWordId[word.id]?.status != WordLearningStatus.KNOWN }
}

fun selectNextQuizWord(
    words: List<Word>,
    hardWordIds: Set<WordId>,
    previousWordId: WordId?,
    random: Random = Random.Default,
): Word? {
    val withoutImmediateRepeat = words.filter { it.id != previousWordId }.ifEmpty { words }
    val weighted = withoutImmediateRepeat.flatMap { word ->
        List(if (word.id in hardWordIds) 3 else 1) { word }
    }
    return weighted.randomOrNull(random)
}

fun calculateQuizScore(total: Int, mistakes: Map<String, Int>): Int {
    return (total - mistakes.keys.size).coerceIn(0, total)
}

fun statusLabel(status: WordLearningStatus): String = when (status) {
    WordLearningStatus.NEW -> "Новое слово"
    WordLearningStatus.LEARNING -> "Изучаю"
    WordLearningStatus.HARD -> "Повторять чаще"
    WordLearningStatus.KNOWN -> "Знаю"
}

fun playbackStartPosition(playbackState: Int, currentPosition: Long): Long {
    return if (playbackState == Player.STATE_ENDED) 0L else currentPosition.coerceAtLeast(0L)
}

fun mistakeWordLabel(wordId: String): String {
    return GLOBAL_POOL.firstOrNull { it.id == wordId }?.lt
        ?: wordId.substringBefore("::")
}

@SuppressLint("DiscouragedApi")
private fun audioResourceId(resources: Resources, packageName: String, trackId: Int): Int {
    return resources.getIdentifier("audio_$trackId", "raw", packageName)
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
