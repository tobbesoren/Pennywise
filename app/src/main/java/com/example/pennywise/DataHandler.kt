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
            updateList(documentSnapShot)
            logData()
        }.addOnFailureListener { exception ->
                Log.d("!!!!", exception.toString())
            }
    }


    fun updateList(documentSnapShot: QuerySnapshot) {
        allTransactions.clear()
        for (document in documentSnapShot.documents) {
            val item = document.toObject<Transaction>()
            if (item != null) {
                allTransactions.add(item)
            }
        }
        allTransactions.sortByDescending { it.timeStamp }
    }

    fun filteredList(startDate: String, endDate: String, category: String)
    : MutableList<Transaction> {
        val filteredList = mutableListOf<Transaction>()
        for(transaction in allTransactions){
            if(transaction.timeStamp.slice(0..10) in startDate..endDate
                && (transaction.category == category || category == "All")) {
                filteredList.add(transaction)
            }
        }
        Log.d("!!!", filteredList.toString())
        return filteredList
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

    fun listToBalance(transactions: MutableList<Transaction>) : Balance {
        var sum : Long = 0
        var balance = Balance()
        for (transaction in transactions) sum += transaction.amount

        balance.setAmount(sum)

        return balance
    }


}