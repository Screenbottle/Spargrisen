package com.example.spargrisen


import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.*
import android.os.Bundle
import android.util.Log
import android.view.SurfaceHolder
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_camera.*
import kotlinx.android.synthetic.main.activity_camera.overlay
import com.example.spargrisen.databinding.ActivityCameraBinding


class CameraActivity : AppCompatActivity() {

    //private lateinit var surfaceView: SurfaceView
    private lateinit var holder: SurfaceHolder
    private lateinit var viewBinding: ActivityCameraBinding
    lateinit var scanResult: String

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
        val cancelButton = findViewById<Button>(R.id.cancelButton)
        cancelButton.setOnClickListener {
            Log.d("!!!", "pushed")
            finish()
        }

        val confirmButton = findViewById<Button>(R.id.confirmButton)

        confirmButton.setOnClickListener {
            var text = scanResult
            // replaces any , with . so that the text can be converted from a string into a double
            if (!text.equals(R.string.placeholder.toString(), true)) {
                text = text.replace(",", ".")
                val scannedFloat = text.toDouble()
                val intent = Intent(this, ManualInput::class.java)
                intent.putExtra("ScannedMoney", scannedFloat)
                startActivity(intent)
            }
        }


    }


    private val cameraAdapter = CameraAdapter {
        setScannedText(it)
    }

    companion object {
        private val TAG = CameraActivity::class.java.name
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
        const val DESIRED_WIDTH_CROP_PERCENT = 8
        const val DESIRED_HEIGHT_CROP_PERCENT = 74
    }


    private val isAllPermissionsGranted get() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun setScannedText(scannedText: String) {
        // this function is passed to the camera adapter and then to the image analyzer, and then to
        // the text recognizer, it simply places the scanned text into a view
        val textView = findViewById<TextView>(R.id.scannedText)
        textView.text = scannedText
        scanResult = scannedText
        Log.d("!!!", scanResult)
    }

    // draws the rectangle overlay
    private fun drawOverlay(
        holder: SurfaceHolder,
        heightCropPercent: Int,
        widthCropPercent: Int
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
        val rectTop = surfaceHeight * heightCropPercent / 2 / 100f
        val rectLeft = surfaceWidth * widthCropPercent / 2 / 100f
        val rectRight = surfaceWidth * (1 - widthCropPercent / 2 / 100f)
        val rectBottom = surfaceHeight * (1 - heightCropPercent / 2 / 100f)

        // Set rect centered in frame
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

    // activity needs permission in order to use the camera, requests them from the user if it's not already given
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

    override fun onRestart() {
        super.onRestart()
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraAdapter.shutdown()
    }

}