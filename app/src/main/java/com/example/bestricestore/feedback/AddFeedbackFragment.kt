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

class AddFeedbackFragment : Fragment() {
    private var mViewModel: AddFeedbackViewModel? = null
    private var binding: FragmentAddFeedbackBinding? = null
    var uri: Uri? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mViewModel = ViewModelProvider(this).get(AddFeedbackViewModel::class.java)
        binding = FragmentAddFeedbackBinding.inflate(inflater, container, false)
        binding!!.imageFeedback.setOnClickListener({ v: View? -> selectImage() })
        binding!!.buttonSend.setOnClickListener({ v: View? -> sendFeedback() })
        binding!!.date.setOnClickListener({ v: View? -> selectDate() })
        return binding!!.root
    }

    private fun selectDate() {
        val calendar: Calendar = Calendar.getInstance()
        val year: Int = calendar.get(Calendar.YEAR)
        val month: Int = calendar.get(Calendar.MONTH)
        val day: Int = calendar.get(Calendar.DAY_OF_MONTH)
        val etDate: TextView = binding!!.date
        val datePickerDialog: DatePickerDialog = DatePickerDialog(
            (requireContext()), object : OnDateSetListener {
                override fun onDateSet(
                    view: DatePicker,
                    year1: Int,
                    month1: Int,
                    day1: Int
                ) {
                    var month1: Int = month1
                    month1 = month1 + 1
                    val date: String = day1.toString() + "/" + month1 + "/" + year1
                    etDate.text = date
                    Toast.makeText(context, "Select date successfully!", Toast.LENGTH_LONG)
                        .show()
                }
            }, year, month, day
        )
        datePickerDialog.show()
    }

    private fun sendFeedback() {
        val progressDialog: ProgressDialog = ProgressDialog(context)
        progressDialog.show()
        val user: FirebaseUser? = FirebaseAuth.getInstance().currentUser
        val title: String = binding!!.editTextTitle.text.toString()
        val purpose: String = binding!!.editTextPurpose.text.toString()
        val date: String = binding!!.date.text.toString()
        if (uri != null) {
            val formatter: SimpleDateFormat = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss")
            val now: Date = Date()
            val fileName: String = formatter.format(now) // tao ten rieng biet cho file
            val storageReference: StorageReference =
                FirebaseStorage.getInstance().getReference("feedback/" + fileName)
            storageReference.putFile(uri!!)
                .addOnSuccessListener(object : OnSuccessListener<UploadTask.TaskSnapshot> {
                    override fun onSuccess(taskSnapshot: UploadTask.TaskSnapshot) {
                        // lay url theo cach moi
                        val task: Task<Uri> = taskSnapshot.metadata!!
                            .reference!!.downloadUrl
                        task.addOnSuccessListener(object : OnSuccessListener<Uri> {
                            override fun onSuccess(uri: Uri) {
                                val f: Feedback = Feedback(
                                    title,
                                    date,
                                    purpose,
                                    uri.toString(),
                                    user!!.uid,
                                    user.phoneNumber,
                                    mViewModel!!.muser!!.username,
                                    Constants.FEEDBACK_NOT_RESPONDED
                                )
                                mViewModel!!.sendFeedback(f)
                                Toast.makeText(context, "Successful!!", Toast.LENGTH_SHORT)
                                    .show()
                                progressDialog.dismiss()
                                findNavController(requireView()).navigateUp()
                            }
                        })
                    }
                })
                .addOnFailureListener(object : OnFailureListener {
                    override fun onFailure(e: Exception) {
                        progressDialog.dismiss()
                        Toast.makeText(
                            context,
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
                user!!.uid,
                user.phoneNumber,
                mViewModel!!.muser!!.username,
                Constants.FEEDBACK_NOT_RESPONDED
            )
            mViewModel!!.sendFeedback(f)
            Toast.makeText(context, "Successful!!", Toast.LENGTH_SHORT).show()
            progressDialog.dismiss()
            findNavController(requireView()).navigateUp()
        }
    }

    private fun selectImage() {
        val intent: Intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, 100)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if ((requestCode == 100) && (data != null) && (data.data != null)) {
            uri = data.data
            binding!!.imageFeedback.setImageURI(uri)
        }
    }

    companion object {
        fun newInstance(): AddFeedbackFragment {
            return AddFeedbackFragment()
        }
    }
}