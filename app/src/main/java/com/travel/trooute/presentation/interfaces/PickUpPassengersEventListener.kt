package com.travel.trooute.presentation.interfaces

import com.travel.trooute.data.model.Enums.PickUpPassengersStatus
import com.travel.trooute.data.model.trip.response.Booking

interface PickUpPassengersEventListener {
    fun onMapButtonClick(data: Booking)
    fun onUpdateStatusButtonClick(data: Booking, status: PickUpPassengersStatus)
}