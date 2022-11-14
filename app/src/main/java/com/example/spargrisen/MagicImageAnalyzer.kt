package com.example.spargrisen

import android.graphics.*
import android.media.Image
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer
import com.example.spargrisen.utils.BitmapUtils




class MagicImageAnalyzer(onTextFound: (String) -> Unit)  : ImageAnalysis.Analyzer {
    private val textRecognizer = MagicTextRecognizer(onTextFound)

    @ExperimentalGetImage
    override fun analyze(imageProxy: ImageProxy) {
        val image = imageProxy.image ?: return

        textRecognizer.recognizeImageText(image, imageProxy.imageInfo.rotationDegrees) {
            imageProxy.close()
        }
    }

}