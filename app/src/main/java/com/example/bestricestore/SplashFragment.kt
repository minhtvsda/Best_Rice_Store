package com.example.bestricestore

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation

import com.example.bestricestore.data.Constants
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

class SplashFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val handler: Handler = Handler()
        handler.postDelayed(object : Runnable {
            override fun run() {
                nextFragment()
            }
        }, 2000)
        return inflater.inflate(R.layout.fragment_splash, container, false)
    }

    private fun nextFragment() {
        val user: FirebaseUser? = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            Navigation.findNavController(requireView()).navigate(R.id.phoneSignUpFragment)
        } else {
            FirebaseFirestore.getInstance().collection(Constants.FS_USER)
                .document(user.uid).get()
                .addOnCompleteListener(object : OnCompleteListener<DocumentSnapshot?> {
                    //lay du lieu
                    override fun onComplete(task: Task<DocumentSnapshot?>) {
                        if (task.isSuccessful) {
                            val document: DocumentSnapshot = task.result!!
                            if (document.exists()) {
                                Log.d(
                                    Constants.FIRE_STORE,
                                    "DocumentSnapshot data: " + document.data
                                )
                                val roles: String? = document.getString("roles")
                                if ((roles == Constants.FS_ROLE_BAN)) {
                                    Toast.makeText(
                                        context,
                                        "Your account has been banned!",
                                        Toast.LENGTH_LONG
                                    ).show()
                                    Navigation.findNavController(requireView()).navigate(R.id.phoneSignUpFragment)
                                } else if ((roles == Constants.DELIVERER_REGISTER_WAITING)) {
                                    Toast.makeText(
                                        context,
                                        "Your account is being register! Please wait for our call!",
                                        Toast.LENGTH_LONG
                                    ).show()
                                    Navigation.findNavController(requireView()).navigate(R.id.phoneSignUpFragment)
                                } else if ((roles == Constants.DELIVERER_REGISTER_DECLINED)) {
                                    Toast.makeText(
                                        context,
                                        "Your account register is declined! Check more information in your email!",
                                        Toast.LENGTH_LONG
                                    ).show()
                                    Navigation.findNavController(requireView()).navigate(R.id.phoneSignUpFragment)
                                } else {
                                    Toast.makeText(context, "Welcome!", Toast.LENGTH_LONG)
                                        .show()
                                    Navigation.findNavController(requireView()).navigate(R.id.mainFragment)
                                }
                            } else {
                                Log.d(Constants.FIRE_STORE, "No such document")
                                Navigation.findNavController(requireView()).navigate(R.id.selectRoleFragment)
                            }
                        } else {
                            Log.d(Constants.FIRE_STORE, "get failed with ", task.exception)
                        }
                    }
                })
        }
    }
}