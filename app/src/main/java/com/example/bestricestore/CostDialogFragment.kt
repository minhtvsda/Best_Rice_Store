package com.example.bestricestore

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation.findNavController
import com.bumptech.glide.Glide
import com.example.bestricestore.R
import com.example.bestricestore.data.Constants
import com.example.bestricestore.data.NewEntity

class CostDialogFragment constructor() : DialogFragment() {
     interface OnInputListener {
        fun sendInput(input1: String?, input2: String?)
    }

    lateinit var onInputListener: OnInputListener
    lateinit var edt_cost_from: EditText
    lateinit var edt_cost_to: EditText
    lateinit var button: Button
    public override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(
            R.layout.dialogfragment_search_cost, container, false
        )
        edt_cost_from = view.findViewById(R.id.cost_from)
        edt_cost_to = view.findViewById(R.id.cost_to)
        button = view.findViewById(R.id.btn_search_by_cost)
        button.setOnClickListener { v: View? ->
            val cost_from: String = edt_cost_from.getText().toString()
            val cost_to: String = edt_cost_to.getText().toString()
            if (((cost_from == Constants.EMPTY_STRING) || (cost_to == Constants.EMPTY_STRING))) {
                Toast.makeText(getActivity(), "Please type the correct Cost!", Toast.LENGTH_SHORT)
                    .show()
            } else if (cost_from.toInt() >= cost_to.toInt()) {
                Toast.makeText(
                    getActivity(),
                    "The cost from must be lower than cost to!",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                onInputListener!!.sendInput(
                    edt_cost_from.getText().toString(),
                    edt_cost_to.getText().toString()
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