package com.example.pennywise

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.fragment.app.Fragment
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.sql.Timestamp
import java.time.Instant

class AddExpenseFragment() : Fragment() {

    private lateinit var amountEditText: EditText
    private lateinit var radioGroup: RadioGroup

    private val uid = Firebase.auth.uid.toString()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add_expense,container, false)

        radioGroup = view.findViewById(R.id.radioGroup)
        amountEditText = view.findViewById(R.id.editTextNumber)

        var amount : Int


        val saveButton = view.findViewById<ImageButton>(R.id.saveImageButton)
        saveButton.setOnClickListener {
            amount = amountEditText.text.toString().toInt()
            val category = checkButton(view)
            val transaction = Transaction(amount,category)

//            db.collection("Transaction").add(transaction)
            DataHandler.addTransaction(uid,transaction)

            Log.d("!!!", category)
            Log.d("!!!", amount.toString())

        }

        return view
    }

    fun checkButton(view: View): String {
        val rButtonId = radioGroup.checkedRadioButtonId

        val radioButton = view.findViewById<RadioButton>(rButtonId)

        return radioButton.text.toString()
    }

}