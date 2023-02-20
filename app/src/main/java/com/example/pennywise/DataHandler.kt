package com.example.pennywise

import android.util.Log
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase

object DataHandler {
    val allTransactions = mutableListOf<Transaction>()
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
        allTransactions.clear()
        var sum : Long = 0
        for (document in documentSnapShot.documents) {
            val item = document.toObject<Transaction>()
            if (item != null) {
                allTransactions.add(item)
                sum += item.amount
            }
        }
        allTransactions.sortByDescending { it.timeStamp }
        balance.dollars = sum / 100
        balance.cents = sum % 100
    }

    fun logData() {
        for (item in allTransactions) {
            Log.d("!!!!", item.toString())
        }
        Log.d("!!!!", balance.toString())
    }

    fun getBalanceByCategory(category: String) : Balance {
        var sum : Long = 0
        var balance = Balance()
        for (transaction in allTransactions)
            if (transaction.category == category) {
                sum += transaction.amount
            }
        balance.dollars = sum / 100
        balance.cents = sum % 100

        return balance
    }
}