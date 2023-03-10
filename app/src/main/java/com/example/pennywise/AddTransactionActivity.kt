package com.example.pennywise

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View.INVISIBLE
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.pennywise.databinding.ActivityAddTransactionBinding
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.io.Serializable
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*

class AddTransactionActivity : AppCompatActivity() {

    private lateinit var amountTextLayout: TextInputLayout
    private lateinit var amountEditText: EditText
    private lateinit var radioGroup: RadioGroup
    private lateinit var noteTextInput: TextInputLayout
    private lateinit var noteEditText: EditText
    private lateinit var deleteImageButton: ImageButton

    private lateinit var binding: ActivityAddTransactionBinding
    private val uid = Firebase.auth.uid.toString()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_transaction)

        val savedTransaction: Transaction? = getSerializable(this,
            "transaction", Transaction::class.java)

        binding = ActivityAddTransactionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.bottomNavigationView.selectedItemId = R.id.add_menu_button
        binding.bottomNavigationView.setOnItemSelectedListener {

            when(it.itemId){

                R.id.overview_menu_button -> goToOverViewActivity()
                R.id.recent_menu_button -> goToRecentTransactionActivity()
                else ->{

                }
            }
            true
        }

        var timeStamp: String = DateTimeFormatter
            .ofPattern("yyyy-MM-dd HH:mm:ss")
            .withZone(ZoneOffset.systemDefault())
            .format(Instant.now())

        var dateOfTransactionSliced = timeStamp.slice(0..9)
        var dateOfTransactionText = findViewById<TextView>(R.id.dateOfTransactionTV)
        dateOfTransactionText.text = dateOfTransactionSliced


        radioGroup = findViewById(R.id.radio_group)
        amountTextLayout = findViewById(R.id.amountTextLayout)
        amountEditText = findViewById((R.id.amountEditText))
        noteTextInput = findViewById(R.id.noteTextInput)
        noteEditText = findViewById((R.id.noteEditText))
        deleteImageButton = findViewById(R.id.deleteImageButton)

        val builder : MaterialDatePicker.Builder<*> = MaterialDatePicker.Builder.datePicker()
        builder.setTitleText("Select date of transaction")
        val picker : MaterialDatePicker<*> = builder.build()

        dateOfTransactionText.setOnClickListener {
            picker.show(supportFragmentManager, picker.toString())
        }

        picker.addOnPositiveButtonClickListener { chosenDate ->

            val datePicked = convertLongToDate(chosenDate.toString().toLong())
            timeStamp = datePicked
            dateOfTransactionText.setText(datePicked.slice(0..9))
            Log.d("!!!", datePicked)
        }



        // The amount transferred from CameraScannerActivity
        val scannedAmount = intent.getStringExtra("Amount")
        if (scannedAmount != null) {
            amountEditText.setText(scannedAmount)
        }

        //Data transferred from Recent Transactions
        if(savedTransaction != null) {
            deleteImageButton.setOnClickListener {
                Log.d("!!!", savedTransaction.toString())
                deleteTransactionReloadAndReturn(savedTransaction)
            }
            val balance = Balance()
            timeStamp = savedTransaction.timeStamp
            balance.setAmount(savedTransaction.amount)
            amountEditText.setText(balance.balanceString(this).dropLast(2))
            noteEditText.setText(savedTransaction.note)
            dateOfTransactionText.text = timeStamp.slice(0..9)
            when(savedTransaction.category) {
                getString(R.string.amusement) -> radioGroup.check(R.id.radio_Amusement)
                getString(R.string.household) -> radioGroup.check(R.id.radio_Household)
                getString(R.string.transportation) -> radioGroup.check(R.id.radio_Transportation)
                getString(R.string.dining) -> radioGroup.check(R.id.radio_Dining)
                getString(R.string.healthcare_wellness) -> radioGroup.check(R.id.radio_Hc_WellN)
                getString(R.string.groceries) -> radioGroup.check(R.id.radio_Groceries)
                getString(R.string.other) -> radioGroup.check(R.id.radio_Other)
            }

        } else {
            deleteImageButton.visibility = INVISIBLE
        }


        //This limits the input to two decimal places
        amountEditText.addDecimalLimiter()



        val returnButton = findViewById<ImageButton>(R.id.returnIB)
        returnButton.setOnClickListener {
            finish()
        }

        val saveButton = findViewById<FloatingActionButton>(R.id.saveFAB)
        saveButton.setOnClickListener {

            val amount = convertAmount()
            //make sure an amount is entered, otherwise there isn't a Transaction to add!
            if (amount <= 0) {
                Toast.makeText(this, getString(R.string.fill_in_an_amount_to_save_transaction)
                    , Toast.LENGTH_LONG).show()
            } else {
                //Here we create a Transaction based on the user input and save to Firestore.
                val category = rButtonChecked()
                val year: String = timeStamp.slice(0..3)
                val month: String = timeStamp.slice(5..6)
                val day: String = timeStamp.slice(8..9)
                val time: String = timeStamp.slice(11..18)
                val note = noteTextInput.editText?.text.toString()

                val transaction = Transaction(
                    null,
                    amount,
                    category,
                    timeStamp,
                    year,
                    month,
                    day,
                    time,
                    note)

                if(savedTransaction != null) {
                    DataHandler.updateTransaction(uid,transaction, savedTransaction.id)
                } else {
                    DataHandler.addTransaction(uid, transaction)
                }
                finish()
            }
        }
    }


    private fun deleteTransactionReloadAndReturn(transaction: Transaction) {
        if(Firebase.auth.uid != null) {
            val docRef = Firebase.firestore.collection("users/${uid}/transactions/")
            docRef.document("${transaction.id}").delete()
                .addOnSuccessListener {
                    Log.d("!!!!", "Transaction deleted")
                    finish()
                }
        } else {
            Log.d("!!!!", "Could not delete transaction - User not logged in")
        }
    }

    /**
     * Handles the getSerializableExtra for different versions.
     */
    @Suppress("DEPRECATION")
    private fun <T : Serializable?> getSerializable(activity: Activity, name: String, clazz: Class<T>): T?
    {
        return if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            activity.intent.getSerializableExtra(name, clazz)
        else
            activity.intent.getSerializableExtra(name) as T
    }

    private fun goToOverViewActivity () {
        val intent = Intent(this, OverView::class.java)
        startActivity(intent)
    }
    private fun goToRecentTransactionActivity () {
        val intent = Intent(this, RecentTransactions::class.java)
        startActivity(intent)
    }


    /**
     * Converts the amount input from the edit text to cents. Replaces amount2()
     * Returns a Long. By saving the money data as integers, we avoid float errors, which would
     * rapidly add up to show the wrong numbers.
     */
    private fun convertAmount(): Long {

        var amount: Long = 0

        if(amountEditText.text.isNotEmpty()) {
            val dollarsAndCents = amountEditText.text.toString()
                .split(getString(R.string.decimal_delimiter))
            amount = dollarsAndCents[0].toLong() * 100
            if(dollarsAndCents.size == 2) amount += dollarsAndCents[1].toLong()
        }
        return amount
    }


    // fun to set the category from RadioButton ID:
    private fun rButtonChecked() : String {

        val rButtonId = radioGroup.checkedRadioButtonId

        val radioButton = findViewById<RadioButton>(rButtonId)

        return radioButton.text.toString()
    }



    /**
     * Adds a DecimalLimiter to an EditText. Limits the number of decimals to two.
     * If the user enters just a decimal delimiter and the cents, it will add a zero before the dot.
     * Also prevents the user from entering anything but numbers and decimal delimiter.
     */
    fun EditText.addDecimalLimiter() {

        this.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable?) {
                val str = this@addDecimalLimiter.text!!.toString()
                if (str.isEmpty()) return
                val str2 = decimalLimiter(str, 2)

                if (str2 != str) {
                    this@addDecimalLimiter.setText(str2)
                    val pos = this@addDecimalLimiter.text!!.length
                    this@addDecimalLimiter.setSelection(pos)
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
        })
    }

    fun decimalLimiter(string: String, MAX_DECIMAL: Int): String {

        var inputString = string
        if (inputString[0].toString() == getString(R.string.decimal_delimiter)) {
            inputString = "0$inputString"
        }
        val max = inputString.length

        var rFinal = ""
        var after = false
        var i = 0
        var up = 0
        var decimal = 0
        var t: Char

        val decimalCount = inputString.count{
            getString(R.string.decimal_delimiter).contains(it)
        }

        if (decimalCount > 1)
            return inputString.dropLast(1)

        while (i < max) {
            t = inputString[i]
            if (t.toString() != getString(R.string.decimal_delimiter) && !after) {
                up++
            } else if (t.toString() == getString(R.string.decimal_delimiter)) {
                after = true
            } else {
                decimal++
                if (decimal > MAX_DECIMAL)
                    return rFinal
            }
            rFinal += t
            i++
        }
        return rFinal
    }
    private fun convertLongToDate(time:Long):String {

        val date = Date(time)
        val format = SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss",
            Locale.getDefault()
        )

        return format.format(date)
    }

}