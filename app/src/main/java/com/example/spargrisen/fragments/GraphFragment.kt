package com.example.spargrisen.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.spargrisen.DatabaseController
import com.example.spargrisen.R
import com.github.aachartmodel.aainfographics.aachartcreator.*
import com.github.aachartmodel.aainfographics.aaoptionsmodel.AASeries
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GetTokenResult
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import kotlinx.android.synthetic.main.fragment_graph.*

class GraphFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    var dbController = DatabaseController()


    lateinit var textTest: TextView
    lateinit var periodGraph: GraphView
    lateinit var yearGraph: AAChartView
    lateinit var pieGraph: AAChartView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_graph, container, false)

        textTest = view.findViewById(R.id.textView6)
        periodGraph = view.findViewById(R.id.period_graph)
        yearGraph = view.findViewById(R.id.year_graph)
        pieGraph = view.findViewById(R.id.categorygraph)


        auth = Firebase.auth

        //Listen for database changes, if true refresh functions
        FirebaseFirestore.getInstance().collection("users")
            .document(dbController.getUID())
            .collection("values")
            .addSnapshotListener { snapshot, e ->
                dbController.purchasesList.clear()
                dbController.getPurchaseData()

                populateGraph()
                graphYear()
                pieChart()
            }

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


                var graphList : ArrayList<DataPoint> = ArrayList()
                var graphArray: Array<DataPoint>

                for (i in dbController.purchasesList.indices) {
                    graphList.add(i, DP(getDay(dbController.purchasesList[i].purchaseDateString),
                        dbController.purchasesList[i].purchaseCost
                    ))
                }
                graphArray = graphList.sortedBy { it.x }.toTypedArray()

                val series: LineGraphSeries<DataPoint> = LineGraphSeries(
                    graphArray
                )
                periodGraph.addSeries(series)
    }

    fun graphYear() {
        var janCost: Long = 0; var febCost: Long = 0; var marCost: Long = 0; var aprCost: Long = 0; var mayCost: Long = 0; var junCost: Long = 0; var julCost: Long = 0; var augCost: Long = 0; var sepCost: Long = 0; var octCost: Long = 0; var novCost: Long = 0; var decCost: Long = 0

        for (i in dbController.purchasesList.indices) {
            if (dbController.getYear(dbController.purchasesList[i].purchaseDateString) == dbController.getCurrentYear()) {
                when (getMonth(dbController.purchasesList[i].purchaseDateString)) {
                    1 -> janCost += dbController.purchasesList[i].purchaseCost
                    2 -> febCost += dbController.purchasesList[i].purchaseCost
                    3 -> marCost += dbController.purchasesList[i].purchaseCost
                    4 -> aprCost += dbController.purchasesList[i].purchaseCost
                    5 -> mayCost += dbController.purchasesList[i].purchaseCost
                    6 -> junCost += dbController.purchasesList[i].purchaseCost
                    7 -> julCost += dbController.purchasesList[i].purchaseCost
                    8 -> augCost += dbController.purchasesList[i].purchaseCost
                    9 -> sepCost += dbController.purchasesList[i].purchaseCost
                    10 -> octCost += dbController.purchasesList[i].purchaseCost
                    11 -> novCost += dbController.purchasesList[i].purchaseCost
                    12 -> decCost += dbController.purchasesList[i].purchaseCost
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
                        .data(arrayOf(
                            arrayOf("January", janCost),
                            arrayOf("February", febCost),
                            arrayOf("March", marCost),
                            arrayOf("April", aprCost),
                            arrayOf("May", mayCost),
                            arrayOf("June", junCost),
                            arrayOf("July", julCost),
                            arrayOf("August", augCost),
                            arrayOf("September", sepCost),
                            arrayOf("October", octCost),
                            arrayOf("November", novCost),
                            arrayOf("December", decCost),
                        ))
                )

            )

        year_graph.aa_drawChartWithChartModel(yearGraph)

    }

    fun pieChart() {
        val db = FirebaseFirestore.getInstance()
        val localPurchases: MutableList<DatabaseController.Purchases> = mutableListOf()

        val pieChart: AAChartModel = AAChartModel()
            .chartType(AAChartType.Pie)
            .title("Expenses this year")
            .dataLabelsEnabled(true)
            .zoomType(AAChartZoomType.XY)
            .series(
                arrayOf(
                    AASeriesElement()
                        .data(arrayOf(
                            arrayOf("Food", 100, 300, 400),
                            arrayOf("Clothes", 200),
                            arrayOf("Clothes", 200),
                            arrayOf("Clothes", 200),
                        ))
                )

            )

        pieGraph.aa_drawChartWithChartModel(pieChart)
    }
}