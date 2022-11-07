package com.example.spargrisen.fragments

import android.widget.Spinner
import com.google.firebase.firestore.DocumentId


data class InputText (
   @DocumentId var documentId: String? = null,
    var purchaseName : String? = null,
    var purchaseCategory : String? = null,
    var purchaseCost :  Double = 0.00,
    var purchaseDate : String? = null,
   var kategorival : ArrayList<Spinner> // fel??
)