package com.travel.trooute.presentation.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.travel.trooute.R
import com.travel.trooute.data.model.common.Passenger
import com.travel.trooute.data.model.wishlist.Message
import com.travel.trooute.databinding.RvTripsItemBinding
import com.travel.trooute.presentation.interfaces.AdapterItemClickListener
import com.travel.trooute.presentation.interfaces.WishListEventListener
import com.travel.trooute.presentation.utils.Utils.formatDateTime
import com.travel.trooute.presentation.utils.ValueChecker.checkLongValue
import com.travel.trooute.presentation.utils.ValueChecker.checkPriceValue
import com.travel.trooute.presentation.utils.ValueChecker.checkStringValue
import com.travel.trooute.presentation.utils.setRVOverlayHorizontal

class WishListAdapter(
    private val wishLisEvent: WishListEventListener? = null,
    private val adapterItemClickListener: AdapterItemClickListener? = null
) : ListAdapter<Message, WishListAdapter.ViewHolder>(DiffCallback()) {

    inner class ViewHolder(private val binding: RvTripsItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bindViews(currentItem: Message?) {
            currentItem?.let { item ->
                binding.apply {
                    tvAvailableSeat.text = "${
                        checkLongValue(item.availableSeats)
                    } Seats Left"
                    ltDriverInfo.isVisible = false
                    ltDriverModePassengers.isVisible = true
                    rvPassengersUserMode.isVisible = false

                    icHeart.setColorFilter(
                        ContextCompat.getColor(
                            icHeart.context, R.color.on_primary_flamingo_color
                        ),
                        android.graphics.PorterDuff.Mode.SRC_IN
                    )

                    icHeart.setOnClickListener {
                        wishLisEvent?.onWishListEventClick(
                            position = bindingAdapterPosition,
                            data = item,
                            added = false
                        )
                    }

                    var passengersList: List<Passenger> = arrayListOf()
                    item.passengers?.let { passengers ->
                        passengersList = passengers
                    }

                    if (passengersList.isEmpty()) {
                        rvPassengersDriverMode.isVisible = false
                        tvPassengersNotAvailable.isVisible = true
                    } else {
                        rvPassengersDriverMode.isVisible = true
                        tvPassengersNotAvailable.isVisible = false
                        rvPassengersDriverMode.apply {
                            val passengersDriverModeAdapter = PassengersDriverModeAdapter()
                            this.setRVOverlayHorizontal()
                            adapter = passengersDriverModeAdapter
                            passengersDriverModeAdapter.submitList(passengersList)
                        }
                    }

                    includeTripRouteLayout.apply {
                        tvAddressFrom.text = checkStringValue(
                            tvAddressFrom.context,
                            item.from_address
                        )
                        formatDateTime(
                            tvDepartureDate.context,
                            tvDepartureDate,
                            item.departureDate
                        )
                        tvAddressWhereto.text = checkStringValue(
                            tvAddressWhereto.context,
                            item.whereTo_address
                        )
                    }

                    tvPricePerPerson.text = checkPriceValue(item.pricePerPerson)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DataBindingUtil.inflate<RvTripsItemBinding>(
            LayoutInflater.from(parent.context), R.layout.rv_trips_item, parent, false
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

    class DiffCallback : DiffUtil.ItemCallback<Message>() {
        override fun areItemsTheSame(
            oldItem: Message, newItem: Message
        ) = oldItem._id == newItem._id

        override fun areContentsTheSame(
            oldItem: Message, newItem: Message
        ) = oldItem == newItem
    }
}