package com.example.sekmeszodynas

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.Before
import org.junit.Assert.assertEquals
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun waitForSplashToFinish() {
        composeRule.waitUntil(timeoutMillis = 5_000) {
            composeRule.onAllNodesWithText("ЛИТОВСКИЙ • КАЖДЫЙ ДЕНЬ").fetchSemanticsNodes().isEmpty()
        }
    }

    @Test
    fun visualCardsShowLithuanianWordsAndNavigate() {
        composeRule.onAllNodesWithText("Словарь")[1].performClick()
        composeRule.onNodeWithText("Смотреть карточки", substring = true).performClick()

        composeRule.onNodeWithText("katė").assertIsDisplayed()
        composeRule.onNodeWithContentDescription("Следующая карточка").performClick()
        composeRule.onNodeWithText("šuo").assertIsDisplayed()
    }

    @Test
    fun topLevelNavigationOpensMoreAndReturnsToHome() {
        composeRule.onNodeWithText("Ещё").performClick()
        composeRule.onNodeWithText("Настройки").assertIsDisplayed()

        composeRule.onNodeWithText("Главная").performClick()
        composeRule.onNodeWithText("Продолжим учиться?").assertIsDisplayed()
    }

    @Test
    fun systemBackReturnsFromThemeSelectionToCourse() {
        composeRule.onNodeWithText("Курсы").performClick()
        composeRule.onAllNodesWithText("Sekmes")[0].performClick()
        composeRule.onNodeWithText("Учить слова").performClick()
        composeRule.onNodeWithText("Все темы курса").assertIsDisplayed()

        composeRule.activityRule.scenario.onActivity {
            it.onBackPressedDispatcher.onBackPressed()
        }

        composeRule.onNodeWithText("Учить слова").assertIsDisplayed()
    }

    @Test
    fun quizScreenUsesCurrentProgressCopy() {
        composeRule.onNodeWithText("Курсы").performClick()
        composeRule.onAllNodesWithText("Sekmes")[0].performClick()
        composeRule.onNodeWithText("Пройти тест").performClick()
        composeRule.onNodeWithText("Все темы курса").performClick()
        composeRule.onNodeWithText("Вопрос", substring = true).assertIsDisplayed()
    }

    @Test
    fun constitutionPreambleShowsTranslationAndCanHideIt() {
        composeRule.onNodeWithText("Ещё").performClick()
        composeRule.onNodeWithText("Конституция Литвы").performClick()
        composeRule.onNodeWithText("1. Основы государства").performClick()
        composeRule.onNodeWithText("Преамбула").performClick()

        composeRule.onNodeWithText("LIETUVIŲ TAUTA").assertIsDisplayed()
        composeRule.onNodeWithText("ЛИТОВСКИЙ НАРОД").assertIsDisplayed()

        composeRule.onNodeWithText("Скрыть русский перевод").performClick()

        assertEquals(0, composeRule.onAllNodesWithText("ЛИТОВСКИЙ НАРОД").fetchSemanticsNodes().size)
    }

    @Test
    fun neDienosCourseShowsThemesAndAudio() {
        composeRule.onNodeWithText("Курсы").performClick()
        composeRule.onAllNodesWithText("Nė dienos be lietuvių kalbos")[0].performClick()
        composeRule.onNodeWithText("Учить слова").performClick()
        composeRule.onNodeWithText("Все темы курса").assertIsDisplayed()
        composeRule.onNodeWithText("Выберите урок").assertIsDisplayed()

        composeRule.activityRule.scenario.onActivity { it.onBackPressedDispatcher.onBackPressed() }
        composeRule.onNodeWithText("Слушать аудио").performClick()
        composeRule.onNodeWithText("Глава 1").assertIsDisplayed()
        composeRule.onNodeWithText("1 skyrius").assertIsDisplayed()
    }

    @Test
    fun grammarCardsSupportCategorySearchAndDetail() {
        composeRule.onNodeWithText("Ещё").performClick()
        composeRule.onNodeWithText("Грамматика").performClick()
        composeRule.onAllNodesWithText("Глаголы")[0].performClick()
        composeRule.onNodeWithText("Прошедшее время: -o tipas").performClick()
        composeRule.onNodeWithText("ragauti, ragauja, ragavo").assertIsDisplayed()
        composeRule.onNodeWithText("Следующая").performClick()
        composeRule.onNodeWithText("su + Įn. (Inst.) kuo?").assertIsDisplayed()
    }
}
