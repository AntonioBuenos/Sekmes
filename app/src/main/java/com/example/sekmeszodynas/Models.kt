package com.example.sekmeszodynas

data class Word(
    val ru: String,
    val lt: String,
    val type: String,
    val id: String = "$lt::$ru"
)

data class Theme(
    val id: String,
    val title: String,
    val words: List<Word>
)
