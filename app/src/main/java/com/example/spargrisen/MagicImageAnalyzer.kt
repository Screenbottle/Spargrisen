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
        //val bitmap = BitmapUtils.getBitmap(imageProxy)

        //if(bitmap != null) {

            //val croppingRect = createRect(bitmap)
            //val croppedImage = cropImage(bitmap, croppingRect)

            textRecognizer.recognizeImageText(image, imageProxy.imageInfo.rotationDegrees) {
                imageProxy.close()
            }
        //}
    }

    private fun cropImage(bitmap: Bitmap, croppingRect: Rect): Bitmap =
        Bitmap.createBitmap(
            bitmap,
            ((bitmap.width - croppingRect.width()) / 2),
            ((bitmap.height - croppingRect.height()) / 2),
            croppingRect.width(),
            croppingRect.height()
    )


    private fun createRect(bitmap: Bitmap): Rect {

        val heightCropPercent = CameraActivity.DESIRED_HEIGHT_CROP_PERCENT
        val widthCropPercent = CameraActivity.DESIRED_WIDTH_CROP_PERCENT

        // Set rect centered in frame
        val rectTop = bitmap.height * heightCropPercent / 2
        val rectLeft = bitmap.width * widthCropPercent / 2
        val rectRight = bitmap.width * (1 - widthCropPercent / 2)
        val rectBottom = bitmap.height * (1 - heightCropPercent / 2)

        return Rect(rectLeft, rectTop, rectRight, rectBottom)

    }

    private fun badGetBitmap(image: Image): Bitmap {
        val planes: Array<Image.Plane> = image.planes
        val yBuffer: ByteBuffer = planes[0].buffer
        val uBuffer: ByteBuffer = planes[1].buffer
        val vBuffer: ByteBuffer = planes[2].buffer
        val ySize: Int = yBuffer.remaining()
        val uSize: Int = uBuffer.remaining()
        val vSize: Int = vBuffer.remaining()
        val nv21 = ByteArray(ySize + uSize + vSize)
        //U and V are swapped
        yBuffer.get(nv21, 0, ySize)
        vBuffer.get(nv21, ySize, vSize)
        uBuffer.get(nv21, ySize + vSize, uSize)
        val yuvImage = YuvImage(nv21, ImageFormat.NV21, image.width, image.height, null)
        val out = ByteArrayOutputStream()
        yuvImage.compressToJpeg(Rect(0, 0, yuvImage.width, yuvImage.height), 75, out)
        val imageBytes: ByteArray = out.toByteArray()
        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
    }

}