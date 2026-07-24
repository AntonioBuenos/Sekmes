# Sėkmės: žodynas 🇱🇹

**Sėkmės: žodynas** is a native Android application designed for Russian-speaking learners of the Lithuanian language. It provides a structured way to learn vocabulary across 21 essential themes through interactive quizzes and audio pronunciation.

This project is a modern Jetpack Compose migration of the original "Sėkmės" vocabulary tool.

## ✨ Features

- **21 Vocabulary Themes**: Covering everything from basic greetings to law, medicine, and holidays.
- **Interactive Quizzes**: Test your knowledge with multiple-choice questions.
- **Intelligent Distractors**: The app generates wrong answers based on the word type (noun, verb, etc.) to provide a meaningful challenge.
- **Offline Audio Course**: Includes 275 bundled conversation, reading, and listening tracks (powered by ExoPlayer).
- **Simplified Dictionary View**: Quick access to word pairs without unnecessary grammatical clutter.
- **Modern UI**: Clean, responsive interface built with Jetpack Compose and Material 3, featuring a color-coded dashboard.
- **Mistake Tracking**: Review your errors after each quiz to focus on difficult words.

## 🛠 Tech Stack

- **Language**: [Kotlin](https://kotlinlang.org/)
- **UI Framework**: [Jetpack Compose](https://developer.android.com/jetpack/compose)
- **Architecture**: Single-activity Jetpack Compose application with saveable in-app navigation state
- **Audio**: [Media3 / ExoPlayer](https://developer.android.com/guide/topics/media/exoplayer)
- **Styling**: Material 3 Design System

## 🚀 Getting Started

### Prerequisites

- Android Studio with JDK 21 support
- Android SDK 36 for compilation
- An API 26+ device or emulator for running the app

### Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/AntonioBuenos/Sekmes.git
   ```
2. Open the project in **Android Studio**.
3. Wait for Gradle sync to complete.
4. Run the app on an emulator or physical device.

## 📖 How to Use

1. **Select a Theme**: Choose from the dashboard (e.g., "Food", "Work", "Education").
2. **Learn/Review**: Use the Dictionary view to see the Lithuanian and Russian word pairs.
3. **Take a Quiz**: Tap "Test" to start. Choose the correct translation for the given word.
4. **Listen**: Open the offline audio course, expand a chapter, and tap a track to play or pause it.
5. **Check Results**: Complete the selected theme to see a first-attempt score and a list of words that required retries.

Wrong answers return to the question pool until they are answered correctly. The **All themes** mode uses unique word pairs so its question count and progress stay consistent.

## ✅ Validation

Run the local checks from the repository root:

```powershell
.\gradlew.bat testDebugUnitTest
.\gradlew.bat assembleDebug
.\gradlew.bat lintDebug
```

With an emulator or device connected, run the Compose UI tests:

```powershell
.\gradlew.bat connectedDebugAndroidTest
```

## 🌿 Development Workflow

`development` is the integration branch. Create each task branch from `development`; Codex-created task branches use the `codex/` prefix by default.

## 🗂 Data Structure

The vocabulary is stored locally in `Data.kt`. Each word contains:
- Russian translation
- Lithuanian translation (including inflections)
- Word type (Noun, Verb, Adj, etc.) for better quiz logic

## 📄 License

This project is for educational purposes. 

---
*Created by [Anton Smirnov](https://github.com/AntonioBuenos)*
