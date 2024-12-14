package com.travel.trooute.data.model.auth.request

data class LogoutRequest(
    val deviceType: String = "android",
    val deviceId: String = ""
)