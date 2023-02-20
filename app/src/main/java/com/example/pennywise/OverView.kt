package com.example.pennywise

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
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

        // Set up the toolbar.
//        (activity as AppCompatActivity).setSupportActionBar(view.app_bar)

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

        // chartFAB
        val chartFAB = findViewById<FloatingActionButton>(R.id.chartFAB)
        chartFAB.setOnClickListener {
            val intent = Intent(this, RecentTransactions::class.java)
            startActivity(intent)
        }

        // fragment Button
        val fragmentButton = findViewById<Button>(R.id.showFragmentButton)
        fragmentButton.setOnClickListener{
            showFragment()
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_pennywise, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.logout -> {
                signOut()
                return true
            } R.id.category -> {
                Toast.makeText(this,"choose MF!",Toast.LENGTH_LONG).show()
                return true
            } R.id.amusementMenu -> {
                setMoneyText(DataHandler.getBalanceByCategory(getString(R.string.amusement)))
                return true
            } R.id.householdMenu -> {
                setMoneyText(DataHandler.getBalanceByCategory(getString(R.string.household)))
                return true
            } R.id.transportationMenu -> {
                setMoneyText(DataHandler.getBalanceByCategory(getString(R.string.transportation)))
                return true
            } R.id.diningMenu -> {
                setMoneyText(DataHandler.getBalanceByCategory(getString(R.string.dining)))
                return true
            } R.id.hcWellnMenu -> {
                setMoneyText(DataHandler.getBalanceByCategory(getString(R.string.healthcare_wellness)))
                return true
            } R.id.groceriesMenu -> {
                setMoneyText(DataHandler.getBalanceByCategory(getString(R.string.groceries)))
                return true
            } R.id.otherMenu -> {
                setMoneyText(DataHandler.getBalanceByCategory(getString(R.string.other)))
                return true
            } else -> super.onOptionsItemSelected(item)
        }
    }

    private fun signOut() {
        Firebase.auth.signOut()
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
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
                setMoneyText(DataHandler.balance)
            }.addOnFailureListener { exception ->
                Log.d("!!!!", exception.toString())
            }
    }

    private fun setMoneyText(amount : Balance) {
        val dollars = amount.dollars.toString()
        val cents = amount.cents.toString().padStart(2,'0')
        expensePresentView.text = getString(R.string.expenses_view, dollars, cents)
        DataHandler.logData()

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