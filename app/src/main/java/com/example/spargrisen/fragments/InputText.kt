package com.example.spargrisen.fragments

import com.google.firebase.firestore.DocumentId


data class InputText (
   // @DocumentId var documentId: String? = null,
    var itemName : String? = null,
    var category : String? = null,
    var price :  Double = 0.00,
    var date : String? = null,
    var done : Boolean = false
)