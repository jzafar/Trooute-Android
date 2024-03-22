package com.travel.trooute.presentation.utils

import android.content.Context
import androidx.core.content.ContextCompat
import com.travel.trooute.R
import com.travel.trooute.core.util.Constants.PRICE_SIGN
import com.travel.trooute.core.util.Constants.WEIGHT_SIGN
import com.travel.trooute.data.model.trip.response.LuggageRestrictions
import com.travel.trooute.data.model.trip.response.LuggageType

object ValueChecker {
    fun checkStringValue(context: Context, value: String?): String {
        var stringValue: String = ContextCompat.getString(context, R.string.not_provided)
        value?.also {
            //This code block will run only if someValue is not null
            if (it.trim() != "" || it.isNotBlank() || it.isNotEmpty()) {
                stringValue = it
            }
        }?.run {
            //This code block should run only when if someValue is null, like else condition
            stringValue = if (value.trim() == "" || value.isBlank() || value.isEmpty()) {
                ContextCompat.getString(context, R.string.not_provided)
            } else {
                value
            }
        }
        return stringValue
    }

    inline fun <T> T?.itOrNull(
        ifValue: (T) -> Unit,
        ifNull: () -> Unit
    ): Unit = when (this) {
        null -> ifNull()
        "" -> ifNull()
        else -> ifValue(this)
    }

    fun checkLongValue(value: Long?): String {
        var lng = "0"
        value?.let {
            lng = it.toString()
        } ?: "0"
        return lng
    }

    fun checkLuggageRestrictionValue(value: List<LuggageRestrictions?>, type: LuggageType, context: Context): String {
        for (luggage in value) {
            if (luggage != null) {
                if (luggage.type == type) {
                    if (luggage.weight == null) {
                        return  context.getString(R.string.not_provided)
                    }
                    return  luggage.weight.toString() + WEIGHT_SIGN
                }
            } else {
                return  context.getString(R.string.not_provided)
            }
        }
        return context.getString(R.string.not_provided)
    }

    fun checkFloatValue(value: Float?): String {
        var flt = "0.0"
        value?.let {
            flt =  String.format("%.2f", it)
        }
        return flt.replace(",",".")
    }

    fun checkDoubleValue(value: Double?): String {
        var dbl = "0.0"
        value?.let {
            dbl = it.toString()
        }
        return dbl
    }

    fun checkPriceValue(value: Double?): String {
        var string = "0.0"
        value?.let {
            string = "${PRICE_SIGN}$it".replace("-", "")
        }
        return string
    }

    fun checkNumOfSeatsValue(value: Long?): String {
        var string = "0 x Seats"
        value?.let {
            string = "$it x Seats"
        }
        return string
    }
}