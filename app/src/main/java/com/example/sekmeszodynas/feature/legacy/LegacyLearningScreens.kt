package com.example.sekmeszodynas

import android.annotation.SuppressLint
import android.content.res.Resources
import android.os.Bundle
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.FactCheck
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.material.icons.rounded.ExpandLess
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.material.icons.rounded.Gavel
import androidx.compose.material.icons.rounded.Headphones
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.foundation.horizontalScroll
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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlin.random.Random
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.example.sekmeszodynas.ui.components.SekmesEmptyState
import com.example.sekmeszodynas.ui.components.AnswerOptionState
import com.example.sekmeszodynas.ui.components.SekmesAnswerOption
import com.example.sekmeszodynas.ui.components.SekmesStatusChip
import com.example.sekmeszodynas.ui.components.SekmesStatusOption
import com.example.sekmeszodynas.ui.components.SekmesStatusTone
import com.example.sekmeszodynas.ui.components.SekmesTopAppBar
import com.example.sekmeszodynas.ui.components.SekmesWordRow
import com.example.sekmeszodynas.ui.theme.SekmesSpacing

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

@Composable
fun MainDashboardScreen(
    onNavigateToDictionary: () -> Unit,
    onNavigateToQuiz: () -> Unit,
    onNavigateToAudio: () -> Unit,
    onNavigateToConstitution: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp).verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Sėkmės",
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
                    Icon(Icons.AutoMirrored.Rounded.MenuBook, contentDescription = null, modifier = Modifier.size(32.dp))
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
                    Icon(Icons.AutoMirrored.Rounded.FactCheck, contentDescription = null, modifier = Modifier.size(32.dp))
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
                    Icon(Icons.Rounded.Headphones, contentDescription = null, modifier = Modifier.size(32.dp))
                    Text("Аудио курс", style = MaterialTheme.typography.titleLarge)
                }
            }
        }

        Card(
            onClick = onNavigateToConstitution,
            modifier = Modifier.fillMaxWidth().height(120.dp).padding(vertical = 8.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Rounded.Gavel, contentDescription = null, modifier = Modifier.size(32.dp))
                    Text("Конституция Литвы", style = MaterialTheme.typography.titleLarge)
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
                Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Назад")
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
    DictionaryWordsScreen(
        words = words,
        themeTitle = themeTitle,
        stateKey = "sekmes:$themeId",
        onBack = onBack,
    )
}

@Composable
fun DictionaryWordsScreen(
    words: List<Word>,
    themeTitle: String,
    stateKey: String,
    onBack: () -> Unit,
) {
    val progressByWordId by ProgressStore.repository().observeAll().collectAsState(initial = emptyMap())
    val settings by SettingsStore.repository().settings.collectAsState(initial = AppSettings())
    val scope = rememberCoroutineScope()
    var showKnown by rememberSaveable(stateKey) { mutableStateOf(settings.showKnownWords) }
    var query by rememberSaveable(stateKey) { mutableStateOf("") }
    var selectedStatus by rememberSaveable(stateKey) { mutableStateOf<WordLearningStatus?>(null) }
    val visibleWords = words.filter { word ->
        val status = progressByWordId[word.id]?.status ?: WordLearningStatus.NEW
        (showKnown || status != WordLearningStatus.KNOWN) &&
            (selectedStatus == null || status == selectedStatus) &&
            (query.isBlank() || word.lt.contains(query, true) || word.ru.contains(query, true))
    }

    val statusOptions = WordLearningStatus.entries.map { it.asStatusOption() }
    Column(modifier = Modifier.fillMaxSize()) {
        SekmesTopAppBar(
            title = themeTitle,
            subtitle = "${visibleWords.size} из ${words.size} слов",
            onBack = onBack,
        )
        Column(
            modifier = Modifier.padding(horizontal = SekmesSpacing.Medium),
            verticalArrangement = Arrangement.spacedBy(SekmesSpacing.XSmall),
        ) {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                label = { Text("Поиск на литовском или русском") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
            Row(
                modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(SekmesSpacing.XSmall),
            ) {
                SekmesStatusChip("Все", SekmesStatusTone.Neutral, onClick = { selectedStatus = null }, selected = selectedStatus == null)
                WordLearningStatus.entries.forEach { status ->
                    val option = status.asStatusOption()
                    SekmesStatusChip(option.label, option.tone, onClick = { selectedStatus = status }, selected = selectedStatus == status)
                }
                SekmesStatusChip(
                    label = "Известные",
                    tone = SekmesStatusTone.Known,
                    selected = showKnown,
                    onClick = { showKnown = !showKnown },
                )
            }
        }
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(SekmesSpacing.Medium),
            verticalArrangement = Arrangement.spacedBy(SekmesSpacing.XSmall),
        ) {
            if (visibleWords.isEmpty()) {
                item {
                    SekmesEmptyState(
                        title = "Ничего не найдено",
                        description = "Измените запрос или фильтры, чтобы увидеть слова.",
                    )
                }
            } else {
                items(visibleWords, key = Word::id) { word ->
                    val status = progressByWordId[word.id]?.status ?: WordLearningStatus.NEW
                    SekmesWordRow(
                        word = word.lt,
                        translation = word.ru,
                        status = status.asStatusOption(),
                        statusOptions = statusOptions,
                        onStatusSelected = { option ->
                            val target = WordLearningStatus.valueOf(option.id)
                            scope.launch {
                                if (target == WordLearningStatus.NEW) ProgressStore.repository().reset(word.id)
                                else ProgressStore.repository().setStatus(word.id, target)
                            }
                        },
                    )
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
    val sourceWords = if (themeId == "all") GLOBAL_POOL else THEMES_DATA[themeId]?.words.orEmpty()
    QuizWordsScreen(
        sessionKey = "sekmes:$themeId",
        sourceWords = sourceWords,
        onQuizFinished = onQuizFinished,
        onBack = onBack,
    )
}

@Composable
fun QuizWordsScreen(
    sessionKey: String,
    sourceWords: List<Word>,
    onQuizFinished: (Int, Int, Map<String, Int>) -> Unit,
    onBack: () -> Unit,
) {
    val liveProgressByWordId by ProgressStore.repository().observeAll().collectAsState(initial = emptyMap())
    val initialProgressByWordId by produceState<Map<WordId, WordProgress>?>(initialValue = null, key1 = sessionKey) {
        value = ProgressStore.repository().observeAll().first()
    }
    val initialSettings by produceState<AppSettings?>(initialValue = null, key1 = sessionKey) {
        value = SettingsStore.repository().settings.first()
    }
    val progressScope = rememberCoroutineScope()
    var quizWordIds by rememberSaveable(sessionKey, stateSaver = StringListSaver) { mutableStateOf(emptyList()) }
    var quizDirectionName by rememberSaveable(sessionKey) { mutableStateOf<String?>(null) }
    var sessionInitialized by rememberSaveable(sessionKey) { mutableStateOf(false) }

    LaunchedEffect(sessionKey, initialProgressByWordId, initialSettings) {
        val progress = initialProgressByWordId ?: return@LaunchedEffect
        val settings = initialSettings ?: return@LaunchedEffect
        if (!sessionInitialized) {
            quizWordIds = createQuizSessionWords(sourceWords, progress, settings.quizSize).map(Word::id)
            quizDirectionName = settings.quizDirection.name
            sessionInitialized = true
        }
    }

    if (!sessionInitialized) {
        Column(Modifier.fillMaxSize()) {
            SekmesTopAppBar("Тест", onBack = onBack)
            SekmesEmptyState(
                title = "Подготавливаем тест",
                description = "Выбираем слова для этой тренировки.",
                modifier = Modifier.weight(1f),
            )
        }
        return
    }

    val quizWords = remember(sourceWords, quizWordIds) {
        val wordsById = sourceWords.distinctBy(Word::id).associateBy(Word::id)
        quizWordIds.mapNotNull(wordsById::get)
    }
    val quizDirection = QuizDirection.valueOf(requireNotNull(quizDirectionName))

    var answeredCorrectlyIds by rememberSaveable(sessionKey, stateSaver = StringSetSaver) {
        mutableStateOf(emptySet())
    }
    var mistakes by rememberSaveable(sessionKey, stateSaver = StringIntMapSaver) {
        mutableStateOf(emptyMap())
    }
    var currentWordId by rememberSaveable(sessionKey) { mutableStateOf<String?>(null) }
    var selectedOption by rememberSaveable(sessionKey) { mutableStateOf<String?>(null) }
    var isCorrect by rememberSaveable(sessionKey) { mutableStateOf<Boolean?>(null) }
    var options by rememberSaveable(sessionKey, stateSaver = StringListSaver) {
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
                hardWordIds = liveProgressByWordId.filterValues { it.status == WordLearningStatus.HARD }.keys,
                previousWordId = currentWordId,
            )
            if (nextWord != null) {
                currentWordId = nextWord.id
                options = generateOptions(nextWord, quizWords, quizDirection)
                selectedOption = null
                isCorrect = null
            }
        }
    }

    LaunchedEffect(sessionKey, quizWords) {
        if (currentWord == null) {
            pickNextWord(answeredCorrectlyIds)
        } else if (options.isEmpty()) {
            options = generateOptions(currentWord, quizWords, quizDirection)
        }
    }

    if (currentWord == null) {
        Column(Modifier.fillMaxSize()) {
            SekmesTopAppBar("Тест", onBack = onBack)
            SekmesEmptyState(
                title = "Для теста пока нет слов",
                description = "Добавьте новые или повторяемые слова в словарь и попробуйте снова.",
                modifier = Modifier.weight(1f),
            )
        }
        return
    }

    val questionNumber = answeredCorrectlyIds.size + 1
    val correctAnswer = answerFor(currentWord, quizDirection)
    Column(modifier = Modifier.fillMaxSize()) {
        SekmesTopAppBar(
            title = "Вопрос $questionNumber из ${quizWords.size}",
            subtitle = "${answeredCorrectlyIds.size} ответов засчитано",
            onBack = onBack,
        )
        Column(
            modifier = Modifier.padding(horizontal = SekmesSpacing.Medium),
            verticalArrangement = Arrangement.spacedBy(SekmesSpacing.Small),
        ) {
            LinearProgressIndicator(
                progress = { answeredCorrectlyIds.size.toFloat() / quizWords.size.coerceAtLeast(1) },
                modifier = Modifier.fillMaxWidth(),
            )
            Text(
                text = questionFor(currentWord, quizDirection),
                style = MaterialTheme.typography.headlineLarge,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth().padding(vertical = SekmesSpacing.Large),
            )
            Row(horizontalArrangement = Arrangement.spacedBy(SekmesSpacing.XSmall)) {
                SekmesStatusChip(
                    label = "Знаю",
                    tone = SekmesStatusTone.Known,
                    onClick = { progressScope.launch { ProgressStore.repository().setStatus(currentWord.id, WordLearningStatus.KNOWN) } },
                )
                SekmesStatusChip(
                    label = "Трудное",
                    tone = SekmesStatusTone.Hard,
                    onClick = { progressScope.launch { ProgressStore.repository().setStatus(currentWord.id, WordLearningStatus.HARD) } },
                )
            }
            options.forEachIndexed { index, option ->
                val state = when {
                    selectedOption == null -> AnswerOptionState.Idle
                    option == correctAnswer -> AnswerOptionState.Correct
                    option == selectedOption -> AnswerOptionState.Incorrect
                    else -> AnswerOptionState.Idle
                }
                SekmesAnswerOption(
                    label = ('А'.code + index).toChar().toString(),
                    option = option,
                    state = state,
                    enabled = selectedOption == null,
                    onClick = {
                        if (selectedOption == null) {
                            selectedOption = option
                            isCorrect = option == correctAnswer
                            progressScope.launch { ProgressStore.repository().recordAnswer(currentWord.id, correct = isCorrect == true) }
                            if (isCorrect == false) mistakes = mistakes + (currentWord.id to ((mistakes[currentWord.id] ?: 0) + 1))
                        }
                    },
                )
            }
            if (selectedOption != null) {
                Text(
                    text = if (isCorrect == true) "Верно. Переходим к следующему вопросу…" else "Верный ответ: $correctAnswer",
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isCorrect == true) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                )
            }
        }
    }
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

@Composable
fun ResultScreen(
    score: Int,
    total: Int,
    mistakes: Map<String, Int>,
    onRestart: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(SekmesSpacing.Medium),
        verticalArrangement = Arrangement.spacedBy(SekmesSpacing.Small),
    ) {
        Text(
            text = "Тренировка завершена",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(top = SekmesSpacing.Large),
        )
        Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
            Column(Modifier.padding(SekmesSpacing.Small)) {
                Text("Результат", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                Text("$score из $total", style = MaterialTheme.typography.displaySmall)
                Text(
                    if (mistakes.isEmpty()) "Без ошибок — отличный результат." else "Ошибки можно повторить в следующей тренировке.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            }
        }

        if (mistakes.isNotEmpty()) {
            val sortedMistakes = mistakes.toList().sortedByDescending { it.second }
            Text("Повторить", style = MaterialTheme.typography.titleMedium)
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(SekmesSpacing.XSmall),
            ) {
                items(sortedMistakes) { (wordId, count) ->
                    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                        Row(
                            modifier = Modifier.padding(SekmesSpacing.Small).fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = mistakeWordLabel(wordId),
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary,
                            )
                            Text("$count ошибок", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }
        } else {
            SekmesEmptyState("Ошибок нет", "Вы ответили правильно на все вопросы.", modifier = Modifier.weight(1f))
        }
        Button(
            onClick = onRestart,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Вернуться к темам")
        }
    }
}

@Composable
fun AudioScreen(courseId: String = SEKMES_COURSE_ID, onBack: () -> Unit) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val resources = androidx.compose.ui.platform.LocalResources.current
    val exoPlayer = remember { ExoPlayer.Builder(context).build() }
    var playingTrackId by rememberSaveable { mutableStateOf<Int?>(null) }
    var playbackPosition by rememberSaveable { mutableLongStateOf(0L) }
    var shouldResumePlaying by rememberSaveable { mutableStateOf(false) }
    var isExoPlaying by remember { mutableStateOf(false) }
    val audioBooks = audioBooksForCourse(courseId)

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
        val track = audioBooks.flatMap { it.chapters }.flatMap { it.tracks }.firstOrNull { it.id == trackId }
        val resourceName = track?.resourceName ?: "audio_$trackId"
        val resId = audioResourceId(resources, context.packageName, resourceName)
        if (resId == 0) {
            android.util.Log.e("AudioPlayer", "Resource not found: $resourceName")
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

    val activeTrack = audioBooks.flatMap { it.chapters }.flatMap { it.tracks }.firstOrNull { it.id == playingTrackId }
    fun toggleTrack(track: AudioTrack) {
        if (playingTrackId == track.id) {
            if (exoPlayer.isPlaying) {
                playbackPosition = exoPlayer.currentPosition
                shouldResumePlaying = false
                exoPlayer.pause()
            } else {
                val startPosition = playbackStartPosition(exoPlayer.playbackState, exoPlayer.currentPosition)
                playbackPosition = startPosition
                if (startPosition != exoPlayer.currentPosition) exoPlayer.seekTo(startPosition)
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

    var expandedChapters by rememberSaveable(stateSaver = StringSetSaver) { mutableStateOf(emptySet()) }
    Column(modifier = Modifier.fillMaxSize()) {
        SekmesTopAppBar("Аудиокурс", subtitle = "Материалы доступны офлайн", onBack = onBack)
        activeTrack?.let { track ->
            Card(
                modifier = Modifier.fillMaxWidth().padding(horizontal = SekmesSpacing.Medium),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
            ) {
                Row(Modifier.padding(SekmesSpacing.Small), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Rounded.Headphones, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.width(SekmesSpacing.XSmall))
                    Column(Modifier.weight(1f)) {
                        Text(track.title, style = MaterialTheme.typography.titleMedium)
                        Text(
                            "${audioTypeLabel(track.type)} · ${formatAudioPosition(playbackPosition)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                        )
                    }
                    IconButton(onClick = { toggleTrack(track) }) {
                        Icon(
                            if (isExoPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                            contentDescription = if (isExoPlaying) "Пауза" else "Воспроизвести",
                        )
                    }
                }
            }
        }
        if (audioBooks.isEmpty()) {
            SekmesEmptyState(
                title = "Аудиоматериалы готовятся",
                description = "Для этого курса пока нет доступных офлайн-треков.",
                modifier = Modifier.weight(1f),
            )
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(SekmesSpacing.Medium),
                verticalArrangement = Arrangement.spacedBy(SekmesSpacing.XSmall),
            ) {
                audioBooks.forEach { book ->
                    item { Text("Книга ${book.number}", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(top = SekmesSpacing.XSmall)) }
                    book.chapters.forEach { chapter ->
                        val chapterKey = "${book.number}_${chapter.number}"
                        val isExpanded = chapterKey in expandedChapters
                        item {
                            Card(
                                onClick = { expandedChapters = if (isExpanded) expandedChapters - chapterKey else expandedChapters + chapterKey },
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = if (isExpanded) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant),
                            ) {
                                Row(Modifier.padding(SekmesSpacing.Small), verticalAlignment = Alignment.CenterVertically) {
                                    Column(Modifier.weight(1f)) {
                                        Text("Глава ${chapter.number}", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                                        Text(chapter.title, style = MaterialTheme.typography.titleMedium)
                                    }
                                    Icon(if (isExpanded) Icons.Rounded.ExpandLess else Icons.Rounded.ExpandMore, contentDescription = if (isExpanded) "Свернуть главу" else "Развернуть главу")
                                }
                            }
                        }
                        if (isExpanded) {
                            items(chapter.tracks, key = AudioTrack::id) { track ->
                                val isCurrent = track.id == playingTrackId
                                OutlinedCard(onClick = { toggleTrack(track) }, modifier = Modifier.fillMaxWidth()) {
                                    Row(Modifier.padding(SekmesSpacing.Small), verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            if (isCurrent && isExoPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                                            contentDescription = if (isCurrent && isExoPlaying) "Пауза" else "Воспроизвести",
                                            tint = if (isCurrent) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                        )
                                        Spacer(Modifier.width(SekmesSpacing.XSmall))
                                        Column(Modifier.weight(1f)) {
                                            Text(track.title, style = MaterialTheme.typography.bodyLarge)
                                            Text(audioTypeLabel(track.type), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
}

private fun audioTypeLabel(type: AudioType): String = when (type) {
    AudioType.POKALBIS -> "Диалог"
    AudioType.SKAITYMAS -> "Чтение"
    AudioType.KLAUSYMAS -> "Аудирование"
}

private fun formatAudioPosition(positionMs: Long): String {
    val totalSeconds = (positionMs / 1_000).coerceAtLeast(0)
    return "%d:%02d".format(totalSeconds / 60, totalSeconds % 60)
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

/**
 * Chooses a fixed set of words when a training session starts. Progress may change while
 * answering, but the active session must not shrink or reshuffle as a side effect.
 */
fun createQuizSessionWords(
    sourceWords: List<Word>,
    progressByWordId: Map<WordId, WordProgress>,
    quizSize: Int,
    random: Random = Random.Default,
): List<Word> = sourceWords
    .distinctBy(Word::id)
    .filter { word -> progressByWordId[word.id]?.status != WordLearningStatus.KNOWN }
    .shuffled(random)
    .let { words -> if (quizSize > 0) words.take(quizSize) else words }

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

private fun WordLearningStatus.asStatusOption() = SekmesStatusOption(
    id = name,
    label = statusLabel(this),
    tone = when (this) {
        WordLearningStatus.NEW -> SekmesStatusTone.Neutral
        WordLearningStatus.LEARNING -> SekmesStatusTone.Learning
        WordLearningStatus.HARD -> SekmesStatusTone.Hard
        WordLearningStatus.KNOWN -> SekmesStatusTone.Known
    },
)

fun playbackStartPosition(playbackState: Int, currentPosition: Long): Long {
    return if (playbackState == Player.STATE_ENDED) 0L else currentPosition.coerceAtLeast(0L)
}

fun mistakeWordLabel(wordId: String): String {
    return GLOBAL_POOL.firstOrNull { it.id == wordId }?.lt
        ?: wordId.substringBefore("::")
}

@SuppressLint("DiscouragedApi")
private fun audioResourceId(resources: Resources, packageName: String, resourceName: String): Int {
    return resources.getIdentifier(resourceName, "raw", packageName)
}


fun questionFor(word: Word, direction: QuizDirection): String =
    if (direction == QuizDirection.RUSSIAN_TO_LITHUANIAN) word.ru else word.lt

fun answerFor(word: Word, direction: QuizDirection): String =
    if (direction == QuizDirection.RUSSIAN_TO_LITHUANIAN) word.lt else word.ru

fun generateOptions(
    correctWord: Word,
    themeWords: List<Word>,
    direction: QuizDirection = QuizDirection.RUSSIAN_TO_LITHUANIAN,
): List<String> {
    val correctAnswer = answerFor(correctWord, direction)
    // 1. Пытаемся найти дистракторы в текущей теме (того же типа)
    val themeDistractors = themeWords
        .asSequence()
        .filter { it.type == correctWord.type && answerFor(it, direction) != correctAnswer }
        .map { answerFor(it, direction) }
        .distinct()
        .shuffled()
        .take(3)
        .toMutableList()

    // 2. Если в теме мало слов того же типа, берем любые другие из этой же темы
    if (themeDistractors.size < 3) {
        val extraFromTheme = themeWords
            .asSequence()
            .filter { answerFor(it, direction) != correctAnswer && !themeDistractors.contains(answerFor(it, direction)) }
            .map { answerFor(it, direction) }
            .distinct()
            .shuffled()
            .take(3 - themeDistractors.size)
        themeDistractors.addAll(extraFromTheme)
    }

    // 3. Если всё еще не хватает (очень маленькая тема), добираем из глобального пула
    if (themeDistractors.size < 3) {
        val extraGlobal = GLOBAL_POOL
            .asSequence()
            .filter { answerFor(it, direction) != correctAnswer && !themeDistractors.contains(answerFor(it, direction)) }
            .map { answerFor(it, direction) }
            .distinct()
            .shuffled()
            .take(3 - themeDistractors.size)
        themeDistractors.addAll(extraGlobal)
    }

    return (themeDistractors + correctAnswer).shuffled()
}
