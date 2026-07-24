# Sekmes

## Scope and layout

`Sekmes` is a single-module native Android application for Russian-speaking learners of Lithuanian. It uses Kotlin, Jetpack Compose, Material 3, and Media3/ExoPlayer. The Android module is `app`; the application ID and namespace are `com.example.sekmeszodynas`.

- `app/src/main/java/com/example/sekmeszodynas/MainActivity.kt` contains the activity, in-memory screen navigation, dashboard, dictionary, quiz, results, audio UI, and quiz-option helpers.
- `Models.kt` defines `Word` and `Theme`; `Data.kt` is the local vocabulary source, grouped by theme ID.
- `AudioModels.kt` defines audiobook/chapter/track metadata. Track IDs map to files named `app/src/main/res/raw/audio_<id>.mp3`; keep the mapping valid when changing either side.
- `app/src/main/java/com/example/sekmeszodynas/ui/theme/` holds Compose color, typography, and app-theme definitions.
- Android resources are under `app/src/main/res/`; retain UTF-8 Lithuanian and Russian text exactly, including diacritics.
- Versions and dependencies are centralized in `gradle/libs.versions.toml`. Module settings are in `app/build.gradle.kts` (min SDK 26, target/compile SDK 36).

## Development rules

- Base integration work on `development`; create task branches from `development` using the `codex/` prefix unless the user requests another branch name.
- Prefer small, localized Compose changes. The app currently has no navigation library or view-model layer; do not introduce an architectural migration unless requested.
- Preserve vocabulary semantics: `Word` types (`n`, `v`, `adj`, `adv`, `other`) feed quiz distractor selection in `generateOptions`.
- Avoid renaming Android resources casually: resource names must be lowercase and audio names are resolved dynamically as `audio_<track.id>`.
- Do not edit generated or machine-specific files: `.gradle/`, `build/`, `.idea/`, or `local.properties`.
- Keep user-facing language consistent with the existing Russian/Lithuanian content.

## Validation

From the repository root on Windows, use:

```powershell
.\gradlew.bat testDebugUnitTest
.\gradlew.bat assembleDebug
.\gradlew.bat lintDebug
```

For UI, playback, or resource changes, also run `.\gradlew.bat connectedDebugAndroidTest` and check the affected audio track on an API 26+ emulator/device.

## Tests

- Local unit tests: `app/src/test/`.
- Instrumented tests: `app/src/androidTest/`; run them only with a connected emulator/device.
