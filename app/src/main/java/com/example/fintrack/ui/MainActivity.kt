package com.example.fintrack.ui

import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.fintrack.CategoryListAdapter
import com.example.fintrack.ui.category.CategoryUiData
import com.example.fintrack.ExpenseListAdapter
import com.example.fintrack.ui.expense.ExpenseUiData
import com.example.fintrack.R
import com.example.fintrack.data.CategoryEntity
import com.example.fintrack.data.ExpenseEntity
import com.example.fintrack.data.FinTrackDataBase
import com.example.fintrack.data.dao.CategoryDao
import com.example.fintrack.data.dao.ExpenseDao
import com.example.fintrack.ui.category.CreateCategoryBottomSheet
import com.example.fintrack.ui.category.InfoBottomSheet
import com.example.fintrack.ui.expense.CreateOrUpdateExpenseBottomSheet
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private var categories = listOf<CategoryUiData>()
    private var categoriesEntity = listOf<CategoryEntity>()
    private var expenses = listOf<ExpenseUiData>()

    private lateinit var rvCategory: RecyclerView
    private lateinit var ctnEmptyView: LinearLayout
    private lateinit var fabCreateExpense: FloatingActionButton
    private lateinit var tvSubTitleMain: TextView
    private lateinit var tvTotalExpenses: TextView

    private val categoryAdapter = CategoryListAdapter()
    private val expenseAdapter by lazy {
        ExpenseListAdapter { expense ->
            showCreateUpdateExpenseBottomSheet(expense)
        }
    }

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

        ctnEmptyView = findViewById(R.id.ll_empty_view)
        rvCategory = findViewById(R.id.rv_category_list)
        tvSubTitleMain = findViewById(R.id.tv_title_main)
        tvTotalExpenses = findViewById(R.id.tv_total_expenses)
        fabCreateExpense = findViewById(R.id.fab_create_expense)
        val btnCreateEmpty = findViewById<Button>(R.id.btn_create_empty)
        val rvExpense = findViewById<RecyclerView>(R.id.rv_expense_list)

        btnCreateEmpty.setOnClickListener {
            showCreateCategoryBottomSheet()
        }

        fabCreateExpense.setOnClickListener {
            showCreateUpdateExpenseBottomSheet()
        }

        categoryAdapter.setOnLongClickListener { categoryToBeDeleted ->

            if (categoryToBeDeleted.name != "+" && categoryToBeDeleted.name != "ALL") {

                val title: String = this.getString(R.string.category_delete_title)
                val description: String = this.getString(R.string.category_delete_description)
                val btnText: String = this.getString(R.string.delete)

                showInfoDialog(
                    title,
                    description,
                    btnText,
                ) {
                    val categoryEntityToBeDeleted = CategoryEntity(
                        categoryToBeDeleted.name,
                        categoryToBeDeleted.isSelected,
                        categoryToBeDeleted.icon,
                        categoryToBeDeleted.color
                    )
                    deleteCategory(categoryEntityToBeDeleted)
                }
            }
        }

        categoryAdapter.setOnClickListener { selected ->

            if (selected.name == "+") {
                showCreateCategoryBottomSheet()
            } else {

                val categoryTemp = categories.map { item ->
                    when {
                        item.name == selected.name && item.isSelected -> item.copy(
                            isSelected = true
                        )

                        item.name == selected.name && !item.isSelected -> item.copy(
                            isSelected = true
                        )

                        item.name != selected.name && item.isSelected -> item.copy(
                            isSelected = false
                        )

                        else -> item
                    }
                }

                if (selected.name != "ALL") {
                    filterExpenseByCategoryName(selected.name)
                } else {
                    GlobalScope.launch(Dispatchers.IO) {
                        getExpensesFromDataBase()
                    }
                }

                categoryAdapter.submitList(categoryTemp)
            }
        }

        rvCategory.adapter = categoryAdapter
        GlobalScope.launch(Dispatchers.IO) {
            getCategoriesFromDataBase()
        }

        rvExpense.adapter = expenseAdapter

        GlobalScope.launch(Dispatchers.IO) {
            getExpensesFromDataBase()
        }
    }

    private fun showInfoDialog(
        title: String,
        description: String,
        btnText: String,
        onClick: () -> Unit
    ) {
        val infoBottomSheet = InfoBottomSheet(
            title = title,
            description = description,
            btnText = btnText,
            onClick
        )

        infoBottomSheet.show(
            supportFragmentManager,
            "infoBottomSheet"
        )
    }

    private fun getCategoriesFromDataBase() {
        val categoriesFromDb: List<CategoryEntity> = categoryDao.getAllCategories()
        categoriesEntity = categoriesFromDb

        GlobalScope.launch(Dispatchers.Main) {

            if (categoriesEntity.isEmpty()) {
                rvCategory.isVisible = false
                fabCreateExpense.isVisible = false
                tvSubTitleMain.isVisible = false
                ctnEmptyView.isVisible = true
                tvTotalExpenses.isVisible = false
            } else {
                rvCategory.isVisible = true
                tvTotalExpenses.isVisible = true
                fabCreateExpense.isVisible = true
                tvSubTitleMain.isVisible = true
                ctnEmptyView.isVisible = false
            }
        }

        val categoriesUiData = categoriesFromDb.map {
            CategoryUiData(
                name = it.name,
                isSelected = it.isSelected,
                icon = it.iconResId,
                color = it.color
            )
        }
            .toMutableList()

        categoriesUiData.add(
            CategoryUiData(
                name = "+",
                isSelected = false,
                icon = 0,
                color = 0
            )
        )
        val categoryListTemp = mutableListOf(
            CategoryUiData(
                name = "ALL",
                isSelected = true,
                icon = 0,
                color = 0
            )
        )

        categoryListTemp.addAll(categoriesUiData)
        GlobalScope.launch(Dispatchers.Main) {
            categories = categoryListTemp
            categoryAdapter.submitList(categories)
        }
    }

    private fun getExpensesFromDataBase() {
        val expensesFromDb: List<ExpenseEntity> = expenseDao.getAllExpenses()
        val categoryMap = categoriesEntity.associateBy { it.name }

        val expenseUiData = expensesFromDb.map {
            val category = categoryMap[it.category]
            ExpenseUiData(
                id = it.id,
                name = it.name,
                category = it.category,
                amount = it.amount,
                iconResId = category?.iconResId ?: R.drawable.ic_graphic,
                color = category?.color ?: Color.BLACK
            )
        }

        GlobalScope.launch(Dispatchers.Main) {
            expenses = expenseUiData
            expenseAdapter.submitList(expenseUiData)

            updateTotalExpenses() // Atualiza o total de despesas
        }
    }

    private fun insertCategory(categoryEntity: CategoryEntity) {
        GlobalScope.launch(Dispatchers.IO) {
            categoryDao.insert(categoryEntity)
            getCategoriesFromDataBase()
        }
    }

    private fun insertExpense(expenseEntity: ExpenseEntity) {
        GlobalScope.launch(Dispatchers.IO) {
            expenseDao.insert(expenseEntity)
            getExpensesFromDataBase()
        }
    }

    private fun updateExpense(expenseEntity: ExpenseEntity) {
        GlobalScope.launch(Dispatchers.IO) {
            expenseDao.update(expenseEntity)
            getExpensesFromDataBase()
        }
    }

    private fun deleteExpense(expenseEntity: ExpenseEntity) {
        GlobalScope.launch(Dispatchers.IO) {
            expenseDao.delete(expenseEntity)
            getExpensesFromDataBase()
        }
    }

    private fun deleteCategory(categoryEntity: CategoryEntity) {
        GlobalScope.launch(Dispatchers.IO) {
            val expensesToBeDeleted = expenseDao.getAllByCategoryName(categoryEntity.name)
            expenseDao.deleteAll(expensesToBeDeleted)
            categoryDao.delete(categoryEntity)
            getCategoriesFromDataBase()
            getExpensesFromDataBase()
        }
    }


    private fun filterExpenseByCategoryName(category: String) {
        GlobalScope.launch(Dispatchers.IO) {
            val expensesFromDb: List<ExpenseEntity> = expenseDao.getAllByCategoryName(category)
            val categoryEntity = categoriesEntity.find { it.name == category }

            val expenseUiData = expensesFromDb.map {
                ExpenseUiData(
                    id = it.id,
                    name = it.name,
                    category = it.category,
                    amount = it.amount,
                    iconResId = categoryEntity?.iconResId ?: R.drawable.ic_graphic,
                    color = categoryEntity?.color ?: Color.BLACK
                )
            }

            GlobalScope.launch(Dispatchers.Main) {
                expenses = expenseUiData
                expenseAdapter.submitList(expenseUiData)

                updateTotalExpenses() // Atualiza o total de despesas
            }
        }
    }

    private fun showCreateUpdateExpenseBottomSheet(expenseUiData: ExpenseUiData? = null) {

        val createExpenseBottomSheet = CreateOrUpdateExpenseBottomSheet(
            expense = expenseUiData,
            categoryList = categoriesEntity,
            onCreateClicked = { expenseToBeCreated ->
                val expenseEntityToBeInsert = ExpenseEntity(
                    name = expenseToBeCreated.name,
                    category = expenseToBeCreated.category,
                    amount = expenseToBeCreated.amount
                )
                insertExpense(expenseEntityToBeInsert)

            },
            onUpdateClicked = { expenseToBeUpdated ->
                val expenseEntityToBeUpdate = ExpenseEntity(
                    id = expenseToBeUpdated.id,
                    name = expenseToBeUpdated.name,
                    category = expenseToBeUpdated.category,
                    amount = expenseToBeUpdated.amount
                )
                updateExpense(expenseEntityToBeUpdate)
            },
            onDeleteClicked = { expenseToBeDeleted ->
                val expenseEntityToBeDeleted = ExpenseEntity(
                    id = expenseToBeDeleted.id,
                    name = expenseToBeDeleted.name,
                    category = expenseToBeDeleted.category,
                    amount = expenseToBeDeleted.amount
                )
                deleteExpense(expenseEntityToBeDeleted)
            }
        )
        createExpenseBottomSheet.show(
            supportFragmentManager,
            "createExpenseBottomSheet"
        )

    }

    private fun showCreateCategoryBottomSheet() {
        val createCategoryBottomSheet =
            CreateCategoryBottomSheet { categoryName, iconRedId, color ->
                val categoryEntity = CategoryEntity(
                    name = categoryName,
                    isSelected = false,
                    iconResId = iconRedId,
                    color = color
                )
                insertCategory(categoryEntity)
            }

        createCategoryBottomSheet.show(supportFragmentManager, "createCategoryBottomSheet")
    }

    private fun updateTotalExpenses() {
        val total = expenses.sumOf { it.amount }
        tvTotalExpenses.text = getString(R.string.value_total_expenses, total)
    }
}