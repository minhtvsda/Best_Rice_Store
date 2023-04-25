package com.example.bestricestore.newfeed

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.bestricestore.R
import com.example.bestricestore.data.NewEntity
import com.example.bestricestore.databinding.ListNewBinding
import com.example.bestricestore.newfeed.NewListAdapter.NewViewHolder

class NewListAdapter constructor(
    private val context: Context?,
    private val newList: List<NewEntity>,
    private val listener: ListNewListener
) : RecyclerView.Adapter<NewViewHolder>() {
    open interface ListNewListener {
        fun onItemClick(newId: String)
    }

    inner class NewViewHolder constructor(itemView: View?) : RecyclerView.ViewHolder(
        (itemView)!!
    ) {
        private val newViewBinding: ListNewBinding

        init {
            newViewBinding = ListNewBinding.bind((itemView)!!)
        }

        fun bindData(nData: NewEntity?) {
            newViewBinding.newTitle.text = "Title: " + nData!!.title
            newViewBinding.newInfo.text = "Main info: \n" + nData.newInfo
            newViewBinding.newDate.text = "Date: " + nData.date
            newViewBinding.root
                .setOnClickListener { v: View? -> listener.onItemClick(nData.id!!) }
            Glide.with((context)!!).load(nData.imageUrl)
                .error(R.drawable.profile)
                .override(600, 200).fitCenter()
                .into(newViewBinding.image)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_new, parent, false)
        //meaning get everything from app
        return NewViewHolder(view)
    }

    override fun onBindViewHolder(holder: NewViewHolder, position: Int) {
        val nData: NewEntity = newList.get(position)
        holder.bindData(nData)
    }

    override fun getItemCount(): Int {
        return newList.size
    }
}