package com.example.fintrack.ui.expense

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import androidx.core.view.isVisible
import com.example.fintrack.R
import com.example.fintrack.data.entities.CategoryEntity
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText

class CreateOrUpdateExpenseBottomSheet(
    private val categoryList: List<CategoryEntity>,
    private val expense: ExpenseUiData? = null,
    private val selectedCategory: String?,
    private val onCreateClicked: (ExpenseUiData) -> Unit,
    private val onUpdateClicked: (ExpenseUiData) -> Unit,
    private val onDeleteClicked: (ExpenseUiData) -> Unit
) : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.create_or_update_expense_bottom_sheet, container)

        // Inicialização dos elementos da interface
        val tvTitle = view.findViewById<TextView>(R.id.tv_title)
        val btnCreateOrUpdate = view.findViewById<Button>(R.id.btn_expense_create_or_update)
        val btnDelete = view.findViewById<Button>(R.id.btn_expense_delete)
        val tieExpenseName = view.findViewById<TextInputEditText>(R.id.tie_expense_name)
        val tieExpenseAmount = view.findViewById<TextInputEditText>(R.id.tie_expense_amount)
        val spinner: Spinner = view.findViewById(R.id.sp_expense_list)

        // Variável para armazenar a categoria selecionada
        var expenseCategory: String? = null

        // Construção da lista de categorias para o spinner
        val categoryListTemp = mutableListOf("Select")
        categoryListTemp.addAll(
            categoryList.map { it.name }
        )
        val expenseStr: List<String> = categoryListTemp

        // Configuração do adapter para o spinner
        ArrayAdapter(
            requireActivity().baseContext,
            R.layout.spinner_item,
            expenseStr.toList()
        ).also { adapter ->
            adapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
            spinner.adapter = adapter
        }

        // Manipulador de evento para seleção de item no spinner
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                // Atualiza a categoria selecionada
                expenseCategory = expenseStr[position]
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }

        // Configuração da interface com base na presença ou ausência de uma expense existente
        if (expense == null) {
            // Configuração para adicionar uma nova despesa
            btnDelete.isVisible = false
            tvTitle.setText(R.string.add_expense_title)
            btnCreateOrUpdate.setText(R.string.add)

            // Pré-seleciona a categoria se não for "ALL"
            selectedCategory?.let {
                if (it != "ALL") {
                    val currentCategoryIndex = categoryListTemp.indexOf(it)
                    if (currentCategoryIndex != -1) {
                        spinner.setSelection(currentCategoryIndex)
                    }
                }
            }
        } else {
            // Configuração para atualizar uma despesa existente
            tvTitle.setText(R.string.update_expense_title)
            btnCreateOrUpdate.setText(R.string.update)
            tieExpenseName.setText(expense.name)
            tieExpenseAmount.setText(expense.amount.toString())
            btnDelete.isVisible = true

            // Seleciona a categoria da despesa existente no spinner
            val currentCategoryIndex = categoryListTemp.indexOf(expense.category)
            if (currentCategoryIndex != -1) {
                spinner.setSelection(currentCategoryIndex)
            }
        }

        // Manipulador para o botão de delete
        btnDelete.setOnClickListener{
            if (expense != null){
                onDeleteClicked.invoke(expense)
                dismiss()
            }else{
                Log.d("CreateOrUpdateExpense", "Expense not found")
            }
        }

        // Manipulador para o botão de create ou update
        btnCreateOrUpdate.setOnClickListener {
            val name = tieExpenseName.text.toString().trim()
            val amount = tieExpenseAmount.text.toString().toDoubleOrNull() ?: 0.0

            // Verifica se a categoria e o nome da despesa estão preenchidos
            if (expenseCategory != "Select" && name.isNotEmpty()) {

                requireNotNull(expenseCategory)

                // Determina se é uma criação ou atualização de expense e executa a ação correspondente
                if (expense == null) {

                    // Cria uma nova expense
                    onCreateClicked.invoke(
                        ExpenseUiData(
                            id = 0,
                            name = name,
                            category = requireNotNull(expenseCategory),
                            amount = amount,
                            iconResId = 0,
                            color = 0
                        )
                    )
                } else {

                    // Atualiza uma expense existente
                    onUpdateClicked.invoke(
                        ExpenseUiData(
                            id = expense.id,
                            name = name,
                            category = requireNotNull(expenseCategory),
                            amount = amount,
                            iconResId = 0,
                            color = 0
                        )
                    )
                }
                // Fecha o BottomSheet após criar ou atualizar a despesa
                dismiss()

            } else {
                Snackbar.make(btnCreateOrUpdate, "Please select a category", Snackbar.LENGTH_LONG).show()
            }
        }
        return view
    }
}