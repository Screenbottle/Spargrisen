package com.example.spargrisen


import android.Manifest
import android.content.pm.PackageManager
import android.graphics.*
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.window.layout.WindowMetricsCalculator
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_camera.*
import kotlinx.android.synthetic.main.activity_camera.overlay
import com.example.spargrisen.databinding.ActivityCameraBinding


class CameraActivity : AppCompatActivity() {

    //private lateinit var surfaceView: SurfaceView
    private lateinit var holder: SurfaceHolder
    private lateinit var viewBinding: ActivityCameraBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)

        viewBinding = ActivityCameraBinding.inflate(layoutInflater)


        if (isAllPermissionsGranted) startCamera() else requestPermissions()

        overlay.apply {
            setZOrderOnTop(true)
            holder.setFormat(PixelFormat.TRANSPARENT)
            holder.addCallback(object : SurfaceHolder.Callback {
                override fun surfaceChanged(
                    holder: SurfaceHolder,
                    format: Int,
                    width: Int,
                    height: Int
                ) {
                }

                override fun surfaceDestroyed(holder: SurfaceHolder) {
                }

                override fun surfaceCreated(holder: SurfaceHolder) {
                    drawOverlay(holder,
                        DESIRED_HEIGHT_CROP_PERCENT,
                        DESIRED_WIDTH_CROP_PERCENT
                    )
                }

            })
        }



        //Create the bounding box
        //surfaceView = findViewById(R.id.overlay)
        //surfaceView.setZOrderOnTop(true)
        //holder = surfaceView.holder
        //holder.setFormat(PixelFormat.TRANSPARENT)

    }

    companion object {
        private val TAG = CameraActivity::class.java.name
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
        const val DESIRED_WIDTH_CROP_PERCENT = 8
        const val DESIRED_HEIGHT_CROP_PERCENT = 74
    }

    private val cameraAdapter = CameraAdapter {
        Log.d(TAG, "Text Found: $it")
    }

    private val isAllPermissionsGranted get() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun drawScannedText(
        holder: SurfaceHolder,
        text: String
    ) {
        val canvas = holder.lockCanvas()
        val textPaint = Paint()

        val surfaceWidth = holder.surfaceFrame.width()
        val surfaceHeight = holder.surfaceFrame.height()

        textPaint.color = Color.WHITE
        textPaint.textSize = 50F

        val textBounds = Rect()
        textPaint.getTextBounds(text, 0, text.length, textBounds)
        val textX = (surfaceWidth - textBounds.width()) / 2f
        val textY = textBounds.height() + surfaceHeight / 4f
        canvas.drawText(text, textX, textY, textPaint)
    }

    private fun drawOverlay(
        holder: SurfaceHolder,
        heightCropPercent: Int,
        widthCropPercent: Int,
    ) {
        val canvas = holder.lockCanvas()
        val bgPaint = Paint().apply {
            alpha = 140
        }
        canvas.drawPaint(bgPaint)
        val rectPaint = Paint()
        rectPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
        rectPaint.style = Paint.Style.FILL
        rectPaint.color = Color.WHITE
        val outlinePaint = Paint()
        outlinePaint.style = Paint.Style.STROKE
        outlinePaint.color = Color.WHITE
        outlinePaint.strokeWidth = 4f
        val surfaceWidth = holder.surfaceFrame.width()
        val surfaceHeight = holder.surfaceFrame.height()

        val cornerRadius = 25f
        // Set rect centered in frame
        val rectTop = surfaceHeight * heightCropPercent / 2 / 100f
        val rectLeft = surfaceWidth * widthCropPercent / 2 / 100f
        val rectRight = surfaceWidth * (1 - widthCropPercent / 2 / 100f)
        val rectBottom = surfaceHeight * (1 - heightCropPercent / 2 / 100f)
        val rect = RectF(rectLeft, rectTop, rectRight, rectBottom)
        canvas.drawRoundRect(
            rect, cornerRadius, cornerRadius, rectPaint
        )
        canvas.drawRoundRect(
            rect, cornerRadius, cornerRadius, outlinePaint
        )
        val textPaint = Paint()
        textPaint.color = Color.WHITE
        textPaint.textSize = 50F

        val overlayText = getString(R.string.overlay_help)
        val textBounds = Rect()
        textPaint.getTextBounds(overlayText, 0, overlayText.length, textBounds)
        val textX = (surfaceWidth - textBounds.width()) / 2f
        val textY = rectBottom + textBounds.height() + 15f // put text below rect and 15f padding
        canvas.drawText(getString(R.string.overlay_help), textX, textY, textPaint)


        holder.unlockCanvasAndPost(canvas)
    }

    private fun requestPermissions() = ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (isAllPermissionsGranted) {
                startCamera()
                drawOverlay(holder,
                    DESIRED_HEIGHT_CROP_PERCENT,
                    DESIRED_WIDTH_CROP_PERCENT
                )
            } else {
                Snackbar.make(viewFinder, "Camera permission not granted. \nCannot perform magic ritual.", Snackbar.LENGTH_LONG).setAction("Retry") {
                    requestPermissions()
                }.show()
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun startCamera() = cameraAdapter.startCamera(this, this, viewFinder.surfaceProvider)

    override fun onDestroy() {
        super.onDestroy()
        cameraAdapter.shutdown()
    }

}