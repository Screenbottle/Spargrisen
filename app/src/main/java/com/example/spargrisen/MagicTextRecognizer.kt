package com.example.spargrisen


import android.graphics.Bitmap
import android.media.Image
import android.util.Log
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.util.regex.Pattern
import java.util.regex.Pattern.MULTILINE

class MagicTextRecognizer(private val onTextFound: (String) -> Unit)  {

    fun recognizeImageText(image: Image, rotationDegrees: Int, onResult: (Boolean) -> Unit) {
        val inputImage = InputImage.fromMediaImage(image, rotationDegrees)
        TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
            .process(inputImage)
            .addOnSuccessListener { recognizedText ->
                processTextFromImage(recognizedText)
                onResult(true)
            }
            .addOnFailureListener { error ->
                Log.d(TAG, "Failed to recognize image text")
                error.printStackTrace()
                onResult(false)
            }
    }

    private fun processTextFromImage(text: Text) {
        val regex = Pattern.compile("\$?(?:(?:[1-9][0-9]{0,2})(?:,[0-9]{3})+|[1-9][0-9]*|0)(?:[.,][0-9][0-9]?)?(?![0-9]+)", MULTILINE)

        text.textBlocks.joinToString {
            it.text.lines().joinToString(" ")
        }.let {
            if (it.isNotBlank()) {

                //val matcher = regex.matcher(it)
                //if (matcher.find()) {
                //    matcher.
                //}
                Log.d(TAG, "TextRecognizer: $it")
                onTextFound(it)
            }
        }
    }


    companion object {
        private val TAG = MagicTextRecognizer::class.java.name
    }
}