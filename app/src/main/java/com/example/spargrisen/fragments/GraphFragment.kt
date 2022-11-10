package com.example.spargrisen.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.print.PrintManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.spargrisen.DatabaseController
import com.example.spargrisen.MyPrintDocumentAdapter
import com.example.spargrisen.R
import com.github.aachartmodel.aainfographics.aachartcreator.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import kotlinx.android.synthetic.main.fragment_graph.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [GraphFragment.newInstance] factory method to
 * create an instance of this fragment.
 */

class GraphFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var auth: FirebaseAuth
    var dbController = DatabaseController()

    lateinit var textTest: TextView
    lateinit var periodGraph: GraphView
    lateinit var yearGraph: AAChartView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }


    }

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_graph, container, false)

        textTest = view.findViewById(R.id.textView6)
        periodGraph = view.findViewById(R.id.period_graph)
        yearGraph = view.findViewById(R.id.year_graph)

        auth = Firebase.auth

        populateGraph()
        graphYear()

        return view
    }

    fun DP(a: Long, b : Long): DataPoint {
        return DataPoint(a.toDouble(), b.toDouble()) // Konvertera Long till DataPoint för att kunna användas i grafer
    }

    fun getDay(a: String): Long {
        return a.substringBefore("/").toLong() // Få datumet från purchaseDateString genom att ta bort allt efter /
    }

    fun getMonth(a: String): Int {
        return a.substring(3, 5).toInt()
    }

    fun populateGraph() {
        val db = FirebaseFirestore.getInstance()
        var localPurchaseList: MutableList<DatabaseController.Purchases> = mutableListOf()

        dbController.dbRef
            .addSnapshotListener { snapshot, e ->
                snapshot?.documents?.forEach { document ->
                    localPurchaseList.add(document.toObject(DatabaseController.Purchases::class.java)!!)
                    Log.d("DB", localPurchaseList.toString())
                }

                var graphList : ArrayList<DataPoint> = ArrayList()
                var graphArray: Array<DataPoint>

                for (i in localPurchaseList.indices) {
                    graphList.add(i, DP(getDay(localPurchaseList[i].purchaseDateString), localPurchaseList[i].purchaseCost))
                }
                graphArray = graphList.sortedBy { it.x }.toTypedArray()

                val series: LineGraphSeries<DataPoint> = LineGraphSeries(
                    graphArray
                )
                periodGraph.addSeries(series)
            }
    }

    fun graphYear() {
            val db = FirebaseFirestore.getInstance()
            val localPurchases: MutableList<DatabaseController.Purchases> = mutableListOf()

            var janCost: Long = 0
            var febCost: Long = 0
            var marCost: Long = 0
            var aprCost: Long = 0
            var mayCost: Long = 0
            var junCost: Long = 0
            var julCost: Long = 0
            var augCost: Long = 0
            var sepCost: Long = 0
            var octCost: Long = 0
            var novCost: Long = 0
            var decCost: Long = 0

            var graphList: ArrayList<DataPoint> = ArrayList()

            // TODO: Add year in query
            val query =
                FirebaseFirestore.getInstance().collection("users").document(dbController.getUID())
                    .collection("values")

            query.addSnapshotListener { snapshot, e ->
                snapshot?.documents?.forEach { document ->
                    localPurchases.add(document.toObject(DatabaseController.Purchases::class.java)!!)
                }
                for (i in localPurchases.indices) {

                    when (getMonth(localPurchases[i].purchaseDateString)) {
                        1 -> {
                            Log.d("January", localPurchases[i].purchaseDateString)
                            janCost += localPurchases[i].purchaseCost
                        }
                        2 -> {
                            Log.d("February", localPurchases[i].purchaseDateString)
                            febCost += localPurchases[i].purchaseCost
                        }

                        3 -> {
                            Log.d("March", localPurchases[i].purchaseDateString)
                            marCost += localPurchases[i].purchaseCost
                        }

                        4 -> {
                            Log.d("April", localPurchases[i].purchaseDateString)
                            aprCost += localPurchases[i].purchaseCost
                        }

                        5 -> {
                            Log.d("May", localPurchases[i].purchaseDateString)
                            mayCost += localPurchases[i].purchaseCost
                        }

                        6 -> {
                            Log.d("June", localPurchases[i].purchaseDateString)
                            junCost += localPurchases[i].purchaseCost
                        }

                        7 -> {
                            Log.d("July", localPurchases[i].purchaseDateString)
                            julCost += localPurchases[i].purchaseCost
                        }

                        8 -> {
                            Log.d("August", localPurchases[i].purchaseDateString)
                            augCost += localPurchases[i].purchaseCost
                        }

                        9 -> {
                            Log.d("September", localPurchases[i].purchaseDateString)
                            sepCost += localPurchases[i].purchaseCost
                        }

                        10 -> {
                            Log.d("October", localPurchases[i].purchaseDateString)
                            octCost += localPurchases[i].purchaseCost
                        }

                        11 -> {
                            Log.d("November", localPurchases[i].purchaseDateString)
                            novCost += localPurchases[i].purchaseCost
                        }

                        12 -> {
                            Log.d("December", localPurchases[i].purchaseDateString)
                            decCost += localPurchases[i].purchaseCost
                        }
                    }
                }

                val yearGraph: AAChartModel = AAChartModel()
                    .chartType(AAChartType.Column)
                    .title("Expenses this year")
                    .dataLabelsEnabled(true)
                    .zoomType(AAChartZoomType.XY)
                    .series(
                        arrayOf(
                            AASeriesElement()
                                .name("January")
                                .data(arrayOf(janCost)),
                            AASeriesElement()
                                .name("February")
                                .data(arrayOf(febCost)),
                            AASeriesElement()
                                .name("March")
                                .data(arrayOf(marCost)),
                            AASeriesElement()
                                .name("April")
                                .data(arrayOf(aprCost)),
                            AASeriesElement()
                                .name("May")
                                .data(arrayOf(mayCost)),
                            AASeriesElement()
                                .name("June")
                                .data(arrayOf(junCost)),
                            AASeriesElement()
                                .name("July")
                                .data(arrayOf(julCost)),
                            AASeriesElement()
                                .name("Augusti")
                                .data(arrayOf(augCost)),
                            AASeriesElement()
                                .name("September")
                                .data(arrayOf(sepCost)),
                            AASeriesElement()
                                .name("October")
                                .data(arrayOf(octCost)),
                            AASeriesElement()
                                .name("November")
                                .data(arrayOf(novCost)),
                            AASeriesElement()
                                .name("December")
                                .data(arrayOf(decCost)),
                        )
                    )

                year_graph.aa_drawChartWithChartModel(yearGraph)
        }
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment GraphFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            GraphFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }


}