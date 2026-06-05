# SekmesZodynas App Migration

This project is a migration of the "Sėkmės: žodynas" Lithuanian-Russian vocabulary learning application from HTML/JS to native Android with Jetpack Compose.

## Key Components

- **Models.kt**: Defines `Word` and `Theme` data structures.
- **Data.kt**: Contains the complete vocabulary (21 themes) ported from the original source.
- **MainActivity.kt**: Implements the main UI and quiz logic using Jetpack Compose.

## Screens

1. **Theme Selection**: Allows users to choose one of the 21 categorized themes.
2. **Quiz**: Interactive multiple-choice quiz (1 correct answer + 3 distractors). Distractors are intelligently picked based on word type (verb, noun, etc.).
3. **Results**: Shows final score and a list of mistakes made during the quiz.

## Development Progress

- [x] Vocabulary Porting
- [x] Data Modeling
- [x] Theme Selection UI
- [x] Quiz Logic & Distractor Generation
- [x] Results & Mistake Tracking
