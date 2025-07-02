package com.travel.trooute.data.model.bookings.request

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ConfirmBookingsRequest(
    val  paymentMethod: String
): Parcelable
