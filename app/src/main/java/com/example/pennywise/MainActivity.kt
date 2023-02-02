package com.example.pennywise

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

//These are used when creating test data:
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

class MainActivity : AppCompatActivity() {

    private lateinit var emailEditText : EditText
    private lateinit var passwordEditText : EditText

    private val auth = Firebase.auth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val db = Firebase.firestore
        emailEditText = findViewById(R.id.editTextEmail)
        passwordEditText = findViewById(R.id.editTextPassword)

        checkIfLoggedIn()

        //Lets set up the buttons! Log in...
        val logInButton = findViewById<Button>(R.id.buttonLogIn)
        logInButton.setOnClickListener {
            login()
        }

        //...and create user.
        val createUserButton = findViewById<Button>(R.id.buttonCreateUser)
        createUserButton.setOnClickListener {
            createUser()
        }


        //Adding test data to firebase firestore
        /*val transaction = Transaction(
            1000,
            DateTimeFormatter
            .ofPattern("yyyy-MM-dd HH:mm:ss")
            .withZone(ZoneOffset.UTC)
            .format(Instant.now()),
            "default"
        )

        db.collection(auth.uid.toString())
            .add(transaction)
            .addOnSuccessListener { documentReference ->
            Log.d("!!!!", "DocumentSnapshot added with ID: ${documentReference.id}")
        }*/
    }

    /**
     * Called when Create User Button is pressed. Creates a new user if e-mail and
     * password is entered - if not, returns with a Toast.
     */
    private fun createUser() {
        val email = emailEditText.text.toString()
        val password = passwordEditText.text.toString()

        /*
        Check to see if the user has entered e-mail and desired password. Returns early if that is
        not the case (and displays a Toast)
        */

        if(email.isEmpty() || password.isEmpty()) {
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
                    Log.d("!!!!", "create user successful")
                    goToOverView()
                } else {
                    Toast.makeText(this, "${getString(
                        R.string.user_not_created)} ${task.exception.toString()}",
                        Toast.LENGTH_SHORT).show()
                    Log.d("!!!!", "user not created ${task.exception}")
                }
            }
    }


    /**
     *Called when the login button is clicked. Uses the entered e-mail and password to log in the
     *user.
     */
    private fun login() {
        val email = emailEditText.text.toString()
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
                    Toast.makeText(this, "${getString(
                        R.string.user_not_logged_in)} ${task.exception.toString()}"
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