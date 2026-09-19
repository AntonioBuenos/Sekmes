package com.example.sekmeszodynas

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.settingsDataStore by preferencesDataStore(name = "app_settings")

enum class QuizDirection { RUSSIAN_TO_LITHUANIAN, LITHUANIAN_TO_RUSSIAN }

data class AppSettings(
    val quizDirection: QuizDirection = QuizDirection.RUSSIAN_TO_LITHUANIAN,
    val quizSize: Int = 20,
    val showKnownWords: Boolean = false,
    val showConstitutionTranslation: Boolean = true,
    val lastOpenedCourseId: String? = null,
)

class AppSettingsRepository(private val context: Context) {
    val settings: Flow<AppSettings> = context.settingsDataStore.data.map { preferences ->
        AppSettings(
            quizDirection = preferences[QUIZ_DIRECTION]?.let(QuizDirection::valueOf) ?: QuizDirection.RUSSIAN_TO_LITHUANIAN,
            quizSize = preferences[QUIZ_SIZE] ?: 20,
            showKnownWords = preferences[SHOW_KNOWN] ?: false,
            showConstitutionTranslation = preferences[SHOW_CONSTITUTION_TRANSLATION] ?: true,
            lastOpenedCourseId = preferences[LAST_OPENED_COURSE_ID],
        )
    }

    suspend fun update(transform: (AppSettings) -> AppSettings) {
        context.settingsDataStore.edit { preferences ->
            val current = AppSettings(
                quizDirection = preferences[QUIZ_DIRECTION]?.let(QuizDirection::valueOf) ?: QuizDirection.RUSSIAN_TO_LITHUANIAN,
                quizSize = preferences[QUIZ_SIZE] ?: 20,
                showKnownWords = preferences[SHOW_KNOWN] ?: false,
                showConstitutionTranslation = preferences[SHOW_CONSTITUTION_TRANSLATION] ?: true,
                lastOpenedCourseId = preferences[LAST_OPENED_COURSE_ID],
            )
            transform(current).also { updated ->
                preferences[QUIZ_DIRECTION] = updated.quizDirection.name
                preferences[QUIZ_SIZE] = updated.quizSize
                preferences[SHOW_KNOWN] = updated.showKnownWords
                preferences[SHOW_CONSTITUTION_TRANSLATION] = updated.showConstitutionTranslation
                updated.lastOpenedCourseId?.let { preferences[LAST_OPENED_COURSE_ID] = it }
            }
        }
    }

    suspend fun setLastOpenedCourseId(courseId: String) {
        context.settingsDataStore.edit { preferences ->
            preferences[LAST_OPENED_COURSE_ID] = courseId
        }
    }

    private companion object {
        val QUIZ_DIRECTION = stringPreferencesKey("quiz_direction")
        val QUIZ_SIZE = intPreferencesKey("quiz_size")
        val SHOW_KNOWN = booleanPreferencesKey("show_known")
        val SHOW_CONSTITUTION_TRANSLATION = booleanPreferencesKey("show_constitution_translation")
        val LAST_OPENED_COURSE_ID = stringPreferencesKey("last_opened_course_id")
    }
}

object SettingsStore {
    private var repository: AppSettingsRepository? = null
    fun initialize(context: Context) { repository = AppSettingsRepository(context.applicationContext) }
    fun repository(): AppSettingsRepository = requireNotNull(repository)
}
