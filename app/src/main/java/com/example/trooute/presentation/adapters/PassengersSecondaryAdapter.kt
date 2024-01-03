package com.example.trooute.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.trooute.R
import com.example.trooute.data.model.common.Passenger
import com.example.trooute.databinding.RvPassengersSecondaryItemBinding
import com.example.trooute.presentation.utils.loadProfileImage

class PassengersSecondaryAdapter : ListAdapter<Passenger, PassengersSecondaryAdapter.ViewHolder>(DiffCallback()) {
    inner class ViewHolder(private val binding: RvPassengersSecondaryItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindViews(currentItem: Passenger?) {
            currentItem?.let { item ->
                binding.apply {
                    loadProfileImage(imgPassengerProfile, item.photo)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DataBindingUtil.inflate<RvPassengersSecondaryItemBinding>(
            LayoutInflater.from(parent.context), R.layout.rv_passengers_secondary_item, parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bindViews(currentItem)
    }

    class DiffCallback :
        DiffUtil.ItemCallback<Passenger>() {
        override fun areItemsTheSame(
            oldItem: Passenger,
            newItem: Passenger
        ) = oldItem._id == newItem._id

        override fun areContentsTheSame(
            oldItem: Passenger,
            newItem: Passenger
        ) = oldItem == newItem
    }
}