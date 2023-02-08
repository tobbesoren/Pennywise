package com.example.pennywise

import android.util.Log
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase

object DataHandler {
    val itemsToView = mutableListOf<Transaction>()
    var balance = Balance()


    fun addTransaction(userID: String,transaction: Transaction) {
        if(Firebase.auth.uid != null) {
            Firebase.firestore.collection("users/${userID}/transactions").add(transaction)
                .addOnSuccessListener { documentReference ->
                    Log.d("!!!!", "DocumentSnapshot added with ID: ${documentReference.id}")
                }.addOnFailureListener { exception ->
                    Log.d("!!!!", exception.toString())
                }
        } else {
            Log.d("!!!!", "Could not add transaction - User not logged in")
        }
    }



    fun loadAllData(userID: String) {
        Firebase.firestore.collection("users/${userID}/transactions")
            .get()
            .addOnSuccessListener { documentSnapShot ->
            makeListAndBalance(documentSnapShot)
            logData()
        }.addOnFailureListener { exception ->
                Log.d("!!!!", exception.toString())
            }
    }


    fun makeListAndBalance(documentSnapShot: QuerySnapshot) {
        itemsToView.clear()
        var sum : Long = 0
        for (document in documentSnapShot.documents) {
            val item = document.toObject<Transaction>()
            if (item != null) {
                itemsToView.add(item)
                sum += item.amount
            }
        }
        balance.kronor = sum / 100
        balance.ore = sum % 100
    }

    fun logData() {
        for (item in itemsToView) {
            Log.d("!!!!", item.toString())
        }
        Log.d("!!!!", balance.toString())
    }
}