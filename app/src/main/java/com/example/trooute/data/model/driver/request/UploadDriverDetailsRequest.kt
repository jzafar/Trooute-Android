package com.example.trooute.data.model.driver.request

import java.io.File

data class UploadDriverDetailsRequest(
    val make: String?,
    val model: String?,
    val registrationNumber: String?,
    val year: String?,
    val color: String?,
    val carPhoto: File?,
    val driverLicense: File?
)
