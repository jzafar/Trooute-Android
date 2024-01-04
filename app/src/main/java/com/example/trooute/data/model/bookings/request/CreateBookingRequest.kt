package com.example.trooute.data.model.bookings.request

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CreateBookingRequest(
    val amount: Double?,
    val note: String?,
    val numberOfSeats: Long?,
    val pickupLocation: PickupLocation?,
    val trip: String?,
    val plateFormFee: Double = 1.0
) : Parcelable {
    @Parcelize
    data class PickupLocation(
        val address: String?,
        val location: List<Double?>
    ) : Parcelable
}