package com.instantpersian.screentranslator.data

data class TranslationRecord(
    val sourceText: String,
    val translatedText: String,
    val sourceLang: String,
    val targetLang: String = "fa",
    val extractionMethod: String,
    val createdAtMillis: Long = System.currentTimeMillis()
)
