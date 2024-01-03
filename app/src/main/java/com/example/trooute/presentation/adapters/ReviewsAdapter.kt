package com.example.trooute.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.trooute.R
import com.example.trooute.data.model.auth.response.User
import com.example.trooute.databinding.RvReviewsItemBinding

class ReviewsAdapter : ListAdapter<User, ReviewsAdapter.ViewHolder>(DiffCallback()) {
    inner class ViewHolder(private val binding: RvReviewsItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindViews(currentItem: User?) {
            currentItem?.let { item ->

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DataBindingUtil.inflate<RvReviewsItemBinding>(
            LayoutInflater.from(parent.context), R.layout.rv_reviews_item, parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bindViews(currentItem)
    }

    class DiffCallback :
        DiffUtil.ItemCallback<User>() {
        override fun areItemsTheSame(
            oldItem: User,
            newItem: User
        ) = oldItem.name == newItem.name

        override fun areContentsTheSame(
            oldItem: User,
            newItem: User
        ) = oldItem == newItem
    }
}