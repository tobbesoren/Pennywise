package com.example.pennywise

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
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
        val scanExpenseFAB = findViewById<FloatingActionButton>(R.id.scanExpenseFAB)
        scanExpenseFAB.setOnClickListener {
            val intent = Intent(this, CameraScanner::class.java)
            startActivity(intent)
        }

        // addExpenseFAB
        val  addExpenseFAB = findViewById<FloatingActionButton>(R.id.addExpenseFAB)
        addExpenseFAB.setOnClickListener {
            val intent = Intent(this, AddTransactionActivity::class.java)
            startActivity(intent)
        }

        // fragment Button
        val fragmentButton = findViewById<Button>(R.id.showFragmentButton)
        fragmentButton.setOnClickListener{
            showFragment()
        }
    }

    /**
     * Updates ExpensePresentView on resuming
     */
    override fun onResume() {
        super.onResume()
        loadData(uid)
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

    // FRAGMENT UP FOR DIBBS
    private fun showFragment() {
        val fragment = supportFragmentManager.findFragmentByTag("addExpenseFragment")


        if (fragment != null) {
            val transaction = supportFragmentManager.beginTransaction()
            transaction.remove(fragment)
            transaction.commit()

        } else {
            val fragmentUpForDibbs = FragmentUpForDibbs()
            val transaction = supportFragmentManager.beginTransaction()
            transaction.add(R.id.container, fragmentUpForDibbs, "addExpenseFragment")
            transaction.commit()
        }
    }

}