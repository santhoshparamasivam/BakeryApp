package com.cbe.bakery.fragments
import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.fragment.app.Fragment
import com.cbe.bakery.R
import java.text.SimpleDateFormat
import java.util.*

class StockMovementSummary : Fragment(){
    lateinit var edtfromDate:EditText
    lateinit var edttoDate:EditText
    lateinit var spinner:Spinner
    lateinit var edtItem:EditText
    lateinit var edtReason:EditText
    lateinit var show:Button
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.move_stock_summary, container, false)
        edtfromDate=view.findViewById(R.id.edtfromDate);
        edttoDate=view.findViewById(R.id.edttoDate);
        spinner=view.findViewById(R.id.spinner);
        edtItem=view.findViewById(R.id.edtItem);
        edtReason=view.findViewById(R.id.edtReason);
        show=view.findViewById(R.id.show)
        val sdf = SimpleDateFormat("dd/M/yyyy")
        val currentDate = sdf.format(Date())
        edtfromDate.setText(currentDate)
        edttoDate.setText(currentDate)
        edtfromDate.isClickable=false
        edtfromDate.isCursorVisible=false
        edttoDate.isClickable=false
        edttoDate.isCursorVisible=false

        edtfromDate.setOnClickListener {
            pickDateTime(edtfromDate)
        }
        edttoDate.setOnClickListener {
            pickDateTime(edttoDate)
        }
   return view }
    private fun pickDateTime(dateSelect: EditText) {
        val currentDateTime = Calendar.getInstance()
        val startYear = currentDateTime.get(Calendar.YEAR)
        val startMonth = currentDateTime.get(Calendar.MONTH)
        val startDay = currentDateTime.get(Calendar.DAY_OF_MONTH)


        DatePickerDialog(
            requireContext(),
            { _, year, month, day ->
                dateSelect.setText("$day/$month/$year")
            },
            startYear,
            startMonth,
            startDay
        ).show()
    }
}