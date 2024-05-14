package com.example.fintrack

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val adapterCategory = CategoryListAdapter()
        val rvCategory = findViewById<RecyclerView>(R.id.rv_category_list)

        val adapterExpense = ExpenseListAdapter()
        val rvExpense = findViewById<RecyclerView>(R.id.rv_expense_list)

        adapterCategory.setOnClickListener { selected ->
            val categoryTemp = categories.map { item ->
                when{
                    item.name == selected.name && !item.isSelected -> item.copy(isSelected = true)
                    item.name == selected.name && item.isSelected -> item.copy(isSelected = false)
                    else -> item
                }
            }

            val expenseTemp =
                if (selected.name != "All") {
                    amount.filter { it.category == selected.name }
                } else {
                    amount
                }
            adapterExpense.submitList(expenseTemp)

            adapterCategory.submitList(categoryTemp)
        }


        rvCategory.adapter = adapterCategory
        adapterCategory.submitList(categories)

        rvExpense.adapter = adapterExpense
        adapterExpense.submitList(amount)








    }
    val categories = listOf(
        CategoryUiData(
            name = "All",
            isSelected = false
        ),
        CategoryUiData(
            name = "Wi-fi",
            isSelected = false
        ),
        CategoryUiData(
            name = "Study",
            isSelected = false
        ),
        CategoryUiData(
            name = "Work",
            isSelected = false
        ),
        CategoryUiData(
            name = "Wellness",
            isSelected = false
        ),
        CategoryUiData(
            name = "Home",
            isSelected = false
        ),
        CategoryUiData(
            name = "Health",
            isSelected = false
        ),
    )

    val amount = listOf(
        ExpenseUiData(
            "Wi-fi",149.43f ),
        ExpenseUiData(
            "Wellness",149.43f ),
        ExpenseUiData(
            "Work",149.43f ),
        ExpenseUiData(
            "Home",149.43f ),
        ExpenseUiData(
            "Health",149.43f ),
        ExpenseUiData(
            "Study",149.43f ),
    )


}