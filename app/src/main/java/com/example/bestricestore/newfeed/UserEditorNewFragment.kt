package com.example.bestricestore.newfeed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.bestricestore.R
import com.example.bestricestore.data.NewEntity
import com.example.bestricestore.databinding.FragmentUserEditorNewBinding
import com.example.bestricestore.ImageDialogFragment

class UserEditorNewFragment : Fragment() {
    private var mViewModel: UserEditorNewViewModel? = null
    private var binding: FragmentUserEditorNewBinding? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mViewModel = ViewModelProvider(this).get(UserEditorNewViewModel::class.java)
        binding = FragmentUserEditorNewBinding.inflate(inflater, container, false)
        mViewModel!!.news.observe(
            viewLifecycleOwner
        ) { newEntity: NewEntity? ->
            binding!!.title.text = "Title: " + newEntity!!.title
            binding!!.newInfo.text = "Main information: \n" + newEntity.newInfo
            binding!!.textDate.text = "Date: " + newEntity.date
            if (newEntity.imageUrl != null) {
                Glide.with(requireContext()).load(newEntity.imageUrl)
                    .error(R.drawable.profile).into(binding!!.imageNew)
            }
            binding!!.imageNew.setOnClickListener { v: View? ->
                showImage(newEntity.imageUrl)
            }
            requireActivity().invalidateOptionsMenu()
        }
        val newId: String? = requireArguments().getString("newId")
        mViewModel!!.getNewById(newId)
        return binding!!.root
    }

    private fun showImage(url: String?) {
        val fragment: ImageDialogFragment = ImageDialogFragment()
        val result: Bundle = Bundle()
        result.putString("bundleKeyImageUrl", url)
        // The child fragment needs to still set the result on its parent fragment manager
        childFragmentManager.setFragmentResult("requestKey", result)
        fragment.show(childFragmentManager, "TAG1")
    }

    companion object {
        fun newInstance(): UserEditorNewFragment {
            return UserEditorNewFragment()
        }
    }
}