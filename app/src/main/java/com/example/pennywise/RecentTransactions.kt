package com.example.pennywise

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters
import java.util.*
import kotlin.collections.ArrayList

class RecentTransactions : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    //Main data
    var transactionList : List<Transaction> = DataHandler.allTransactions
    //What is the category spinner currently set to?
    var currentCat : String = "All"
    //What is the days/months spinner currently set to?
    var currentInterval : String = "Days"

    //For dates
    private var startDate = LocalDate.now().with(TemporalAdjusters.firstDayOfYear()).toString()
    private var endDate = LocalDate.now().with(TemporalAdjusters.lastDayOfYear()).toString()


    //For spinners, gets called when option is selected by user.
    // I believe this can be broken down into smaller functions
    override fun onItemSelected(parent: AdapterView<*>, View: View?, pos: Int, id: Long){
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)

        val values : MutableList<Float> = ArrayList()
        val labels : MutableList<String> = ArrayList()
        //CASE: "Days" selected, current category can be retrieved from currentCat
        if(parent.getItemAtPosition(pos).equals("Days")){
            //Set currentInterval to chosen spinner option
            currentInterval = parent.getItemAtPosition(pos).toString()
            //Order and format list by days
            //val filteredTransactionList = sortListOnCategory(transactionList.toMutableList(), currentCat)
            val filteredTransactionList = DataHandler.filteredList(startDate,endDate,currentCat)
            //val newTransactionList = sortTransactionList(filteredTransactionList)
            val daysTransactionList = formatListForDays(filteredTransactionList.asReversed())
            for (i in daysTransactionList.indices){
                values.add(daysTransactionList[i].amount.toFloat())
                labels.add(daysTransactionList[i].day + "/" + daysTransactionList[i].month)
            }
            //Set the graph
            setBarGraph(values, labels, R.color.cornflower)
        //CASE: "Months" selected, current category can be retrieved from currentCat
        } else if(parent.getItemAtPosition(pos).equals("Months")){ //If "Months"
            //Set currentInterval to chosen spinner option
            currentInterval = parent.getItemAtPosition(pos).toString()
            //Order and format list by months
            //val filteredTransactionList = sortListOnCategory(transactionList.toMutableList(), currentCat)
            val filteredTransactionList = DataHandler.filteredList(startDate,endDate,currentCat)
            //val newTransactionList = sortTransactionList(filteredTransactionList)
            val monthsTransactionList = formatListForMonths(filteredTransactionList.asReversed())
            //Convert to separate lists
            for (i in monthsTransactionList.indices){
                values.add(monthsTransactionList[i].amount.toFloat())
                labels.add(monthsTransactionList[i].month)
            }
            //Set the bar graph
            setBarGraph(values,labels, ContextCompat.getColor(this, R.color.yellow_orange))
        //CASE: A category has been selected, and "Days" has previously been selected
        } else if(currentInterval == "Days") {
            //Set currentCat to whichever category is chosen
            currentCat = parent.getItemAtPosition(pos).toString()
            //Run the same code as in the "days" if-statement.. this could probably be broken out
            //Order and format list by days
            //val filteredTransactionList =
            //    sortListOnCategory(transactionList.toMutableList(), currentCat)
            val filteredTransactionList = DataHandler.filteredList(startDate,endDate,currentCat)
            //val newTransactionList = sortTransactionList(filteredTransactionList)
            val daysTransactionList = formatListForDays(filteredTransactionList.asReversed())
            //Convert to separate lists
            for (i in daysTransactionList.indices) {
                values.add(daysTransactionList[i].amount.toFloat())
                labels.add(daysTransactionList[i].day + "/" + daysTransactionList[i].month)
            }
            //Set the graph
            setBarGraph(values, labels, R.color.cornflower)
        //CASE: A category has been selected, and "Months" has previously been selected
        } else{
            //Same but for month, this can also probably be broken out to another function
            currentCat = parent.getItemAtPosition(pos).toString()
            //Order and format list by months
            //val filteredTransactionList = sortListOnCategory(transactionList.toMutableList(), currentCat)
            val filteredTransactionList = DataHandler.filteredList(startDate,endDate,currentCat)
            //val newTransactionList = sortTransactionList(filteredTransactionList)
            val monthsTransactionList = formatListForMonths(filteredTransactionList.asReversed())
            //Convert to separate lists
            for (i in monthsTransactionList.indices){
                values.add(monthsTransactionList[i].amount.toFloat())
                labels.add(monthsTransactionList[i].month)
            }
            //Set the bar graph
            setBarGraph(values,labels, ContextCompat.getColor(this, R.color.yellow_orange))
        }

    }
    override fun onNothingSelected(parent:AdapterView<*>){
        //Another interface callback
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recent_transactions)

        //Setting up recyclerView
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val adapter = TransactionRecyclerAdapter(this, DataHandler.allTransactions)
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

        val daysTransactionList = formatListForMonths(transactionList)

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

    //Sorts the transaction list by year -> month -> day
    //NO LONGER USED! As DataHandler sorts it. Can probably be removed
    fun sortTransactionList(transactions : List<Transaction>) : MutableList<Transaction> {
        val newTransactionList = transactions.sortedWith(compareBy<Transaction> {it.year}.thenBy {it.month}
            .thenBy {it.day})
        if(newTransactionList.isEmpty()){
            return noDataList() //If empty return this list instead (prevents crash)
        }
        return newTransactionList as MutableList<Transaction>
    }

    //Adds all values for days together
    fun formatListForDays(transactions : List<Transaction>) : MutableList<Transaction>{
        val formattedList : MutableList<Transaction> = ArrayList()
        //For every transaction in the transaction list
        for (i in transactions.indices){
            if (formattedList.isEmpty()){
                formattedList.add(transactions[i]) //If this is the first call, just add it
            //If the date of this transaction is the same as the previous one added, "merge" them
            } else if(formattedList.last().day.equals(transactions[i].day)
                && formattedList.last().month.equals(transactions[i].month)
                && formattedList.last().year.equals(transactions[i].year)){
                formattedList[formattedList.size - 1] = Transaction(formattedList.last().amount + transactions[i].amount,formattedList.last().category,formattedList.last().timeStamp,formattedList.last().year,formattedList.last().month,formattedList.last().day,formattedList.last().time,formattedList.last().note)
                // ^Temporary horrible code line as I couldn't update just the amount

            //If the date differs from the previous one added, add as new entry
            } else{
                formattedList.add(transactions[i])
            }
        }
        //Return the new list
        return formattedList
    }

    //Adds all values for months together (only one line differs.. can these be combined?)
    fun formatListForMonths(transactions : List<Transaction>) : MutableList<Transaction>{
        val formattedList : MutableList<Transaction> = ArrayList()
        //For every transaction in the transaction list
        for (i in transactions.indices){
            if (formattedList.isEmpty()){
                formattedList.add(transactions[i]) //If this is the first call, just add it
            //If the month+year of this transaction is the same as the previous one added, "merge" them
            } else if(formattedList.last().month.equals(transactions[i].month)
                && formattedList.last().year.equals(transactions[i].year)){
                formattedList[formattedList.size - 1] = Transaction(formattedList.last().amount + transactions[i].amount,formattedList.last().category,formattedList.last().timeStamp,formattedList.last().year,formattedList.last().month,formattedList.last().day,formattedList.last().time,formattedList.last().note)
                // ^Temporary horrible code line as I couldn't update just the amount

            //If the month+year differs from the previous one added, add as new entry
            } else{
                formattedList.add(transactions[i])
            }
        }
        //Return the new list
        return formattedList
    }

    //Filter the list based on category,
    //NO LONGER USED! Can probably be removed
    fun sortListOnCategory(transactions : MutableList<Transaction>, cat : String) : MutableList<Transaction>{
        if(cat.equals("All")){
            return transactions
        }else{
            val sortedList = transactions.filter { it.category == cat }
            if(sortedList.isEmpty()){
                return noDataList() //If empty return this list instead (prevents crash)
            }
            return sortedList.toMutableList()
        }
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
            entries.add(BarEntry(i.toFloat(),entryValues[i]/100)) //divide by 100 because of öre
        }

        //Make a dataset using the entries list
        val dataSet = BarDataSet(entries, "") // add entries to dataset

        //The color needs to be set here, using a defined color in colors.xml
        if(chartColor != 0){
            dataSet.setColor(chartColor)
        }

        //Draws a 1-pixel-wide borderline around the bars, not sure if we want this
        //dataSet.barBorderWidth = 1f

        val barData = BarData(dataSet)

        barData.setBarWidth(0.6f)
        barData.setDrawValues(true)

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


        //Remove small description in the corner
        chart.getDescription().setText("")
        //Remove X-axis grid lines and numbers (they're not needed)
        chart.getXAxis().setEnabled(false)

        //Simple animation
        chart.animateY(3000 , Easing.EaseOutBack )

        //Update the actual bar graph
        chart.invalidate()
    }

    //Returns an empty transaction list (used to handle cases with no data)
    //NO LONGER USED! If the sortTransactionList and sortListOnCategory are removed, this can be too
    fun noDataList() : MutableList<Transaction>{
        val noDataTransactionList : MutableList<Transaction> =
            ArrayList()
        noDataTransactionList.add(Transaction(0))
        return noDataTransactionList
    }
}