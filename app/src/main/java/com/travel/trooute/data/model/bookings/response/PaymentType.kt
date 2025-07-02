package com.travel.trooute.data.model.bookings.response

import android.os.Parcelable
import com.travel.trooute.R
import kotlinx.parcelize.Parcelize
import android.content.Context
import androidx.annotation.StringRes

@Parcelize
enum class PaymentType(val value: String) : Parcelable {
    CASH("cash"),
    PAYPAL("paypal"),
    STRIPE("stripe");

    val id: String
        get() = value

    private val localizedDriverString: Int
        get() = when (this) {
            CASH -> R.string.cash_payments
            PAYPAL -> R.string.paypal_payments
            STRIPE -> R.string.stripe_payments
        }


    private val localizedPassengersStringRes: Int
        get() = when (this) {
            CASH -> R.string.pay_with_cash
            PAYPAL -> R.string.pay_with_paypal
            STRIPE -> R.string.pay_with_card
        }

    fun getLocalizedPassengersString(context: Context): String {
        return context.getString(localizedPassengersStringRes)
    }

    fun getLocalizedDriverString(context: Context): String {
        return context.getString(localizedDriverString)
    }

    companion object {
        val allCases: List<PaymentType> = values().toList()
    }
}