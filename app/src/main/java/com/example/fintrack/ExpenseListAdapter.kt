package com.example.fintrack

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class ExpenseListAdapter :
    ListAdapter<ExpenseUiData, ExpenseListAdapter.ExpenseViewHolder>(ExpenseListAdapter) {

    private lateinit var callback: (ExpenseUiData) -> Unit
    fun setOnCLickListener(onClick: (ExpenseUiData)-> Unit){
        callback = onClick
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_expense, parent, false)

        return ExpenseViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
        val expense = getItem(position)
        holder.bind(expense, callback)
    }

    class ExpenseViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        val tvTittle = view.findViewById<TextView>(R.id.tv_expense_tittle)
        val tvAmount = view.findViewById<TextView>(R.id.tv_expense_amount)

        fun bind(expense: ExpenseUiData, callback: (ExpenseUiData) -> Unit ) {
            val formattedAmount = " -R$ ${expense.amount.toString().replace(".", ",")}"
            tvTittle.text = expense.name
            tvAmount.text = formattedAmount

            view.setOnClickListener{
                callback.invoke(expense)
            }
        }
    }

    companion object ExpenseDiffUtil : ItemCallback<ExpenseUiData>() {
        override fun areItemsTheSame(oldItem: ExpenseUiData, newItem: ExpenseUiData): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: ExpenseUiData, newItem: ExpenseUiData): Boolean {
            return oldItem.category == newItem.category
        }
    }
}