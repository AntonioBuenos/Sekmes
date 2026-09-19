package com.example.sekmeszodynas.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.EditNote
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material.icons.rounded.MenuBook
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.sekmeszodynas.AudioScreen
import com.example.sekmeszodynas.ConstitutionArticleScreen
import com.example.sekmeszodynas.ConstitutionBlockScreen
import com.example.sekmeszodynas.ConstitutionDictionaryScreen
import com.example.sekmeszodynas.ConstitutionHomeScreen
import com.example.sekmeszodynas.ConstitutionQuizScreen
import com.example.sekmeszodynas.DictionaryScreen
import com.example.sekmeszodynas.PartOfSpeech
import com.example.sekmeszodynas.QuizScreen
import com.example.sekmeszodynas.ResultScreen
import com.example.sekmeszodynas.SettingsStore
import com.example.sekmeszodynas.ThemeSelectionScreen
import com.example.sekmeszodynas.VocabularyScope
import com.example.sekmeszodynas.WordId
import com.example.sekmeszodynas.feature.course.CourseDictionaryScreen
import com.example.sekmeszodynas.feature.course.CourseHomeScreen
import com.example.sekmeszodynas.feature.course.CourseQuizScreen
import com.example.sekmeszodynas.feature.course.CourseThemeSelectionScreen
import com.example.sekmeszodynas.feature.course.LearningHubScreen
import com.example.sekmeszodynas.feature.grammar.GrammarCardScreen
import com.example.sekmeszodynas.feature.grammar.GrammarCardsScreen
import com.example.sekmeszodynas.feature.more.MoreScreen
import com.example.sekmeszodynas.feature.settings.SettingsScreen
import com.example.sekmeszodynas.feature.settings.WordStatusManagerScreen
import com.example.sekmeszodynas.feature.vocabulary.GlobalDictionaryScreen
import com.example.sekmeszodynas.feature.vocabulary.GlobalQuizScreen
import com.example.sekmeszodynas.feature.vocabulary.GlobalVocabularyScreen
import com.example.sekmeszodynas.feature.vocabulary.VisualVocabularyCardsScreen
import com.example.sekmeszodynas.feature.words.MyWordsScreen
import kotlinx.coroutines.launch

private object AppRoute {
    const val dashboard = "dashboard"
    const val dictionaryThemes = "dictionary/themes"
    const val quizThemes = "quiz/themes"
    const val dictionary = "dictionary/{themeId}"
    const val quiz = "quiz/{themeId}"
    const val results = "results"
    const val audio = "audio"
    const val constitutionHome = "constitution"
    const val constitutionBlock = "constitution/block/{blockId}"
    const val constitutionArticle = "constitution/block/{blockId}/article/{contentId}"
    const val constitutionDictionary = "constitution/dictionary/{blockId}"
    const val constitutionQuiz = "constitution/quiz/{blockId}"
    const val courseHome = "course/{courseId}"
    const val courseDictionaryThemes = "course/{courseId}/dictionary/themes"
    const val courseQuizThemes = "course/{courseId}/quiz/themes"
    const val courseDictionary = "course/{courseId}/dictionary/{lessonId}"
    const val courseQuiz = "course/{courseId}/quiz/{lessonId}"
    const val courseAudio = "course/{courseId}/audio"
    const val globalVocabulary = "vocabulary"
    const val globalDictionary = "vocabulary/dictionary/{partOfSpeech}/{sourceId}"
    const val globalQuiz = "vocabulary/quiz/{partOfSpeech}/{sourceId}"
    const val visualCards = "vocabulary/cards/{partOfSpeech}/{sourceId}"
    const val settings = "settings"
    const val statusManager = "settings/statuses"
    const val myWords = "my-words"
    const val more = "more"
    const val grammar = "grammar"
    const val grammarCard = "grammar/{cardId}"

    fun dictionary(themeId: String) = "dictionary/$themeId"
    fun quiz(themeId: String) = "quiz/$themeId"
    fun constitutionBlock(blockId: String) = "constitution/block/$blockId"
    fun constitutionArticle(blockId: String, contentId: String) = "constitution/block/$blockId/article/$contentId"
    fun constitutionDictionary(blockId: String?) = "constitution/dictionary/${blockId ?: ALL_BLOCKS}"
    fun constitutionQuiz(blockId: String?) = "constitution/quiz/${blockId ?: ALL_BLOCKS}"
    fun courseHome(courseId: String) = "course/$courseId"
    fun courseDictionaryThemes(courseId: String) = "course/$courseId/dictionary/themes"
    fun courseQuizThemes(courseId: String) = "course/$courseId/quiz/themes"
    fun courseDictionary(courseId: String, lessonId: String?) = "course/$courseId/dictionary/${lessonId ?: ALL_LESSONS}"
    fun courseQuiz(courseId: String, lessonId: String?) = "course/$courseId/quiz/${lessonId ?: ALL_LESSONS}"
    fun courseAudio(courseId: String) = "course/$courseId/audio"
    fun globalDictionary(scope: VocabularyScope) = "vocabulary/dictionary/${scope.partOfSpeech?.name ?: ALL_PARTS}/${scope.sourceId ?: ALL_SOURCES}"
    fun globalQuiz(scope: VocabularyScope) = "vocabulary/quiz/${scope.partOfSpeech?.name ?: ALL_PARTS}/${scope.sourceId ?: ALL_SOURCES}"
    fun visualCards(scope: VocabularyScope) = "vocabulary/cards/${scope.partOfSpeech?.name ?: ALL_PARTS}/${scope.sourceId ?: ALL_SOURCES}"

    const val ALL_BLOCKS = "all"
    const val ALL_LESSONS = "all"
    const val ALL_PARTS = "all"
    const val ALL_SOURCES = "all"
}

private const val RESULT_SCORE = "result_score"
private const val RESULT_TOTAL = "result_total"
private const val RESULT_MISTAKE_IDS = "result_mistake_ids"
private const val RESULT_MISTAKE_COUNTS = "result_mistake_counts"

private data class TopLevelDestination(
    val route: String,
    val label: String,
    val icon: ImageVector,
)

private val topLevelDestinations = listOf(
    TopLevelDestination(AppRoute.dashboard, "Главная", Icons.Rounded.Home),
    TopLevelDestination(AppRoute.globalVocabulary, "Словарь", Icons.Rounded.MenuBook),
    TopLevelDestination(AppRoute.myWords, "Мои слова", Icons.Rounded.EditNote),
    TopLevelDestination(AppRoute.more, "Ещё", Icons.Rounded.Menu),
)

@Composable
fun SekmesAppNavigation() {
    val navController = rememberNavController()
    val coroutineScope = rememberCoroutineScope()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val showTopLevelNavigation = topLevelDestinations.any { it.route == currentRoute }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val useNavigationRail = maxWidth >= 600.dp
        val navigateToTopLevel = { route: String -> navController.navigateToTopLevel(route) }
        val navHost: @Composable (Modifier) -> Unit = { modifier ->
            SekmesNavHost(
                navController = navController,
                modifier = modifier,
                onCourseSelected = { courseId ->
                    coroutineScope.launch { SettingsStore.repository().setLastOpenedCourseId(courseId) }
                    navController.navigate(AppRoute.courseHome(courseId))
                },
            )
        }

        when {
            useNavigationRail && showTopLevelNavigation -> {
                Row(Modifier.fillMaxSize()) {
                    SekmesNavigationRail(currentRoute, navigateToTopLevel)
                    navHost(Modifier.weight(1f).fillMaxHeight())
                }
            }
            else -> Scaffold(
                modifier = Modifier.fillMaxSize(),
                bottomBar = {
                    if (showTopLevelNavigation && !useNavigationRail) {
                        SekmesNavigationBar(currentRoute, navigateToTopLevel)
                    }
                },
            ) { innerPadding ->
                navHost(Modifier.fillMaxSize().padding(innerPadding))
            }
        }
    }
}

@Composable
private fun SekmesNavigationBar(currentRoute: String?, onNavigate: (String) -> Unit) {
    NavigationBar {
        topLevelDestinations.forEach { destination ->
            NavigationBarItem(
                selected = currentRoute == destination.route,
                onClick = { onNavigate(destination.route) },
                icon = { Icon(destination.icon, contentDescription = null) },
                label = { androidx.compose.material3.Text(destination.label) },
            )
        }
    }
}

@Composable
private fun SekmesNavigationRail(currentRoute: String?, onNavigate: (String) -> Unit) {
    NavigationRail {
        topLevelDestinations.forEach { destination ->
            NavigationRailItem(
                selected = currentRoute == destination.route,
                onClick = { onNavigate(destination.route) },
                icon = { Icon(destination.icon, contentDescription = null) },
                label = { androidx.compose.material3.Text(destination.label) },
            )
        }
    }
}

@Composable
private fun SekmesNavHost(
    navController: NavHostController,
    onCourseSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    NavHost(navController = navController, startDestination = AppRoute.dashboard, modifier = modifier) {
        composable(AppRoute.dashboard) {
            LearningHubScreen(
                onCourseSelected = onCourseSelected,
                onConstitutionSelected = { navController.navigate(AppRoute.constitutionHome) },
                onGlobalVocabulary = { navController.navigateToTopLevel(AppRoute.globalVocabulary) },
                onMyWords = { navController.navigateToTopLevel(AppRoute.myWords) },
                onGrammar = { navController.navigate(AppRoute.grammar) },
            )
        }
        composable(AppRoute.globalVocabulary) {
            GlobalVocabularyScreen(
                onDictionary = { navController.navigate(AppRoute.globalDictionary(it)) },
                onQuiz = { navController.navigate(AppRoute.globalQuiz(it)) },
                onVisualCards = { navController.navigate(AppRoute.visualCards(it)) },
                onBack = { navController.navigateToTopLevel(AppRoute.dashboard) },
            )
        }
        composable(AppRoute.myWords) { MyWordsScreen { navController.navigateToTopLevel(AppRoute.dashboard) } }
        composable(AppRoute.more) {
            MoreScreen(
                onGrammar = { navController.navigate(AppRoute.grammar) },
                onConstitution = { navController.navigate(AppRoute.constitutionHome) },
                onSettings = { navController.navigate(AppRoute.settings) },
            )
        }
        composable(AppRoute.grammar) { GrammarCardsScreen({ navController.navigate("grammar/$it") }) { navController.navigateUp() } }
        composable(AppRoute.grammarCard) { entry ->
            GrammarCardScreen(
                id = entry.stringArgument("cardId"),
                onCard = { navController.navigate("grammar/$it") },
                onBack = { navController.navigateUp() },
            )
        }
        composable(AppRoute.settings) { SettingsScreen({ navController.navigate(AppRoute.statusManager) }) { navController.navigateUp() } }
        composable(AppRoute.statusManager) { WordStatusManagerScreen { navController.navigateUp() } }
        composable(AppRoute.globalDictionary) { entry -> GlobalDictionaryScreen(entry.vocabularyScopeArgument()) { navController.navigateUp() } }
        composable(AppRoute.visualCards) { entry -> VisualVocabularyCardsScreen(entry.vocabularyScopeArgument()) { navController.navigateUp() } }
        composable(AppRoute.globalQuiz) { entry ->
            GlobalQuizScreen(entry.vocabularyScopeArgument(), { score, total, mistakes -> navController.publishResult(score, total, mistakes) }, { navController.navigateUp() })
        }
        composable(AppRoute.courseHome) { entry ->
            val courseId = entry.stringArgument("courseId")
            CourseHomeScreen(
                courseId = courseId,
                onDictionary = { navController.navigate(AppRoute.courseDictionaryThemes(courseId)) },
                onQuiz = { navController.navigate(AppRoute.courseQuizThemes(courseId)) },
                onAudio = { navController.navigate(AppRoute.courseAudio(courseId)) },
                onBack = { navController.navigateUp() },
            )
        }
        composable(AppRoute.courseDictionaryThemes) { entry ->
            val courseId = entry.stringArgument("courseId")
            CourseThemeSelectionScreen(courseId, "Словарь", { lessonId -> navController.navigate(AppRoute.courseDictionary(courseId, lessonId)) }, { navController.navigateUp() })
        }
        composable(AppRoute.courseQuizThemes) { entry ->
            val courseId = entry.stringArgument("courseId")
            CourseThemeSelectionScreen(courseId, "Тест", { lessonId -> navController.navigate(AppRoute.courseQuiz(courseId, lessonId)) }, { navController.navigateUp() })
        }
        composable(AppRoute.courseDictionary) { entry ->
            CourseDictionaryScreen(entry.stringArgument("courseId"), entry.stringArgument("lessonId").takeUnless { it == AppRoute.ALL_LESSONS }) { navController.navigateUp() }
        }
        composable(AppRoute.courseQuiz) { entry ->
            CourseQuizScreen(
                entry.stringArgument("courseId"),
                entry.stringArgument("lessonId").takeUnless { it == AppRoute.ALL_LESSONS },
                { score, total, mistakes -> navController.publishResult(score, total, mistakes) },
                { navController.navigateUp() },
            )
        }
        composable(AppRoute.courseAudio) { entry -> AudioScreen(courseId = entry.stringArgument("courseId"), onBack = { navController.navigateUp() }) }
        composable(AppRoute.dictionaryThemes) {
            ThemeSelectionScreen("Словарь: Выбор темы", { navController.navigate(AppRoute.dictionary(it)) }, { navController.navigateUp() })
        }
        composable(AppRoute.quizThemes) {
            ThemeSelectionScreen("Тест: Выбор темы", { navController.navigate(AppRoute.quiz(it)) }, { navController.navigateUp() })
        }
        composable(AppRoute.dictionary) { entry -> DictionaryScreen(entry.stringArgument("themeId")) { navController.navigateUp() } }
        composable(AppRoute.quiz) { entry ->
            QuizScreen(entry.stringArgument("themeId"), { score, total, mistakes -> navController.publishResult(score, total, mistakes) }, { navController.navigateUp() })
        }
        composable(AppRoute.results) {
            val result = navController.readResult()
            ResultScreen(result.score, result.total, result.mistakes, onRestart = { navController.popBackStack(AppRoute.dashboard, inclusive = false) })
        }
        composable(AppRoute.audio) { AudioScreen(onBack = { navController.navigateUp() }) }
        composable(AppRoute.constitutionHome) {
            ConstitutionHomeScreen(
                onBlockSelected = { navController.navigate(AppRoute.constitutionBlock(it)) },
                onDictionary = { navController.navigate(AppRoute.constitutionDictionary(null)) },
                onQuiz = { navController.navigate(AppRoute.constitutionQuiz(null)) },
                onBack = { navController.navigateUp() },
            )
        }
        composable(AppRoute.constitutionBlock) { entry ->
            val blockId = entry.stringArgument("blockId")
            ConstitutionBlockScreen(
                blockId = blockId,
                onContentSelected = { navController.navigate(AppRoute.constitutionArticle(blockId, it)) },
                onDictionary = { navController.navigate(AppRoute.constitutionDictionary(blockId)) },
                onQuiz = { navController.navigate(AppRoute.constitutionQuiz(blockId)) },
                onBack = { navController.navigateUp() },
            )
        }
        composable(AppRoute.constitutionArticle) { entry ->
            val blockId = entry.stringArgument("blockId")
            ConstitutionArticleScreen(blockId, entry.stringArgument("contentId")) { navController.navigateUp() }
        }
        composable(AppRoute.constitutionDictionary) { entry ->
            ConstitutionDictionaryScreen(entry.stringArgument("blockId").takeUnless { it == AppRoute.ALL_BLOCKS }) { navController.navigateUp() }
        }
        composable(AppRoute.constitutionQuiz) { entry ->
            ConstitutionQuizScreen(
                entry.stringArgument("blockId").takeUnless { it == AppRoute.ALL_BLOCKS },
                { score, total, mistakes -> navController.publishResult(score, total, mistakes) },
                { navController.navigateUp() },
            )
        }
    }
}

private fun NavHostController.navigateToTopLevel(route: String) {
    navigate(route) {
        popUpTo(graph.findStartDestination().id) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}

private data class QuizResult(val score: Int, val total: Int, val mistakes: Map<WordId, Int>)

private fun NavHostController.publishResult(score: Int, total: Int, mistakes: Map<WordId, Int>) {
    currentBackStackEntry?.savedStateHandle?.apply {
        set(RESULT_SCORE, score)
        set(RESULT_TOTAL, total)
        set(RESULT_MISTAKE_IDS, ArrayList(mistakes.keys))
        set(RESULT_MISTAKE_COUNTS, ArrayList(mistakes.values))
    }
    navigate(AppRoute.results)
}

private fun NavHostController.readResult(): QuizResult {
    val state = previousBackStackEntry?.savedStateHandle
    val ids = state?.get<ArrayList<String>>(RESULT_MISTAKE_IDS).orEmpty()
    val counts = state?.get<ArrayList<Int>>(RESULT_MISTAKE_COUNTS).orEmpty()
    return QuizResult(state?.get<Int>(RESULT_SCORE) ?: 0, state?.get<Int>(RESULT_TOTAL) ?: 0, ids.zip(counts).toMap())
}

private fun NavBackStackEntry.stringArgument(name: String): String = requireNotNull(arguments?.getString(name)) { "Missing navigation argument: $name" }

private fun NavBackStackEntry.partOfSpeechArgument(name: String): PartOfSpeech? = stringArgument(name).takeUnless { it == AppRoute.ALL_PARTS }?.let(PartOfSpeech::valueOf)

private fun NavBackStackEntry.vocabularyScopeArgument(): VocabularyScope = VocabularyScope(
    partOfSpeech = partOfSpeechArgument("partOfSpeech"),
    sourceId = stringArgument("sourceId").takeUnless { it == AppRoute.ALL_SOURCES },
)
