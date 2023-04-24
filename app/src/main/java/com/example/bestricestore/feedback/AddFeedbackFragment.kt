package com.example.bestricestore.feedback

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation.findNavController
import com.example.bestricestore.data.Constants
import com.example.bestricestore.data.Feedback
import com.example.bestricestore.databinding.FragmentAddFeedbackBinding
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import java.text.SimpleDateFormat
import java.util.*

class AddFeedbackFragment constructor() : Fragment() {
    private var mViewModel: AddFeedbackViewModel? = null
    private var binding: FragmentAddFeedbackBinding? = null
    var uri: Uri? = null
    public override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mViewModel = ViewModelProvider(this).get(AddFeedbackViewModel::class.java)
        binding = FragmentAddFeedbackBinding.inflate(inflater, container, false)
        binding!!.imageFeedback.setOnClickListener(View.OnClickListener({ v: View? -> selectImage() }))
        binding!!.buttonSend.setOnClickListener(View.OnClickListener({ v: View? -> sendFeedback() }))
        binding!!.date.setOnClickListener(View.OnClickListener({ v: View? -> selectDate() }))
        return binding!!.getRoot()
    }

    private fun selectDate() {
        val calendar: Calendar = Calendar.getInstance()
        val year: Int = calendar.get(Calendar.YEAR)
        val month: Int = calendar.get(Calendar.MONTH)
        val day: Int = calendar.get(Calendar.DAY_OF_MONTH)
        val etDate: TextView = binding!!.date
        val datePickerDialog: DatePickerDialog = DatePickerDialog(
            (requireContext()), object : OnDateSetListener {
                public override fun onDateSet(
                    view: DatePicker,
                    year1: Int,
                    month1: Int,
                    day1: Int
                ) {
                    var month1: Int = month1
                    month1 = month1 + 1
                    val date: String = day1.toString() + "/" + month1 + "/" + year1
                    etDate.setText(date)
                    Toast.makeText(getContext(), "Select date successfully!", Toast.LENGTH_LONG)
                        .show()
                }
            }, year, month, day
        )
        datePickerDialog.show()
    }

    private fun sendFeedback() {
        val progressDialog: ProgressDialog = ProgressDialog(getContext())
        progressDialog.show()
        val user: FirebaseUser? = FirebaseAuth.getInstance().getCurrentUser()
        val title: String = binding!!.editTextTitle.getText().toString()
        val purpose: String = binding!!.editTextPurpose.getText().toString()
        val date: String = binding!!.date.getText().toString()
        if (uri != null) {
            val formatter: SimpleDateFormat = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss")
            val now: Date = Date()
            val fileName: String = formatter.format(now) // tao ten rieng biet cho file
            val storageReference: StorageReference =
                FirebaseStorage.getInstance().getReference("feedback/" + fileName)
            storageReference.putFile(uri!!)
                .addOnSuccessListener(object : OnSuccessListener<UploadTask.TaskSnapshot> {
                    public override fun onSuccess(taskSnapshot: UploadTask.TaskSnapshot) {
                        // lay url theo cach moi
                        val task: Task<Uri> = taskSnapshot.getMetadata()!!
                            .getReference()!!.getDownloadUrl()
                        task.addOnSuccessListener(object : OnSuccessListener<Uri> {
                            public override fun onSuccess(uri: Uri) {
                                val f: Feedback = Feedback(
                                    title,
                                    date,
                                    purpose,
                                    uri.toString(),
                                    user!!.getUid(),
                                    user.getPhoneNumber(),
                                    mViewModel!!.muser!!.username,
                                    Constants.FEEDBACK_NOT_RESPONDED
                                )
                                mViewModel!!.sendFeedback(f)
                                Toast.makeText(getContext(), "Successful!!", Toast.LENGTH_SHORT)
                                    .show()
                                progressDialog.dismiss()
                                findNavController(requireView()).navigateUp()
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
            val f: Feedback = Feedback(
                title,
                date,
                purpose,
                null,
                user!!.getUid(),
                user.getPhoneNumber(),
                mViewModel!!.muser!!.username,
                Constants.FEEDBACK_NOT_RESPONDED
            )
            mViewModel!!.sendFeedback(f)
            Toast.makeText(getContext(), "Successful!!", Toast.LENGTH_SHORT).show()
            progressDialog.dismiss()
            findNavController(requireView()).navigateUp()
        }
    }

    private fun selectImage() {
        val intent: Intent = Intent()
        intent.setType("image/*")
        intent.setAction(Intent.ACTION_GET_CONTENT)
        startActivityForResult(intent, 100)
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if ((requestCode == 100) && (data != null) && (data.getData() != null)) {
            uri = data.getData()
            binding!!.imageFeedback.setImageURI(uri)
        }
    }

    companion object {
        fun newInstance(): AddFeedbackFragment {
            return AddFeedbackFragment()
        }
    }
}