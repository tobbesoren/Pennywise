package com.example.pennywise

import com.google.firebase.firestore.DocumentId
import java.io.Serializable
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

//Now we have a function in DataHandler to filter the allTransactions list, so maybe we don't need
// .year, .month, .day and .time anymore. They are currently used by RecentTransactions to
//populate the chart, so I won't remove them just yet.
data class Transaction (@DocumentId val id : String? = null,
                        val amount: Long = 0,
                        val category: String = "",
                        val timeStamp: String ="",
                        val year: String = "",
                        val month: String = "",
                        val day: String = "",
                        val time: String = "",
                        val note: String = "",): Serializable


