package com.example.spargrisen

import android.graphics.RectF

data class AnalyzerData (
    val onTextFound: (String) -> Unit,
    val rectF: RectF

    )