package com.example.pennywise

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class MainActivity : AppCompatActivity() {

    private lateinit var emailEditText : TextInputLayout
    private lateinit var passwordEditText : EditText

    private val auth = Firebase.auth


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        emailEditText = findViewById(R.id.textInputLayout2)
        passwordEditText = findViewById(R.id.password_edit_text)

        checkIfLoggedIn()

        //Lets set up the buttons! Log in...
        val signInButton = findViewById<Button>(R.id.signInButton)
        signInButton.setOnClickListener {
            login()
        }

        //...and create user.
        val signUpButton = findViewById<Button>(R.id.signUpButton)
        signUpButton.setOnClickListener {
            createUser()
        }
    }

    /**
     * Called when Create User Button is pressed. Creates a new user if e-mail and
     * password is entered - if not, returns with a Toast.
     */
    private fun createUser() {

        val email = emailEditText.editText?.text.toString()
        val password = passwordEditText.text.toString()

        /*
        Check to see if the user has entered e-mail and desired password. Returns early if that is
        not the case (and displays a Toast)
        */

        if(email.trim().isEmpty() || password.trim().isEmpty()) {
            Toast.makeText(this, getString(R.string.enter_email_and_password),
                Toast.LENGTH_SHORT).show()
            return
        }

        /*
        Tries to create a user. Shows a Toast on completion, which tells the user if the operation
        was successful or not.
         */
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if(task.isSuccessful) {
                    Toast.makeText(this,
                        getString(R.string.user_created),
                        Toast.LENGTH_SHORT).show()
                    Log.d("!!!!", "User created")
                    goToOverView()
                } else {
                    Toast.makeText(this, getString(
                        R.string.user_not_created, task.exception
                    ),
                        Toast.LENGTH_SHORT).show()
                    passwordEditText.error
                    Log.d("!!!!", "User not created ${task.exception}")
                }
            }
    }


    /**
     *Called when the login button is clicked. Uses the entered e-mail and password to log in the
     *user.
     */
    private fun login() {
        val email = emailEditText.editText?.text.toString()
        val password = passwordEditText.text.toString()

        //If the user hasn't entered email and/or password, we return early and get some Toast.
        if(email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, getString(R.string.to_log_in),
                Toast.LENGTH_SHORT).show()
            return
        }

        //Tries to login. Have some Toast!
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if(task.isSuccessful) {
                    Toast.makeText(this, getString(R.string.user_logged_in),
                        Toast.LENGTH_SHORT).show()
                    Log.d("!!!!", "User logged in")
                    goToOverView()
                } else {
                    Toast.makeText(this, getString(
                        R.string.user_not_logged_in, task.exception)
                        , Toast.LENGTH_SHORT).show()
                    Log.d("!!!!", "User not logged in: ${task.exception}")
                }
            }
    }


    /**
     *Starts OverView.
     */
    private fun goToOverView() {
        val intent = Intent(this, OverView::class.java)
        startActivity(intent)
    }


    /**
     *If a user is logged in, move directly to OverView.
     */
    private fun checkIfLoggedIn() {
        if(auth.currentUser != null) {
            goToOverView()
        }
    }
}