package com.travel.trooute.data.model.trip.response

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class LuggageRestrictions(
    val type: LuggageType? = null ?: LuggageType.HandCarry,
    val weight: Long? = null
) : Parcelable