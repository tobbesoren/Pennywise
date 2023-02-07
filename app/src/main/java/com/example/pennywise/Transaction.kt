package com.example.pennywise

import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

data class Transaction (val amount: Long = 0,
                        val category: String = "",
                        //Auto-timestamp
                        val timeStamp: String = DateTimeFormatter
                            .ofPattern("yyyy-MM-dd HH:mm:ss")
                            .withZone(ZoneOffset.systemDefault())
                            .format(Instant.now()))
