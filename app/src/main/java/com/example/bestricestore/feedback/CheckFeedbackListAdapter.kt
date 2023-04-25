package com.example.bestricestore.feedbackimport

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.bestricestore.R
import com.example.bestricestore.data.Feedback
import com.example.bestricestore.databinding.ListFeedbackBinding

class CheckFeedbackListAdapter constructor(
    private var feedbackList: List<Feedback?>,
    private val listener: CheckFeedbackListAdapter.ListItemListener
) : RecyclerView.Adapter<CheckFeedbackListAdapter.FeedbackViewHolder>() {
    open interface ListItemListener {
        fun onItemClick(feedbackId: String?)
    }

    inner class FeedbackViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val FeedbackViewBinding: ListFeedbackBinding

        init {
            FeedbackViewBinding = ListFeedbackBinding.bind(itemView)
        }

        fun bindData(fData: Feedback?) {
            FeedbackViewBinding.purpose.text = "Purpose: " + fData?.purpose
            FeedbackViewBinding.title.text = "Title: : " + fData?.title
            FeedbackViewBinding.date.text = "Date: " + fData?.date
            FeedbackViewBinding.userPhone.text = "UserPhone: " + fData?.userPhone
            FeedbackViewBinding.status.text = "Status: " + fData?.feedbackStatus
            FeedbackViewBinding.root
                .setOnClickListener{ v: View? -> listener.onItemClick(fData?.id) }
        }
    }

    fun filterList(feedbackList: List<Feedback?>) {
        this.feedbackList = feedbackList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CheckFeedbackListAdapter.FeedbackViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_feedback, parent, false)
        return FeedbackViewHolder(view)
    }

    override fun onBindViewHolder(holder: FeedbackViewHolder, position: Int) {
        val fData: Feedback = feedbackList[position]!!
        holder.bindData(fData)
    }

    override fun getItemCount(): Int {
        return feedbackList.size
    }
}