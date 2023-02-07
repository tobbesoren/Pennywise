package com.example.pennywise

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageButton

class CameraScanner : AppCompatActivity() {

    private lateinit var input : EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera_scanner)

        input = findViewById(R.id.editTextNumberDecimal)

        val toAddTransactionButton = findViewById<ImageButton>(R.id.toTransactionIB)
        toAddTransactionButton.setOnClickListener {


            val intent = Intent(this, AddTransactionActivity::class.java)
            intent.putExtra("Amount",input.text.toString().toInt())
            Log.d("!!!","amount before startactivity = ${input.text.toString().toInt()}")
            finish()
            startActivity(intent)

        }
    }
}