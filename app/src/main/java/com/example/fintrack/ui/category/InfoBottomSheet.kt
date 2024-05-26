package com.example.fintrack.ui.category

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.example.fintrack.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class InfoBottomSheet(
    private val title: String,
    private val description: String,
    private val btnText: String,
    private val onClicked: () -> Unit

) : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Infla o layout do bottom sheet
        val view = inflater.inflate(R.layout.info_bottom_sheet, container)

        // Obtém as referências dos elementos de visualização no layout
        val tvTitle = view.findViewById<TextView>(R.id.tv_info_title)
        val tvDesc = view.findViewById<TextView>(R.id.tv_info_description)
        val btnAction = view.findViewById<Button>(R.id.btn_info)

        // Define o texto do título, descrição e botão com base nos parâmetros fornecidos
        tvTitle.text = title
        tvDesc.text = description
        btnAction.text = btnText

        // Define o evento de click do botão para executar o callback e fechar o bottom sheet
        btnAction.setOnClickListener{
            onClicked.invoke()
            dismiss()
        }

        return view
    }
}