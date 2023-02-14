package com.example.pennywise

import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

//Maybe we should add more data here? I'm thinking:
// -comment, to be able to add info about the transaction
// -year, month, day - apart from the timestamp, to make searching easier (not sure about this)
data class Transaction (val amount: Long = 0,
                        val category: String = "",
                        //Auto-timestamp
                        val timeStamp: String = DateTimeFormatter
                            .ofPattern("yyyy-MM-dd HH:mm:ss")
                            .withZone(ZoneOffset.systemDefault())
                            .format(Instant.now()))
