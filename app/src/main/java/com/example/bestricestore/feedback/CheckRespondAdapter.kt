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
            respondViewBinding.date.setText("Date: " + rData?.date)
            respondViewBinding.title.setText("Title:" + rData?.title)
            respondViewBinding.userPhone.setText("UserPhone: " + rData?.userPhone)
            respondViewBinding.purpose.setText("Purpose: " + rData?.purpose)
            respondViewBinding.getRoot()
                .setOnClickListener(View.OnClickListener({ v: View? -> listener.onItemClick(rData?.id) }))
        }
    }

    fun filterList(filterList: List<Respond?>?) {
        respondList = filterList
        notifyDataSetChanged()
    }

    public override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RespondViewHolder {
        val view: View = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.list_respond, parent, false)
        return RespondViewHolder(view)
    }

    public override fun onBindViewHolder(holder: RespondViewHolder, position: Int) {
        val respond: Respond? = respondList!!.get(position)
        holder.bindData(respond)
    }

    public override fun getItemCount(): Int {
        return respondList!!.size
    }
}