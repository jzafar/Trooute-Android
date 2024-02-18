package com.travel.trooute.presentation.adapters

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.adapters.TextViewBindingAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.travel.trooute.R
import com.travel.trooute.data.model.chat.Inbox
import com.travel.trooute.databinding.RvInboxItemBinding
import com.travel.trooute.presentation.interfaces.AdapterItemClickListener
import com.travel.trooute.presentation.utils.Utils.convertTimeStampToDate
import com.travel.trooute.presentation.utils.ValueChecker.checkStringValue
import com.travel.trooute.presentation.utils.loadProfileImage

class InboxAdapter(
    private val adapterItemClickListener: AdapterItemClickListener
) : ListAdapter<Inbox, InboxAdapter.ViewHolder>(DiffCallback()) {
    inner class ViewHolder(private val binding: RvInboxItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("RestrictedApi")
        fun bindViews(currentItem: Inbox?) {
            currentItem?.let { item ->
                binding.apply {
                    tvUserName.text = checkStringValue(
                        tvUserName.context,
                        item.user?.name
                    )

                    Log.e("InboxAdapter", "bindViews: id -> " + item.user?._id.toString() )

                    if (item.user?.seen == true) {
                        tvUserName.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
                    } else {
                        TextViewBindingAdapter.setDrawableEnd(
                            tvUserName,
                            ContextCompat.getDrawable(
                                tvUserName.context,
                                R.drawable.ic_status_waiting
                            )
                        )
                    }

                    Log.e("InboxAdapter", "bindViews: item.lastMessage.toString() -> " + item.lastMessage.toString() )

                    tvInboxTime.text = item.timestamp?.let { time ->
                        convertTimeStampToDate(time)
                    }
                    tvLastMessage.text = checkStringValue(
                        tvLastMessage.context,
                        item.lastMessage.toString()
                    )
                    loadProfileImage(userImage, item.user?.photo.toString())
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DataBindingUtil.inflate<RvInboxItemBinding>(
            LayoutInflater.from(parent.context), R.layout.rv_inbox_item, parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bindViews(currentItem)

        holder.itemView.setOnClickListener {
            adapterItemClickListener.onAdapterItemClicked(position, currentItem)
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Inbox>() {
        override fun areItemsTheSame(
            oldItem: Inbox,
            newItem: Inbox
        ) = oldItem.user?._id == newItem.user?._id

        override fun areContentsTheSame(
            oldItem: Inbox,
            newItem: Inbox
        ) = oldItem == newItem
    }
}