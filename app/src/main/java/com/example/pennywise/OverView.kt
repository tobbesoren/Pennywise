package com.example.pennywise

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase

class OverView : AppCompatActivity() {

    private lateinit var expensePresentView: TextView

    private val uid = Firebase.auth.uid.toString()

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_over_view)

        expensePresentView = findViewById(R.id.expenseTextView)

        loadData(uid)



        // scanExpenseFAB functionality

        // addExpenseFAB

        val  addExpenseFAB = findViewById<FloatingActionButton>(R.id.addExpenseFAB)
        addExpenseFAB.setOnClickListener {
            val intent = Intent(this, AddTransactionActivity::class.java)
            startActivity(intent)
        }

//        val addExpenseFAB = findViewById<FloatingActionButton>(R.id.addExpenseFAB)
//        addExpenseFAB.setOnClickListener{
//
//            showAddExpenseFragment()
//        }
    }

    /**
     * Updates ExpensePresentView on resuming
     */
    override fun onResume() {
        super.onResume()
        loadData(uid)
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
    private fun loadData(userID: String) {
        Firebase.firestore.collection("users/${userID}/transactions")
            .get()
            .addOnSuccessListener { documentSnapShot ->
                DataHandler.makeListAndBalance(documentSnapShot)
                expensePresentView.text = "${DataHandler.balance.kronor},${DataHandler.balance.ore} kr"
                DataHandler.logData()
            }.addOnFailureListener { exception ->
                Log.d("!!!!", exception.toString())
            }
    }

}