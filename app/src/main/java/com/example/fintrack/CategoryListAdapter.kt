package com.example.fintrack

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.fintrack.ui.category.CategoryUiData

class CategoryListAdapter :
    ListAdapter<CategoryUiData, CategoryListAdapter.CategoryViewHolder>(CategoryListAdapter) {

    // Variáveis de callback para clicks curtos e longos em itens da lista
    private lateinit var onClick: (CategoryUiData) -> Unit
    private lateinit var onLongClick: (CategoryUiData) -> Unit

    // Método para definir o callback de click curto
    fun setOnClickListener(onClick: (CategoryUiData) -> Unit) {
        this.onClick = onClick
    }

    // Método para definir o callback de click longo
    fun setOnLongClickListener(onLongClick: (CategoryUiData) -> Unit) {
        this.onLongClick = onLongClick
    }

    // Criação de novas visualizações de item da lista
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view: View = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_category, parent, false)

        return CategoryViewHolder(view)
    }

    // Vinculação dos dados do item da lista às visualizações
    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = getItem(position)
        holder.bind(category, onClick, onLongClick)
    }

    // Classe interna que representa cada item na lista
    class CategoryViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        val tvCategory = view.findViewById<TextView>(R.id.tv_category)

        // Método para vincular os dados do item e os callbacks aos elementos de visualização
        fun bind(
            category: CategoryUiData,
            onClick: (CategoryUiData) -> Unit,
            onLongClickListener: (CategoryUiData) -> Unit
        ) {
            tvCategory.text = category.name
            tvCategory.isSelected = category.isSelected

            // Configuração do clique curto
            view.setOnClickListener {
                onClick.invoke(category)
            }

            // Configuração do clique longo
            view.setOnLongClickListener {
                onLongClickListener.invoke(category)
                true // Retorna true para indicar que o evento foi consumido
            }
        }
    }

    // Classe de utilitário para calcular as diferenças entre as listas de itens
    companion object CategoryDiffUtils : DiffUtil.ItemCallback<CategoryUiData>() {
        override fun areItemsTheSame(oldItem: CategoryUiData, newItem: CategoryUiData): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: CategoryUiData, newItem: CategoryUiData): Boolean {
            return oldItem.name == newItem.name
        }
    }
}