package com.travel.trooute.data.model.Enums

enum class PickUpPassengersStatus {
    // DriverSide Statues
    NotStarted,
    PickupStarted,
    PassengerNotified,
    PassengerPickedup,
    PassengerNotShowedup,

    // PassengersSide Statues
    NotSetYet,
    DriverPickedup,
    DriverNotShowedup
}
