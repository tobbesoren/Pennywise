package com.example.pennywise

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class AddTransactionActivity : AppCompatActivity() {

    private lateinit var amountEditText: EditText
    private lateinit var radioGroup: RadioGroup

    private val uid = Firebase.auth.uid.toString()

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_transaction)

        radioGroup = findViewById(R.id.radio_group)
        amountEditText = findViewById(R.id.amountEditText)


        // The amount transferred from CameraScannerActivity
        val scannedAmount = intent.getStringExtra("Amount")
        if (scannedAmount != null) {
            amountEditText.setText(scannedAmount)
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
            val category = rButtonChecked()
            val transaction = Transaction(amount, category)

            if (amount <= 0) {
                Toast.makeText(this, getString(R.string.fill_in_an_amount_to_save_transaction)
                    , Toast.LENGTH_LONG).show()
            } else {
                DataHandler.addTransaction(uid, transaction)
                Log.d("!!!", " transaction contains 1.Amount: ${transaction.amount}. " +
                        "2. Category: ${transaction.category}. " +
                        "2. Date: ${transaction.timeStamp}")
                finish()
            }
        }
    }


    /**
     * Converts the amount input from the edit text to cents. Replaces amount2()
     * Returns a Long.
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

    /*
    I found this solution on Stack Overflow - I don't fully understand it yet, but I'm
    looking into it...
     */
    /**
     * Adds a DecimalLimiter to an EditText. Default value is 2 decimal places.
     */
    fun EditText.addDecimalLimiter(maxLimit: Int = 2) {

        this.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable?) {
                val str = this@addDecimalLimiter.text!!.toString()
                if (str.isEmpty()) return
                val str2 = decimalLimiter(str, maxLimit)

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
}