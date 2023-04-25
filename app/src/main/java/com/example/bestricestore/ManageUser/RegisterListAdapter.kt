package com.example.bestricestore.ManageUser

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.bestricestore.ManageUser.RegisterListAdapter.UserViewHolder
import com.example.bestricestore.R
import com.example.bestricestore.data.User
import com.example.bestricestore.databinding.ListRegisterBinding

class RegisterListAdapter constructor(
    private var userList: List<User>,
    private val listener: RegisterListAdapter.ListUserListener
) : RecyclerView.Adapter<UserViewHolder>() {
    open interface ListUserListener {
        fun onItemClick(userId: String?)
    }

    inner class UserViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val userViewBinding: ListRegisterBinding

        init {
            userViewBinding = ListRegisterBinding.bind(itemView)
        }

        fun bindData(uData: User) {
            userViewBinding.userName.text = "Name: " + uData.username
            userViewBinding.userPhone.text = "Phone:" + uData.phoneNumber
            userViewBinding.userAddress.text = "Address: " + uData.address
            userViewBinding.userRole.text = "Role: " + uData.roles
            userViewBinding.root
                .setOnClickListener { v: View? -> listener.onItemClick(uData.id) }
        }
    }

    fun filterList(filterList: List<User>) {
        userList = filterList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): UserViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_register, parent, false) //create view for each of food null;
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: UserViewHolder,
        position: Int
    ) {
        val uData: User = userList.get(position)
        holder.bindData(uData)
    }

    override fun getItemCount(): Int {
        return userList.size
    }
}