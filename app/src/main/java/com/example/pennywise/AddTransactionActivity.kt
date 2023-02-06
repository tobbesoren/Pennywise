package com.example.pennywise

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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

        val returnButton = findViewById<ImageButton>(R.id.returnIB)
        returnButton.setOnClickListener {
            finish()
        }

        val saveButton = findViewById<FloatingActionButton>(R.id.saveFAB)
        saveButton.setOnClickListener {

            val amount = amount2()
            val category = rButtonChecked()
            val transaction = Transaction(amount, category)

            if (amount <= 0) {
                Toast.makeText(this, "Fill in an amount to save transaction"
                    , Toast.LENGTH_LONG).show()
            } else {

                DataHandler.addTransaction(uid,transaction)
                Log.d("!!!", " transaction contains 1.Amount: ${transaction.amount}. " +
                        "2. Category: ${transaction.category}. " +
                        "2. Date: ${transaction.timeStamp}")
                finish()
            }

        }


    }

    private fun amount2 () : Int {

        var amount = 0
        if (amountEditText.text.isNotEmpty() && amountEditText.text.isDigitsOnly()) {
            amount = amountEditText.text.toString().toInt()
        }
        return amount

    }

    private fun rButtonChecked() : String {

        val rButtonId = radioGroup.checkedRadioButtonId

        val radioButton = findViewById<RadioButton>(rButtonId)

        return radioButton.text.toString()
    }
}