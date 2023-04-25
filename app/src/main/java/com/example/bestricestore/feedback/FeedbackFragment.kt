package com.example.bestricestore.feedback

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation.findNavController
import com.example.bestricestore.R

class FeedbackFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_feedback, container, false)
        val button: Button = view.findViewById<View>(R.id.add_feedback) as Button
        val button1: Button = view.findViewById<View>(R.id.check_respond) as Button
        val button2: Button = view.findViewById<View>(R.id.btn_check_user_feedback) as Button
        button.setOnClickListener({ v: View? ->
            findNavController(
                view
            ).navigate(R.id.addFeedbackFragment)
        })
        button1.setOnClickListener({ v: View? ->
            findNavController(
                view
            ).navigate(R.id.checkRespondFragment, Bundle())
        })
        button2.setOnClickListener { v: View? ->
            findNavController(
                view
            ).navigate(R.id.checkFeedbackFragment, Bundle())
        }
        return view
    }

}