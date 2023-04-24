package com.example.bestricestore.feedback

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation.findNavController
import com.bumptech.glide.Glide
import com.example.bestricestore.ImageDialogFragment
import com.example.bestricestore.R
import com.example.bestricestore.data.Constants
import com.example.bestricestore.data.Feedback
import com.example.bestricestore.data.Respond
import com.example.bestricestore.databinding.FragmentEditorFeedbackBinding
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import java.text.SimpleDateFormat
import java.util.*

class EditorFeedbackFragment constructor() : Fragment() {
    private lateinit var mViewModel: EditorFeedbackViewModel
    private lateinit var binding: FragmentEditorFeedbackBinding
    var respondUri: Uri? = null
    public override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mViewModel = ViewModelProvider(this).get(EditorFeedbackViewModel::class.java)
        binding = FragmentEditorFeedbackBinding.inflate(inflater, container, false)
        mViewModel!!.feedback.observe(
            getViewLifecycleOwner(),
            Observer { feedback: Feedback ->
                binding!!.date.setText("Date: " + feedback.date)
                //                    binding.userName.setText("UserName: " + feedback.getUserName());
                binding!!.title.setText("Title: " + feedback.title)
                binding!!.purpose.setText("Purpose: " + feedback.purpose)
                binding!!.userPhone.setText("UserPhone: " + feedback.userPhone)
                binding!!.userName.setText("UserName: " + feedback.userName)
                Glide.with(requireContext()).load(feedback.imageUrl)
                    .error(R.drawable.profile)
                    .fitCenter().into(binding!!.imageFeedback)
                if ((feedback.feedbackStatus == Constants.FEEDBACK_ALREADY_RESPONDED)) {
                    binding!!.respondTitle.setText("You have already responded this feedback. Please navigate to the respond screen!")
                    binding!!.respondMainAnswer.setVisibility(View.GONE)
                    binding!!.btnSave.setVisibility(View.GONE)
                    binding!!.btnNavigateRespond.setVisibility(View.VISIBLE)
                    binding!!.btnNavigateRespond.setOnClickListener(View.OnClickListener { v: View? ->
                        val b = Bundle()
                        b.putString("role", Constants.ROLE_ADMIN)
                        findNavController(requireView()).navigate(R.id.checkRespondFragment, b)
                    })
                }
            }
        )
        val feedbackId: String? = requireArguments().getString("feedbackId")
        mViewModel!!.getFeedbackById(feedbackId)
        binding!!.imageRespond.setOnClickListener(View.OnClickListener { v: View? -> selectImage() })
        binding!!.btnSave.setOnClickListener(View.OnClickListener { v: View? -> saveRespond() })
        binding!!.btnDelete.setOnClickListener(View.OnClickListener { v: View? ->
            mViewModel!!.deleteFeedback(feedbackId)
            findNavController(requireView()).navigateUp()
        })
        binding!!.imageRespond.setOnClickListener(View.OnClickListener { v: View? -> showImage() })
        return binding!!.root
    }

    private fun showImage() {
        val fragment: ImageDialogFragment = ImageDialogFragment()
        val result = Bundle()
        result.putString("bundleKeyImageUrl", mViewModel!!.feedback.getValue()!!.imageUrl)
        // The child fragment needs to still set the result on its parent fragment manager
                setFragmentResult("requestKey", result)
        fragment.show(childFragmentManager, "TAG")
    }

    private fun saveRespond() {
        val f: Feedback = mViewModel!!.feedback.value!!
        val respondTitle: String = binding!!.respondTitle.getText().toString()
        val respondAnswer: String = binding!!.respondMainAnswer.getText().toString()
        if (((Constants.EMPTY_STRING == respondAnswer) || (Constants.EMPTY_STRING == respondTitle))) {
            Toast.makeText(getContext(), "You need to type required fields!", Toast.LENGTH_SHORT)
                .show()
            return
        }
        val progressDialog: ProgressDialog = ProgressDialog(context)
        progressDialog.show()
        if (respondUri != null) {
            val formatter: SimpleDateFormat = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss")
            val now: Date = Date()
            val fileName: String = formatter.format(now) // tao ten rieng biet cho file
            val storageReference: StorageReference =
                FirebaseStorage.getInstance().getReference("respond/" + fileName)
            storageReference.putFile(respondUri!!)
                .addOnSuccessListener(object : OnSuccessListener<UploadTask.TaskSnapshot> {
                    public override fun onSuccess(taskSnapshot: UploadTask.TaskSnapshot) {
                        // lay url theo cach moi
                        val task: Task<Uri> = taskSnapshot.getMetadata()!!
                            .getReference()!!.getDownloadUrl()
                        task.addOnSuccessListener(object : OnSuccessListener<Uri> {
                            public override fun onSuccess(uri: Uri) {
                                mViewModel!!.changFeedbackStatus(f.id)
                                val respond: Respond = Respond(
                                    f.imageUrl, f.userId, f.userPhone,
                                    f.userName, f.date, f.title,
                                    f.purpose, respondTitle, respondAnswer, uri.toString()
                                )
                                mViewModel!!.sendRespond(respond)
                                Toast.makeText(getContext(), "Successful!!", Toast.LENGTH_SHORT)
                                    .show()
                                progressDialog.dismiss()
                                findNavController(view!!).navigateUp()
                            }
                        })
                    }
                })
                .addOnFailureListener(object : OnFailureListener {
                    public override fun onFailure(e: Exception) {
                        progressDialog.dismiss()
                        Toast.makeText(
                            getContext(),
                            "Failed to upload Image!Try again.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
        } else {
            mViewModel!!.changFeedbackStatus(f.id)
            val respond: Respond = Respond(
                f.imageUrl, f.userId, f.userPhone,
                f.userName, f.date, f.title,
                f.purpose, respondTitle, respondAnswer, ""
            )
            mViewModel!!.sendRespond(respond)
            Toast.makeText(getContext(), "Successful!!", Toast.LENGTH_SHORT).show()
            progressDialog.dismiss()
            findNavController(requireView()).navigateUp()
        }
    }

    private fun selectImage() {
        val intent: Intent = Intent()
        intent.setType("image/*")
        intent.setAction(Intent.ACTION_GET_CONTENT)
        startActivityForResult(intent, 120)
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if ((requestCode == 120) && (data != null) && (data.getData() != null)) {
            respondUri = data.getData()
            binding!!.imageRespond.setImageURI(respondUri)
        }
    }

    companion object {
        fun newInstance(): EditorFeedbackFragment {
            return EditorFeedbackFragment()
        }
    }
}