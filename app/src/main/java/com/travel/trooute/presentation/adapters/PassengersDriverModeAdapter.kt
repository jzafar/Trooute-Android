package com.travel.trooute.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.travel.trooute.R
import com.travel.trooute.data.model.common.Passenger
import com.travel.trooute.databinding.RvDriverPassengersItemBinding
import com.travel.trooute.presentation.utils.loadProfileImage

class PassengersDriverModeAdapter : ListAdapter<Passenger, PassengersDriverModeAdapter.ViewHolder>(DiffCallback()) {
    inner class ViewHolder(private val binding: RvDriverPassengersItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindViews(currentItem: Passenger?) {
            currentItem?.let { item ->
                binding.apply {
                    loadProfileImage(imgDriverSidePassengerProfile, item.photo)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DataBindingUtil.inflate<RvDriverPassengersItemBinding>(
            LayoutInflater.from(parent.context), R.layout.rv_driver_passengers_item, parent, false
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