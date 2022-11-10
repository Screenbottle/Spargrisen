package com.example.spargrisen.utils

import android.util.Log
import com.gkemon.XMLtoPDF.PdfGeneratorListener
import com.gkemon.XMLtoPDF.model.FailureResponse
import com.gkemon.XMLtoPDF.model.SuccessResponse

class PDFListener: PdfGeneratorListener() {
    override fun onStartPDFGeneration() {

    }

    override fun onFinishPDFGeneration() {

    }

    override fun onFailure(failureResponse: FailureResponse?) {
        super.onFailure(failureResponse)
    }

    override fun showLog(log: String?) {
        super.showLog(log)
        if (log != null) {
            Log.d("PDF-Generation", log)
        }
    }

    override fun onSuccess(response: SuccessResponse?) {
        super.onSuccess(response)
    }
}