package com.example.bestricestore
import android.app.Dialog
import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.View.GONE
import android.widget.*
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuItemCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bestricestore.data.Constants
import com.example.bestricestore.data.FoodEntity
import com.example.bestricestore.databinding.FragmentMainBinding
import com.example.bestricestore.notification.PushNotification
import com.example.bestricestore.notification.RetrofitInstance
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.api.Distribution.BucketOptions.Linear
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*


class MainFragment constructor() : Fragment(), FoodListAdapter.ListFoodListener, FoodLikeListAdapter.ListFoodLikeListener,
                            FoodSoldListAdapter.ListFoodSoldListener{
    private lateinit var mViewModel: MainViewModel
    private lateinit var binding: FragmentMainBinding
    private lateinit var adapter: FoodListAdapter
    private lateinit var adapterLike: FoodLikeListAdapter
    private lateinit var adapterSold: FoodSoldListAdapter
    private lateinit var navController : NavController
    var userRole: String? = null
    var tokenId: String? = null
    public override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val app: AppCompatActivity? = activity as AppCompatActivity?
        val ab: ActionBar? = app!!.supportActionBar
        ab!!.setDisplayHomeAsUpEnabled(true)
        ab.title = "Home"
        ab.setDisplayShowHomeEnabled(true)
        ab.setDisplayHomeAsUpEnabled(true)
        ab.setHomeAsUpIndicator(R.drawable.outline_menu_24)
        FirebaseMessaging.getInstance().subscribeToTopic(Constants.TOPIC_ORDER)
        //subscribes the topic
        setHasOptionsMenu(true)
        mViewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        binding = FragmentMainBinding.inflate(inflater, container, false)
        val rvLike = binding.recyclerViewLike
        rvLike.hasFixedSize()
        rvLike.layoutManager = LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL, false)
        rvLike.addItemDecoration(
            DividerItemDecoration(
                rvLike.context,
                DividerItemDecoration.HORIZONTAL
            )
        )
        val rvSold = binding.recyclerViewSold
        rvSold.hasFixedSize()
        rvSold.layoutManager = LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL, false)
        rvSold.addItemDecoration(
            DividerItemDecoration(
                context, LinearLayoutManager.HORIZONTAL
            ))
        val rv = binding!!.recyclerView
        rv.setHasFixedSize(true)
        rv.addItemDecoration(
            DividerItemDecoration(
                context,
                (LinearLayoutManager(context).orientation)
            )
        )
        mViewModel.foodListLike.observe(viewLifecycleOwner){
            adapterLike = FoodLikeListAdapter(requireContext(), it, this)
            rvLike.adapter = adapterLike

        }
        mViewModel.foodListSell.observe(viewLifecycleOwner){
//            val compareByType: Comparator<FoodEntity> =
//                Comparator<FoodEntity> { f1, f2 ->
//                    f1.totalLike!!.compareTo(f2.totalLike!!)
//                }
//            Collections.sort(it, compareByType)
            adapterSold = FoodSoldListAdapter(requireContext(), it, this)
            rvSold.adapter = adapterSold
        }

        mViewModel!!.foodList.observe(
            viewLifecycleOwner
        ) {
                foodList: List<FoodEntity> ->
            adapter = FoodListAdapter(requireContext(), foodList, this)
            binding!!.recyclerView.adapter = adapter
            binding!!.recyclerView.layoutManager = LinearLayoutManager(activity)
        }
        binding!!.fabAddFood.setOnClickListener {
//            FirebaseMessaging.getInstance().subscribeToTopic(Constants.TOPIC_ORDER)
            onItemClick(Constants.NEW_ID)
//            sortByType()
        }
        binding.bottomNavigation.setOnNavigationItemSelectedListener {
            onNavigationItemSelected(it)
        }
        hideUIwithRoles()

        return binding.root
    }

    private fun hideUIwithRoles() {
        val db: FirebaseFirestore = FirebaseFirestore.getInstance()
        val user: FirebaseUser = FirebaseAuth.getInstance().currentUser ?: return

        FirebaseMessaging.getInstance().token.addOnSuccessListener {
            if (it != null){
                tokenId = it
                db.collection(Constants.FS_USER).document(user.getUid()).update("tokenId", it)
            }
        }
            .addOnFailureListener{
                Toast.makeText(context, "Failed save token", Toast.LENGTH_LONG).show()
            }
        db.collection(Constants.FS_USER).document(user.getUid()).get()
            .addOnCompleteListener(object : OnCompleteListener<DocumentSnapshot> {
                override fun onComplete(task: Task<DocumentSnapshot>) {
                    if (task.isSuccessful()) {
                        val document: DocumentSnapshot = task.getResult()
                        if (document.exists()) {
                            Log.d(ContentValues.TAG, "DocumentSnapshot data: " + document.getData())
                            userRole = document.getString("roles")
                            if ((userRole == Constants.ROLE_ADMIN)) {
                                binding.fabAddFood.visibility = View.VISIBLE
                                mViewModel.getFoodAdmin()
                            }   else{
                                mViewModel.getFoods()
                            }

                        }
                    }
                }
            })
    }

    public override fun onItemClick(foodId: String?) {
        val bundle: Bundle = Bundle()
        bundle.putString("foodId", foodId)
        if ((userRole == Constants.ROLE_ADMIN)) {
            findNavController(requireView()).navigate(R.id.editorFragment, bundle)
        } else {
            bundle.putString("userToken", tokenId)
            findNavController(requireView()).navigate(R.id.foodDetailFragment, bundle)
        }
    }


    public override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_main, menu)
        val menuItem: MenuItem = menu.findItem(R.id.action_search) // get our menu item.
        //Interface for direct access to a previously created menu item.
        val sv =
            MenuItemCompat.getActionView(menuItem) as androidx.appcompat.widget.SearchView // getting search view of our item.
        sv.setMaxWidth(Int.MAX_VALUE)
        // below line is to call set on query text listener method.
        sv.setQueryHint("Search by Type, Name, Cost")
        sv.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            public override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            public override fun onQueryTextChange(newText: String): Boolean {
                if (mViewModel!!.foodList.getValue() != null) {
                    filter(newText)
                }
                return false
            }
        })
    }

    fun filter(text: String) {
        val filterList: MutableList<FoodEntity> = ArrayList()
        for (fe: FoodEntity in mViewModel!!.foodList.getValue()!!) {
            if ((fe.name!!.uppercase(Locale.getDefault())
                    .contains(text.uppercase(Locale.getDefault()))
                        || fe.type!!.uppercase(Locale.getDefault())
                    .contains(text.uppercase(Locale.getDefault()))
                        || fe.cost.toString()
                    .contains(text.uppercase(Locale.getDefault())))
            ) {
                filterList.add(fe)
            }
        }
        adapter!!.filterList(filterList)
    }

    fun filterType(type: String): Boolean {
        val filterList: MutableList<FoodEntity> = ArrayList()
        for (fe: FoodEntity in mViewModel!!.foodList.getValue()!!) {
            if (fe.type!!.lowercase(Locale.getDefault())
                    .contains(type.lowercase(Locale.getDefault()))
            ) {
                filterList.add(fe)
            }
        }
        adapter!!.filterList(filterList)
        return true
    }

    private fun showSearchCost() {
        val dialog: Dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.dialogfragment_search_cost)
        dialog.show()
        val edt_cost_from: EditText = dialog.findViewById(R.id.cost_from)
        val edt_cost_to: EditText = dialog.findViewById(R.id.cost_to)
        val button: Button = dialog.findViewById(R.id.btn_search_by_cost)
        button.setOnClickListener(View.OnClickListener { v: View? ->
            val cost_from: String = edt_cost_from.getText().toString()
            val cost_to: String = edt_cost_to.getText().toString()
            if (((cost_from == Constants.EMPTY_STRING) || (cost_to == Constants.EMPTY_STRING))) {
                Toast.makeText(getActivity(), "Please type the correct Cost!", Toast.LENGTH_SHORT)
                    .show()
            } else if (cost_from.toInt() >= cost_to.toInt()) {
                Toast.makeText(
                    getActivity(),
                    "The cost from must be lower than cost to!",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                filterbyCost(cost_from, cost_to)
                dialog.dismiss()
                Toast.makeText(
                    getContext(),
                    "Search cost from" + cost_from + "VND to " + cost_to + "VND",
                    Toast.LENGTH_LONG
                ).show()
            }
        })
    }
    fun searchMenu(){
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.dialogfragment_search_menu_main)
        dialog.window?.setGravity(Gravity.START)

        dialog.show()
        val button_drink = dialog.findViewById<ImageView>(R.id.search_drink)
        val button_noddle = dialog.findViewById<ImageView>(R.id.search_noodle)
        val button_rice = dialog.findViewById<ImageView>(R.id.search_rice)
        val button_all = dialog.findViewById<ImageView>(R.id.show_all)
        val search_favorite = dialog.findViewById<TextView>(R.id.search_favorite)
        val search_sold = dialog.findViewById<TextView>(R.id.search_total_sold)
        search_favorite.visibility = GONE
        search_sold.visibility = GONE
        search_favorite.setOnClickListener{
            sortBy("favorite")
            dialog.dismiss()
        }
        search_sold.setOnClickListener{
            sortBy("sold")
            dialog.dismiss()
        }
        button_noddle.setOnClickListener{
            filterType("Noodle")
            Toast.makeText(context, "Search all the noodles", Toast.LENGTH_LONG).show()
            dialog.dismiss()
        }
        button_drink.setOnClickListener{
            filterType("Drink")
            Toast.makeText(context, "Search all the drinks", Toast.LENGTH_LONG).show()
            dialog.dismiss()
        }
        button_rice.setOnClickListener{
            filterType("Rice")
            Toast.makeText(context, "Search all the rice", Toast.LENGTH_LONG).show()
            dialog.dismiss()
        }
        button_all.setOnClickListener {
            mViewModel.getFoods()
            Toast.makeText(context, "Show all food", Toast.LENGTH_LONG).show()
            dialog.dismiss()
        }
    }
    fun filterbyCost(cost_from: String, cost_to: String) {
        val from: Int = cost_from.toInt()
        val to: Int = cost_to.toInt()
        val filterList: MutableList<FoodEntity> = ArrayList()
        for (fe: FoodEntity in mViewModel.foodList.getValue()!!) {
            if (fe.cost in from..to) {
                filterList.add(fe)
            }
        }
        adapter!!.filterList(filterList)
    }

    public override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.getItemId()) {
            android.R.id.home -> searchMenu()
            R.id.action_search_type_drink -> filterType("Drink")
            R.id.action_search_type_rice -> filterType("Rice")
            R.id.action_search_type_noodle -> filterType("Noodle")
            R.id.action_search_by_cost -> {
                showSearchCost()
                return super.onOptionsItemSelected(item)
            }
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.getItemId()) {
            R.id.Home -> {
                if (userRole == Constants.ROLE_ADMIN) {
                mViewModel.getFoodAdmin()
            }   else{
                mViewModel.getFoods()
            }
                Toast.makeText(context, "You are in Home screen now!", Toast.LENGTH_SHORT)
                    .show()
            }
            R.id.shoppingCart -> {
                if ((userRole == Constants.ROLE_CUSTOMER)) {
                    findNavController(requireView()).navigate(R.id.cartFragment)
                }
                if ((userRole == Constants.ROLE_DELIVERER)) {
                    findNavController(requireView()).navigate(R.id.checkOrderFragment)
                }
                if ((userRole == Constants.ROLE_ADMIN)) {
                    findNavController(requireView()).navigate(R.id.orderAdminFragment)
                }
            }
            R.id.News -> findNavController(requireView()).navigate(R.id.newFragment)
            R.id.Profile -> findNavController(requireView()).navigate(R.id.profileFragment)
        }
        return true
    }
    fun sendNotification(notification: PushNotification) = CoroutineScope(Dispatchers.IO).launch {
        try {
            val  respond = RetrofitInstance.api.postNotification(notification)
            if(respond.isSuccessful){

            } else{
                Log.e("sendNotification", respond.errorBody().toString())
            }
        } catch (e: Exception){
            Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show()
        }
    }

    fun sortBy(value: String){

        val compareByType: Comparator<FoodEntity> =
            Comparator<FoodEntity> { f1, f2 ->
                if (value == "favorite") f1.totalLike!!.compareTo(f2.totalLike!!) else f1.totalSell!!.compareTo(f2.totalSell!!)
            }
        val list = mViewModel.foodList.value!!
        Collections.sort(mViewModel.foodList.value!!, compareByType)
        adapter.filterList(list)
    }

}