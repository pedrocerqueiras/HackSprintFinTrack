package com.example.fintrack.ui

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.widget.Button
import androidx.fragment.app.DialogFragment
import com.example.fintrack.R
import com.google.android.material.textfield.TextInputEditText
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class DateFilterDialog : DialogFragment() {

    private lateinit var tieStartDate: TextInputEditText
    private lateinit var tieEndDate: TextInputEditText
    private lateinit var applyButton: Button

    private lateinit var listener: DateFilterListener

    companion object {
        fun newInstance(listener: DateFilterListener): DateFilterDialog {
            val fragment = DateFilterDialog()
            fragment.listener = listener
            return fragment
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (!::listener.isInitialized) {
            listener = context as? DateFilterListener
                ?: throw ClassCastException("$context must implement DateFilterListener")
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): AlertDialog {
        val dialogView = requireActivity().layoutInflater.inflate(R.layout.dialog_filter_by_date, null)
        val dialogBuilder = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setTitle("Filter by date")

        tieStartDate = dialogView.findViewById(R.id.tie_start_date)
        tieEndDate = dialogView.findViewById(R.id.tie_end_date)
        applyButton = dialogView.findViewById(R.id.btn_apply_filter)

        tieStartDate.setOnClickListener {
            showDatePicker { date ->
                tieStartDate.setText(date)
            }
        }

        tieEndDate.setOnClickListener {
            showDatePicker { date ->
                tieEndDate.setText(date)
            }
        }

        applyButton.setOnClickListener {
            val startDate = tieStartDate.text.toString().toCalendar()
            val endDate = tieEndDate.text.toString().toCalendar()
            listener.onDateFilterApplied(startDate, endDate)
            dismiss()
        }

        return dialogBuilder.create()
    }

    private fun showDatePicker(callback: (String) -> Unit) {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                val selectedDate = String.format("%02d/%02d/%04d", dayOfMonth, month + 1, year)
                callback(selectedDate)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    interface DateFilterListener {
        fun onDateFilterApplied(startDate: Calendar?, endDate: Calendar?)
    }
}

fun String.toCalendar(): Calendar? {
    return if (this.isNotEmpty()) {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val date = dateFormat.parse(this)
        Calendar.getInstance().apply { time = date }
    } else {
        null
    }
}