package com.instantpersian.screentranslator.data

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

object TranslationHistoryManager {
    private const val PREFS = "translation_history"
    private const val KEY_RECORDS = "records"
    private const val MAX_RECORDS = 30
    private val memoryRecords = mutableListOf<TranslationRecord>()

    @Synchronized
    fun addRecord(record: TranslationRecord) {
        memoryRecords.add(0, record)
        while (memoryRecords.size > MAX_RECORDS) memoryRecords.removeAt(memoryRecords.lastIndex)
    }

    @Synchronized
    fun addRecord(context: Context, record: TranslationRecord) {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val old = try { JSONArray(prefs.getString(KEY_RECORDS, "[]")) } catch (_: Exception) { JSONArray() }
        val array = JSONArray()
        array.put(JSONObject().apply {
            put("sourceText", record.sourceText)
            put("translatedText", record.translatedText)
            put("sourceLang", record.sourceLang)
            put("targetLang", record.targetLang)
            put("extractionMethod", record.extractionMethod)
            put("createdAtMillis", record.createdAtMillis)
        })
        for (i in 0 until old.length().coerceAtMost(MAX_RECORDS - 1)) {
            old.optJSONObject(i)?.let { array.put(it) }
        }
        prefs.edit().putString(KEY_RECORDS, array.toString()).apply()
    }

    @Synchronized
    fun recent(): List<TranslationRecord> = memoryRecords.toList()
}
