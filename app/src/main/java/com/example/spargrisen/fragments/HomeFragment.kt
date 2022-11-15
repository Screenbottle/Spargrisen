package com.example.spargrisen.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.provider.ContactsContract.Data
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.spargrisen.*
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_manual_input.*
import kotlinx.android.synthetic.main.category_list.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await

class HomeFragment : Fragment() {

    lateinit var  recyclerView: RecyclerView

    var db = Firebase.firestore
    var isOpen = false


    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val fabOpen = AnimationUtils.loadAnimation(requireContext(),R.anim.fab_open)
        val fabClose = AnimationUtils.loadAnimation(requireContext(),R.anim.fab_close)
        val fabRClockwise = AnimationUtils.loadAnimation(requireContext(),R.anim.rotate_clockwise)
        val fabRAntiClockwise = AnimationUtils.loadAnimation(requireContext(),R.anim.rotate_anticlockwise)


        var view = inflater.inflate(R.layout.fragment_home, container, false,)

        var dbController = DatabaseController()


        recyclerView = view.findViewById(R.id.recyclerview)
        recyclerView.layoutManager = LinearLayoutManager(this.context)
        //recyclerView.setHasFixedSize(true)
        //recyclerView.adapter = MyAdapter(inputList)
        //inputList = arrayListOf()

        val addInputBtn = view.findViewById<FloatingActionButton>(R.id.addInputBtn)
        val addInputCameraBtn = view.findViewById<FloatingActionButton>(R.id.addInputCameraBtn)
        val addManualInputBtn = view.findViewById<FloatingActionButton>(R.id.addManualInputBtn)

        val expensesText = view.findViewById<TextView>(R.id.expense)
        val bugetText = view.findViewById<TextView>(R.id.budget)
        val totalText = view.findViewById<TextView>(R.id.balance)

        //detect change in database
        db.collection("users")
            .document(dbController.getUID())
            .addSnapshotListener { snapshot, e ->
                var remainingBudget: Long = getRemainingBudget()!!

                expensesText.text = getExpensesMonth().toString()
                bugetText.text = getBudgetMonth().toString()
                totalText.text = remainingBudget.toString()

                if (remainingBudget < 0) {
                    totalText.setTextColor(Color.RED)
                } else {
                    totalText.setTextColor(Color.RED)
                }

                recyclerView.adapter = MyAdapter(DatabaseController().periodList)
            }


        addInputBtn.setOnClickListener {
//            val intent = Intent(requireContext(), LoginActivity::class.java)
//            startActivity(intent)
            if (isOpen){

                addManualInputBtn.startAnimation(fabClose)
                addInputCameraBtn.startAnimation(fabClose)
                addInputBtn.startAnimation(fabRClockwise)

                isOpen= false
            }

            else{
                addManualInputBtn.startAnimation(fabOpen)
                addInputCameraBtn.startAnimation(fabOpen)
                addInputBtn.startAnimation(fabRAntiClockwise)

                addManualInputBtn.isClickable
                addInputCameraBtn.isClickable

                isOpen = true

            }

            addInputCameraBtn.setOnClickListener{

                 val intent = Intent(requireContext(), CameraActivity::class.java)
                startActivity(intent)
            }

            addManualInputBtn.setOnClickListener{
                val intent = Intent(requireContext(), ManualInput::class.java)
                startActivity(intent)

            }

        }



        return view
    }


    fun getUID() : String {
        val auth = Firebase.auth

        val uid : String = auth.uid.toString()
        return uid
    }

    fun getBudgetMonth(): Long? {
        var usersBudget: Long? = 0

        runBlocking {
            usersBudget = db.collection("users").document(getUID())
                .get()
                .await()
                .get("budget") as Long
        }
        return usersBudget
    }

    fun getExpensesMonth(): Long {
        var totalCost: Long = 0

        for (i in DatabaseController().periodList) {
            totalCost += i.purchaseCost
            Log.d("purchaseCost", i.purchaseName)
        }
        return totalCost
    }

    fun getRemainingBudget(): Long? {
        var remainingBudget = getBudgetMonth()?.minus (getExpensesMonth())

        return remainingBudget
    }
}


