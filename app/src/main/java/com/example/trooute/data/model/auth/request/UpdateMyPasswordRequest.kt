package com.example.trooute.data.model.auth.request

data class UpdateMyPasswordRequest(
    val password: String,
    val passwordConfirm: String,
    val passwordCurrent: String
)