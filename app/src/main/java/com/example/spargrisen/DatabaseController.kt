package com.example.spargrisen

import android.util.Log
import com.example.spargrisen.fragments.GraphFragment
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
        var purchaseName: String = "",
        var purchaseCost: Long = 0,
        var purchaseCategory: String = "",
        var purchaseDate: Long = 0,
        var purchaseDateString: String = "",
    )

    var purchasesList: MutableList<Purchases> = mutableListOf() // Här hamnar alla inköp som hämtas från databasen
    var periodList: MutableList<Purchases> = mutableListOf() // Här hamnar alla inköp i den tidsperioden användaren har valt

    var currentMonthSelected: Int = -1 // Startar på -1 så att perioden som visas vid start är från förra lönen till nästa
    var periodDate: String = getTodaysDate() // Används för att visa vilken period användaren befinner sig i

    init { // Körs när appen startas
        getPurchaseData() // Hämta alla köp från databasen och lägg i purchasesList
        setPeriodList() // Hämta alla köp från tidsperioden som användaren har valt och lägg i periodList
    }

    fun getPurchaseData() {
        runBlocking { //Coroutine så att funktionen väntar på att datan hämtats från databasen innan den fortsätter
            val itemsFromDb: List<Purchases> =
                FirebaseFirestore.getInstance().collection("users").document(getUID())
                    .collection("itemList")
                    .get()
                    .await()
                    .documents
                    .map { itemDocument ->
                        Purchases(
                            purchaseName = itemDocument.getString("Item")!!,
                            purchaseCost = itemDocument.getLong("Price")!!,
                            purchaseDate = itemDocument.getLong("purchaseDate")!!,
                            purchaseDateString = itemDocument.getString("purchaseDateString")!!,
                            purchaseCategory = itemDocument.getString("Category")!!
                        )
                    }
            purchasesList.addAll(itemsFromDb) // Lägg till alla köp i purchasesList
        }
    }

    fun getPeriodMinusMonths(date: String, minusMonths: Int): Long { // Hämta datumet minus/plus månader
        val sdf = SimpleDateFormat("dd/MM/yyyy")
        val date = sdf.parse(date)
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.add(Calendar.MONTH, minusMonths)

        return calendar.timeInMillis // returnera datumet i millisekunder
    }

    fun listBetweenDates(startDate: Long, endDate: Long): MutableList<Purchases> { // Hämta alla köp mellan två datum

        periodDate = "${convertMillisToDateString(startDate)} - ${convertMillisToDateString(endDate)}" // Ändra periodDate till den nya perioden

        val list = mutableListOf<Purchases>()
        for (i in purchasesList) {
            if (i.purchaseDate in startDate!!..endDate!!) {
                list.add(i)
            }
        }
        return list
    }

    fun setPeriodList() {
        periodList = listBetweenDates(
            getPeriodMinusMonths(getTodaysDate(), currentMonthSelected),
            getPeriodMinusMonths(getTodaysDate(), currentMonthSelected + 1)
        )
    }

    fun nextDate() {
        currentMonthSelected++
    }

    fun backDate() {
        currentMonthSelected--
    }

    fun setUsersBudget(budgetInput: Long) {
        val db = FirebaseFirestore.getInstance()
        val user = Firebase.auth.currentUser
        val uid = user!!.uid

        val budget = HashMap<String, Any>()
        budget["budget"] = budgetInput

        db.collection("users").document(uid).set(budget)
    }

    //Get current users UID
    //Example: val currentUID = getUID()
    fun getUID(): String {
        val auth = Firebase.auth

        return auth.uid.toString()
    }

    //Use this to get full name from db
    //Example: val fName = getFullName(getUID())

    fun getUserName(uid: String): String? {
        val db = FirebaseFirestore.getInstance()
        var userName: String? = ""

        db.collection("users").document(uid)
            .get()
            .addOnSuccessListener { document ->
                userName = document.getString("fName")
                Log.d("DB", userName.toString())
            }

        return userName
    }

    //Used to register user to the Firestore db
    fun registerUserToFirestore(inputFullname: String, inputEmail: String) {
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
    fun addInputData(purchaseName: String, purchaseCost: Long, purchaseCategory: String, purchaseDate: String) {
        val db = FirebaseFirestore.getInstance()

        val purchaseDateToMillis =
            convertDateToMillis(purchaseDate) // Convert timestamp to millis so we can query it in firestore

        val inputData: MutableMap<Any, Any> = HashMap()
        inputData["Item"] = purchaseName
        inputData["Price"] = purchaseCost
        inputData["Category"] = purchaseCategory
        inputData["purchaseDate"] = purchaseDateToMillis!!
        inputData["purchaseDateString"] = purchaseDate

        val inputRef = db.collection("users").document(getUID()).collection("itemList").document()

        inputRef.set(inputData).addOnSuccessListener {
            Log.d("DB", "Input added")
        }.addOnFailureListener {
            Log.d("DB", "Input failed")
        }
    }


    //Use this function to convert a string date to millis
    //Example: val dateInMillis = convertDateToMillis("02/03/2022")
    fun convertDateToMillis(date: String): Long? {
        val simpleDate = SimpleDateFormat("dd/M/yyyy")

        val mDate: java.util.Date? = simpleDate.parse(date) // Convert string into Date
        val timeInMilliseconds: Long? =
            mDate?.time // Convert mDate to millis and put the value in a long

        Log.d("DB", timeInMilliseconds.toString())

        return timeInMilliseconds
    }

    fun convertMillisToDateString(millis: Long): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy")
        val date = Date(millis)
        return sdf.format(date)
    }

    //Get current year
    fun getCurrentYear(): String {
        return Calendar.getInstance().get(Calendar.YEAR).toString()
    }

    fun getYear(a: String): String {
        return a.substring(6, 10)
    }

    fun getTodaysDate(): String {
        val sdf = SimpleDateFormat("25/MM/yyyy")
        val currentDate = sdf.format(Date())
        return currentDate
    }
}

