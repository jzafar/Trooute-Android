package com.example.trooute.data.model.common

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Ratings(
    val `1`: Float? = null,
    val `2`: Float? = null,
    val `3`: Float? = null,
    val `4`: Float? = null,
    val `5`: Float? = null
): Parcelable