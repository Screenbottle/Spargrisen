package com.example.spargrisen


import android.media.Image
import android.util.Log
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.util.regex.Pattern.*


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

        //Regex magic that extracts the important numbers
        val regex = "(?<=^TOTAL\\,|TOTALT\\,|ATT BETALA\\,|SUMMA\\,|ARTIKLAR\\)\\,)\\s*(\\d+\\,?\\d*)"

        val pattern = compile(regex, MULTILINE)

        text.textBlocks.joinToString {
            it.text.lines().joinToString(" ")
        }.let {
            if (it.isNotBlank()) {
                val matcher = pattern.matcher(it)
                if (matcher.find()) {
                    // displays the scanned number to the user with the function that was passed along as a constructor
                    Log.d(TAG, "TextRecognizer: ${matcher.group(1)}")
                    matcher.group(0)?.let { it1 -> onTextFound(it1) }
                }
            }
        }
    }


    companion object {
        private val TAG = MagicTextRecognizer::class.java.name
    }
}