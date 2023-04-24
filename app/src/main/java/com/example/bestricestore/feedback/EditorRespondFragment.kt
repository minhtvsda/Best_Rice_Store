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
import com.example.bestricestore.data.Respond
import com.example.bestricestore.databinding.FragmentEditorRespondBinding
import com.example.bestricestore.ImageDialogFragment
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.FirebaseFirestore

class EditorRespondFragment constructor() : Fragment() {
    private lateinit var mViewModel: EditorRespondViewModel
    private lateinit var binding: FragmentEditorRespondBinding
    var isFeedbackImageFitScreen: Boolean = false
    var isRespondImageFitScreen: Boolean = false
    public override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mViewModel = ViewModelProvider(this).get(EditorRespondViewModel::class.java)
        binding = FragmentEditorRespondBinding.inflate(inflater, container, false)
        mViewModel!!.respond.observe(
            getViewLifecycleOwner()
        ) { respond: Respond ->
            binding!!.date.setText("Date: " + respond.date)
            binding!!.purpose.setText("Purpose: " + respond.purpose)
            binding!!.respondTitle.setText("RespondTitle: " + respond.title)
                    binding !!. userName . setText ("UserName: " + respond.userName)
                    binding !!. respondMainAnswer . setText ("Main answer: " + respond.respondAnswer)
                    binding !!. title . setText ("Feedback title: " + respond.title)
                    binding !!. userPhone . setText ("UserPhone: " + respond.userPhone)
                    Glide . with (requireContext()).load(respond.respondUrl)
                .error(R.drawable.profile).fitCenter()
                .into(binding!!.imageRespond)
                    Glide . with (requireContext()).load(respond.feedbackUrl)
                .error(R.drawable.profile).fitCenter()
                .into(binding!!.imageFeedback)
                    binding !!. imageRespond . setOnClickListener (View.OnClickListener { v: View? ->
                        showImageDialog(
                            respond.respondUrl
                        )
                    })
                    binding !!. imageFeedback . setOnClickListener (View.OnClickListener { v: View? ->
                        showImageDialog(
                            respond.feedbackUrl
                        )
                    })
        }
        val respondId: String? = requireArguments().getString("respondId")
        mViewModel!!.getRespondById(respondId)
        binding!!.btnDelete.setOnClickListener { v: View? ->
            deleteRespond(
                respondId
            )
        }
        return binding!!.getRoot()
    }

    private fun showImageDialog(url: String?) {
        val fragment: ImageDialogFragment = ImageDialogFragment()
        val result: Bundle = Bundle()
        result.putString("bundleKeyImageUrl", url)
        // The child fragment needs to still set the result on its parent fragment manager
        getChildFragmentManager().setFragmentResult("requestKey", result)
        fragment.show(getChildFragmentManager(), "TAG")
    }

    private fun deleteRespond(id: String?) {
        FirebaseFirestore.getInstance().collection(Constants.FS_RESPOND) //lay collection
            .document((id)!!)
            .delete()
            .addOnSuccessListener { avoid: Void? ->
                Toast.makeText(requireContext(), "Delete Successful!", Toast.LENGTH_SHORT).show()
                findNavController(requireView()).navigateUp()
            }
    }

    private fun imageRespond(url: String) {
//        if(isRespondImageFitScreen) {
//            isRespondImageFitScreen=false;
//            binding.imageRespond.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
//            binding.imageRespond.setAdjustViewBounds(true);
//        }else{
//            isRespondImageFitScreen=true;
//            binding.imageRespond.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
//            binding.imageRespond.setScaleType(ImageView.ScaleType.FIT_XY);
//        }
    }

    private fun imageFeedback(url: String) {
//        if(isFeedbackImageFitScreen) {
//            isFeedbackImageFitScreen =false;
//            binding.imageFeedback.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
//            binding.imageFeedback.setAdjustViewBounds(true);
//        } else {
//            isFeedbackImageFitScreen=true;
//            binding.imageFeedback.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
//            binding.imageFeedback.setScaleType(ImageView.ScaleType.FIT_CENTER);
//        }
    }

    companion object {
        fun newInstance(): EditorRespondFragment {
            return EditorRespondFragment()
        }
    }
}