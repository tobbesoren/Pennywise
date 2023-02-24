package com.example.pennywise

import android.util.Log
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase

object DataHandler {
    //This list holds all the transactions loaded from Firestore
    val allTransactions = mutableListOf<Transaction>()
    //Removed .balance, since we don't use it anymore.

    /**
     * Used to ad a transaction to Firestore. Takes current user ID and a Transaction as argument.
     */
    //Should make Toast instead of Log.
    fun addTransaction(userID: String,transaction: Transaction) {
        if(Firebase.auth.uid != null) {
            Firebase.firestore.collection(
                "users/${userID}/transactions").add(transaction)
                .addOnSuccessListener { documentReference ->
                    Log.d(
                        "!!!!", "DocumentSnapshot added with ID: ${documentReference.id}")
                }.addOnFailureListener { exception ->
                    Log.d("!!!!", exception.toString())
                }
        } else {
            Log.d("!!!!", "Could not add transaction - User not logged in")
        }
    }


    /**
     * Not used at the moment (and probably won't be). Gets a snapshot of the users data from
     * Firestore. Now, we load the data from OverView activity instead - this way, we can update
     * textView etc. onSuccess.
     */
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


    /**
     * This builds the allTransactionsList from the QuerySnapshot. Can only be used when the data is
     * successfully downloaded from the database. Makes Transaction objects from the data, adds them
     * to allTransactions and sorts the list by Transaction.timestamp in descending order.
     */
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

    /**
     * This is used to create a filtered list, used in OverView and RecentTransactions (actually,
     * its not used in Recent Transactions just yet, but it will be). Takes startDate and endDate
     * as Strings, in the format yyy-MM-dd and category, as a String. Returns a MutableList of
     * Transaction-objects, containing all Transactions within the chosen range belonging to the
     * selected category.
     */
    fun filteredList(startDate: String, endDate: String, category: String)
    : MutableList<Transaction> {
        val filteredList = mutableListOf<Transaction>()
        for(transaction in allTransactions){
            if(transaction.timeStamp.slice(0..9) in startDate..endDate
                && (transaction.category == category || category == "All")) {
                filteredList.add(transaction)
            }
        }
        Log.d("!!!", filteredList.toString())
        return filteredList
    }


    /**
     * Is used to print all objects in allTransactions to the log.
     */
    fun logData() {
        for (item in allTransactions) {
            Log.d("!!!!", item.toString())
        }
    }


    /**
     * Not used anymore! I leave it here if it turns out to be useful later. It takes a category
     * as a String and returns the total balance of this category as a Balance object.
     * listToBalance is now used instead.
     */
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


    /**
     * Takes a list of Transactions and returns the total balance, as a Balance object.
     */
    fun listToBalance(transactions: MutableList<Transaction>) : Balance {
        var sum : Long = 0
        var balance = Balance()
        for (transaction in transactions) sum += transaction.amount

        balance.setAmount(sum)

        return balance
    }


}