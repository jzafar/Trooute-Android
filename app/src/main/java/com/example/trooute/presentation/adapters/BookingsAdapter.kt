package com.example.trooute.presentation.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.trooute.R
import com.example.trooute.core.util.SharedPreferenceManager
import com.example.trooute.data.model.bookings.response.BookingData
import com.example.trooute.data.model.common.User
import com.example.trooute.databinding.RvBookingsItemBinding
import com.example.trooute.presentation.interfaces.AdapterItemClickListener
import com.example.trooute.presentation.utils.FilterBookings
import com.example.trooute.presentation.utils.StatusChecker.checkStatus
import com.example.trooute.presentation.utils.Utils.formatDateTime
import com.example.trooute.presentation.utils.Utils.getSubString
import com.example.trooute.presentation.utils.ValueChecker.checkFloatValue
import com.example.trooute.presentation.utils.ValueChecker.checkLongValue
import com.example.trooute.presentation.utils.ValueChecker.checkNumOfSeatsValue
import com.example.trooute.presentation.utils.ValueChecker.checkPriceValue
import com.example.trooute.presentation.utils.ValueChecker.checkStringValue
import com.example.trooute.presentation.utils.loadImage
import com.example.trooute.presentation.utils.loadProfileImage

class BookingsAdapter(
    private val adapterItemClickListener: AdapterItemClickListener,
    private val sharedPreferenceManager: SharedPreferenceManager,
    private val startMessaging: (User?) -> Unit,
    private val startCall: (User?) -> Unit
) : ListAdapter<BookingData, BookingsAdapter.ViewHolder>(DiffCallback()), Filterable {

    inner class ViewHolder(private val binding: RvBookingsItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n", "RestrictedApi", "ResourceAsColor")
        fun bindViews(currentItem: BookingData?) {
            currentItem?.let { item ->
                binding.apply {
                    checkStatus(sharedPreferenceManager.driverMode(), tvStatus, item.status)

                    tvBookingId.text = "Booking # ${getSubString(item._id)}"

                    formatDateTime(
                        tvDepartureDate.context,
                        tvDepartureDate,
                        item.trip?.departureDate
                    )

                    if (sharedPreferenceManager.driverMode()) {
                        includeUserInfoLayout.ltRoot.isVisible = false
                        includeDriverSideUserInfoLayout.ltRoot.apply {
                            isVisible = true
                            setCardBackgroundColor(
                                ContextCompat.getColor(
                                    this.context,
                                    R.color.secondary_athens_gray_color
                                )
                            )
                            cardElevation = 0F
                        }
                        includeDriverSideUserInfoLayout.apply {
                            loadProfileImage(imgUserProfile, item.user?.photo)
                            tvUserName.text = checkStringValue(
                                tvUserName.context,
                                item.user?.name
                            )
                            tvAvgRating.text = checkFloatValue(
                                item.user?.reviewsStats?.avgRating
                            )
                            tvTotalReviews.text = "(${
                                checkLongValue(item.user?.reviewsStats?.totalReviews)
                            })"

                            messageIcon.setOnClickListener {
                                startMessaging.invoke(item.user)
                            }

                            callIcon.setOnClickListener {
                                startCall.invoke(item.user)
                            }
                        }

                        ltNxSeats.isVisible = true
                        includeDivider.divider.isVisible = true

                        tvNxSeats.text = checkNumOfSeatsValue(item.numberOfSeats)
                        tvNxSeatsPrice.text = checkPriceValue(item.amount)
                    } else {
                        includeDriverSideUserInfoLayout.ltRoot.isVisible = false
                        includeUserInfoLayout.ltRoot.isVisible = true
                        includeUserInfoLayout.apply {
                            loadProfileImage(imgUserProfile, item.trip?.driver?.photo)
                            tvUserName.text = checkStringValue(
                                tvUserName.context,
                                item.trip?.driver?.name
                            )
                            tvAvgRating.text = checkFloatValue(
                                item.trip?.driver?.reviewsStats?.avgRating
                            )
                            tvTotalReviews.text = "(${
                                checkLongValue(item.trip?.driver?.reviewsStats?.totalReviews)
                            })"

                            loadImage(imgCarImage, item.trip?.driver?.carDetails?.photo)
                            tvCarModel.text = checkStringValue(
                                tvCarModel.context,
                                item.trip?.driver?.carDetails?.model
                            )
                            tvCarRegistrationNumber.text = checkStringValue(
                                tvCarRegistrationNumber.context,
                                item.trip?.driver?.carDetails?.registrationNumber
                            )
                        }

                        ltNxSeats.isVisible = false
                        includeDivider.divider.isVisible = false
                    }

                    includeTripRouteLayout.apply {
                        tvAddressFrom.text = checkStringValue(
                            tvAddressFrom.context,
                            item.trip?.from_address
                        )
                        formatDateTime(
                            tvDepartureDate.context,
                            tvDepartureDate,
                            item.trip?.departureDate.toString()
                        )
                        tvAddressWhereto.text = checkStringValue(
                            tvAddressWhereto.context,
                            item.trip?.whereTo_address
                        )
                    }

                    tvPricePerPerson.text = checkPriceValue(item.amount)
                    tvPersonLabel.isVisible = false
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DataBindingUtil.inflate<RvBookingsItemBinding>(
            LayoutInflater.from(parent.context), R.layout.rv_bookings_item, parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bindViews(currentItem)

        holder.itemView.setOnClickListener {
            adapterItemClickListener.onAdapterItemClicked(position = position, data = currentItem)
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<BookingData>() {
        override fun areItemsTheSame(
            oldItem: BookingData,
            newItem: BookingData
        ) = oldItem._id == newItem._id

        override fun areContentsTheSame(
            oldItem: BookingData,
            newItem: BookingData
        ) = oldItem == newItem
    }

    private val filter: FilterBookings? = null
    var originalList = ArrayList<BookingData>()
    override fun getFilter(): Filter {
        if (filter == null) return FilterBookings(this, originalList)
        return filter
    }
}