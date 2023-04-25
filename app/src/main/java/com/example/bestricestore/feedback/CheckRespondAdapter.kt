package com.example.bestricestore.feedback

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.bestricestore.R
import com.example.bestricestore.data.Respond
import com.example.bestricestore.databinding.ListRespondBinding
import com.example.bestricestore.feedback.CheckRespondAdapter.RespondViewHolder

class CheckRespondAdapter constructor(
    private var respondList: List<Respond?>?,
    private val listener: ListItemListener
) : RecyclerView.Adapter<RespondViewHolder>() {
    open interface ListItemListener {
        fun onItemClick(id: String?)
    }

    inner class RespondViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val respondViewBinding: ListRespondBinding

        init {
            respondViewBinding = ListRespondBinding.bind(itemView)
        }

        fun bindData(rData: Respond?) {
            respondViewBinding.date.text = "Date: " + rData?.date
            respondViewBinding.title.text = "Title:" + rData?.title
            respondViewBinding.userPhone.text = "UserPhone: " + rData?.userPhone
            respondViewBinding.purpose.text = "Purpose: " + rData?.purpose
            respondViewBinding.root
                .setOnClickListener({ v: View? -> listener.onItemClick(rData?.id) })
        }
    }

    fun filterList(filterList: List<Respond?>?) {
        respondList = filterList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RespondViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_respond, parent, false)
        return RespondViewHolder(view)
    }

    override fun onBindViewHolder(holder: RespondViewHolder, position: Int) {
        val respond: Respond? = respondList!!.get(position)
        holder.bindData(respond)
    }

    override fun getItemCount(): Int {
        return respondList!!.size
    }
}