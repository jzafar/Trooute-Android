package com.example.trooute.presentation.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.trooute.R
import com.example.trooute.core.util.SharedPreferenceManager
import com.example.trooute.data.model.common.User
import com.example.trooute.data.model.trip.response.Booking
import com.example.trooute.databinding.RvDriverSidePassengersItemBinding
import com.example.trooute.presentation.utils.StatusChecker.checkStatus
import com.example.trooute.presentation.utils.Utils.formatDateTime
import com.example.trooute.presentation.utils.Utils.getSubString
import com.example.trooute.presentation.utils.ValueChecker.checkFloatValue
import com.example.trooute.presentation.utils.ValueChecker.checkLongValue
import com.example.trooute.presentation.utils.ValueChecker.checkNumOfSeatsValue
import com.example.trooute.presentation.utils.ValueChecker.checkPriceValue
import com.example.trooute.presentation.utils.ValueChecker.checkStringValue
import com.example.trooute.presentation.utils.loadProfileImage

class DriverSidePassengersAdapter(
    private val sharedPreferenceManager: SharedPreferenceManager,
    private val startMessaging: (User?) -> Unit,
    private val startCall: (User?) -> Unit
) : ListAdapter<Booking, DriverSidePassengersAdapter.ViewHolder>(DiffCallback()) {

    inner class ViewHolder(private val binding: RvDriverSidePassengersItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n", "RestrictedApi")
        fun bindViews(currentItem: Booking?) {
            currentItem?.let { item ->
                binding.apply {
                    checkStatus(sharedPreferenceManager.driverMode(), tvBookingStatus, item.status)

                    tvBookingId.text = "Booking # ${getSubString(item._id)}"
                    formatDateTime(tvBookingDate.context, tvBookingDate, item.createdAt)

                    includeUserDetail.apply {
                        loadProfileImage(imgUserProfile, item.user?.photo)
                        tvUserName.text = checkStringValue(
                            tvUserName.context,
                            item.user?.name
                        )
                        tvAvgRating.text = checkFloatValue(item.user?.reviewsStats?.avgRating)
                        tvTotalReviews.text = "(${
                            checkLongValue(item.user?.reviewsStats?.totalReviews)
                        })"

                        if(currentItem.status.equals(ltCallInboxSection.context.getString(R.string.confirmed))) {
                            ltCallInboxSection.isVisible = true
                        } else {
                            ltCallInboxSection.isVisible = false
                        }

                        messageIcon.setOnClickListener {
                            startMessaging.invoke(item.user)
                        }

                        callIcon.setOnClickListener {
                            startCall.invoke(item.user)
                        }
                    }

                    tvNxSeats.text = checkNumOfSeatsValue(item.numberOfSeats)
                    tvNxSeatsPrice.text = checkPriceValue(item.amount)
                    tvTotalPrice.text = checkPriceValue(item.amount)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DataBindingUtil.inflate<RvDriverSidePassengersItemBinding>(
            LayoutInflater.from(parent.context),
            R.layout.rv_driver_side_passengers_item,
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bindViews(currentItem)
    }

    class DiffCallback : DiffUtil.ItemCallback<Booking>() {
        override fun areItemsTheSame(
            oldItem: Booking,
            newItem: Booking
        ) = oldItem._id == newItem._id

        override fun areContentsTheSame(
            oldItem: Booking,
            newItem: Booking
        ) = oldItem == newItem
    }
}