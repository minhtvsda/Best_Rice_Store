package com.example.bestricestore.login

import com.example.bestricestore.data.User

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation.findNavController
import com.example.bestricestore.R
import com.example.bestricestore.data.Constants
import com.example.bestricestore.data.DeliLink
import com.example.bestricestore.databinding.FragmentChangePasswordBinding
import com.example.bestricestore.databinding.FragmentDeliSignUpBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import java.text.SimpleDateFormat
import java.util.*

class DeliSignUpFragment constructor() : Fragment() {
    private lateinit var binding: FragmentDeliSignUpBinding
    private var uriId: Uri? = null
    private var uri2: Uri? = null
    private var uri3: Uri? = null
    private var photoLink: String? = null
    private var photoLink2: String? = null
    public override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentDeliSignUpBinding.inflate(inflater, container, false)
        binding!!.imageViewId.setOnClickListener(View.OnClickListener({ v: View? -> selectImage() }))
        binding!!.imageViewDrivingLicence.setOnClickListener(View.OnClickListener({ v: View? -> selectImage2() }))
        binding!!.imageViewMotorbikeLicence.setOnClickListener(View.OnClickListener({ v: View? -> selectImage3() }))
        binding!!.button.setOnClickListener(View.OnClickListener({ v: View? -> register() }))
        return binding!!.getRoot()
    }

    private fun selectImage() {
        val intent: Intent = Intent()
        intent.setType("image/*")
        intent.setAction(Intent.ACTION_GET_CONTENT)
        startActivityForResult(intent, 101)
    }

    private fun selectImage2() {
        val intent: Intent = Intent()
        intent.setType("image/*")
        intent.setAction(Intent.ACTION_GET_CONTENT)
        startActivityForResult(intent, 102)
    }

    private fun selectImage3() {
        val intent: Intent = Intent()
        intent.setType("image/*")
        intent.setAction(Intent.ACTION_GET_CONTENT)
        startActivityForResult(intent, 103)
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if ((requestCode == 102) && (data != null) && (data.getData() != null)) {
            uri2 = data.getData()
            binding!!.imageViewDrivingLicence.setImageURI(uri2)
        }
        if ((requestCode == 101) && (data != null) && (data.getData() != null)) {
            uriId = data.getData()
            binding!!.imageViewId.setImageURI(uriId)
        }
        if ((requestCode == 103) && (data != null) && (data.getData() != null)) {
            uri3 = data.getData()
            binding!!.imageViewMotorbikeLicence.setImageURI(uri3)
        }
    }

    private fun register() {
        val db: FirebaseFirestore = FirebaseFirestore.getInstance()
        val user: FirebaseUser? = FirebaseAuth.getInstance().getCurrentUser()
        val progressDialog: ProgressDialog = ProgressDialog(activity)
        progressDialog.setTitle("Sending your request")
        progressDialog.show()
        val username: String = binding!!.edtName.getText().toString()
        val email: String = binding!!.edtEmail.getText().toString()
        val address: String = binding!!.edtAddress.getText().toString()
        val u= User(
            username,
            email,
            address,
            Constants.DELIVERER_REGISTER_WAITING,
            user!!.getPhoneNumber(),
            ""
        )
        val formatter: SimpleDateFormat = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss")
        val now: Date = Date()
        val fileName: String = formatter.format(now) // tao ten rieng biet cho file
        val storageReference: StorageReference =
            FirebaseStorage.getInstance().getReference("customer/" + fileName)
        storageReference.putFile((uriId)!!)
            .addOnSuccessListener(object : OnSuccessListener<UploadTask.TaskSnapshot> {
                public override fun onSuccess(taskSnapshot: UploadTask.TaskSnapshot) {
                    // lay url theo cach moi
                    val task: Task<Uri> = taskSnapshot.getMetadata()!!
                        .getReference()!!.getDownloadUrl()
                    task.addOnSuccessListener(object : OnSuccessListener<Uri> {
                        public override fun onSuccess(uri: Uri) {
                            photoLink = uri.toString()
                        }
                    })
                }
            })
            .addOnFailureListener(object : OnFailureListener {
                public override fun onFailure(e: Exception) {
                    Toast.makeText(
                        getContext(),
                        "Failed to upload Image!Try again.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        val storageReference2: StorageReference =
            FirebaseStorage.getInstance().getReference("customer/1" + fileName)
        storageReference2.putFile((uri2)!!)
            .addOnSuccessListener(object : OnSuccessListener<UploadTask.TaskSnapshot> {
                public override fun onSuccess(taskSnapshot: UploadTask.TaskSnapshot) {
                    // lay url theo cach moi
                    val task: Task<Uri> = taskSnapshot.getMetadata()!!
                        .getReference()!!.getDownloadUrl()
                    task.addOnSuccessListener(object : OnSuccessListener<Uri> {
                        public override fun onSuccess(uri: Uri) {
                            photoLink2 = uri.toString()
                            //                                    Toast.makeText(getContext(), "Upload image successfully!", Toast.LENGTH_SHORT).show();
                        }
                    })
                }
            })
        val storageReference3: StorageReference =
            FirebaseStorage.getInstance().getReference("customer/2" + fileName)
        storageReference3.putFile((uri3)!!)
            .addOnSuccessListener(object : OnSuccessListener<UploadTask.TaskSnapshot> {
                public override fun onSuccess(taskSnapshot: UploadTask.TaskSnapshot) {
                    // lay url theo cach moi
                    val task: Task<Uri> = taskSnapshot.getMetadata()!!
                        .getReference()!!.getDownloadUrl()
                    task.addOnSuccessListener(object : OnSuccessListener<Uri> {
                        public override fun onSuccess(uri: Uri) {
                            val photoLink3: String = uri.toString()
                            val c: DeliLink = DeliLink(photoLink, photoLink2, photoLink3)
                            db.collection(Constants.FS_DELI_LINK).document(
                                user.getUid()
                            )
                                .set(c)
                            db.collection(Constants.FS_USER).document(
                                user.getUid()
                            ).set(u)
                            progressDialog.dismiss()
                            Toast.makeText(
                                getContext(),
                                "Successful! Wait for the restaurant check your register! Please check your email daily!",
                                Toast.LENGTH_LONG
                            ).show()
                            findNavController(requireView()).navigate(R.id.splashFragment)
                        }
                    })
                }
            }).addOnFailureListener(OnFailureListener { e: Exception? ->
                progressDialog.dismiss()
                Toast.makeText(
                    getContext(),
                    "Up load your image failed. Try again!",
                    Toast.LENGTH_LONG
                ).show()
            })
    }
}