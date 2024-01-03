package com.example.trooute.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.trooute.core.util.SharedPreferenceManager
import com.example.trooute.data.model.chat.Message
import com.example.trooute.databinding.RvReceivedMessageItemBinding
import com.example.trooute.databinding.RvSendMessageItemBinding
import com.example.trooute.presentation.utils.Utils.convertTimeStampToDate
import com.example.trooute.presentation.utils.ValueChecker.checkStringValue

class ChatAdapter(
    private val sharedPreferenceManager: SharedPreferenceManager
) : ListAdapter<Message, RecyclerView.ViewHolder>(MessageDiffCallback()) {

    companion object {
        private const val VIEW_TYPE_MESSAGE_SENT = 1
        private const val VIEW_TYPE_MESSAGE_RECEIVED = 2
    }

    inner class SentMessageHolder(private val binding: RvSendMessageItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindSentMessageViews(item: Message?) {
            binding.apply {
                item?.let {
                    tvMessage.text = checkStringValue(tvMessage.context, it.message)
                    tvMessageTime.text = it.timestamp?.let { time ->
                        convertTimeStampToDate(time)
                    }
                }
            }
        }
    }

    inner class ReceivedMessageHolder(private val binding: RvReceivedMessageItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindReceivedMessageViews(item: Message?) {
            binding.apply {
                item?.let {
                    tvMessage.text = it.message.toString()
                    tvMessageTime.text = it.timestamp?.let { time ->
                        convertTimeStampToDate(time)
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            VIEW_TYPE_MESSAGE_SENT -> {
                val binding = RvSendMessageItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return SentMessageHolder(binding)
            }

            VIEW_TYPE_MESSAGE_RECEIVED -> {
                val binding = RvReceivedMessageItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return ReceivedMessageHolder(binding)
            }

            else -> {
                throw IllegalArgumentException("Invalid view type")
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is SentMessageHolder) {
            holder.bindSentMessageViews(getItem(position))
        } else if (holder is ReceivedMessageHolder) {
            holder.bindReceivedMessageViews(getItem(position))
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (getItem(position).senderId == sharedPreferenceManager.getAuthIdFromPref()) {
            // If the current user is the sender of the message
            VIEW_TYPE_MESSAGE_SENT
        } else {
            // If some other user sent the message
            VIEW_TYPE_MESSAGE_RECEIVED
        }
    }

    class MessageDiffCallback : DiffUtil.ItemCallback<Message>() {
        override fun areItemsTheSame(
            oldItem: Message,
            newItem: Message
        ) = oldItem.message == newItem.message


        override fun areContentsTheSame(
            oldItem: Message,
            newItem: Message
        ) = oldItem == newItem
    }
}