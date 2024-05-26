package com.example.fintrack.ui.category

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.GridLayout
import androidx.core.content.ContextCompat
import com.example.fintrack.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText

class CreateCategoryBottomSheet(
    private val onCreateClicked: (String, Int, Int) -> Unit
) : BottomSheetDialogFragment() {

    // Variáveis para armazenar o ID do ícone e da cor selecionados
    private var selectedIconId: Int? = null
    private var selectedColorId: Int? = null

    // Views para armazenar os itens selecionados atualmente
    private var selectedIconView: View? = null
    private var selectedColorView: View? = null

    // Variável para armazenar o plano de fundo anterior do item de cor selecionado
    private var previousColorBackground: Drawable? = null


    // Listas de IDs de ícones e cores disponíveis
    private val availableIconIds = listOf(
        R.drawable.ic_car,
        R.drawable.ic_clothes,
        R.drawable.ic_credit_card,
        R.drawable.ic_electricity,
        R.drawable.ic_game_control,
        R.drawable.ic_gas_station,
        R.drawable.ic_graphic,
        R.drawable.ic_home,
        R.drawable.ic_key,
        R.drawable.ic_shopping_cart,
        R.drawable.ic_water_drop,
        R.drawable.ic_wifi
    )
    private val availableColorIds = listOf(
        R.color.white,
        R.color.black,
        R.color.brown,
        R.color.blue,
        R.color.red,
        R.color.green,
        R.color.grey,
        R.color.pink,
        R.color.violet,
        R.color.purple,
        R.color.medium_yellow,
        R.color.water_green,
        R.color.medium_orange,
        R.color.light_yellow,
        R.color.light_orange,
        R.color.ocean_blue
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.create_category_bottom_sheet, container, false)

        // Inicialização dos elementos da interface
        val btnCreate = view.findViewById<Button>(R.id.btn_category_create)
        val tieCategoryName = view.findViewById<TextInputEditText>(R.id.tie_category_name)
        val iconContainer = view.findViewById<GridLayout>(R.id.category_container)
        val colorContainer = view.findViewById<GridLayout>(R.id.color_container)

        // Configuração dos cliques nos itens de ícones
        for (i in 0 until iconContainer.childCount) {
            val iconView = iconContainer.getChildAt(i)
            iconView.setOnClickListener {

                // Remove o plano de fundo de um ícone anteriormente selecionado
                selectedIconView?.background = null

                // Atualiza o ícone selecionado e seu plano de fundo
                selectedIconView = iconView
                selectedIconId = availableIconIds[i]
                iconView.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.filter_border_background)
            }
        }

        // Configuração dos cliques nos itens de cores
        for (i in 0 until colorContainer.childCount) {
            val colorView = colorContainer.getChildAt(i)
            colorView.setOnClickListener {

                // Restaura o plano de fundo da cor selecionada anteriormente
                selectedColorView?.background = previousColorBackground

                // Atualiza a cor selecionada e seu plano de fundo
                selectedColorView = colorView
                selectedColorId = availableColorIds[i]

                // Armazena o plano de fundo atual antes de mudar para o plano de fundo selecionado
                previousColorBackground = colorView.background
                colorView.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.baseline_check_24)
            }
        }

        // Manipulador para o botão de criação de categoria
        btnCreate.setOnClickListener {
            val name = tieCategoryName.text.toString()

            // Verifica se todos os campos foram preenchidos antes de criar a categoria
            if (name.isNotEmpty() && selectedIconId != null && selectedColorId != null) {
                // Chama a função de retorno com os dados da nova categoria
                onCreateClicked.invoke(
                    name,
                    selectedIconId!!,
                    selectedColorId!!
                )
                // Fecha o BottomSheet após criar a categoria
                dismiss()
            } else {
                Snackbar.make(btnCreate, "Please fill all fields", Snackbar.LENGTH_LONG).show()
            }
        }
        return view
    }
}