package com.example.trooute.data.model.auth.request

import java.io.File

data class SignupRequest(
    val photo: File?,
    val email: String,
    val name: String,
    val password: String,
    val phoneNumber: String
)