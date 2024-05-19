package com.example.fintrack.ui.category

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

    private var selectedIconId: Int? = null
    private var selectedColorId: Int? = null


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
        R.id.color_white,
        R.id.color_black,
        R.id.color_brown,
        R.id.color_blue,
        R.id.color_red,
        R.id.color_green,
        R.id.color_grey,
        R.id.color_pink,
        R.id.color_violet,
        R.id.color_purple,
        R.id.color_yellow,
        R.id.color_water_green,
        R.id.color_orange,
        R.id.color_light_yellow,
        R.id.color_light_orange,
        R.id.color_ocean_blue
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.create_category_bottom_sheet, container, false)

        val btnCreate = view.findViewById<Button>(R.id.btn_category_create)
        val tieCategoryName = view.findViewById<TextInputEditText>(R.id.tie_category_name)
        val iconContainer = view.findViewById<GridLayout>(R.id.category_container)
        val colorContainer = view.findViewById<GridLayout>(R.id.color_container)


        for (i in 0 until iconContainer.childCount) {
            val iconView = iconContainer.getChildAt(i)
            iconView.setOnClickListener {
                selectedIconId = availableIconIds[i]

            }
        }

        for (i in 0 until colorContainer.childCount) {
            val colorView = colorContainer.getChildAt(i)
            colorView.setOnClickListener {
                selectedColorId = availableColorIds[i]
            }
        }

        btnCreate.setOnClickListener{
            val name = tieCategoryName.text.toString()
            if (name.isNotEmpty() && selectedIconId != null && selectedColorId != null) {
                onCreateClicked.invoke(
                    name,
                    selectedIconId!!,
                    selectedColorId!!
                )
                dismiss()

            }else{
                Snackbar.make(btnCreate, "Please fill all fields", Snackbar.LENGTH_LONG).show()
            }
        }
        return view
    }
}