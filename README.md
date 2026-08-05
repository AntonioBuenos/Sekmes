# Sėkmės: žodynas 🇱🇹

**Sėkmės: žodynas** is an offline native Android application for Russian-speaking learners of Lithuanian. It brings together multiple courses, a Constitution module, a global vocabulary, personal words, tests, audio, and compact grammar reference cards.

This project is a modern Jetpack Compose migration of the original "Sėkmės" vocabulary tool.

## ✨ Features

- **Courses**: Sėkmės (21 themes, 1,039 words, 275 bundled audio tracks) and *Nė dienos be lietuvių kalbos* (12 themes, 588 words, 147 bundled audio tracks).
- **Vocabulary and quizzes**: Search and filter by course, learning status, and part of speech; choose translation direction and a 10/20/50/all-word test session.
- **Constitution of Lithuania**: Parallel Lithuanian/Russian reading, terminology and quizzes by block. Interactive terms adapt to light and dark themes.
- **Personal learning data**: Persistent word statuses, answer statistics, app settings, and a personal-word list with editing and duplicate detection.
- **Grammar cards**: JSON-based compact rules with tables, examples, hints, category filters, search, favorites, and next/previous navigation.
- **Offline audio**: A shared Media3/ExoPlayer player for all course audio.

## 🛠 Tech Stack

- **Language**: [Kotlin](https://kotlinlang.org/)
- **UI Framework**: [Jetpack Compose](https://developer.android.com/jetpack/compose)
- **Architecture**: Single-activity Jetpack Compose application with Navigation Compose, Room, DataStore, and asset-based content catalogs
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

1. **Choose a course or module** on the home screen.
2. **Learn or review** in a course dictionary, the global dictionary, or your personal list.
3. **Take a quiz** for a course, lesson, global selection, or selected part of speech.
4. **Listen**: open a course audio section, expand a chapter, and tap a track to play or pause it.
5. **Manage progress** in Settings: restore hidden known words or change their status individually or in bulk.

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

Built-in content is stored locally in `app/src/main/assets/content/`:

- course and dictionary metadata in JSON;
- grammar cards in `grammar-cards.json`;
- Constitution content in its course JSON;
- audio metadata in `audio-catalog.json`, with MP3 files bundled in `res/raw`.

User progress and personal words are local Room data; display and quiz preferences are stored in DataStore.

## 📄 License

This project is for educational purposes. 

---
*Created by [Anton Smirnov](https://github.com/AntonioBuenos)*
