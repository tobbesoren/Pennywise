package com.example.pennywise

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TransactionRecyclerAdapter (context: Context, private var transactions: List<Transaction>)
    : RecyclerView.Adapter<TransactionRecyclerAdapter.ViewHolder>() {

    val context = context

    var layoutInflater: LayoutInflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = layoutInflater.inflate(R.layout.list_item, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val transaction = transactions[position]

        val balance = Balance()
        balance.setAmount(transaction.amount)
        holder.amountTextView.text = balance.balanceString(context)
        holder.timestampTextView.text = transaction.timeStamp
        holder.categoryTextView.text = transaction.category
        holder.noteTextView.text = transaction.note

        holder.itemView.setOnClickListener {
            val intent = Intent(context, AddTransactionActivity::class.java)
            intent.putExtra("transaction", transaction)
            context.startActivity(intent)
        }
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

    fun setNewData(filteredTransactions: MutableList<Transaction>) {
        transactions = filteredTransactions
        notifyDataSetChanged()
    }

}