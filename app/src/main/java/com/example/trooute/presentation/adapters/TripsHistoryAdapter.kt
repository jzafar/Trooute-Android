package com.example.trooute.presentation.adapters

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.trooute.R
import com.example.trooute.data.model.trip.response.TripsData
import com.example.trooute.databinding.RvTripHistoryItemBinding
import com.example.trooute.presentation.interfaces.AdapterItemClickListener
import com.example.trooute.presentation.utils.Utils.formatDateTime
import com.example.trooute.presentation.utils.Utils.getSubString
import com.example.trooute.presentation.utils.ValueChecker.checkPriceValue
import com.example.trooute.presentation.utils.ValueChecker.checkStringValue
import com.example.trooute.presentation.utils.setRVHorizontal
import com.example.trooute.presentation.utils.setRVOverlayHorizontal

class TripsHistoryAdapter(
    private val adapterItemClickListener: AdapterItemClickListener? = null
) : ListAdapter<TripsData, TripsHistoryAdapter.ViewHolder>(DiffCallback()) {

    inner class ViewHolder(private val binding: RvTripHistoryItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bindViews(currentItem: TripsData?) {
            currentItem?.let { item ->
                binding.apply {
                    tvTripsHistoryId.text = "Trip # ${getSubString(item._id)}"
                    formatDateTime(tvTripsHistoryDateTime.context, tvTripsHistoryDateTime, item.departureDate.toString())

                        if (item.passengers?.isEmpty() == true) {
                            tvNoPassengersAvailable.isVisible = true
                            rvPassengers.isVisible = false
                        } else {
                            tvNoPassengersAvailable.isVisible = false
                            rvPassengers.isVisible = true
                            val passengersAdapter = PassengersPrimaryAdapter(null)
                            rvPassengers.setRVOverlayHorizontal()
                            rvPassengers.adapter = passengersAdapter
                            passengersAdapter.submitList(item.passengers)
                        }

                    includeTripRouteLayout.apply {
                        tvAddressFrom.text = checkStringValue(
                            tvAddressFrom.context,
                            item.from_address
                        )
                        formatDateTime(tvDepartureDate.context, tvDepartureDate, item.departureDate)
                        tvAddressWhereto.text = checkStringValue(
                            tvAddressWhereto.context,
                            item.whereTo_address
                        )
                    }

                    tvPricePerPerson.text = checkPriceValue(item.pricePerPerson)
                    tvTotalPrice.text = checkPriceValue(item.totalAmount)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DataBindingUtil.inflate<RvTripHistoryItemBinding>(
            LayoutInflater.from(parent.context), R.layout.rv_trip_history_item, parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bindViews(currentItem)

        holder.itemView.setOnClickListener {
            adapterItemClickListener?.onAdapterItemClicked(position = position, data = currentItem)
        }
    }

    class DiffCallback :
        DiffUtil.ItemCallback<TripsData>() {
        override fun areItemsTheSame(
            oldItem: TripsData,
            newItem: TripsData
        ) = oldItem._id == newItem._id

        override fun areContentsTheSame(
            oldItem: TripsData,
            newItem: TripsData
        ) = oldItem == newItem
    }
}