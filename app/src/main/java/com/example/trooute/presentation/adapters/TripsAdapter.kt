package com.example.trooute.presentation.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.trooute.R
import com.example.trooute.core.util.SharedPreferenceManager
import com.example.trooute.data.model.common.Passenger
import com.example.trooute.data.model.trip.response.TripsData
import com.example.trooute.databinding.RvTripsItemBinding
import com.example.trooute.presentation.interfaces.AdapterItemClickListener
import com.example.trooute.presentation.interfaces.WishListEventListener
import com.example.trooute.presentation.ui.trip.SearchForTripsActivity
import com.example.trooute.presentation.utils.Utils.formatDateTime
import com.example.trooute.presentation.utils.ValueChecker.checkFloatValue
import com.example.trooute.presentation.utils.ValueChecker.checkLongValue
import com.example.trooute.presentation.utils.ValueChecker.checkPriceValue
import com.example.trooute.presentation.utils.ValueChecker.checkStringValue
import com.example.trooute.presentation.utils.loadImage
import com.example.trooute.presentation.utils.loadProfileImage
import com.example.trooute.presentation.utils.setRVOverlayHorizontal

class TripsAdapter(
    private val sharedPreferenceManager: SharedPreferenceManager,
    private val adapterItemClickListener: AdapterItemClickListener? = null,
    private val wishListEventListener: WishListEventListener? = null
) : ListAdapter<TripsData, TripsAdapter.ViewHolder>(DiffCallback()) {

    inner class ViewHolder(private val binding: RvTripsItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bindViews(currentItem: TripsData?) {
            currentItem?.let { item ->
                binding.apply {
                    val isDriverMode = sharedPreferenceManager?.driverMode()
                    if (isDriverMode == true) {
                        tvAvailableSeat.text = "${
                            checkLongValue(item.availableSeats)
                        } Seats Left"
                        ltDriverInfo.isVisible = false
                        ltDriverModePassengers.isVisible = true
                        rvPassengersUserMode.isVisible = false

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
                    } else {
                        tvAvailableSeat.text = "${
                            checkLongValue(item.availableSeats)
                        } Seats Available"
                        ltDriverInfo.isVisible = true
                        ltDriverModePassengers.isVisible = false
                        rvPassengersUserMode.isVisible = true

                        if (item.isAddedInWishList){
                            icHeart.isVisible = false
                            icRedHeart.isVisible = true
                        }else {
                            icHeart.isVisible = true
                            icRedHeart.isVisible = false
                        }

                        icHeart.setOnClickListener {
                            icHeart.isVisible = false
                            icRedHeart.isVisible = true
                            wishListEventListener?.onWishListEventClick(position = bindingAdapterPosition, data = item, added = true)
                        }

                        icRedHeart.setOnClickListener {
                            icHeart.isVisible = true
                            icRedHeart.isVisible = false
                            wishListEventListener?.onWishListEventClick(position = bindingAdapterPosition, data = item, added = false)
                        }

                        includeDriverInfo.apply {
                            loadProfileImage(imgUserProfile, item.driver?.photo)
                            tvUserName.text = checkStringValue(
                                tvUserName.context,
                                item.driver?.name
                            )

                            tvAvgRating.text = checkFloatValue(item.driver?.reviewsStats?.avgRating)
                            tvTotalReviews.text = "(${
                                checkLongValue(item.driver?.reviewsStats?.totalReviews)
                            })"

                            loadImage(imgCarImage, item.driver?.carDetails?.photo)
                            tvCarModel.text = checkStringValue(
                                tvCarModel.context,
                                item.driver?.carDetails?.model
                            )
                            tvCarRegistrationNumber.text = checkStringValue(
                                tvCarRegistrationNumber.context,
                                item.driver?.carDetails?.registrationNumber
                            )
                        }

                        rvPassengersUserMode.apply {
                            val passengersAdapter = PassengersSecondaryAdapter()
                            this.setRVOverlayHorizontal()
                            adapter = passengersAdapter
                            passengersAdapter.submitList(item.passengers)
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

    class DiffCallback : DiffUtil.ItemCallback<TripsData>() {
        override fun areItemsTheSame(
            oldItem: TripsData, newItem: TripsData
        ) = oldItem._id == newItem._id

        override fun areContentsTheSame(
            oldItem: TripsData, newItem: TripsData
        ) = oldItem == newItem
    }
}