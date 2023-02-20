package com.example.pennywise

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TransactionRecyclerAdapter (context: Context, private val transactions: List<Transaction>)
    : RecyclerView.Adapter<TransactionRecyclerAdapter.ViewHolder>() {

    var layoutInflater: LayoutInflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = layoutInflater.inflate(R.layout.list_item, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val transaction = transactions[position]

        //Change to actual views
        holder.amountTextView.text = transaction.amount.toString()
        holder.timestampTextView.text = transaction.timeStamp
        holder.categoryTextView.text = transaction.category
        holder.noteTextView.text = transaction.note
    }

    override fun getItemCount(): Int {
        return transactions.size
    }

    inner class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {

        //change to actual views
        val amountTextView = itemView.findViewById<TextView>(R.id.amountTextView)
        val timestampTextView = itemView.findViewById<TextView>(R.id.timestampTextView)
        val categoryTextView = itemView.findViewById<TextView>(R.id.categoryTextView)
        val noteTextView = itemView.findViewById<TextView>(R.id.noteTextView)
    }

}