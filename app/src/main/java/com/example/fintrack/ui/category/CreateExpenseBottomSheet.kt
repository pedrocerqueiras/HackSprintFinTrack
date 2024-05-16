package com.example.fintrack.ui.category

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import com.example.fintrack.CategoryUiData
import com.example.fintrack.ExpenseUiData
import com.example.fintrack.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText

class CreateExpenseBottomSheet (
    private val categoryList: List<CategoryUiData>,
    private val onCreateClicked: (ExpenseUiData) -> Unit
) : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.create_expense_bottom_sheet, container)

        val btnCreate = view.findViewById<Button>(R.id.btn_expense_create)
        val tieTaskName = view.findViewById<TextInputEditText>(R.id.tie_expense_name)

        var expenseCategory: String? = null

        btnCreate.setOnClickListener{
            val name = tieTaskName.text.toString()
            if (expenseCategory != null){

                requireNotNull(expenseCategory)

                onCreateClicked.invoke(
                    ExpenseUiData(
                        name = name,
                        category = requireNotNull(expenseCategory),
                        amount = Float.MAX_VALUE  //verificar
                    )
                )
                dismiss()
            } else {
                Snackbar.make(btnCreate, "Please select a category", Snackbar.LENGTH_LONG).show()
            }
        }

        val expenseStr: List<String> = categoryList.map { it.name }

        val spinner: Spinner = view.findViewById(R.id.sp_expense_list)
        ArrayAdapter(
            requireActivity().baseContext,
            android.R.layout.simple_spinner_item,
            expenseStr.toList()
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_item)
            spinner.adapter = adapter
        }

        spinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
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

        return view
    }
}