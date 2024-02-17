package com.example.trooute.data.model.trip.response

import android.os.Parcelable
import com.example.trooute.data.model.common.PickupLocation
import com.example.trooute.data.model.common.User
import kotlinx.parcelize.Parcelize

@Parcelize
data class Booking(
    val __v: Long? = null,
    val _id: String? = null ?: "",
    val amount: Double? = null ?: 0.0,
    val note: String? = null ?: "",
    var driverId: String? = null ?: "",
    val numberOfSeats: Long? = null ?: 0,
    var pricePerPerson: Double? = null ?: 0.0,
    val pickupLocation: PickupLocation? = null,
    val reviewsGivenToCar: ReviewsGiven? = null,
    val reviewsGivenToDriver: ReviewsGiven? = null,
    var reviewsGivenToUser: ReviewsGiven? = null,
    var reviewsGivenToUsersByUser: List<ReviewsGiven?> = emptyList(),
    val status: String? = null ?: "",
    val createdAt:String? = null?: "",
    val updatedAt:String? = null?: "",
    val trip: String? = null ?: "",
    val tripData: Trip? = null,
    val user: User? = null,
    val pickupStatus: PickupStatus? = null
) : Parcelable