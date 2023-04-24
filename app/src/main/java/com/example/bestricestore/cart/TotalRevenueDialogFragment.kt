package com.example.bestricestore.cart

import android.app.Dialog
import android.os.Bundle
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.example.bestricestore.R

class TotalRevenueDialogFragment : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialogBuilder = Dialog(requireActivity())
        dialogBuilder.setContentView(R.layout.dialog_total_revenue)
        val text_total_revenue = dialogBuilder.findViewById<TextView>(R.id.total_revenue)
        parentFragmentManager
            .setFragmentResultListener("requestKey", this) { requestKey, bundle ->
                val result = bundle.getString("bundleKeytotalRevenue")
                text_total_revenue.text = "Your total Revenue is :$result VND.\n Have a good day!"
            }
        return dialogBuilder
    }
}