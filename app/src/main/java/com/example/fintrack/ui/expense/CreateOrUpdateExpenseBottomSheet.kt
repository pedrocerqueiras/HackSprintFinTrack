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
import com.example.fintrack.ui.category.CategoryUiData
import com.example.fintrack.R
import com.example.fintrack.data.CategoryEntity
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText

class CreateOrUpdateExpenseBottomSheet(
    private val categoryList: List<CategoryEntity>,
    private val expense: ExpenseUiData? = null,
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

        val tvTitle = view.findViewById<TextView>(R.id.tv_title)
        val btnCreateOrUpdate = view.findViewById<Button>(R.id.btn_expense_create_or_update)
        val btnDelete = view.findViewById<Button>(R.id.btn_expense_delete)
        val tieExpenseName = view.findViewById<TextInputEditText>(R.id.tie_expense_name)
        val tieExpenseAmount = view.findViewById<TextInputEditText>(R.id.tie_expense_amount)
        val spinner: Spinner = view.findViewById(R.id.sp_expense_list)

        var expenseCategory: String? = null
        val categoryListTemp = mutableListOf("Select")
        categoryListTemp.addAll(
            categoryList.map { it.name }
        )
        val expenseStr: List<String> = categoryListTemp

        ArrayAdapter(
            requireActivity().baseContext,
            R.layout.spinner_item,
            expenseStr.toList()
        ).also { adapter ->
            adapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
            spinner.adapter = adapter
        }

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                expenseCategory = expenseStr[position]
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }

        if (expense == null) {
            btnDelete.isVisible = false
            tvTitle.setText(R.string.add_expense_title)
            btnCreateOrUpdate.setText(R.string.add)
        } else {
            tvTitle.setText(R.string.update_expense_title)
            btnCreateOrUpdate.setText(R.string.update)
            tieExpenseName.setText(expense.name)
            tieExpenseAmount.setText(expense.amount.toString())
            btnDelete.isVisible = true


            val currentCategoryIndex = categoryListTemp.indexOf(expense.category)
            // Verifica se a categoria foi encontrada e ajusta a seleção do spinner
            if (currentCategoryIndex != -1) {
                spinner.setSelection(currentCategoryIndex)
            }
        }

        btnDelete.setOnClickListener{
            if (expense != null){
                onDeleteClicked.invoke(expense)
                dismiss()
            }else{
                Log.d("CreateOrUpdateExpense", "Expense not found")
            }

        }

        btnCreateOrUpdate.setOnClickListener {
            val name = tieExpenseName.text.toString().trim()
            val amount = tieExpenseAmount.text.toString().toDoubleOrNull() ?: 0.0

            if (expenseCategory != "Select" && name.isNotEmpty()) {

                requireNotNull(expenseCategory)

                if (expense == null) {

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
                dismiss()
            } else {
                Snackbar.make(btnCreateOrUpdate, "Please select a category", Snackbar.LENGTH_LONG).show()
            }
        }
        return view
    }
}