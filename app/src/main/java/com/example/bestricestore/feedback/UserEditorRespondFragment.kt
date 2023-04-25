package com.example.bestricestore.feedback
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.bestricestore.R

class UserEditorRespondFragment : Fragment() {
    private lateinit var mViewModel: UserEditorRespondViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_user_editor_respond, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
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