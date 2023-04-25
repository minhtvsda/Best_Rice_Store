package com.example.bestricestore.ManageUser


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.bestricestore.R
import com.example.bestricestore.data.Constants
import com.example.bestricestore.data.User
import com.example.bestricestore.databinding.ListUserBinding

class UserListAdapter constructor(
    private var userList: List<User>,
    private val listener: UserListAdapter.ListUserListener
) : RecyclerView.Adapter<UserListAdapter.UserViewHolder>() {
    open interface ListUserListener {
        fun onItemClick(userId: String?, i: Int)
    }

    inner class UserViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val userViewBinding: ListUserBinding

        init {
            userViewBinding = ListUserBinding.bind(itemView)
        }

        fun bindData(uData: User) {
            userViewBinding.userName.text = "Name: " + uData.username
            userViewBinding.userPhone.text = "Phone:" + uData.phoneNumber
            userViewBinding.userAddress.text = "Address: " + uData.address
            userViewBinding.userRole.text = "Role: " + uData.roles
            if (((uData.roles == Constants.FS_ROLE_BAN) || (uData.roles == Constants.DELIVERER_REGISTER_WAITING))) {
                userViewBinding.buttonBan.visibility = View.GONE
            }
            userViewBinding.buttonBan.setOnClickListener { v: View? ->
                listener.onItemClick(
                    uData.id,
                    1
                )
            }
            userViewBinding.root
                .setOnClickListener { v: View? ->
                    listener.onItemClick(
                        uData.id,
                        2
                    )
                }
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
            .inflate(R.layout.list_user, parent, false) //create view for each of food null;
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val uData: User = userList.get(position)
        holder.bindData(uData)
    }

    override fun getItemCount(): Int {
        return userList.size
    }
}