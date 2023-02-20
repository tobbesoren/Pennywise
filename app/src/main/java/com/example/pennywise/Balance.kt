package com.example.pennywise

import android.content.Context

data class Balance(var dollars: Long = 0,
                   var cents: Long = 0) {
    /**
     * Use this to get a String representation of the Balance item.
     * Needs a Context to have access to string resources.
     */
    fun balanceString(context: Context): String {
        val dollars = dollars.toString()
        val cents = cents.toString().padStart(2, '0')
        return context.getString(R.string.expenses_view, dollars, cents)
    }

    /**
     * Used to set dollars and cents - just give the amount in cents as a Long.
     */
    fun setAmount(amount: Long) {
        dollars = amount / 100
        cents = amount % 100
    }
}
