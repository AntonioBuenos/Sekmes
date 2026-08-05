package com.example.sekmeszodynas

data class Word(
    val ru: String,
    val lt: String,
    val type: String,
    val id: String,
    val sourceIds: Set<String> = emptySet(),
)

data class Theme(
    val id: String,
    val title: String,
    val words: List<Word>
)
