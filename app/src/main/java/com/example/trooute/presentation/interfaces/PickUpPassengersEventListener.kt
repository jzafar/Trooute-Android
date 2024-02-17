package com.example.trooute.presentation.interfaces

import com.example.trooute.data.model.Enums.PickUpPassengersStatus
import com.example.trooute.data.model.trip.response.Booking

interface PickUpPassengersEventListener {
    fun onMapButtonClick(data: Booking)
    fun onUpdateStatusButtonClick(data: Booking, status: PickUpPassengersStatus)
}