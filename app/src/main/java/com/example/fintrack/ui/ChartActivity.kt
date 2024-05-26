package com.example.fintrack.ui

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Canvas
import android.media.MediaScannerConnection
import android.os.Bundle
import android.os.Environment
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
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class ChartActivity : AppCompatActivity() {

    // View binding para acessar as views no layout
    private lateinit var binding: ActivityChartBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Recupera os dados enviados da MainActivity
        val expenses = intent.getParcelableArrayListExtra<ExpenseEntity>("expenses") ?: listOf()
        val categories = intent.getParcelableArrayListExtra<CategoryEntity>("categories") ?: listOf()

        // Configura o gráfico de pizza com os dados recuperados
        setupPieChart(expenses, categories)

        // Configura o botão de salvar o gráfico
        binding.btnSaveChart.setOnClickListener {
            saveChart()
        }

        // Configura o botão de voltar para a atividade anterior
        binding.btnBack.setOnClickListener {
            onBackPressed()
        }

    }

    // Configura o PieChart
    private fun setupPieChart(expenses: List<ExpenseEntity>, categories: List<CategoryEntity>) {
        val pieChart = binding.pieChart

        // Desativa a descrição do gráfico
        pieChart.description.isEnabled = false

        // Verifica se há despesas e categorias válidas
        if (expenses.isNotEmpty() && categories.isNotEmpty()) {

            // Calcula valores das fatias do gráfico com base nos dados das despesas
            val pieEntries = calculatePieEntries(expenses, categories)

            // Cria o conjunto de dados do PieChart
            val dataSet = PieDataSet(pieEntries, "").apply {
                sliceSpace = 3f
                selectionShift = 5f
                colors = ColorTemplate.MATERIAL_COLORS.toList()
            }

            // Cria o objeto de dados do PieChart
            val data = PieData(dataSet).apply {
                setValueTextSize(10f)
                setValueTextColor(Color.BLACK)
            }

            // Define os dados do gráfico
            pieChart.data = data
            pieChart.animateY(1000)

            // Configurações da legenda do gráfico
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
            // Lida com o caso em que não há expenses ou categorias válidas
            Toast.makeText(this, "No valid data available", Toast.LENGTH_SHORT).show()
        }
    }

    // Calcula as entradas para o gráfico de pizza
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

            // Armazena o total no mapa
            categoryTotalMap[category.name] = totalAmount
        }

        // Converte o mapa em uma lista de PieEntries
        return categoryTotalMap.map { (category, total) ->
            PieEntry(total.toFloat(), category)
        }
    }

    // Salva o gráfico como uma imagem
    private fun saveChart() {

        // Captura a visualização do gráfico como um bitmap
        val bitmap = Bitmap.createBitmap(binding.pieChart.width, binding.pieChart.height, Bitmap.Config.ARGB_8888)

        // Define a cor de fundo do bitmap como branco
        bitmap.eraseColor(Color.WHITE)

        // Desenha o gráfico no canvas
        val canvas = Canvas(bitmap)
        binding.pieChart.draw(canvas)

        // Salva o bitmap como uma imagem PNG
        val file = saveBitmap(bitmap)

        // Adiciona o arquivo à galeria de fotos
        if (file != null) {
            // Escaneia o arquivo para adicioná-lo à galeria de fotos
            MediaScannerConnection.scanFile(
                this,
                arrayOf(file.absolutePath),
                arrayOf("image/png"),
                null
            )
            Toast.makeText(this, "Image saved in gallery!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Failed to save chart", Toast.LENGTH_SHORT).show()
        }
    }

    // Salva o bitmap em um arquivo
    private fun saveBitmap(bitmap: Bitmap): File? {
        // Gera um nome de arquivo com base na data e hora atual
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val fileName = "chart_$timeStamp.png"

        // Diretório onde a imagem será salva
        val folder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        val file = File(folder, fileName)

        var outputStream: OutputStream? = null
        try {
            // Cria um output stream para o arquivo
            outputStream = FileOutputStream(file)

            // Comprime e salva o bitmap no arquivo
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            outputStream.flush()

            // Retorna o arquivo salvo
            return file
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        } finally {
            try {
                // Fecha o output stream
                outputStream?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}