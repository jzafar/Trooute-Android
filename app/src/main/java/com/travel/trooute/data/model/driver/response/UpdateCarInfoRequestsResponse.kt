package com.travel.trooute.data.model.driver.response

import android.os.Parcelable
import com.travel.trooute.data.model.common.CarDetails
import kotlinx.parcelize.Parcelize

@Parcelize
data class UpdateCarInfoRequestsResponse(
    val `data`: CarDetails? = null,
    val message: String? = null ?: "",
    val success: Boolean
) : Parcelable