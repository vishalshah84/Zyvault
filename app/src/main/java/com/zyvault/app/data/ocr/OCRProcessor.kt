package com.zyvault.app.data.ocr

import android.content.Context
import android.net.Uri
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.tasks.await

class OCRProcessor(private val context: Context) {
    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    suspend fun processImage(uri: Uri): Map<String, String> {
        val image = InputImage.fromFilePath(context, uri)
        val result = recognizer.process(image).await()
        
        val fullText = result.text
        val fields = mutableMapOf<String, String>()
        
        // Simple heuristic for extraction
        if (fullText.contains("Driver License", ignoreCase = true) || fullText.contains("DL", ignoreCase = true)) {
            fields["type"] = "Driver's License"
            extractDLFields(fullText, fields)
        } else if (fullText.contains("Passport", ignoreCase = true)) {
            fields["type"] = "Passport"
            extractPassportFields(fullText, fields)
        } else {
            fields["type"] = "Document"
        }
        
        fields["rawText"] = fullText
        return fields
    }

    private fun extractDLFields(text: String, fields: MutableMap<String, String>) {
        // Mock extraction logic - in real world use regex
        val lines = text.split("\n")
        lines.forEach { line ->
            if (line.contains("EXP", ignoreCase = true)) {
                fields["expiry"] = line.substringAfter("EXP").trim()
            }
            if (line.contains("LN", ignoreCase = true) || line.contains("Name", ignoreCase = true)) {
                fields["name"] = line.substringAfter(":").trim()
            }
        }
    }

    private fun extractPassportFields(text: String, fields: MutableMap<String, String>) {
        // Mock extraction logic
    }
}
