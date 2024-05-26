package com.example.fintrack

import android.content.Context
import android.content.res.Resources
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


// Função para obter a cor de um recurso de cor
fun getColor(context: Context, colorResId: Int): Int {
    return ContextCompat.getColor(context, colorResId)
}

// Classe do Adapter para a lista de expenses
class ExpenseListAdapter(
    private val onClick: (ExpenseUiData) -> Unit
) : ListAdapter<ExpenseUiData, ExpenseListAdapter.ExpenseViewHolder>(ExpenseDiffCallback()) {

    // Classe interna ViewHolder para cada item da lista
    inner class ExpenseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Elementos de visualização do item da lista
        private val tvExpenseTitle: TextView = itemView.findViewById(R.id.tv_expense_title)
        private val tvExpenseAmount: TextView = itemView.findViewById(R.id.tv_expense_amount)
        private val ivCategoryIcon: ImageView = itemView.findViewById(R.id.iv_category_icon)
        private val viewCategoryColor: View = itemView.findViewById(R.id.view_category_color)

        // Método para vincular os dados do item aos elementos de visualização
        fun bind(expense: ExpenseUiData) {
            // Define o título e o valor da expense
            tvExpenseTitle.text = expense.name
            tvExpenseAmount.text = "-R$%.2f".format(expense.amount)

            // Define o ícone da categoria
            ivCategoryIcon.setImageResource(expense.iconResId)


            // Obtém a cor da categoria (com fallback para preto se não for encontrada)
            val color = try {
                getColor(itemView.context, expense.color)
            } catch (e: Resources.NotFoundException) {
                getColor(itemView.context, R.color.color_black)
            }

            // Define a cor da view de categoria
            viewCategoryColor.setBackgroundColor(color)

            // Configura o click no item
            itemView.setOnClickListener {
                onClick(expense)
            }
        }
    }

    // Criação de novas visualizações de item da lista
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_expense, parent, false)
        return ExpenseViewHolder(view)
    }

    // Vinculação dos dados do item aos elementos de visualização
    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    // Classe de utilitário para calcular as diferenças entre as listas de itens
    class ExpenseDiffCallback : DiffUtil.ItemCallback<ExpenseUiData>() {
        override fun areItemsTheSame(oldItem: ExpenseUiData, newItem: ExpenseUiData): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ExpenseUiData, newItem: ExpenseUiData): Boolean {
            return oldItem == newItem
        }
    }
}