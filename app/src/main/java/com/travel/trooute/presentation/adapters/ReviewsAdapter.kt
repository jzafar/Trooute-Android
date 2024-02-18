package com.travel.trooute.presentation.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.travel.trooute.R
import com.travel.trooute.data.model.review.response.Reviews
import com.travel.trooute.databinding.RvReviewsItemBinding
import com.travel.trooute.presentation.utils.ValueChecker
import com.travel.trooute.presentation.utils.loadProfileImage


class ReviewsAdapter : ListAdapter<Reviews, ReviewsAdapter.ViewHolder>(DiffCallback()) {
    inner class ViewHolder(private val binding: RvReviewsItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n", "RestrictedApi", "ResourceAsColor")
        fun bindViews(currentItem: Reviews?) {
            currentItem?.let { item ->
                binding.apply {
                    loadProfileImage(userImage, item.user?.photo.toString())
                    tvUserName.text = ValueChecker.checkStringValue(
                        tvUserName.context, item.user?.name
                    )
                    tvAvgRating.text =
                        ValueChecker.checkFloatValue(item.rating?.toFloat())
                    tvComment.text =
                        ValueChecker.checkStringValue(
                            tvComment.context, item.comment
                        )
                }
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
        DiffUtil.ItemCallback<Reviews>() {
        override fun areItemsTheSame(
            oldItem: Reviews,
            newItem: Reviews
        ) = oldItem._id == newItem._id

        override fun areContentsTheSame(
            oldItem: Reviews,
            newItem: Reviews
        ) = oldItem == newItem
    }
}