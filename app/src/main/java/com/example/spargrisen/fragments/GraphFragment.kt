package com.example.spargrisen.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
    lateinit var pieGraph: AAChartView
    lateinit var backButton: ImageButton
    lateinit var nextButton: ImageButton
    lateinit var dateText: TextView
    lateinit var yearGraph: AAChartView



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_graph, container, false)

        periodGraph = view.findViewById(R.id.period_graph)
        pieGraph = view.findViewById(R.id.categorygraph)
        backButton = view.findViewById(R.id.backDate)
        nextButton = view.findViewById(R.id.nextDate)
        dateText = view.findViewById(R.id.dateText)
        yearGraph = view.findViewById(R.id.year_graph)



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
        val yearGraph = view?.findViewById<AAChartView>(R.id.year_graph)
        var janCost: Long = 0; var febCost: Long = 0; var marCost: Long = 0; var aprCost: Long = 0; var mayCost: Long = 0; var junCost: Long = 0; var julCost: Long = 0; var augCost: Long = 0; var sepCost: Long = 0; var octCost: Long = 0; var novCost: Long = 0; var decCost: Long = 0

        if (yearGraph != null) { // yearGraph returnerar null efter man lägger till input så det här behövs, inte säker varför den blir null
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
                .yAxisTitle("SEK")
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
        else {
            Log.d("Graph", "yearGraph is null")
        }
    }

    fun pieChart() {
        var foodCost: Long = 0; var homeCost: Long = 0; var shoppingCost: Long = 0; var healthCost: Long = 0; var freeTimeCost: Long = 0; var transportCost: Long = 0; var gardenCost: Long = 0; var otherCost: Long = 0

        //Put all purchases in the correct category
        for (i in dbc.periodList.indices) {
            when (dbc.periodList[i].purchaseCategory) {
                " Food" -> foodCost += dbc.periodList[i].purchaseCost
                " accommodation" -> homeCost += dbc.periodList[i].purchaseCost
                " Shopping" -> shoppingCost += dbc.periodList[i].purchaseCost
                " Health" -> healthCost += dbc.periodList[i].purchaseCost
                " Leisure" -> freeTimeCost += dbc.periodList[i].purchaseCost
                " Transport" -> transportCost += dbc.periodList[i].purchaseCost
                " Home" -> gardenCost += dbc.periodList[i].purchaseCost
                " Other" -> otherCost += dbc.periodList[i].purchaseCost
            }
        }


        val pieChart: AAChartModel = AAChartModel()
            .chartType(AAChartType.Pie)
            .title("Expenses this month")
            .dataLabelsEnabled(false)
            .colorsTheme(arrayOf("#FF0000", "#FFA500", "#FFFF00", "#008000", "#0000FF", "#4B0082", "#EE82EE", "#000000"))
            .series(
                arrayOf(
                    AASeriesElement()
                        .data(arrayOf(
                            arrayOf("Food", foodCost),
                            arrayOf("accommodation", homeCost),
                            arrayOf("Shopping", shoppingCost),
                            arrayOf("Health", healthCost),
                            arrayOf("Leisure", freeTimeCost),
                            arrayOf("Transport", transportCost),
                            arrayOf("Home", gardenCost),
                            arrayOf("Other", otherCost),

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