package com.example.pennywise

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class OverView : AppCompatActivity() {

    private lateinit var expensePresentView: TextView

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_over_view)

        expensePresentView = findViewById(R.id.expenseTextView)

        // scanExpenseFAB functionality

        // addExpenseFAB functionality
        val addExpenseFAB = findViewById<FloatingActionButton>(R.id.addExpenseFAB)
        addExpenseFAB.setOnClickListener{

            showAddExpenseFragment()
        }
    }
    private fun showAddExpenseFragment() {
        val addExpenseFragment = supportFragmentManager.findFragmentByTag("addExpenseFragment")

        if (addExpenseFragment != null) {
            val transaction = supportFragmentManager.beginTransaction()
            transaction.remove(addExpenseFragment)
            transaction.commit()

        } else {
            val addExpenseFragment = AddExpenseFragment()
            val transaction = supportFragmentManager.beginTransaction()
            transaction.add(R.id.container, addExpenseFragment, "addExpenseFragment")
            transaction.commit()
        }
    }
}