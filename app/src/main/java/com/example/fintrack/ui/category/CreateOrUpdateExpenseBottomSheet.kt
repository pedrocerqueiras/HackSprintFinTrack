package com.example.fintrack.ui.category

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import com.example.fintrack.CategoryUiData
import com.example.fintrack.ExpenseUiData
import com.example.fintrack.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText

class CreateOrUpdateExpenseBottomSheet(
    private val categoryList: List<CategoryUiData>,
    private val expense: ExpenseUiData? = null,
    private val onCreateClicked: (ExpenseUiData) -> Unit,
    private val onUpdateClicked: (ExpenseUiData) -> Unit
) : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.create_or_update_expense_bottom_sheet, container)

        val tvTitle = view.findViewById<TextView>(R.id.tv_title)
        val btnCreate = view.findViewById<Button>(R.id.btn_expense_create)
        val tieExpenseName = view.findViewById<TextInputEditText>(R.id.tie_expense_name)
        val spinner: Spinner = view.findViewById(R.id.sp_expense_list)

        val expenseStr: List<String> = categoryList.map { it.name }
        var expenseCategory: String? = null



        ArrayAdapter(
            requireActivity().baseContext,
            android.R.layout.simple_spinner_item,
            expenseStr.toList()
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_item)
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
            tvTitle.setText(R.string.add_expense_title)
            btnCreate.setText(R.string.add)
        } else {
            tvTitle.setText(R.string.update_expense_title)
            btnCreate.setText(R.string.update)
            tieExpenseName.setText(expense.name)

            val currentCategory = categoryList.first { it.name == expense.category }
            val index = categoryList.indexOf(currentCategory)
            spinner.setSelection(index)
        }

        btnCreate.setOnClickListener {
            val name = tieExpenseName.text.toString()
            if (expenseCategory != null) {

                requireNotNull(expenseCategory)

                if (expense == null) {

                    onCreateClicked.invoke(
                        ExpenseUiData(
                            id = 0,
                            name = name,
                            category = requireNotNull(expenseCategory),
                            amount = Float.MIN_VALUE  //verificar
                        )
                    )
                } else {
                    onUpdateClicked.invoke(
                        ExpenseUiData(
                            id = expense.id,
                            name = name,
                            category = requireNotNull(expenseCategory),
                            amount = Float.MIN_VALUE
                        )
                    )
                }
                dismiss()
            } else {
                Snackbar.make(btnCreate, "Please select a category", Snackbar.LENGTH_LONG).show()
            }
        }
        return view
    }
}