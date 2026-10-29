package com.instantpersian.screentranslator.ocr

import android.graphics.Bitmap
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

object MlKitOcrEngine {
    private val recognizer by lazy { TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS) }

    suspend fun recognizeText(bitmap: Bitmap): String = suspendCancellableCoroutine { continuation ->
        try {
            recognizer.process(InputImage.fromBitmap(bitmap, 0))
                .addOnSuccessListener { result ->
                    val text = result.textBlocks
                        .sortedWith(compareBy({ it.boundingBox?.top ?: 0 }, { it.boundingBox?.left ?: 0 }))
                        .map { it.text.trim() }
                        .filter { it.isNotEmpty() }
                        .joinToString("\n\n")
                    if (continuation.isActive) continuation.resume(text)
                }
                .addOnFailureListener {
                    if (continuation.isActive) continuation.resume("")
                }
        } catch (_: Exception) {
            if (continuation.isActive) continuation.resume("")
        }
    }
}
