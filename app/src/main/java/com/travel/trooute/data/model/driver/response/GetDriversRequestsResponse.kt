package com.travel.trooute.data.model.driver.response

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class GetDriversRequestsResponse(
    val `data`: List<Driver>? = null,
    val message: String? = null ?: "",
    val success: Boolean
) : Parcelable