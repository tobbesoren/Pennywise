package com.example.pennywise

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry

class RecentTransactions : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recent_transactions)

        

    }

    //Init or change the graph, give it a list of floats and a list of strings (labels)
    //  The lists need to be the same length (i.e. as many values as labels)
    fun setBarGraph(entryValues : List<Float>, labels : List<String>){
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
        val barData = BarData(dataSet)

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