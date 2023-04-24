package com.example.bestricestore.feedbackimport

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

import com.example.bestricestore.R
import com.example.bestricestore.data.CartItem
import com.example.bestricestore.data.Constants
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
            FeedbackViewBinding.purpose.setText("Purpose: " + fData?.purpose)
            FeedbackViewBinding.title.setText("Title: : " + fData?.title)
            FeedbackViewBinding.date.setText("Date: " + fData?.date)
            FeedbackViewBinding.userPhone.setText("UserPhone: " + fData?.userPhone)
            FeedbackViewBinding.status.setText("Status: " + fData?.feedbackStatus)
            FeedbackViewBinding.getRoot()
                .setOnClickListener{ v: View? -> listener.onItemClick(fData?.id) }
        }
    }

    fun filterList(feedbackList: List<Feedback?>) {
        this.feedbackList = feedbackList
        notifyDataSetChanged()
    }

    public override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CheckFeedbackListAdapter.FeedbackViewHolder {
        val view: View = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.list_feedback, parent, false)
        return FeedbackViewHolder(view)
    }

    public override fun onBindViewHolder(holder: FeedbackViewHolder, position: Int) {
        val fData: Feedback = feedbackList[position]!!
        holder.bindData(fData)
    }

    public override fun getItemCount(): Int {
        return feedbackList.size
    }
}