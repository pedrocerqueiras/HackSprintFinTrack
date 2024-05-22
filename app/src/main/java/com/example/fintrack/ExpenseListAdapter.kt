package com.example.fintrack

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.fintrack.ui.expense.ExpenseUiData

class ExpenseListAdapter(
    private val onClick: (ExpenseUiData) -> Unit
) : ListAdapter<ExpenseUiData, ExpenseListAdapter.ExpenseViewHolder>(ExpenseDiffCallback()) {

    inner class ExpenseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvExpenseTitle: TextView = itemView.findViewById(R.id.tv_expense_title)
        private val tvExpenseAmount: TextView = itemView.findViewById(R.id.tv_expense_amount)
        private val ivCategoryIcon: ImageView = itemView.findViewById(R.id.iv_category_icon)
        private val viewCategoryColor: View = itemView.findViewById(R.id.view_category_color)

        fun bind(expense: ExpenseUiData) {
            tvExpenseTitle.text = expense.name
            tvExpenseAmount.text = "-R$%.2f".format(expense.amount)


            ivCategoryIcon.setImageResource(expense.iconResId)

            viewCategoryColor.setBackgroundColor(
                ContextCompat.getColor(
                    itemView.context,
                    expense.color
                )
            )

            itemView.setOnClickListener {
                onClick(expense)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_expense, parent, false)
        return ExpenseViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ExpenseDiffCallback : DiffUtil.ItemCallback<ExpenseUiData>() {
        override fun areItemsTheSame(oldItem: ExpenseUiData, newItem: ExpenseUiData): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ExpenseUiData, newItem: ExpenseUiData): Boolean {
            return oldItem == newItem
        }
    }
}