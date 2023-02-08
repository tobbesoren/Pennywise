package com.example.pennywise

import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter

class GraphFormatter : ValueFormatter() {

    //Array to hold the labels, this could probably be empty at init
    private var labels = arrayOf("Mo", "Tu", "Wed", "Thur", "Fri", "Sat", "Sun")

    override fun getAxisLabel(value: Float, axis: AxisBase?): String {
        return labels.getOrNull(value.toInt()) ?: value.toString()
    }

    // override this for BarChart
    override fun getBarLabel(barEntry: BarEntry?): String {
        return labels.getOrNull(barEntry?.x!!.toInt())!!
    }

    //Sets the labels
    fun setLabels(newLabels : List<String>){
        labels = newLabels.toTypedArray()
    }

}