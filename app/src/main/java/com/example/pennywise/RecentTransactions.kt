package com.example.pennywise

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pennywise.databinding.ActivityRecentTransactionsBinding
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters
import java.util.*
import kotlin.collections.ArrayList

class RecentTransactions : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    private lateinit var binding : ActivityRecentTransactionsBinding
    private val uid = Firebase.auth.uid.toString()

    //Main data
    var transactionList : List<Transaction> = DataHandler.allTransactions
    lateinit var adapter: TransactionRecyclerAdapter
    //What is the category spinner currently set to?
    var currentCat : String = "All"
    //What is the days/months spinner currently set to?
    var currentInterval : String = "Days"

    //For dates
    private var startDate = LocalDate.now().with(TemporalAdjusters.firstDayOfYear()).toString()
    private var endDate = LocalDate.now().with(TemporalAdjusters.lastDayOfYear()).toString()


    //For spinners, gets called when option is selected by user.
    override fun onItemSelected(parent: AdapterView<*>, View: View?, pos: Int, id: Long){
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)

        if(parent.getItemAtPosition(pos) == "Days" || parent.getItemAtPosition(pos) == "Months"){
            //Set currentInterval to chosen spinner option
            currentInterval = parent.getItemAtPosition(pos).toString()
            updateGraphWithSorting()
        } else{
            //Set the current category to chosen spinner option
            currentCat = parent.getItemAtPosition(pos).toString()
            updateGraphWithSorting()
        }

    }
    override fun onNothingSelected(parent:AdapterView<*>){
        //Not used in this activity, but needs to be overridden
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recent_transactions)

        //Init adapter
        adapter = TransactionRecyclerAdapter(this, DataHandler.allTransactions)

        binding = ActivityRecentTransactionsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.bottomNavigationView.selectedItemId = R.id.recent_menu_button
        binding.bottomNavigationView.setOnItemSelectedListener {

            when(it.itemId){

                R.id.add_menu_button -> goToAddTransactionActivity()
                R.id.overview_menu_button -> goToOverViewActivity()
                else ->{

                }
            }
            true
        }


        //Setting up recyclerView
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        recyclerView.adapter = adapter


        val returnButton = findViewById<ImageButton>(R.id.returnIB)
        returnButton.setOnClickListener {
            finish()
        }


        val calendarButton = findViewById<ImageButton>(R.id.calendarButtonRT)
        calendarButton.setOnClickListener{
            showDateRangePicker()
        }


        //Giving the graph some initial values, this will be "overridden" once the spinners load 
        val values : MutableList<Float> = ArrayList()
        val labels : MutableList<String> = ArrayList()

        val daysTransactionList = formatListForDaysOrMonths(transactionList)

        for (i in daysTransactionList.indices){
            values.add(daysTransactionList[i].amount.toFloat())
            labels.add(daysTransactionList[i].timeStamp)
        }
        setBarGraph(values, labels, 0)

        //Setting up the spinners
        val spinner : Spinner = findViewById(R.id.spinner1)
        spinner.onItemSelectedListener = this
        // Create an ArrayAdapter using a string array and the default spinner layout
        ArrayAdapter.createFromResource(
            this,
            R.array.days_months_array,
            android.R.layout.simple_spinner_dropdown_item
        ).also{ adapter ->
            // specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // apply the adapter to the spinner
            spinner.adapter = adapter
        }
        //Same for second spinner
        val spinner2 : Spinner = findViewById(R.id.spinner2)
        spinner2.onItemSelectedListener = this
        ArrayAdapter.createFromResource(
            this,
            R.array.category_array,
            android.R.layout.simple_spinner_dropdown_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner2.adapter = adapter
        }


    }


    override fun onResume() {
        super.onResume()
        loadData(uid)
    }

    private fun loadData(uid: String) {
        Firebase.firestore.collection("users/${uid}/transactions")
            .get()
            .addOnSuccessListener { documentSnapShot ->
                DataHandler.updateList(documentSnapShot)
                updateGraphWithSorting()
            }.addOnFailureListener { exception ->
                Log.d("!!!!", exception.toString())
            }
    }

    private fun goToAddTransactionActivity () {
        val intent = Intent(this, AddTransactionActivity::class.java)
        startActivity(intent)
    }
    private fun goToOverViewActivity () {
        val intent = Intent(this, OverView::class.java)
        startActivity(intent)
    }

    //Adds all values for days, or months, together, so that they can be a single bar in the graph
    fun formatListForDaysOrMonths(transactions : List<Transaction>) : MutableList<Transaction>{
        var formattedList : MutableList<Transaction> = ArrayList()
        //For every transaction in the transaction list
        for (i in transactions.indices){
            if (formattedList.isEmpty()){
                formattedList.add(transactions[i]) //If this is the first call, just add it
            //If the date (or just month+year, if currentInterval == month) of this transaction
            //is the same as the previous one added, "merge" them
            } else if((formattedList.last().day.equals(transactions[i].day) || currentInterval == "Months")
                && formattedList.last().month.equals(transactions[i].month)
                && formattedList.last().year.equals(transactions[i].year)){
                formattedList = addValueToLastTransactionInList(formattedList, transactions[i].amount)

            //If the date differs from the previous one added, add as new entry
            } else{
                formattedList.add(transactions[i])
            }
        }
        //Return the new list
        return formattedList
    }

    //Adds the value "value" onto the last transaction in the list
    //Used to "lump" days or months together for the graph
    fun addValueToLastTransactionInList(transactions : MutableList<Transaction>, value : Long) : MutableList<Transaction>{
        transactions[transactions.size - 1] = Transaction(null,
            transactions.last().amount + value,transactions.last().category,
            transactions.last().timeStamp,transactions.last().year,transactions.last().month,
            transactions.last().day,transactions.last().time,transactions.last().note)
        return transactions
    }

    //Show the date picker (mostly same as in overview)
    private fun showDateRangePicker() {

        val dateRangePicker = MaterialDatePicker.Builder
            .dateRangePicker()
            .setTitleText("Select Date")
            .build()

        dateRangePicker.show(
            supportFragmentManager,
            "date_range_picker"
        )

        dateRangePicker.addOnPositiveButtonClickListener { datePicked ->

            startDate = convertLongToDate(datePicked.first)
            endDate = convertLongToDate(datePicked.second)
            updateGraphWithSorting()

        }
    }

    //Same as in overview, credit to Philip
    private fun convertLongToDate(time:Long):String {

        val date = java.util.Date(time)
        val format = SimpleDateFormat(
            "yyyy-MM-dd",
            Locale.getDefault()
        )

        return format.format(date)
    }

    //Init or change the graph, give it a list of floats, a list of strings (labels)
    //  Giving "0" as the color will set it to the default blue.
    //  The lists need to be the same length (i.e. as many values as labels).
    fun setBarGraph(entryValues : List<Float>, labels : List<String>, chartColor : Int){
        val chart = findViewById<View>(R.id.chart) as BarChart

        if(entryValues.size != labels.size){
            return //If number of labels do not match number of values (i.e. bars)
        }

        //Make a list of "entries"
        val entries : MutableList<BarEntry> = ArrayList()

        //Put all floats in entryValues in the entry list
        for (i in entryValues.indices){
            entries.add(BarEntry(i.toFloat(),entryValues[i]/100)) //divide by 100 because of ??re
        }

        //Make a dataset using the entries list
        val dataSet = BarDataSet(entries, "") // add entries to dataset

        //The color needs to be set here, using a defined color in colors.xml
        if(chartColor != 0){
            dataSet.setColor(chartColor)
        }

        val barData = BarData(dataSet)

        barData.setBarWidth(0.6f) //Set the spacing between bars (the actual width depends on zoom)
        barData.setDrawValues(true) //Write values to the side of the graph
        barData.setHighlightEnabled(false) //Disable changing color of a bar when touching it

        //This separate class-file is used to change the labels on top of bars.
        val formatter : GraphFormatter = GraphFormatter()
        formatter.setLabels(labels)
        barData.setValueFormatter(formatter)

        //Connect the data to the visual graph
        chart.data = barData

        //Changing some style settings
        chart.setDrawGridBackground(false) //Hide grid-pattern behind the chart
        chart.setDrawBorders(true) //Show the chart's borders
        chart.setBorderWidth(1f) //Set the border to width 1
        chart.getLegend().setEnabled(false) //Hide an unneeded description under the chart
        chart.setDoubleTapToZoomEnabled(false) //Disable zooming into the chart by tapping
        chart.setVisibleXRangeMaximum(6f) //Show no more than 6 bars (this enables scrolling if there are more)
        //chart.setVisibleXRangeMinimum(6f) //Always show at least room for 6 bars
        // (makes the bar width static, but centers the bars to the left, which looks odd)
        chart.setAutoScaleMinMaxEnabled(false) //Disable that the chart "jumps" as you scroll
        chart.moveViewToX(entries.size.toFloat()) //Center the view towards the right
        chart.zoomOut() //Zoom out in case the graph was previously zoomed in
        chart.zoomOut() //Call twice as a single zoom-out isn't always enough


        //Remove small description in the corner
        chart.getDescription().setText("")
        //Remove X-axis grid lines and numbers (they're not needed)
        chart.getXAxis().setEnabled(false)

        //Simple animation
        chart.animateY(3000 , Easing.EaseOutBack )

        //Update the actual bar graph
        chart.invalidate()
    }

    //Sorts everything in accordance to the spinners, and then runs setBarGraph
    fun updateGraphWithSorting(){
        //Get list filtered on date and category
        val filteredTransactionList = DataHandler.filteredList(startDate,endDate,currentCat)
        //Add the days, or months, together
        val formattedTransactionList : List<Transaction>
        formattedTransactionList = formatListForDaysOrMonths(filteredTransactionList.asReversed())
        //Convert to separate lists of values and strings, so that they can be fed to the graph
        val values : MutableList<Float> = ArrayList()
        val labels : MutableList<String> = ArrayList()
        for (i in formattedTransactionList.indices){
            values.add(formattedTransactionList[i].amount.toFloat())
            if(currentInterval == "Months") {
                labels.add(formattedTransactionList[i].month)
            } else{
                labels.add(formattedTransactionList[i].day + "/" + formattedTransactionList[i].month)
            }
        }
        //To fix no-data bug, add a zero amount transaction in case there's no data
        if(values.isEmpty()){
            values.add(0f)
            labels.add("")
        }
        //Set the bar graph
        if(currentInterval == "Days"){
            setBarGraph(values, labels, ContextCompat.getColor(this, R.color.cornflower))
        } else{
            setBarGraph(values,labels, ContextCompat.getColor(this, R.color.yellow_orange))
        }
        //Reset adapter data
        adapter.setNewData(filteredTransactionList)
    }


}