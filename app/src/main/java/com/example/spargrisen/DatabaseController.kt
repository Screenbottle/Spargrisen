package com.example.spargrisen

import android.provider.ContactsContract.Data
import android.util.Log
import com.google.android.gms.common.api.internal.LifecycleCallback
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat


/* Noteringar till teamet

Firestore struktur:
users -> UID -> values -> Document ID
         -> fName                    -> purchaseName : String
         -> email                    -> purchaseAmount : Number
                                     -> purchaseCost : Number
                                     -> purchaseDate : Number (millisekunder lagras i den här)
                                     -> purchaseDateString : String (tänker att den här kan användas om man ska printa datumet)

Tiden måste konverteras till millisekunder om den ska användas i en query i databasen då Firestore inte verkar kunna
jämföra timestamps, så tänkte att det lättaste blir att konvertera tiden till ett nummer som kan användas i en query.
Använd convertDateToMillis("DD/MM/YYYY") för att konvertera tid.

 */

class DatabaseController {

    data class Purchases(
        var purchaseName : String = "",
        var purchaseAmount : Long = 0,
        var purchaseCost : Long = 0,
        var purchaseDate : Long = 0,
        var purchaseDateString : String = ""
    )

    var purchaseList : MutableList<Purchases>? = mutableListOf()

    //Get current users UID
    //Example: val currentUID = getUID()
    fun getUID(): String {
        val auth = Firebase.auth

        return auth.uid.toString()
    }

    //Use this to get full name from db
    //Example: val fName = getFullName(getUID())
    fun getFullName(uid : String) : String? {
        val db = FirebaseFirestore.getInstance()
        var fullName : String? = ""

        db.collection("users").document(uid)
            .get()
            .addOnSuccessListener { document ->
                fullName = document.getString("fName")
                Log.d("DB", fullName.toString())
            }

        return fullName
    }

    //Use this to get email from db
    //Example: val email = getEmail(getUID())
    fun getEmail(uid : String) : String? {
        val db = FirebaseFirestore.getInstance()
        var email : String? = ""
        db.collection("users").document(uid)
            .get()
            .addOnSuccessListener { document ->
                email = document.getString("email")
                Log.d("DB", email.toString())
            }
        return email
    }

    //Used to register user to the Firestore db
    fun registerUserToFirestore(inputFullname : String, inputEmail : String) {
            val db = FirebaseFirestore.getInstance()
            val auth = Firebase.auth

            //Add data to firestore db
            val user: MutableMap<String, Any> = HashMap()
            user["fName"] = inputFullname
            user["email"] = inputEmail

            val documentReference = db.collection("users").document(getUID())

            documentReference.set(user).addOnSuccessListener {
                Log.d("DB", "onSuccess: user Profile created: $userID")
            }.addOnFailureListener { e -> Log.d("DB", "onFailure: $e") }
    }

    // Use this to add input to the database
    // Example: addInputData("Burger king", 1, 363, "01/01/2003")
    fun addInputData(purchaseName : String, purchaseAmount : Long, purchaseCost : Long, purchaseDate : String) { // purchaseDate = "DD/MM/YEAR"
        val db = FirebaseFirestore.getInstance()

        val purchaseDateToMillis = convertDateToMillis(purchaseDate) // Convert timestamp to millis so we can query it in firestore

        val inputData: MutableMap<Any, Any> = HashMap()
        inputData["purchaseName"] = purchaseName
        inputData["purchaseAmount"] = purchaseAmount
        inputData["purchaseCost"] = purchaseCost
        inputData["purchaseDate"] = purchaseDateToMillis!!
        inputData["purchaseDateString"] = purchaseDate

        val inputRef = db.collection("users").document(getUID()).collection("values").document()

        inputRef.set(inputData).addOnSuccessListener {
            Log.d("DB", "Input added")
        }.addOnFailureListener {
            Log.d("DB", "Input failed")
        }
    }

    //Use this function to get documents from the DB between specific dates
    //Example: documentsBetweenDates("01/02/2022", "01/02/2023)
    fun documentsBetweenDates(date1 : String, date2 : String) {
        val db = FirebaseFirestore.getInstance()
        var localPurchaseList: MutableList<Purchases>? = null

        db.collection("users").document(getUID())
            .collection("values")
            .whereGreaterThanOrEqualTo("purchaseDate", convertDateToMillis(date1)!!)
            .whereLessThanOrEqualTo("purchaseDate", convertDateToMillis(date2)!!)
            .get()
            .addOnSuccessListener { document ->
                for (documents in document) {
                    localPurchaseList = (document.toObjects(Purchases::class.java))
                }
                purchaseList = localPurchaseList
                Log.d("DB", localPurchaseList.toString())
            }

        Log.d("DBC", purchaseList.toString())


    }

    fun getTotalCost(date1 : String, date2 : String) {
        var totalCost: Int = 0
        GlobalScope.launch(Dispatchers.IO) {
            documentsBetweenDates(date1, date2)

            for(i in purchaseList!!.indices) {

                totalCost += purchaseList!![i].purchaseCost.toInt()

            }
            Log.d("tag4", totalCost.toString())

        }
    }

    //Use this function to convert a string date to millis
    //Example: val dateInMillis = convertDateToMillis("02/03/2022")
    fun convertDateToMillis (date : String) : Long? {
        val simpleDate = SimpleDateFormat("dd/M/yyyy")

        val mDate: java.util.Date? = simpleDate.parse(date) // Convert string into Date
        val timeInMilliseconds : Long? = mDate?.time // Convert mDate to millis and put the value in a long

        Log.d("DB", timeInMilliseconds.toString())

        return timeInMilliseconds
    }



}
