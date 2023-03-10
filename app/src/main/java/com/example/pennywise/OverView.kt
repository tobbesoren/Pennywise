package com.example.pennywise

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import com.example.pennywise.databinding.ActivityOverViewBinding
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters
import java.util.*

class OverView : AppCompatActivity() {

    private lateinit var expensePresentView: TextView
    private lateinit var expenseTextView: TextView

    private lateinit var binding: ActivityOverViewBinding

    private val uid = Firebase.auth.uid.toString()

    //These variables keep track of the selected date range and category, to help populate the
    //filteredTransactions list.
    private lateinit var categorySelected : String
    private var startDate = LocalDate.now().with(TemporalAdjusters.firstDayOfMonth()).toString()
    private var endDate = LocalDate.now().with(TemporalAdjusters.lastDayOfMonth()).toString()
    //This holds the currently selected items to show. Filtered by date and category.
    private lateinit var filteredTransactions : MutableList<Transaction>


    override fun onCreate(savedInstanceState: Bundle?) {

        categorySelected = getString(R.string.all)

        super.onCreate(savedInstanceState)
        binding = ActivityOverViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.bottomNavigationView.selectedItemId = R.id.overview_menu_button
        binding.bottomNavigationView.setOnItemSelectedListener {

            when(it.itemId){

                R.id.add_menu_button -> goToAddTransactionActivity()
                R.id.recent_menu_button -> goToRecentTransactionActivity()
                else ->{

                }
            }
            true
        }

        expensePresentView = findViewById(R.id.expenseTextView)
        expenseTextView = findViewById(R.id.expense2TextView)

        binding.fromTW.text = startDate.toString()
        binding.toTW.text = endDate.toString()

        loadDataUpdateMoneyText(uid)

//         Set up the toolbar.
//        (activity as AppCompatActivity).setSupportActionBar(view.app_bar)
        binding.fromTW.setOnClickListener {
            showDateRangePicker()
        }
        binding.toTW.setOnClickListener {
            showDateRangePicker()
        }

        // scanExpenseFAB functionality
        val scanExpenseFAB = findViewById<FloatingActionButton>(R.id.scanExpenseFAB)
        scanExpenseFAB.setOnClickListener {
            val intent = Intent(this, CameraScanner::class.java)
            startActivity(intent)
        }
    }

    private fun goToAddTransactionActivity () {
        val intent = Intent(this, AddTransactionActivity::class.java)
        startActivity(intent)
    }
    private fun goToRecentTransactionActivity () {
        val intent = Intent(this, RecentTransactions::class.java)
        startActivity(intent)
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_pennywise, menu)
//        menuInflater.inflate(R.menu.bottom_nav, menu2)
        return true
    }
    // functionality for expenseTextView
    //This is an abomination and needs to be fixed!!!
    // with kind regards, Tobbe the Hack
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.logout -> {
                signOut()
                return true
            } R.id.category -> {
                return true
            } R.id.allMenu -> {
                expenseTextView.setText(R.string.expenses)
                categorySelected = getString(R.string.all)
                filteredTransactions = DataHandler.filteredList(startDate,
                    endDate, categorySelected)
                setMoneyText(DataHandler.listToBalance(filteredTransactions))
                return true
            } R.id.amusementMenu -> {
                setExpensesByCategoryAndDate("Amusement")
            } R.id.householdMenu -> {
                setExpensesByCategoryAndDate("Household")
            } R.id.transportationMenu -> {
                setExpensesByCategoryAndDate("Transportation")
            } R.id.diningMenu -> {
                setExpensesByCategoryAndDate("Dining")
            } R.id.hcWellnMenu -> {
                setExpensesByCategoryAndDate("Healthcare / Wellness")
            } R.id.groceriesMenu -> {
                setExpensesByCategoryAndDate("Groceries")
            } R.id.otherMenu -> {
                setExpensesByCategoryAndDate("Other")
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
        loadDataUpdateMoneyText(uid)
    }

    private fun setExpensesByCategoryAndDate(category: String) : Boolean {
        expenseTextView.text = category
        categorySelected = category
        filteredTransactions = DataHandler.filteredList(startDate,
            endDate, categorySelected)
        setMoneyText(DataHandler.listToBalance(filteredTransactions))
        return true
    }
    /**
     * This is called onCreate and onResume.
     *Loads data from Firestore. Calls DataHandler.updateList to make the allTransactions list.
     * Creates the filteredTransactions list by calling DataHandler.filteredList with the selected
     * arguments(startDate, endDate and category). Sets ExpensePresentView to the correct amount
     * by calling setMoneyText with the Balance gotten from DataHandler.listToBalance, using
     * filteredTransactions. Yeah, it's a mouthful.
     */
    private fun loadDataUpdateMoneyText(userID: String) {
        Firebase.firestore.collection("users/${userID}/transactions").get()
            .addOnSuccessListener { documentSnapShot ->
                DataHandler.updateList(documentSnapShot)
                filteredTransactions = DataHandler.filteredList(
                    startDate,
                    endDate,
                    categorySelected)
                setMoneyText(DataHandler.listToBalance(filteredTransactions))

            }.addOnFailureListener { exception ->
                Log.d("!!!!", exception.toString())
            }
    }
    /**
     * This sets the expensePresentView with the current sum of the given Balance.
     */
    private fun setMoneyText(amount : Balance) {
        expensePresentView.text = amount.balanceString(this)
        DataHandler.logData()
    }
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

            binding.fromTW.text = startDate
            binding.toTW.text = endDate
            filteredTransactions = DataHandler.filteredList(startDate,
            endDate, categorySelected)
            val currentBalance = DataHandler.listToBalance(filteredTransactions)
            setMoneyText(currentBalance)
        }
    }
    private fun convertLongToDate(time:Long):String {

        val date = java.util.Date(time)
        val format = SimpleDateFormat(
            "yyyy-MM-dd",
            Locale.getDefault()
        )

        return format.format(date)
    }
}