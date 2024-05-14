package com.example.fintrack

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class ExpenseListAdapter :
    ListAdapter<ExpenseUiData, ExpenseListAdapter.ViewHolderExpense>(ExpenseDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderExpense {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_expense, parent, false)

        return ViewHolderExpense(view)
    }

    override fun onBindViewHolder(holder: ViewHolderExpense, position: Int) {
        val expense = getItem(position)
        holder.bind(expense)
    }

    class ViewHolderExpense(private val view: View) : RecyclerView.ViewHolder(view) {
        val tvTittle = view.findViewById<TextView>(R.id.tv_expense_tittle)
        val tvAmount = view.findViewById<TextView>(R.id.tv_expense_amount)

        fun bind(expenseUiData: ExpenseUiData) {
            val formattedAmount = " -R$ ${expenseUiData.amount.toString().replace(".", ",")}"
            tvTittle.text = expenseUiData.category
            tvAmount.text = formattedAmount
        }
    }

    class ExpenseDiffUtil : ItemCallback<ExpenseUiData>() {
        override fun areItemsTheSame(oldItem: ExpenseUiData, newItem: ExpenseUiData): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: ExpenseUiData, newItem: ExpenseUiData): Boolean {
            return oldItem.category == newItem.category
        }

    }

}