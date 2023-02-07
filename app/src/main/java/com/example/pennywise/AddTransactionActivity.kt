package com.example.pennywise

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import android.widget.ImageButton
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.core.text.isDigitsOnly
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
        // Needs to be fixed - if nothing is added in CameraScannerActivity, the app crashes!
        val scannedAmount = intent.getIntExtra("Amount",0)
        if (scannedAmount > 0) {
            amountEditText.setText(scannedAmount.toString())
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
                Toast.makeText(this, "Fill in an amount to save transaction"
                    , Toast.LENGTH_LONG).show()
                Log.d("!!!","amount after[ amount = amount2() ] = $amount")
            } else {

                DataHandler.addTransaction(uid,transaction)
                Log.d("!!!", " transaction contains 1.Amount: ${transaction.amount}. " +
                        "2. Category: ${transaction.category}. " +
                        "2. Date: ${transaction.timeStamp}")
                finish()
            }
        }
    }
    // Sets text from amountEditText as Amount if filled in.
    private fun amount2 () : Int {

        var amount = 0

        if (amountEditText.text.isNotEmpty() && amountEditText.text.isDigitsOnly()) {
            amount = amountEditText.text.toString().toInt()
        }
        return amount

    }





    /**
     * Converts the amount input to ören. Replaces amount2()
     */
    private fun convertAmount(): Long {
        return (amountEditText.text.toString().toFloat() * 100).toLong()
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