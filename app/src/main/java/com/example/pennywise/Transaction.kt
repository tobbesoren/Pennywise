package com.example.pennywise

data class Transaction (val amount: Int,
                        //I changed "date" to "timeStamp", since we save the time as well as
                        // the date. /Tobbe
                        val timeStamp: String, //Format: yyyy-MM-dd HH:mm:ss
                        val category: String)