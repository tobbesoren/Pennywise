package com.example.pennywise

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry

class RecentTransactions : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recent_transactions)


        //Just giving the graph some initial, mock-values
        val values : MutableList<Float> = ArrayList()
        values.add(178f)
        values.add(204f)
        values.add(200f)
        values.add(183f)
        values.add(194f)
        values.add(142f)

        val values2 : MutableList<Float> = ArrayList()
        values2.add(101f)
        values2.add(90f)
        values2.add(71f)
        values2.add(141f)
        values2.add(51f)
        values2.add(142f)

        val labels : MutableList<String> = ArrayList()
        labels.add("04/02")
        labels.add("05/02")
        labels.add("06/02")
        labels.add("07/02")
        labels.add("Yesterday")
        labels.add("Today")

        setBarGraph(values, labels, 0)

        //Black bars
        //setBarGraph(values, labels, ContextCompat.getColor(this, R.color.black))

        //Buttons for testing changing of charts
        val button1 = findViewById<Button>(R.id.buttontest1)
        button1.setOnClickListener {
            setBarGraph(values, labels, 0)
        }
        val button2 = findViewById<Button>(R.id.buttontest2)
        button2.setOnClickListener {
            setBarGraph(values2,labels,ContextCompat.getColor(this, R.color.yellow_orange))
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