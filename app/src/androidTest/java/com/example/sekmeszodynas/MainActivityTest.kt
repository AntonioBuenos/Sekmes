package com.example.sekmeszodynas

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.Assert.assertEquals
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun systemBackReturnsFromThemeSelectionToDashboard() {
        composeRule.onNodeWithText("Sekmes").performClick()
        composeRule.onNodeWithText("Словарь").performClick()
        composeRule.onNodeWithText("Словарь: Sekmes").assertIsDisplayed()

        composeRule.activityRule.scenario.onActivity {
            it.onBackPressedDispatcher.onBackPressed()
        }

        composeRule.onNodeWithText("Пройти тест").assertIsDisplayed()
    }

    @Test
    fun quizProgressSurvivesActivityRecreation() {
        val theme = THEMES_DATA.values.first()
        composeRule.onNodeWithText("Sekmes").performClick()
        composeRule.onNodeWithText("Пройти тест").performClick()
        composeRule.onNodeWithText("${theme.title} (${theme.words.size})").performClick()

        val currentWord = theme.words.first { word ->
            composeRule.onAllNodesWithText(word.ru).fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onNodeWithText(currentWord.lt).performClick()

        val expectedProgress = "1 / ${theme.words.size} изучено"
        composeRule.waitUntil(timeoutMillis = 3_000) {
            composeRule.onAllNodesWithText(expectedProgress).fetchSemanticsNodes().isNotEmpty()
        }

        composeRule.activityRule.scenario.recreate()

        composeRule.onNodeWithText(expectedProgress).assertIsDisplayed()
    }

    @Test
    fun constitutionPreambleShowsTranslationAndCanHideIt() {
        composeRule.onNodeWithText("⚖️ Конституция Литвы").performClick()
        composeRule.onNodeWithText("1. Основы государства").performClick()
        composeRule.onNodeWithText("Преамбула").performClick()

        composeRule.onNodeWithText("LIETUVIŲ TAUTA").assertIsDisplayed()
        composeRule.onNodeWithText("ЛИТОВСКИЙ НАРОД").assertIsDisplayed()

        composeRule.onNodeWithText("Скрыть русский перевод").performClick()

        assertEquals(0, composeRule.onAllNodesWithText("ЛИТОВСКИЙ НАРОД").fetchSemanticsNodes().size)
    }

    @Test
    fun neDienosCourseShowsThemesAndAudio() {
        composeRule.onNodeWithText("Nė dienos be lietuvių kalbos").performClick()
        composeRule.onNodeWithText("Словарь").performClick()
        composeRule.onNodeWithText("Все темы курса (588)").assertIsDisplayed()
        composeRule.onNodeWithText("Выберите урок:").assertIsDisplayed()

        composeRule.activityRule.scenario.onActivity { it.onBackPressedDispatcher.onBackPressed() }
        composeRule.onNodeWithText("Аудиокурс").performClick()
        composeRule.onNodeWithText("Глава 1: 1 skyrius").assertIsDisplayed()
    }

    @Test
    fun grammarCardsSupportCategorySearchAndDetail() {
        composeRule.onNodeWithText("🧩 Грамматические карточки").performClick()
        composeRule.onAllNodesWithText("Глаголы")[0].performClick()
        composeRule.onNodeWithText("Прошедшее время: -o tipas").performClick()
        composeRule.onNodeWithText("ragauti, ragauja, ragavo").assertIsDisplayed()
        composeRule.onNodeWithText("Следующая →").performClick()
        composeRule.onNodeWithText("su + Įn. (Inst.) kuo?").assertIsDisplayed()
    }
}
