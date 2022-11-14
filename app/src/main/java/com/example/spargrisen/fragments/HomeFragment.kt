package com.example.spargrisen.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.print.PrintManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.spargrisen.*
import com.example.spargrisen.utils.PDFListener
import com.gkemon.XMLtoPDF.PdfGenerator
import com.gkemon.XMLtoPDF.PdfGeneratorListener
import com.gkemon.XMLtoPDF.model.FailureResponse
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_manual_input.*
import kotlinx.android.synthetic.main.category_list.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_home.view.*


/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {

    lateinit var  recyclerView: RecyclerView
    lateinit var inputList : ArrayList<InputText>
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
        inputList = arrayListOf()

        val addInputBtn = view.findViewById<FloatingActionButton>(R.id.addInputBtn)
        val addInputCameraBtn = view.findViewById<FloatingActionButton>(R.id.addInputCameraBtn)
        val addManualInputBtn = view.findViewById<FloatingActionButton>(R.id.addManualInputBtn)
        val printBtn = view.findViewById<FloatingActionButton>(R.id.printBtn)


        addInputBtn.setOnClickListener {
//            val intent = Intent(requireContext(), LoginActivity::class.java)
//            startActivity(intent)
            if (isOpen){

                addManualInputBtn.startAnimation(fabClose)
                addInputCameraBtn.startAnimation(fabClose)
                printBtn.startAnimation(fabClose)
                addInputBtn.startAnimation(fabRClockwise)

                isOpen= false
            }

            else{
                addManualInputBtn.startAnimation(fabOpen)
                addInputCameraBtn.startAnimation(fabOpen)
                addInputBtn.startAnimation(fabRAntiClockwise)
                printBtn.startAnimation(fabOpen)

                addManualInputBtn.isClickable
                addInputCameraBtn.isClickable
                printBtn.isClickable

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

            printBtn.setOnClickListener {
                doPrint(view)
            }

        }

        db = FirebaseFirestore.getInstance()

        db.collection("users").document(getUID()).collection("itemList").get() // ska vara itemList ist för values
            .addOnSuccessListener {
                if (!it.isEmpty){
                    for (data in it.documents){
                        val inputText : InputText? = data.toObject(InputText ::class.java)
                        if (inputText != null) {
                            inputList.add(inputText)
                        }
                    }

                    recyclerView.adapter = MyAdapter(inputList)

                }
            }


        return view
    }


    fun getUID() : String {
        val auth = Firebase.auth

        val uid : String = auth.uid.toString()
        return uid
    }


    private fun doPrint(view: View) {



        val pdfListener = PDFListener()

        PdfGenerator.getBuilder()
            .setContext(activity)
            .fromViewSource()
            .fromView(view.relative_layout)
            .setFileName("TABLE-PDF")
            .setFolderNameOrPath("TABLE-PDF-Folder")
            .actionAfterPDFGeneration(PdfGenerator.ActionAfterPDFGeneration.OPEN)
            .build(pdfListener)
        }

    private fun prepareView(view: View) {

    }
}


