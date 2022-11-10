package com.example.spargrisen

import android.util.Log
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap


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
        var purchaseDateString : String = "",
    )

    var purchasesList: MutableList<Purchases> = mutableListOf()

    init {
        getPurchaseData()
    }


    val dbRef = FirebaseFirestore.getInstance().collection("users").document(getUID())
        .collection("values")
        //.whereGreaterThanOrEqualTo("purchaseDate", convertDateToMillis("22/02/2020")!!)
       // .whereLessThanOrEqualTo("purchaseDate", convertDateToMillis("22/02/2023")!!)

    //Get current year
    fun getCurrentYear(): String {
        return Calendar.getInstance().get(Calendar.YEAR).toString()
    }

    fun getYear(a: String): String {
        return a.substring(6, 10)
    }

    fun convertYearToMillis(year: String): Long? {
        val sdf = SimpleDateFormat("yyyy")
        val date = sdf.parse(year)
        return date?.time
    }

    //Get current users UID
    //Example: val currentUID = getUID()
    fun getUID(): String {
        val auth = Firebase.auth

        return auth.uid.toString()
    }

    //Use this to get full name from db
    //Example: val fName = getFullName(getUID())

    fun getUserName(uid : String) : String? {
        val db = FirebaseFirestore.getInstance()
        var userName : String? = ""

        db.collection("users").document(uid)
            .get()
            .addOnSuccessListener { document ->
                userName = document.getString("fName")
                Log.d("DB", userName.toString())
            }

        return userName
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


    //Use this function to convert a string date to millis
    //Example: val dateInMillis = convertDateToMillis("02/03/2022")
    fun convertDateToMillis (date : String) : Long? {
        val simpleDate = SimpleDateFormat("dd/M/yyyy")

        val mDate: java.util.Date? = simpleDate.parse(date) // Convert string into Date
        val timeInMilliseconds : Long? = mDate?.time // Convert mDate to millis and put the value in a long

        Log.d("DB", timeInMilliseconds.toString())

        return timeInMilliseconds
    }


    fun getPurchaseData() {
            runBlocking {
                val itemsFromDb: List<Purchases> =
                    FirebaseFirestore.getInstance().collection("users").document(getUID())
                        .collection("values")
                        .get()
                        .await()
                        .documents
                        .map { itemDocument ->
                            Purchases(
                                purchaseName = itemDocument.getString("purchaseName")!!,
                                purchaseAmount = itemDocument.getLong("purchaseAmount")!!,
                                purchaseCost = itemDocument.getLong("purchaseCost")!!,
                                purchaseDate = itemDocument.getLong("purchaseDate")!!,
                                purchaseDateString = itemDocument.getString("purchaseDateString")!!,
                            )
                        }
                purchasesList.addAll(itemsFromDb)
            }
        }
}

