package com.example.trooute.data.model.bookings.response

import android.os.Parcelable
import com.example.trooute.data.model.common.PickupLocation
import com.example.trooute.data.model.common.User
import kotlinx.parcelize.Parcelize

@Parcelize
data class BookingData(
    val __v: Long? = null ?: 0,
    val _id: String? = null ?: "0",
    val amount: Double? = null ?: 0.0,
    val note: String? = null ?: "No note",
    val numberOfSeats: Long? = null ?: 0,
    val pickupLocation: PickupLocation? = null,
    val status: String? = null ?: "No status",
    val trip: Trip? = null,
    val user: User? = null,

    val driverToUserReview: DriverToUserReview? = null,
    val userToCarReview: UserToCarReview? = null,
    val userToDriverReview: UserToDriverReview? = null
) : Parcelable