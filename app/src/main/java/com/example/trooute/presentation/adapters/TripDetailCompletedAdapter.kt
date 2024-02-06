package com.example.trooute.presentation.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getString
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.databinding.adapters.TextViewBindingAdapter.setDrawableEnd
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.trooute.R
import com.example.trooute.core.util.Constants
import com.example.trooute.core.util.SharedPreferenceManager
import com.example.trooute.data.model.trip.response.Booking
import com.example.trooute.databinding.RvTripDetailCompletedItemBinding
import com.example.trooute.presentation.utils.Utils.formatDateTime
import com.example.trooute.presentation.utils.Utils.getSubString
import com.example.trooute.presentation.utils.ValueChecker.checkFloatValue
import com.example.trooute.presentation.utils.ValueChecker.checkLongValue
import com.example.trooute.presentation.utils.ValueChecker.checkNumOfSeatsValue
import com.example.trooute.presentation.utils.ValueChecker.checkPriceValue
import com.example.trooute.presentation.utils.ValueChecker.checkStringValue
import com.example.trooute.presentation.utils.isFieldValid
import com.example.trooute.presentation.utils.loadProfileImage

class TripDetailCompletedAdapter(
    private val submitReviewClicked: (
        position: Int,
        targetId: String,
        targetType: String,
        comment: String,
        rating: Float,
        trip: String
    ) -> Unit,
    private val sharedPreferenceManager: SharedPreferenceManager,
    private val seeReviews:(targetId: String) -> Unit
) : ListAdapter<Booking, TripDetailCompletedAdapter.ViewHolder>(DiffCallback()) {

    inner class ViewHolder(private val binding: RvTripDetailCompletedItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n", "RestrictedApi")
        fun bindViews(currentItem: Booking?) {
            binding.apply {
                currentItem?.let { booking ->
                    tvBookingId.text = "Booking # ${
                        checkStringValue(
                            tvBookingId.context,
                            getSubString(booking._id)
                        )
                    }"
                    formatDateTime(
                        tvBookingDate.context,
                        tvBookingDate,
                        booking.createdAt
                    )


                    // Passenger detail
                    includeUserDetail.apply {

                        loadProfileImage(imgUserProfile, booking.user?.photo)
                        tvUserName.text = checkStringValue(
                            tvUserName.context,
                            booking.user?.name
                        )

                        var genderStr = checkStringValue(
                            gender.context, booking.user?.gender
                        )

                        if (genderStr.equals(gender.context.getString(R.string.not_provided))){
                            gender.isVisible = false
                        }
                        else {
                            gender.text = genderStr
                        }

                        tvAvgRating.text = checkFloatValue(booking.user?.reviewsStats?.avgRating)
                        tvTotalReviews.text = "(${
                            checkLongValue(booking.user?.reviewsStats?.totalReviews)
                        })"
                        ltCallInboxSection.isVisible = false
                        includeUserDetail.root.setOnClickListener{

                            booking.user?._id?.let { it1 -> seeReviews(it1) }
                        }
                    }

                    // hide give review section for himself
                    val currentUser = sharedPreferenceManager.getAuthIdFromPref()
                    if (booking.user?._id == currentUser){
                        includeUserDetailDevider.root.isVisible = false
                        includeUserDetailReviewSection.isVisible = false
                    } else {
                        // Expending review portion

                        includeReviewItem.apply {
                            if(sharedPreferenceManager.driverMode()) {
                                tvExperienceWithDriverTitle.text =  "Experience with passenger"
                            } else {
                                tvExperienceWithDriverTitle.text =  tvExperienceWithDriverTitle.context.getString(R.string.experience_with_driver)
                            }
                            tvReviewsTitle.setOnClickListener {
                                ltReviewsItem.apply {
                                    if (isVisible) {
                                        isVisible = false
                                        setDrawableEnd(
                                            tvReviewsTitle,
                                            ContextCompat.getDrawable(
                                                tvReviewsTitle.context,
                                                R.drawable.ic_arrow_down
                                            )
                                        )
                                    } else {
                                        isVisible = true
                                        setDrawableEnd(
                                            tvReviewsTitle,
                                            ContextCompat.getDrawable(
                                                tvReviewsTitle.context,
                                                R.drawable.ic_arrow_up
                                            )
                                        )
                                    }
                                }
                            }

                            // Review given to driver from user
                            booking.reviewsGivenToDriver?.let {
                                ltUserReviews.isVisible = true
                                includeDivider.divider.isVisible = true

                                tvUserName.text = checkStringValue(
                                    tvUserName.context,
                                    booking.user?.name
                                )
                                tvComment.text = checkStringValue(
                                    tvComment.context,
                                    booking.reviewsGivenToDriver?.comment
                                )
                                rbExperienceWithDriver.rating = checkFloatValue(
                                    booking.reviewsGivenToDriver?.rating
                                ).toFloat()
                            }

                            // Review given to user from driver
                            booking.reviewsGivenToUser?.let {
                                ltWriteReviews.isVisible = false
                                ltDriverReview.isVisible = true

                                tvDriverComment.text = checkStringValue(
                                    tvDriverComment.context,
                                    booking.reviewsGivenToUser?.comment
                                )
                                rbExperience.rating = checkFloatValue(
                                    booking.reviewsGivenToUser?.rating
                                ).toFloat()
                            } ?: run {
                                ltWriteReviews.isVisible = true
                                ltDriverReview.isVisible = false

                                var submitReviewRatingValue = rbSubmitExperienceWithDriver.rating

                                rbSubmitExperienceWithDriver.setOnRatingBarChangeListener { ratingBar, rating, fromUser ->
                                    submitReviewRatingValue = rating
                                }

                                btnSubmitReview.setOnClickListener {
                                    if (
                                        shareYourThoughts.context.isFieldValid(
                                            shareYourThoughts,
                                            "Required"
                                        )
                                    ) {
                                        // Handling on client side
                                        val comment = shareYourThoughts.text.toString()
                                        commentState(binding, comment, submitReviewRatingValue)

                                        var target = "User"
                                        if (booking.user?._id == booking.driverId){
                                            target = "Driver"
                                        }
                                        // Handling on server side
                                        submitReviewClicked(
                                            bindingAdapterPosition,
                                            booking.user?._id.toString(),
                                            target,
                                            shareYourThoughts.text.toString(),
                                            submitReviewRatingValue,
                                            booking.trip.toString()
                                        )
                                    }
                                }
                            }
                        }
                    }


                    val platFormFee = Constants.PLATFORM_FEE_PRICE * booking.numberOfSeats!!
                    val pricePerSeat = (booking?.pricePerPerson ?: 1.0) * booking.numberOfSeats!!
                    tvNxSeats.text = checkNumOfSeatsValue(booking.numberOfSeats)
                    tvNxSeatsPrice.text = checkPriceValue(pricePerSeat)
                    if (sharedPreferenceManager.driverMode()) {
                        tvTotalPrice.text = checkPriceValue(pricePerSeat - platFormFee)
                    } else {
                        // hide price section of other passengers
//                        if (booking.user?._id != currentUser) {
//                            ltPriceSection.isVisible = false
//                        }
                        tvTotalPrice.text = checkPriceValue(pricePerSeat + platFormFee)
                    }

                }
            }
        }

    }

    private fun commentState(
        binding: RvTripDetailCompletedItemBinding,
        comment: String,
        submitReviewRatingValue: Float
    ) {
        binding.apply {
            includeReviewItem.apply {
                ltWriteReviews.isVisible = false
                ltDriverReview.isVisible = true
                tvDriverComment.text = checkStringValue(
                    tvDriverComment.context,
                    comment
                )
                rbExperience.rating = checkFloatValue(
                    submitReviewRatingValue
                ).toFloat()
            }
        }
    }

    class DiffCallback :
        DiffUtil.ItemCallback<Booking>() {
        override fun areItemsTheSame(
            oldItem: Booking,
            newItem: Booking
        ) = oldItem._id == newItem._id

        override fun areContentsTheSame(
            oldItem: Booking,
            newItem: Booking
        ) = oldItem == newItem
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DataBindingUtil.inflate<RvTripDetailCompletedItemBinding>(
            LayoutInflater.from(parent.context),
            R.layout.rv_trip_detail_completed_item,
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bindViews(currentItem)
    }
}