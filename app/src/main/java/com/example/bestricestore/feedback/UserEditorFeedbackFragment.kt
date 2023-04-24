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
import com.example.bestricestore.databinding.FragmentEditorRespondBinding
import com.example.bestricestore.databinding.FragmentUserEditorFeedbackBinding
import com.example.bestricestore.ImageDialogFragment
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.FirebaseFirestore
class UserEditorFeedbackFragment constructor() : Fragment() {
    private lateinit var mViewModel: UserEditorFeedbackViewModel
    private lateinit var binding: FragmentUserEditorFeedbackBinding
    public override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mViewModel = ViewModelProvider(this).get(
            UserEditorFeedbackViewModel::class.java
        )
        binding = FragmentUserEditorFeedbackBinding.inflate(inflater, container, false)
        mViewModel!!.feedback.observe(
            viewLifecycleOwner
        ) { feedback: Feedback ->
            binding!!.date.setText("Date: " + feedback.date)
            //                    binding.userName.setText("UserName: " + feedback.getUserName());
            binding!!.title.setText("Title: " + feedback.title)
            binding!!.purpose.setText("Purpose: " + feedback.purpose)
            binding!!.userPhone.setText("UserPhone: " + feedback.userPhone)
            binding!!.userName.setText("UserName: " + feedback.userName)
            binding!!.status.setText("Status: " + feedback.feedbackStatus)
            Glide.with(requireContext()).load(feedback.imageUrl)
                .error(R.drawable.profile)
                .fitCenter().into(binding!!.imageFeedback)
            if ((feedback.feedbackStatus == Constants.FEEDBACK_ALREADY_RESPONDED)) {
                binding!!.btnNavigateRespond.visibility = View.VISIBLE
            }
            binding!!.imageFeedback.setOnClickListener { v: View? ->
                showImage(
                    feedback.imageUrl
                )
            }
        }
        val feedbackId: String? = requireArguments().getString("feedbackId")
        mViewModel!!.getFeedbackById(feedbackId)
        binding!!.btnDelete.setOnClickListener { v: View? ->
            deleteFeedback(
                feedbackId
            )
        }
        binding!!.btnNavigateRespond.setOnClickListener { v: View? ->
            findNavController(
                requireView()
            )
                .navigate(R.id.checkRespondFragment, Bundle())
        }
        return binding!!.getRoot()
    }

    private fun showImage(url: String?) {
        val fragment: ImageDialogFragment = ImageDialogFragment()
        val result: Bundle = Bundle()
        result.putString("bundleKeyImageUrl", url)
        // The child fragment needs to still set the result on its parent fragment manager
        childFragmentManager.setFragmentResult("requestKey", result)
        fragment.show(childFragmentManager, "TAG1")
    }

    private fun deleteFeedback(id: String?) {
        FirebaseFirestore.getInstance().collection(Constants.FS_FEEDBACK).document(
            (id)!!
        )
            .delete()
            .addOnSuccessListener(object : OnSuccessListener<Void?> {
                public override fun onSuccess(unused: Void?) {
                    Toast.makeText(context, "Delete successful!", Toast.LENGTH_SHORT).show()
                    findNavController(requireView()).navigateUp()
                }
            })
    }

    companion object {
        fun newInstance(): UserEditorFeedbackFragment {
            return UserEditorFeedbackFragment()
        }
    }
}