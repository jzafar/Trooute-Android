package com.travel.trooute.data.model.auth.request

data class UpdateDeviceIdRequest(
    val deviceType: String,
    val deviceId: String,
)