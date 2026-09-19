package com.example.sekmeszodynas.feature.course

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.FactCheck
import androidx.compose.material.icons.rounded.Gavel
import androidx.compose.material.icons.rounded.Headphones
import androidx.compose.material.icons.rounded.Category
import androidx.compose.material.icons.rounded.MenuBook
import androidx.compose.material.icons.rounded.EditNote
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.sekmeszodynas.AppSettings
import com.example.sekmeszodynas.CatalogStore
import com.example.sekmeszodynas.Course
import com.example.sekmeszodynas.CourseCapability
import com.example.sekmeszodynas.CourseVisibility
import com.example.sekmeszodynas.DictionaryEntry
import com.example.sekmeszodynas.SettingsStore
import com.example.sekmeszodynas.Theme
import com.example.sekmeszodynas.Word
import com.example.sekmeszodynas.DictionaryWordsScreen
import com.example.sekmeszodynas.QuizWordsScreen
import com.example.sekmeszodynas.themesForCourse
import com.example.sekmeszodynas.wordsForCourse
import com.example.sekmeszodynas.ui.components.SekmesActionRow
import com.example.sekmeszodynas.ui.components.SekmesCourseCard
import com.example.sekmeszodynas.ui.components.SekmesSectionHeader
import com.example.sekmeszodynas.ui.components.SekmesTopAppBar
import com.example.sekmeszodynas.ui.theme.SekmesSpacing

@Composable
fun LearningHubScreen(
    onCourseSelected: (String) -> Unit,
    onConstitutionSelected: () -> Unit,
    onGlobalVocabulary: () -> Unit,
    onMyWords: () -> Unit,
    onGrammar: () -> Unit,
) {
    val repository = CatalogStore.repository()
    val courses = repository.courses.filter { it.visibility == CourseVisibility.LEARNING }
    val settings = SettingsStore.repository().settings.collectAsState(initial = AppSettings()).value
    val continueCourse = courses.firstOrNull { it.id == settings.lastOpenedCourseId } ?: courses.firstOrNull()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = SekmesSpacing.Medium, vertical = SekmesSpacing.Small),
        verticalArrangement = Arrangement.spacedBy(SekmesSpacing.XSmall),
    ) {
        item { AppBrandHeader() }
        item { Text("Продолжим учиться?", style = MaterialTheme.typography.headlineMedium, modifier = Modifier.padding(top = SekmesSpacing.XSmall)) }
        continueCourse?.let { course ->
            item {
                Card(onClick = { onCourseSelected(course.id) }, colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
                    Column(Modifier.padding(SekmesSpacing.Small)) {
                        Text("Текущий курс", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                        Text(course.title, style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(top = SekmesSpacing.XSmall))
                        Text(courseMetadata(course), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onPrimaryContainer, modifier = Modifier.padding(top = SekmesSpacing.XxxSmall))
                    }
                }
            }
        }
        item { SekmesSectionHeader("Быстрый доступ") }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(SekmesSpacing.XSmall)) {
                QuickAccessCard("Словарь", "Все слова", Icons.Rounded.MenuBook, onGlobalVocabulary, Modifier.weight(1f))
                QuickAccessCard("Грамматика", "Правила", Icons.Rounded.Category, onGrammar, Modifier.weight(1f))
            }
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(SekmesSpacing.XSmall)) {
                QuickAccessCard("Конституция", "Текст и термины", Icons.Rounded.Gavel, onConstitutionSelected, Modifier.weight(1f))
                QuickAccessCard("Мои слова", "Личный список", Icons.Rounded.EditNote, onMyWords, Modifier.weight(1f))
            }
        }
        item { SekmesSectionHeader("Курсы", modifier = Modifier.padding(top = SekmesSpacing.Small)) }
        items(courses, key = Course::id) { course ->
            SekmesCourseCard(course.title, course.description, courseMetadata(course), onClick = { onCourseSelected(course.id) })
        }
    }
}

@Composable
fun CourseHomeScreen(courseId: String, onDictionary: () -> Unit, onQuiz: () -> Unit, onAudio: () -> Unit, onBack: () -> Unit) {
    val repository = CatalogStore.repository()
    val course = repository.courseById[courseId] ?: return
    val hasLessons = repository.lessonsForCourse(courseId).isNotEmpty()
    Column(Modifier.fillMaxSize()) {
        SekmesTopAppBar(course.title, subtitle = courseMetadata(course), onBack = onBack)
        Column(Modifier.padding(horizontal = SekmesSpacing.Medium), verticalArrangement = Arrangement.spacedBy(SekmesSpacing.XSmall)) {
            Text(course.description.ifBlank { "Учебный курс литовского языка." }, style = MaterialTheme.typography.bodyLarge)
            if (!hasLessons) Text("Материалы готовятся. Здесь появятся уроки, словарь и аудиофайлы.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            if (hasLessons && CourseCapability.DICTIONARY in course.capabilities) SekmesActionRow("Учить слова", "Словарь по темам", Icons.Rounded.MenuBook, onDictionary, highlighted = true)
            if (hasLessons && CourseCapability.QUIZ in course.capabilities) SekmesActionRow("Пройти тест", "Настроить и начать", Icons.Rounded.FactCheck, onQuiz)
            if (hasLessons && CourseCapability.AUDIO in course.capabilities) SekmesActionRow("Слушать аудио", "Уроки офлайн", Icons.Rounded.Headphones, onAudio)
        }
    }
}

@Composable
fun CourseThemeSelectionScreen(courseId: String, modeTitle: String, onThemeSelected: (String?) -> Unit, onBack: () -> Unit) {
    val repository = CatalogStore.repository()
    val course = repository.courseById[courseId] ?: return
    val themes = themesForCourse(courseId).values.sortedBy { repository.lessonById[it.id]?.order ?: it.title.substringBefore('.').trim().toIntOrNull() ?: Int.MAX_VALUE }
    Column(Modifier.fillMaxSize()) {
        SekmesTopAppBar("$modeTitle: ${course.title}", onBack = onBack)
        if (themes.isEmpty()) Text("В этом курсе пока нет уроков.", modifier = Modifier.padding(SekmesSpacing.Medium)) else LazyColumn(contentPadding = PaddingValues(horizontal = SekmesSpacing.Medium), verticalArrangement = Arrangement.spacedBy(SekmesSpacing.XSmall)) {
            item { SekmesActionRow("Все темы курса", "${wordsForCourse(courseId).size} слов", Icons.Rounded.MenuBook, onClick = { onThemeSelected(null) }, highlighted = true) }
            item { Text("Выберите урок", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = SekmesSpacing.XSmall)) }
            items(themes, key = Theme::id) { theme -> SekmesCourseCard(theme.title, "${theme.words.size} слов", "Открыть урок", onClick = { onThemeSelected(theme.id) }) }
        }
    }
}

@Composable
fun CourseDictionaryScreen(courseId: String, lessonId: String?, onBack: () -> Unit) {
    val repository = CatalogStore.repository()
    val course = repository.courseById[courseId] ?: return
    val lesson = lessonId?.let(repository.lessonById::get)
    val words = lessonId?.let { repository.wordsForLesson(it).asWords() } ?: wordsForCourse(courseId)
    DictionaryWordsScreen(words, lesson?.title ?: "Словарь: ${course.title}", "course:$courseId:dictionary:${lessonId ?: "all"}", onBack)
}

@Composable
fun CourseQuizScreen(courseId: String, lessonId: String?, onQuizFinished: (Int, Int, Map<String, Int>) -> Unit, onBack: () -> Unit) {
    val repository = CatalogStore.repository()
    val words = lessonId?.let { repository.wordsForLesson(it).asWords() } ?: wordsForCourse(courseId)
    QuizWordsScreen("course:$courseId:quiz:${lessonId ?: "all"}", words, onQuizFinished, onBack)
}

@Composable private fun AppBrandHeader() {
    Row(verticalAlignment = Alignment.CenterVertically) {
        LithuaniaMark()
        Spacer(Modifier.width(SekmesSpacing.XSmall))
        Column { Text("Sėkmės", style = MaterialTheme.typography.headlineMedium); Text("Литовский каждый день", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant) }
    }
}

@Composable private fun LithuaniaMark() {
    Column(Modifier.size(34.dp).clip(MaterialTheme.shapes.small)) {
        Box(Modifier.weight(1f).fillMaxWidth().background(MaterialTheme.colorScheme.secondary))
        Box(Modifier.weight(1f).fillMaxWidth().background(MaterialTheme.colorScheme.primary))
        Box(Modifier.weight(1f).fillMaxWidth().background(MaterialTheme.colorScheme.tertiary))
    }
}

@Composable private fun QuickAccessCard(title: String, description: String, icon: ImageVector, onClick: () -> Unit, modifier: Modifier) {
    OutlinedCard(onClick = onClick, modifier = modifier) { Column(Modifier.padding(SekmesSpacing.XSmall)) { Icon(icon, null, tint = MaterialTheme.colorScheme.primary); Text(title, style = MaterialTheme.typography.titleSmall, modifier = Modifier.padding(top = SekmesSpacing.XSmall)); Text(description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant) } }
}

private fun courseMetadata(course: Course): String = "${CatalogStore.repository().lessonsForCourse(course.id).size} тем · ${wordsForCourse(course.id).size} слов"
private fun List<DictionaryEntry>.asWords() = map { Word(it.ru, it.lt, it.type, it.id) }
