package com.example.bestricestore.feedback
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation.findNavController
import com.bumptech.glide.Glide
import com.example.bestricestore.R
import com.example.bestricestore.data.Constants
import com.example.bestricestore.data.Feedback
import com.example.bestricestore.data.Respond

class UserEditorRespondFragment constructor() : Fragment() {
    private lateinit var mViewModel: UserEditorRespondViewModel
    public override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_user_editor_respond, container, false)
    }

    public override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mViewModel = ViewModelProvider(this).get(
            UserEditorRespondViewModel::class.java
        )
        // TODO: Use the ViewModel
    }

    companion object {
        fun newInstance(): UserEditorRespondFragment {
            return UserEditorRespondFragment()
        }
    }
}