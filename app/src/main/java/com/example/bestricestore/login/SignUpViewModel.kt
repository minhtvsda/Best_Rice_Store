package com.example.bestricestore.login

import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.bestricestore.data.CartItem
import com.example.bestricestore.data.Constants
import com.example.bestricestore.data.User
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
class SignUpViewModel constructor() : ViewModel() {
    var Signup: Boolean = false
    fun onClickSignUp(
        context: Context?,
        username: String?,
        strEmail: String?,
        strPassword: String?,
        strAddress: String?,
        role: String?
    ) {
        val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
        val db: FirebaseFirestore = FirebaseFirestore.getInstance()
        val progressDialog: ProgressDialog = ProgressDialog(context)
        progressDialog.show()
        mAuth.createUserWithEmailAndPassword((strEmail)!!, (strPassword)!!)
            .addOnCompleteListener(object : OnCompleteListener<AuthResult?> {
                public override fun onComplete(task: Task<AuthResult?>) {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        val user: FirebaseUser? = mAuth.getCurrentUser()
                        user!!.sendEmailVerification()
                            .addOnCompleteListener(object : OnCompleteListener<Void?> {
                                public override fun onComplete(task: Task<Void?>) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(
                                            context,
                                            "Register successful! Please verify your account!",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        Signup = true
                                        val u: User = User(username, strEmail, strAddress, role, "","")
                                        db.collection("users").document(user.getUid()).set(u)
                                        progressDialog.dismiss()
                                    } else {
                                        Toast.makeText(
                                            context,
                                            "Fail to sent email verification! Try again!",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        progressDialog.dismiss()
                                    }
                                }
                            })
                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(context, "Fail! Try again!", Toast.LENGTH_SHORT).show()
                        progressDialog.dismiss()
                    }
                }
            })
    } // TODO: Implement the ViewModel
}