package com.example.pennywise

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase

class OverView : AppCompatActivity() {

    private lateinit var expensePresentView: TextView

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_over_view)

        expensePresentView = findViewById(R.id.expenseTextView)

        loadData(uid)



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
    fun loadData(userID: String) {
        Firebase.firestore.collection("users/${userID}/transactions")
            .get()
            .addOnSuccessListener { documentSnapShot ->
                makeListAndBalance(documentSnapShot)
                expensePresentView.text = DataHandler.balance.toString()
                logData()
            }.addOnFailureListener { exception ->
                Log.d("!!!!", exception.toString())
            }
    }
    private fun makeListAndBalance(documentSnapShot: QuerySnapshot) {
        DataHandler.itemsToView.clear()
        DataHandler.balance = 0
        for (document in documentSnapShot.documents) {
            val item = document.toObject<Transaction>()
            if (item != null) {

                DataHandler.itemsToView.add(item)
                DataHandler.balance += item.amount
            }
        }
    }

    private fun logData() {
        for (item in DataHandler.itemsToView) {
            Log.d("!!!!", item.toString())
        }
        Log.d("!!!!", DataHandler.balance.toString())
    }
}