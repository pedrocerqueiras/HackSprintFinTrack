package com.example.fintrack.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.fintrack.CategoryListAdapter
import com.example.fintrack.CategoryUiData
import com.example.fintrack.ExpenseListAdapter
import com.example.fintrack.ExpenseUiData
import com.example.fintrack.R
import com.example.fintrack.data.CategoryEntity
import com.example.fintrack.data.ExpenseEntity
import com.example.fintrack.data.FinTrackDataBase
import com.example.fintrack.data.dao.CategoryDao
import com.example.fintrack.data.dao.ExpenseDao
import com.example.fintrack.ui.category.CreateCategoryBottomSheet
import com.example.fintrack.ui.category.CreateExpenseBottomSheet
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private var categories = listOf<CategoryUiData>()
    private var expenses = listOf<ExpenseUiData>()

    val categoryAdapter = CategoryListAdapter()

    private val db by lazy {
        Room.databaseBuilder(
            applicationContext,
            FinTrackDataBase::class.java, "database-fin-track"
        ).build()
    }

    private val categoryDao: CategoryDao by lazy {
        db.getCategoryDao()
    }

    private val expenseDao: ExpenseDao by lazy {
        db.getExpenseDao()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val rvExpense = findViewById<RecyclerView>(R.id.rv_expense_list)
        val rvCategory = findViewById<RecyclerView>(R.id.rv_category_list)
        val fabCreateExpense = findViewById<FloatingActionButton>(R.id.fab_create_expense)

        fabCreateExpense.setOnClickListener{
            val createExpenseBottomSheet = CreateExpenseBottomSheet (
                categories
            ){expenseToBeCreated ->

            }

            createExpenseBottomSheet.show(
                supportFragmentManager,
                "createExpenseBottomSheet"
            )
        }

        val expenseAdapter = ExpenseListAdapter()

        categoryAdapter.setOnClickListener { selected ->

            if (selected.name == "+") {
                val createCategoryBottomSheet = CreateCategoryBottomSheet{categoryName ->
                    val categoryEntity = CategoryEntity(
                        name = categoryName,
                        isSelected = false
                    )

                    insertCategory(categoryEntity)

                }
                createCategoryBottomSheet.show(supportFragmentManager, "createCategoryBottomSheet")
            } else {

                val categoryTemp = categories.map { item ->
                    when {
                        item.name == selected.name && !item.isSelected -> item.copy(isSelected = true)
                        item.name == selected.name && item.isSelected -> item.copy(isSelected = false)
                        else -> item
                    }
                }

                val expenseTemp =
                    if (selected.name != "All") {
                        expenses.filter { it.category == selected.name }
                    } else {
                        expenses
                    }

                expenseAdapter.submitList(expenseTemp)
                categoryAdapter.submitList(categoryTemp)
            }
        }

        rvCategory.adapter = categoryAdapter
        GlobalScope.launch(Dispatchers.IO) {
            getCategoriesFromDataBase()
        }

        rvExpense.adapter = expenseAdapter
        getExpensesFromDataBase(expenseAdapter)

    }

    private fun getCategoriesFromDataBase() {
            val categoriesFromDb: List<CategoryEntity> = categoryDao.getAllCategories()
            val categoriesUiData = categoriesFromDb.map {
                CategoryUiData(
                    name = it.name,
                    isSelected = it.isSelected
                )
            }

                .toMutableList()

            categoriesUiData.add(
                CategoryUiData(
                    name = "+",
                    isSelected = false
                )
            )
        GlobalScope.launch(Dispatchers.Main) {
            categories = categoriesUiData
            categoryAdapter.submitList(categoriesUiData)
        }
    }

    private fun getExpensesFromDataBase(adapter: ExpenseListAdapter) {
        GlobalScope.launch(Dispatchers.IO) {
            val expensesFromDb: List<ExpenseEntity> = expenseDao.getAllExpenses()
            val expenseUiData = expensesFromDb.map {
                ExpenseUiData(
                    name = it.name,
                    category = it.category,
                    amount = it.amount
                )
            }

            GlobalScope.launch (Dispatchers.IO){
                expenses = expenseUiData
                adapter.submitList(expenseUiData)
            }
        }
    }

    private fun insertCategory(categoryEntity: CategoryEntity) {
        GlobalScope.launch(Dispatchers.IO) {
            categoryDao.insert(categoryEntity)
            getCategoriesFromDataBase()
        }
    }
}




