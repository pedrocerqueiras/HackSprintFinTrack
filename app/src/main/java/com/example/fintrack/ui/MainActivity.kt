package com.example.fintrack.ui

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.PopupMenu
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
import com.example.fintrack.data.entities.CategoryEntity
import com.example.fintrack.data.entities.ExpenseEntity
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
import kotlinx.coroutines.withContext


class MainActivity : AppCompatActivity() {

    // Listas para armazenar dados de categorias e despesas
    private var categories = listOf<CategoryUiData>()
    private var categoriesEntity = listOf<CategoryEntity>()
    private var expenses = listOf<ExpenseUiData>()

    // Variável para armazenar a categoria selecionada
    private var selectedCategory: String? = "ALL"

    // Views do layout
    private lateinit var rvCategory: RecyclerView
    private lateinit var ctnEmptyView: LinearLayout
    private lateinit var fabCreateExpense: FloatingActionButton
    private lateinit var tvSubTitleMain: TextView
    private lateinit var tvTotalExpenses: TextView
    private lateinit var btnMenu: ImageButton

    // Adapters para a listas de categorias e despesas
    private val categoryAdapter = CategoryListAdapter()
    private val expenseAdapter by lazy {
        ExpenseListAdapter { expense ->
            showCreateUpdateExpenseBottomSheet(expense)
        }
    }

    // Instâncias do banco de dados e DAOs
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

        // Inicialização das views
        ctnEmptyView = findViewById(R.id.ll_empty_view)
        rvCategory = findViewById(R.id.rv_category_list)
        tvSubTitleMain = findViewById(R.id.tv_title_main)
        tvTotalExpenses = findViewById(R.id.tv_total_expenses)
        fabCreateExpense = findViewById(R.id.fab_create_expense)
        val btnCreateEmpty = findViewById<Button>(R.id.btn_create_empty)
        val rvExpense = findViewById<RecyclerView>(R.id.rv_expense_list)
        btnMenu = findViewById(R.id.btn_menu)

        // Oculta as views até que os dados sejam carregados
        rvCategory.isVisible = false
        tvTotalExpenses.isVisible = false
        fabCreateExpense.isVisible = false
        tvSubTitleMain.isVisible = false
        btnMenu.isVisible = false
        ctnEmptyView.isVisible = false

        // Configuração do Menu superior-direito
        val popupMenu = PopupMenu(this, btnMenu)
        popupMenu.inflate(R.menu.menu_main)

        btnMenu.setOnClickListener {
            popupMenu.show()
        }

        // Tratamento dos itens do menu
        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_chart -> {

                    // Carrega dados do banco de dados e inicia a ChartActivity
                    GlobalScope.launch(Dispatchers.IO) {
                        val expenses = expenseDao.getAllExpenses()
                        val categories = categoryDao.getAllCategories()

                        withContext(Dispatchers.Main) {
                            val intent = Intent(this@MainActivity, ChartActivity::class.java)
                            intent.putParcelableArrayListExtra("expenses", ArrayList(expenses))
                            intent.putParcelableArrayListExtra("categories", ArrayList(categories))
                            startActivity(intent)
                        }
                    }
                    true
                }
                else -> false
            }
        }

        btnCreateEmpty.setOnClickListener {
            showCreateCategoryBottomSheet()
        }

        fabCreateExpense.setOnClickListener {
            showCreateUpdateExpenseBottomSheet()
        }

        // Configuração do adapter de categorias ao clicar e segurar
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

        // Configuração do adapter de categorias ao clicar
        categoryAdapter.setOnClickListener { selected ->
            selectedCategory = selected.name

            if (selected.name == "+") {
                showCreateCategoryBottomSheet()
            } else {
                val categoryTemp = categories.map { item ->
                    when {
                        item.name == selected.name && item.isSelected -> item.copy(isSelected = true)
                        item.name == selected.name && !item.isSelected -> item.copy(isSelected = true)
                        item.name != selected.name && item.isSelected -> item.copy(isSelected = false)
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

        // Configuração dos adapters dos RecyclerViews
        rvCategory.adapter = categoryAdapter
        rvExpense.adapter = expenseAdapter

        // Carrega categorias e despesas do banco de dados
        GlobalScope.launch(Dispatchers.IO) {
            getCategoriesFromDataBase()
        }

        GlobalScope.launch(Dispatchers.IO) {
            getExpensesFromDataBase()
        }
    }

    // Exibie um dialog de confirmação para deletar categoria
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

    // Busca categorias do banco de dados
    private fun getCategoriesFromDataBase() {
        GlobalScope.launch(Dispatchers.IO) {
            val categoriesFromDb: List<CategoryEntity> = categoryDao.getAllCategories()
            categoriesEntity = categoriesFromDb

            withContext(Dispatchers.Main) {
                if (categoriesEntity.isEmpty()) {
                    rvCategory.isVisible = false
                    fabCreateExpense.isVisible = false
                    tvSubTitleMain.isVisible = false
                    btnMenu.isVisible = false
                    ctnEmptyView.isVisible = true
                    tvTotalExpenses.isVisible = false
                } else {
                    rvCategory.isVisible = true
                    tvTotalExpenses.isVisible = true
                    btnMenu.isVisible = true
                    fabCreateExpense.isVisible = true
                    tvSubTitleMain.isVisible = true
                    ctnEmptyView.isVisible = false
                }

                val categoriesUiData = categoriesFromDb.map {
                    CategoryUiData(
                        name = it.name,
                        isSelected = it.name == selectedCategory, // Seleciona a nova categoria
                        icon = it.iconResId,
                        color = it.color
                    )
                }.toMutableList()

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
                        isSelected = selectedCategory == "ALL",
                        icon = 0,
                        color = 0
                    )
                )

                categoryListTemp.addAll(categoriesUiData)
                categories = categoryListTemp
                categoryAdapter.submitList(categories)

                refreshExpensesForSelectedCategory()
            }
        }
    }

    // Busca expenses do banco de dados
    private fun getExpensesFromDataBase() {
        GlobalScope.launch(Dispatchers.IO) {
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
                    color = category?.color ?: R.color.color_black
                )
            }
            withContext(Dispatchers.Main) {
                expenses = expenseUiData
                expenseAdapter.submitList(expenseUiData)
                updateTotalExpenses()

                selectedCategory?.let { category ->
                    if (category != "ALL") {
                        filterExpenseByCategoryName(category)
                    } else {
                        expenseAdapter.submitList(expenseUiData)
                    }
                }
            }
        }
    }

    // Insere nova categoria no banco de dados
    private fun insertCategory(categoryEntity: CategoryEntity) {
        GlobalScope.launch(Dispatchers.IO) {
            categoryDao.insert(categoryEntity)

            selectedCategory = categoryEntity.name

            getCategoriesFromDataBase()

            withContext(Dispatchers.Main) {
                refreshExpensesForSelectedCategory()
            }
        }
    }

    // Insere nova expense no banco de dados
    private fun insertExpense(expenseEntity: ExpenseEntity) {
        GlobalScope.launch(Dispatchers.IO) {
            expenseDao.insert(expenseEntity)

            withContext(Dispatchers.Main) {
                refreshExpensesForSelectedCategory()
            }
        }
    }

    // Atualiza uma expense existente no banco de dados
    private fun updateExpense(expenseEntity: ExpenseEntity) {
        GlobalScope.launch(Dispatchers.IO) {
            expenseDao.update(expenseEntity)
            refreshExpensesForSelectedCategory()
        }
    }

    // Deleta uma expense do banco de dados
    private fun deleteExpense(expenseEntity: ExpenseEntity) {
        GlobalScope.launch(Dispatchers.IO) {
            expenseDao.delete(expenseEntity)
            refreshExpensesForSelectedCategory()
        }
    }

    // Deleta uma categoria do banco de dados
    private fun deleteCategory(categoryEntity: CategoryEntity) {
        GlobalScope.launch(Dispatchers.IO) {
            val expensesToBeDeleted = expenseDao.getAllByCategoryName(categoryEntity.name)
            expenseDao.deleteAll(expensesToBeDeleted)
            categoryDao.delete(categoryEntity)
            getCategoriesFromDataBase()

            withContext(Dispatchers.Main) {
                selectedCategory = "ALL"
                refreshExpensesForSelectedCategory()
            }
        }
    }

    // Filtra expenses por nome de categoria
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
            withContext(Dispatchers.Main) {
                expenses = expenseUiData
                expenseAdapter.submitList(expenseUiData)
                updateTotalExpenses()
            }
        }
    }

    // Exibe o BottomSheet para criar ou atualizar uma despesa
    private fun showCreateUpdateExpenseBottomSheet(expenseUiData: ExpenseUiData? = null) {
        val createExpenseBottomSheet = CreateOrUpdateExpenseBottomSheet(
            expense = expenseUiData,
            selectedCategory = if (selectedCategory == "ALL") null else selectedCategory,
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

    // Exibe o BottomSheet para criar uma nova categoria
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
        createCategoryBottomSheet.show(
            supportFragmentManager,
            "createCategoryBottomSheet"
        )
    }

    // Atualiza o total de despesas exibido na tela
    private fun updateTotalExpenses() {
        val total = expenses.sumOf { it.amount }
        tvTotalExpenses.text = getString(R.string.value_total_expenses, total)
    }

    // Atualiza a lista de despesas com base na categoria selecionada
    private fun refreshExpensesForSelectedCategory() {
        selectedCategory?.let { category ->
            if (category == "ALL") {
                GlobalScope.launch(Dispatchers.IO) {
                    getExpensesFromDataBase()
                }
            } else {
                filterExpenseByCategoryName(category)
            }
        }
    }
}