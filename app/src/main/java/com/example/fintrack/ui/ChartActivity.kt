package com.example.fintrack.ui

import android.graphics.Color

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.fintrack.data.entities.CategoryEntity
import com.example.fintrack.data.entities.ExpenseEntity
import com.example.fintrack.databinding.ActivityChartBinding
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate


class ChartActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChartBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Recupera os dados enviados da MainActivity
        val expenses = intent.getParcelableArrayListExtra<ExpenseEntity>("expenses") ?: listOf()
        val categories =
            intent.getParcelableArrayListExtra<CategoryEntity>("categories") ?: listOf()

        setupPieChart(expenses, categories)

    }

    private fun setupPieChart(expenses: List<ExpenseEntity>, categories: List<CategoryEntity>) {
        val pieChart = binding.pieChart

        // Desativa a descrição do gráfico
        pieChart.description.isEnabled = false

        // Verifica se há despesas e categorias válidas
        if (expenses.isNotEmpty() && categories.isNotEmpty()) {
            // Calcular valores das fatias do gráfico com base nos dados das despesas
            val pieEntries = calculatePieEntries(expenses, categories)

            // Cria o conjunto de dados do PieChart
            val dataSet = PieDataSet(pieEntries, "").apply {
                sliceSpace = 3f
                selectionShift = 5f
                colors = ColorTemplate.MATERIAL_COLORS.toList()
            }

            val data = PieData(dataSet).apply {
                setValueTextSize(10f)
                setValueTextColor(Color.BLACK)
            }

            pieChart.data = data
            pieChart.animateY(1000)

            // Configurações da legenda
            val legend = pieChart.legend
            legend.apply {
                verticalAlignment = Legend.LegendVerticalAlignment.TOP
                horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
                orientation = Legend.LegendOrientation.VERTICAL
                setDrawInside(false)
                xEntrySpace = 7f
                yEntrySpace = 0f
                yOffset = 0f
                textSize = 14f
            }
        } else {
            // Lida com o caso em que não há despesas ou categorias válidas
            Toast.makeText(this, "No valid data available", Toast.LENGTH_SHORT).show()
        }
    }

    private fun calculatePieEntries(
        expenses: List<ExpenseEntity>,
        categories: List<CategoryEntity>
    ): List<PieEntry> {
        // Mapa para armazenar os gastos totais por categoria
        val categoryTotalMap = mutableMapOf<String, Double>()

        // Calcula os gastos totais por categoria
        for (category in categories) {
            // Soma os valores das despesas filtradas pela categoria
            val totalAmount: Double = expenses
                .filter { it.category == category.name }
                .sumOf { it.amount }

            categoryTotalMap[category.name] = totalAmount
        }

        // Converte o mapa em uma lista de PieEntries
        return categoryTotalMap.map { (category, total) ->
            PieEntry(total.toFloat(), category)
        }
    }
}