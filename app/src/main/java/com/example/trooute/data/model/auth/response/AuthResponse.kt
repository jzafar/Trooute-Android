package com.example.trooute.data.model.auth.response

data class AuthResponse(
    val data: User? = null,
    val message: String? = null,
    val success: Boolean = false,
    val token: String? = null
)