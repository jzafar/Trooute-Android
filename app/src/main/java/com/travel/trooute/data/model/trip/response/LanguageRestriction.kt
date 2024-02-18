package com.travel.trooute.data.model.trip.response

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class LanguageRestriction(
    val text: String? = null ?: "",
    val weight: Long? = null
) : Parcelable