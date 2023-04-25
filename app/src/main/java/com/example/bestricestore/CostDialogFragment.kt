package com.example.bestricestore

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.bestricestore.data.Constants

class CostDialogFragment : DialogFragment() {
     interface OnInputListener {
        fun sendInput(input1: String?, input2: String?)
    }

    lateinit var onInputListener: OnInputListener
    lateinit var edt_cost_from: EditText
    lateinit var edt_cost_to: EditText
    lateinit var button: Button
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view: View = inflater.inflate(
            R.layout.dialogfragment_search_cost, container, false
        )
        edt_cost_from = view.findViewById(R.id.cost_from)
        edt_cost_to = view.findViewById(R.id.cost_to)
        button = view.findViewById(R.id.btn_search_by_cost)
        button.setOnClickListener { v: View? ->
            val cost_from: String = edt_cost_from.text.toString()
            val cost_to: String = edt_cost_to.text.toString()
            if (((cost_from == Constants.EMPTY_STRING) || (cost_to == Constants.EMPTY_STRING))) {
                Toast.makeText(activity, "Please type the correct Cost!", Toast.LENGTH_SHORT)
                    .show()
            } else if (cost_from.toInt() >= cost_to.toInt()) {
                Toast.makeText(
                    activity,
                    "The cost from must be lower than cost to!",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                onInputListener.sendInput(
                    edt_cost_from.text.toString(),
                    edt_cost_to.text.toString()
                )
                dismiss()
            }
        }
        return view
    }

//    public override fun onAttach(context: Context) {
//        super.onAttach(context)
//        try {
//            onInputListener = getParentFragmentManager().getFragments() as OnInputListener?
//        } catch (e: ClassCastException) {
//            e.message
//        }
//    }
}