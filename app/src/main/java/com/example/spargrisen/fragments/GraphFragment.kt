package com.example.spargrisen.fragments

import android.media.Image
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.spargrisen.DatabaseController
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

class GraphFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    var dbc = DatabaseController()

    lateinit var periodGraph: GraphView
    lateinit var yearGraph: AAChartView
    lateinit var pieGraph: AAChartView
    lateinit var backButton: ImageButton
    lateinit var nextButton: ImageButton
    lateinit var dateText: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_graph, container, false)

        periodGraph = view.findViewById(R.id.period_graph)
        yearGraph = view.findViewById(R.id.year_graph)
        pieGraph = view.findViewById(R.id.categorygraph)
        backButton = view.findViewById(R.id.backDate)
        nextButton = view.findViewById(R.id.nextDate)
        dateText = view.findViewById(R.id.dateText)


        auth = Firebase.auth

        nextButton.setOnClickListener {
            dbc.nextDate()
            dbc.setPeriodList()
            init()
        }

        backButton.setOnClickListener {
            dbc.backDate()
            dbc.setPeriodList()
            init()
        }

        //Listen for database changes, if true refresh functions
        FirebaseFirestore.getInstance().collection("users")
            .document(dbc.getUID())
            .collection("itemList")
            .addSnapshotListener { snapshot, e ->
                dbc.purchasesList.clear()
                dbc.getPurchaseData()

                init()
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
        var graphList : MutableList<DataPoint> = mutableListOf()
        var graphArray: Array<DataPoint> = arrayOf<DataPoint>()

        for (i in dbc.periodList.indices) {
            graphList.add(
                i, DP(
                    getDay(dbc.periodList[i].purchaseDateString),
                    dbc.periodList[i].purchaseCost
                )
            )
        }

        graphArray = graphList.sortedBy { it.x }.toTypedArray()

        val series: LineGraphSeries<DataPoint> = LineGraphSeries(
            graphArray
        )
        periodGraph.addSeries(series)
    }

    fun graphYear() {
        var janCost: Long = 0; var febCost: Long = 0; var marCost: Long = 0; var aprCost: Long = 0; var mayCost: Long = 0; var junCost: Long = 0; var julCost: Long = 0; var augCost: Long = 0; var sepCost: Long = 0; var octCost: Long = 0; var novCost: Long = 0; var decCost: Long = 0

        for (i in dbc.purchasesList.indices) {
            if (dbc.getYear(dbc.purchasesList[i].purchaseDateString) == dbc.getCurrentYear()) {
                when (getMonth(dbc.purchasesList[i].purchaseDateString)) {
                    1 -> janCost += dbc.purchasesList[i].purchaseCost
                    2 -> febCost += dbc.purchasesList[i].purchaseCost
                    3 -> marCost += dbc.purchasesList[i].purchaseCost
                    4 -> aprCost += dbc.purchasesList[i].purchaseCost
                    5 -> mayCost += dbc.purchasesList[i].purchaseCost
                    6 -> junCost += dbc.purchasesList[i].purchaseCost
                    7 -> julCost += dbc.purchasesList[i].purchaseCost
                    8 -> augCost += dbc.purchasesList[i].purchaseCost
                    9 -> sepCost += dbc.purchasesList[i].purchaseCost
                    10 -> octCost += dbc.purchasesList[i].purchaseCost
                    11 -> novCost += dbc.purchasesList[i].purchaseCost
                    12 -> decCost += dbc.purchasesList[i].purchaseCost
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
            .title("Expenses this period")
            .dataLabelsEnabled(true)
            .zoomType(AAChartZoomType.XY)
            .series(
                arrayOf(
                    AASeriesElement()
                        .data(arrayOf(
                            arrayOf("Mat & Dryck", 100, 300, 400),
                            arrayOf("Boende & Hushåll", 200),
                            arrayOf("Shopping", 200),
                            arrayOf("Hälsa & Skönhet", 200),
                            arrayOf("Fritid", 200),
                            arrayOf("Transport", 200),
                            arrayOf("Hem & Trädgård", 200),
                            arrayOf("Övrigt", 200),
                        ))
                )

            )

        pieGraph.aa_drawChartWithChartModel(pieChart)
    }

    fun init() {
        dateText.text = dbc.periodDate

        populateGraph()
        graphYear()
        pieChart()
    }
}