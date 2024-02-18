package com.travel.trooute.data.model.auth.request

data class LoginRequest(
    val email: String? = null,
    val password: String? = null
)