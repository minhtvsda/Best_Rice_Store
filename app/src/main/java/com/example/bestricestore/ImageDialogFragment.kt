package com.example.bestricestore

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentResultListener
import com.bumptech.glide.Glide
import com.example.bestricestore.data.Constants


class ImageDialogFragment : DialogFragment() {
    private var result: String? = Constants.EMPTY_STRING
    var dialogView: View? = null
    lateinit var dialogBuilder: Dialog
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        dialogBuilder = Dialog(requireActivity())
        //        LayoutInflater inflater = this.getLayoutInflater();
//        dialogView = inflater.inflate(R.layout.fragment_image_dialog, null);
////        TextView text = (TextView) dialogView.findViewById(R.id.text);
////        ImageView imageView = (ImageView) dialogView.findViewById(R.id.imageView);
//
        dialogBuilder.setContentView(R.layout.fragment_image_dialog)
        val imageView: ImageView =
            dialogBuilder.findViewById<View>(R.id.imageViewDialog) as ImageView
        parentFragmentManager
            .setFragmentResultListener("requestKey", this, object : FragmentResultListener {
                override fun onFragmentResult(requestKey: String, bundle: Bundle) {
                    result = bundle.getString("bundleKeyImageUrl")
                    // Do something with the result
//                        text.setText(result);
                    Glide.with(requireActivity()).load(result)
                        .error(R.drawable.profile).into(imageView)
                }
            })
        return dialogBuilder
    }
}