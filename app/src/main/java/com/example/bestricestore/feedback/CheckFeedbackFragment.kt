package com.example.bestricestore.feedback

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuItemCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bestricestore.R
import com.example.bestricestore.data.Constants
import com.example.bestricestore.data.Feedback
import com.example.bestricestore.databinding.FragmentCheckFeedbackBinding
import com.example.bestricestore.feedbackimport.CheckFeedbackListAdapter
import java.util.*

class CheckFeedbackFragment constructor() : Fragment(), CheckFeedbackListAdapter.ListItemListener {
    private var mViewModel: CheckFeedbackViewModel? = null
    private var binding: FragmentCheckFeedbackBinding? = null
    private var adapter: CheckFeedbackListAdapter? = null
    var role: String? = null
    public override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val app: AppCompatActivity? = getActivity() as AppCompatActivity?
        //get activity, app chinh la ca activity chua fragment  nay
        val ab: ActionBar? = app!!.getSupportActionBar() //lay phan giai mau tim
        ab!!.setHomeButtonEnabled(true)
        ab.setDisplayShowHomeEnabled(true)
        ab.setDisplayHomeAsUpEnabled(true)
        ab.setHomeAsUpIndicator(R.drawable.ic_baseline_home_24)
        setHasOptionsMenu(true)
        mViewModel = ViewModelProvider(this).get(CheckFeedbackViewModel::class.java)
        binding = FragmentCheckFeedbackBinding.inflate(inflater, container, false)
        val rv: RecyclerView = binding!!.recyclerView
        rv.setHasFixedSize(true)
        rv.addItemDecoration(
            DividerItemDecoration(
                getContext(),
                (LinearLayoutManager(getContext()).getOrientation())
            )
        )
        mViewModel!!.feedbackList.observe(
            getViewLifecycleOwner()
        ) { feedbacks: List<Feedback> ->
            adapter = CheckFeedbackListAdapter(feedbacks, this)
            binding!!.recyclerView.setAdapter(adapter)
            binding!!.recyclerView.setLayoutManager(LinearLayoutManager(getActivity()))
        }
        role = requireArguments().getString("role", "")
        if (role === Constants.EMPTY_STRING) {
            mViewModel!!.usergetFeedbacks()
        } else {
            mViewModel!!.getFeedbacksByStatus(Constants.FEEDBACK_NOT_RESPONDED)
            binding!!.buttonRespond.visibility = View.VISIBLE
        }
        binding!!.buttonRespond.setOnClickListener(View.OnClickListener { v: View? -> viewallResponds() })
        return binding!!.root
    }

    private fun viewallResponds() {
        val b: Bundle = Bundle()
        b.putString("role", Constants.ROLE_ADMIN)
        findNavController(requireView()).navigate(R.id.checkRespondFragment, b)
    }

    public override fun onItemClick(feedbackId: String?) {
        val b: Bundle = Bundle()
        b.putString("feedbackId", feedbackId)
        if (role === Constants.EMPTY_STRING) {
            findNavController(requireView()).navigate(R.id.userEditorFeedbackFragment, b)
        } else {
            findNavController(requireView()).navigate(R.id.editorFeedbackFragment, b)
        }
    }

    public override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_check_feedbacks, menu)
        val menuItem: MenuItem = menu.findItem(R.id.action_search_feedback)
        val sv = MenuItemCompat.getActionView(menuItem) as android.widget.SearchView
        sv.setQueryHint("Search by phone, date, username...")
        sv.setOnQueryTextListener(object : android.widget.SearchView.OnQueryTextListener {
            public override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            public override fun onQueryTextChange(newText: String): Boolean {
                filter(newText)
                return false
            }
        })
    }

    public override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.getItemId()) {
            R.id.action_search_not_responded -> if (role === Constants.EMPTY_STRING) {
                mViewModel!!.usergetFeedbacksByStatus(Constants.FEEDBACK_NOT_RESPONDED)
            } else {
                mViewModel!!.getFeedbacksByStatus(Constants.FEEDBACK_NOT_RESPONDED)
            }
            R.id.action_search_already_responded -> if (role === Constants.EMPTY_STRING) {
                mViewModel!!.usergetFeedbacksByStatus(Constants.FEEDBACK_ALREADY_RESPONDED)
            } else {
                mViewModel!!.getFeedbacksByStatus(Constants.FEEDBACK_ALREADY_RESPONDED)
            }
            R.id.action_show_all_feedback -> if (role === Constants.EMPTY_STRING) {
                mViewModel!!.usergetFeedbacks()
            } else {
                mViewModel!!.feedbacks
            }
            android.R.id.home -> findNavController(requireView()).navigate(R.id.mainFragment)
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    private fun filter(text: String) {
        val filterList = ArrayList<Feedback?>()
        for (fb: Feedback? in mViewModel!!.feedbackList.getValue()!!) {
            if ((fb!!.userPhone!!.uppercase(Locale.getDefault())
                    .contains(text.uppercase(Locale.getDefault()))
                        || fb.userName!!.uppercase(Locale.getDefault())
                    .contains(text.uppercase(Locale.getDefault()))
                        || fb.date!!.uppercase(Locale.getDefault())
                    .contains(text.uppercase(Locale.getDefault()))
                        || fb.title!!.uppercase(Locale.getDefault())
                    .contains(text.uppercase(Locale.getDefault())))
            ) {
                filterList.add(fb)
            }
        }
        adapter!!.filterList(filterList.toList())
    } //

    //    @Override
    //    public void onResume() {
    //        super.onResume();
    //        if (role == Constants.EMPTY_STRING){
    //            mViewModel.usergetFeedbacks();
    //        }else {
    //            mViewModel.getFeedbacksByStatus(Constants.FEEDBACK_NOT_RESPONDED);
    //        }
    //    }
    companion object {
        fun newInstance(): CheckFeedbackFragment {
            return CheckFeedbackFragment()
        }
    }
}