package com.example.pennywise

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry

class RecentTransactions : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    //Main data
    var transactionList : List<Transaction> = DataHandler.itemsToView
    //Which category is being viewed?
    var currentCat : String = "All"


    //For spinners
    override fun onItemSelected(parent: AdapterView<*>, View: View?, pos: Int, id: Long){
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)

        val values : MutableList<Float> = ArrayList()
        val labels : MutableList<String> = ArrayList()
        //If the spinner option selected was "Days"
        if(parent.getItemAtPosition(pos).equals("Days")){
            //Order and format list by days
            val filteredTransactionList = sortListOnCategory(transactionList.toMutableList(), currentCat)
            val newTransactionList = sortTransactionList(filteredTransactionList)
            val daysTransactionList = formatListForDays(newTransactionList)
            //Convert to separate lists
            for (i in daysTransactionList.indices){
                values.add(daysTransactionList[i].amount.toFloat())
                labels.add(daysTransactionList[i].day + "/" + daysTransactionList[i].month)
            }
            //Set the graph
            setBarGraph(values, labels, R.color.cornflower)
        } else if(parent.getItemAtPosition(pos).equals("Months")){ //If "Months"
            //Order and format list by months
            val filteredTransactionList = sortListOnCategory(transactionList.toMutableList(), currentCat)
            val newTransactionList = sortTransactionList(filteredTransactionList)
            val monthsTransactionList = formatListForMonths(newTransactionList)
            //Convert to separate lists
            for (i in monthsTransactionList.indices){
                values.add(monthsTransactionList[i].amount.toFloat())
                labels.add(monthsTransactionList[i].month)
            }
            //Set the bar graph
            setBarGraph(values,labels, ContextCompat.getColor(this, R.color.yellow_orange))
        } else{
            //Else happens if the category changed, thus change the currentCat
            currentCat = parent.getItemAtPosition(pos).toString()
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

        val adapter = TransactionRecyclerAdapter(this, DataHandler.itemsToView)
        recyclerView.adapter = adapter

        val returnButton = findViewById<ImageButton>(R.id.returnIB)
        returnButton.setOnClickListener {
            finish()
        }


        //Just giving the graph some initial, mock-values
        val values : MutableList<Float> = ArrayList()
        val labels : MutableList<String> = ArrayList()

        val newTransactionList = sortTransactionList(transactionList)
        //val daysTransactionList = formatListForDays(newTransactionList)
        val daysTransactionList = formatListForMonths(newTransactionList)

        for (i in daysTransactionList.indices){
            values.add(daysTransactionList[i].amount.toFloat())
            labels.add(daysTransactionList[i].timeStamp)
        }

        setBarGraph(values, labels, 0)

        //Spinner stuff
        val spinner : Spinner = findViewById(R.id.spinner1)
        spinner.onItemSelectedListener = this
        // Create an ArrayAdapter using a string array and the default spinner layout
        ArrayAdapter.createFromResource(
            this,
            R.array.days_months_array,
            android.R.layout.simple_spinner_item
        ).also{ adapter ->
            // specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_item)
            // apply the adapter to the spinner
            spinner.adapter = adapter
        }
        //Same for second spinner
        val spinner2 : Spinner = findViewById(R.id.spinner2)
        spinner2.onItemSelectedListener = this
        ArrayAdapter.createFromResource(
            this,
            R.array.category_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_item)
            spinner2.adapter = adapter
        }
    }

    //Sorts the transaction list by year -> month -> day
    fun sortTransactionList(transactions : List<Transaction>) : MutableList<Transaction> {
        return transactions.sortedWith(compareBy<Transaction> {it.year}.thenBy {it.month}
            .thenBy {it.day}) as MutableList<Transaction>
    }

    //Adds all values for days together
    fun formatListForDays(transactions : List<Transaction>) : MutableList<Transaction>{
        val formattedList : MutableList<Transaction> = ArrayList()
        for (i in transactions.indices){
            if (formattedList.isEmpty()){
                formattedList.add(transactions[i]) //If this is the first call, just add it
            } else if(formattedList.last().day.equals(transactions[i].day)
                && formattedList.last().month.equals(transactions[i].month)
                && formattedList.last().year.equals(transactions[i].year)){
                formattedList[formattedList.size - 1] = Transaction(formattedList.last().amount + transactions[i].amount,formattedList.last().category,formattedList.last().timeStamp,formattedList.last().year,formattedList.last().month,formattedList.last().day,formattedList.last().time,formattedList.last().note)
                // ^Temporary horrible code line as I couldn't update just the amount
            } else{
                formattedList.add(transactions[i])
            }
        }
        return formattedList
    }

    //Adds all values for months together (only one line differs.. can these be combined?)
    fun formatListForMonths(transactions : List<Transaction>) : MutableList<Transaction>{
        val formattedList : MutableList<Transaction> = ArrayList()
        for (i in transactions.indices){
            if (formattedList.isEmpty()){
                formattedList.add(transactions[i]) //If this is the first call, just add it
            } else if(formattedList.last().month.equals(transactions[i].month)
                && formattedList.last().year.equals(transactions[i].year)){
                formattedList[formattedList.size - 1] = Transaction(formattedList.last().amount + transactions[i].amount,formattedList.last().category,formattedList.last().timeStamp,formattedList.last().year,formattedList.last().month,formattedList.last().day,formattedList.last().time,formattedList.last().note)
                // ^Temporary horrible code line as I couldn't update just the amount
            } else{
                formattedList.add(transactions[i])
            }
        }
        return formattedList
    }

    //Filter the list based on category
    fun sortListOnCategory(transactions : MutableList<Transaction>, cat : String) : MutableList<Transaction>{
        if(cat.equals("All")){
            return transactions
        }else{
            return transactions.filter { it.category == cat }.toMutableList()
        }
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
            entries.add(BarEntry(i.toFloat(),entryValues[i]))
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
        chart.setDrawGridBackground(false)
        chart.setDrawBorders(true)
        chart.setBorderWidth(1f)
        chart.getLegend().setEnabled(false)
        chart.setAutoScaleMinMaxEnabled(true)

        //Remove small description in the corner
        chart.getDescription().setText("")
        //Remove X-axis grid lines and numbers (they're not needed)
        chart.getXAxis().setEnabled(false)

        //Simple animation
        chart.animateY(3000 , Easing.EaseOutBack )

        //Update the actual bar graph
        chart.invalidate()
    }

}