package com.travel.trooute.presentation.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.travel.trooute.R
import com.travel.trooute.core.util.Constants
import com.travel.trooute.core.util.SharedPreferenceManager
import com.travel.trooute.data.model.common.User
import com.travel.trooute.data.model.trip.response.Booking
import com.travel.trooute.databinding.RvDriverSidePassengersItemBinding
import com.travel.trooute.presentation.interfaces.AdapterItemClickListener
import com.travel.trooute.presentation.utils.StatusChecker.checkStatus
import com.travel.trooute.presentation.utils.Utils.formatDateTime
import com.travel.trooute.presentation.utils.Utils.getSubString
import com.travel.trooute.presentation.utils.ValueChecker
import com.travel.trooute.presentation.utils.ValueChecker.checkFloatValue
import com.travel.trooute.presentation.utils.ValueChecker.checkLongValue
import com.travel.trooute.presentation.utils.ValueChecker.checkNumOfSeatsValue
import com.travel.trooute.presentation.utils.ValueChecker.checkPriceValue
import com.travel.trooute.presentation.utils.ValueChecker.checkStringValue
import com.travel.trooute.presentation.utils.loadProfileImage


class DriverSidePassengersAdapter(
    private val itemClickListener: AdapterItemClickListener,
    private val sharedPreferenceManager: SharedPreferenceManager,
    private val startMessaging: (User?) -> Unit,
    private val startCall: (User?) -> Unit
) : ListAdapter<Booking, DriverSidePassengersAdapter.ViewHolder>(DiffCallback()) {

    inner class ViewHolder(private val binding: RvDriverSidePassengersItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n", "RestrictedApi")
        fun bindViews(currentItem: Booking?) {
            val width = binding.root.context.resources.displayMetrics?.widthPixels
            if (width != null && this@DriverSidePassengersAdapter.itemCount > 1) {
                val params = RecyclerView.LayoutParams((width * 0.85).toInt(), RecyclerView.LayoutParams.WRAP_CONTENT)
                itemView.layoutParams = params
            }

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
                        var genderStr = ValueChecker.checkStringValue(
                            gender.context, item?.user?.gender
                        )

                        if (genderStr.equals(gender.context.getString(R.string.not_provided))){
                            gender.isVisible = false
                        }
                        else {
                            gender.text = genderStr
                        }

                        tvAvgRating.text = checkFloatValue(item.user?.reviewsStats?.avgRating)
                        tvTotalReviews.text = "(${
                            checkLongValue(item.user?.reviewsStats?.totalReviews)
                        })"

                        ltCallInboxSection.isVisible = currentItem.status.equals(ltCallInboxSection.context.getString(R.string.confirmed))

                        messageIcon.setOnClickListener {
                            startMessaging.invoke(item.user)
                        }

                        callIcon.setOnClickListener {
                            startCall.invoke(item.user)
                        }
                    }
                    val platFormFee = Constants.PLATFORM_FEE_PRICE * item.numberOfSeats!!
                    if (sharedPreferenceManager.driverMode()) {
                        val pricePerSeat = (item.pricePerPerson ?: 1.0) * item.numberOfSeats!!
                        tvNxSeats.text = checkNumOfSeatsValue(item.numberOfSeats)
                        tvNxSeatsPrice.text = checkPriceValue(pricePerSeat)
                        tvTotalPrice.text = checkPriceValue(pricePerSeat - platFormFee)
                    } else {

                    }





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
        holder.itemView.setOnClickListener {
            itemClickListener?.onAdapterItemClicked(position = position, data = currentItem)
        }
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