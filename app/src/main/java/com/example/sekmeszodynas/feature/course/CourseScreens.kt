package com.example.sekmeszodynas.feature.course

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sekmeszodynas.*

@Composable
fun LearningHubScreen(onCourseSelected: (String) -> Unit, onConstitutionSelected: () -> Unit, onGlobalVocabulary: () -> Unit, onMyWords: () -> Unit, onGrammar: () -> Unit, onSettings: () -> Unit) {
    val courses = CatalogStore.repository().courses.filter { it.visibility == CourseVisibility.LEARNING }
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        item {
            Text("🇱🇹 Sėkmės", style = MaterialTheme.typography.displayMedium, fontWeight = FontWeight.Bold)
            Text("Учебные курсы", style = MaterialTheme.typography.headlineSmall, modifier = Modifier.padding(top = 12.dp))
            Text("Выберите курс, чтобы открыть словарь, тесты и аудиоматериалы.", modifier = Modifier.padding(top = 4.dp))
        }
        item { TextButton(onClick = onSettings, modifier = Modifier.fillMaxWidth()) { Text("⚙️ Настройки") } }
        item { TextButton(onClick = onMyWords, modifier = Modifier.fillMaxWidth()) { Text("✍️ Мои слова") } }
        item { TextButton(onClick = onGrammar, modifier = Modifier.fillMaxWidth()) { Text("🧩 Грамматические карточки") } }
        items(courses, key = Course::id) { course -> CourseCard(course) { onCourseSelected(course.id) } }
        item { Text("Отдельные модули", style = MaterialTheme.typography.headlineSmall, modifier = Modifier.padding(top = 16.dp)) }
        item {
            Card(onClick = onConstitutionSelected, modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
                Column(Modifier.padding(16.dp)) {
                    Text("⚖️ Конституция Литвы", style = MaterialTheme.typography.titleLarge)
                    Text("Параллельное чтение текста, термины и тесты по блокам.", modifier = Modifier.padding(top = 4.dp))
                }
            }
        }
        item {
            Card(onClick = onGlobalVocabulary, modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)) {
                Column(Modifier.padding(16.dp)) {
                    Text("📚 Общий словарь", style = MaterialTheme.typography.titleLarge)
                    Text("Слова всех курсов с выбором части речи и тестом.", modifier = Modifier.padding(top = 4.dp))
                }
            }
        }
    }
}

@Composable
fun CourseHomeScreen(courseId: String, onDictionary: () -> Unit, onQuiz: () -> Unit, onAudio: () -> Unit, onBack: () -> Unit) {
    val repository = CatalogStore.repository()
    val course = repository.courseById[courseId] ?: return
    val hasLessons = repository.lessonsForCourse(courseId).isNotEmpty()
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        CourseHeader(course.title, onBack)
        Text(course.description.ifBlank { "Учебный курс литовского языка." }, modifier = Modifier.padding(vertical = 8.dp))
        if (!hasLessons) {
            Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)) {
                Text("Материалы готовятся. Здесь появятся уроки, словарь и аудиофайлы.", modifier = Modifier.padding(16.dp))
            }
        }
        CourseActionCard("📖", "Словарь", hasLessons && CourseCapability.DICTIONARY in course.capabilities, onDictionary)
        CourseActionCard("📝", "Пройти тест", hasLessons && CourseCapability.QUIZ in course.capabilities, onQuiz)
        CourseActionCard("🎧", "Аудиокурс", hasLessons && CourseCapability.AUDIO in course.capabilities, onAudio)
    }
}

@Composable
fun CourseThemeSelectionScreen(courseId: String, modeTitle: String, onThemeSelected: (String?) -> Unit, onBack: () -> Unit) {
    val repository = CatalogStore.repository()
    val course = repository.courseById[courseId] ?: return
    val themes = themesForCourse(courseId).values.sortedBy(Theme::title)
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        CourseHeader("$modeTitle: ${course.title}", onBack)
        if (themes.isEmpty()) {
            Text("В этом курсе пока нет уроков.", modifier = Modifier.padding(top = 16.dp))
            return@Column
        }
        Button(onClick = { onThemeSelected(null) }, modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
            Text("Все темы курса (${wordsForCourse(courseId).size})")
        }
        Text("Выберите урок:", modifier = Modifier.padding(vertical = 8.dp))
        LazyColumn(Modifier.weight(1f)) {
            items(themes, key = Theme::id) { theme ->
                Card(onClick = { onThemeSelected(theme.id) }, modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Text("${theme.title} (${theme.words.size})", modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.titleMedium)
                }
            }
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

@Composable private fun CourseCard(course: Course, onClick: () -> Unit) {
    Card(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) { Text(course.title, style = MaterialTheme.typography.titleLarge); Text(course.description, modifier = Modifier.padding(top = 4.dp)) }
    }
}

@Composable private fun CourseActionCard(emoji: String, title: String, enabled: Boolean, onClick: () -> Unit) {
    Card(onClick = onClick, enabled = enabled, modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Row(Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) { Text(emoji, fontSize = 28.sp); Text(title, style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(start = 16.dp)) }
    }
}

@Composable private fun CourseHeader(title: String, onBack: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) { IconButton(onClick = onBack) { Text("←", fontSize = 24.sp) }; Text(title, style = MaterialTheme.typography.headlineSmall) }
}

private fun List<DictionaryEntry>.asWords() = map { Word(it.ru, it.lt, it.type, it.id) }
