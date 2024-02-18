package com.travel.trooute.data.model.common

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class BaseResponse(
    val message: String? = null ?: "",
    val success: Boolean,
    val url: String? = null ?: ""
) : Parcelable