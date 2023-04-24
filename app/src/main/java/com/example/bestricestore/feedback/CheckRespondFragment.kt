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
import com.example.bestricestore.data.Respond
import com.example.bestricestore.databinding.FragmentCheckRespondBinding
import java.util.*

class CheckRespondFragment constructor() : Fragment(), CheckRespondAdapter.ListItemListener {
    private var mViewModel: CheckRespondViewModel? = null
    private var binding: FragmentCheckRespondBinding? = null
    private var adapter: CheckRespondAdapter? = null
    var role: String? = null
    public override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val app = activity as AppCompatActivity?
        //get activity, app chinh la ca activity chua fragment  nay
        val ab: ActionBar? = app!!.getSupportActionBar() //lay phan giai mau tim
        ab!!.setHomeButtonEnabled(true)
        ab.setDisplayShowHomeEnabled(true)
        ab.setDisplayHomeAsUpEnabled(true)
        ab.setHomeAsUpIndicator(R.drawable.ic_baseline_home_24)
        setHasOptionsMenu(true)
        mViewModel = ViewModelProvider(this).get(CheckRespondViewModel::class.java)
        binding = FragmentCheckRespondBinding.inflate(inflater, container, false)
        val rv: RecyclerView = binding!!.recyclerView
        rv.setHasFixedSize(true)
        rv.addItemDecoration(
            DividerItemDecoration(
                getContext(),
                (LinearLayoutManager(getContext()).getOrientation())
            )
        )
        mViewModel!!.respondList.observe(
            viewLifecycleOwner
        ) { responds: List<Respond?>? ->
            adapter = CheckRespondAdapter(responds, this)
            rv.setAdapter(adapter)
            rv.setLayoutManager(LinearLayoutManager(getContext()))
        }
        role = requireArguments().getString("role", "")
        responds
        return binding!!.getRoot()
    }

    private val responds: Unit
        private get() {
            if (role === Constants.EMPTY_STRING) {
                mViewModel!!.responds
            } else {
                mViewModel!!.allResponds
            }
        }

    public override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_check_responds, menu)
        val menuItem: MenuItem = menu.findItem(R.id.action_search_respond) // get our menu item.
        //Interface for direct access to a previously created menu item.
        val sv =
            MenuItemCompat.getActionView(menuItem) as android.widget.SearchView // getting search view of our item.
        sv.setMaxWidth(Int.MAX_VALUE)
        // below line is to call set on query text listener method.
        sv.setOnQueryTextListener(object : android.widget.SearchView.OnQueryTextListener {
            public override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            public override fun onQueryTextChange(newText: String): Boolean {
                if (mViewModel!!.respondList.getValue() != null) {
                    filter(newText)
                }
                return false
            }
        })
    }

    public override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.getItemId()) {
            android.R.id.home -> findNavController(requireView()).navigate(R.id.mainFragment)
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    fun filter(text: String) {
        val filterList: MutableList<Respond?> = ArrayList()
        for (fr: Respond? in mViewModel!!.respondList.getValue()!!) {
            if ((fr!!.title!!.uppercase(Locale.getDefault())
                    .contains(text.uppercase(Locale.getDefault()))
                        || fr.userPhone!!.uppercase(Locale.getDefault())
                    .contains(text.uppercase(Locale.getDefault()))
                        || fr.date!!.uppercase(Locale.getDefault())
                    .contains(text.uppercase(Locale.getDefault())))
            ) {
                filterList.add(fr)
            }
        }
        adapter!!.filterList(filterList)
    }

    public override fun onItemClick(id: String?) {
        val bundle: Bundle = Bundle()
        bundle.putString("respondId", id)
        findNavController(requireView()).navigate(R.id.editorRespondFragment, bundle)
    }

    public override fun onResume() {
        super.onResume()
        responds
    }

    companion object {
        fun newInstance(): CheckRespondFragment {
            return CheckRespondFragment()
        }
    }
}