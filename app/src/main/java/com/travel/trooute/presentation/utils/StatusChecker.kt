package com.travel.trooute.presentation.utils

import android.annotation.SuppressLint
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.databinding.adapters.TextViewBindingAdapter
import com.travel.trooute.R

object StatusChecker {
    @SuppressLint("RestrictedApi")
    fun checkStatus(isDriverApproved: Boolean, tvStatus: AppCompatTextView, status: String?) {
        val context = tvStatus.context
        when (status.toString()) {
            context.getString(R.string.waiting) -> {
                TextViewBindingAdapter.setDrawableStart(
                    tvStatus,
                    ContextCompat.getDrawable(context, R.drawable.ic_status_waiting)
                )

                if (isDriverApproved) {
                    tvStatus.text = ContextCompat.getString(context, R.string.waiting)
                } else {
                    tvStatus.text = ContextCompat.getString(context, R.string.waiting)
                }
            }

            context.getString(R.string.canceled) -> {
                TextViewBindingAdapter.setDrawableStart(
                    tvStatus,
                    ContextCompat.getDrawable(context, R.drawable.ic_status_cancelled)
                )

                if (isDriverApproved) {
                    tvStatus.text = ContextCompat.getString(context, R.string.cancelled)
                } else {
                    tvStatus.text = ContextCompat.getString(context, R.string.cancelled)
                }
            }

            context.getString(R.string.approved) -> {
                TextViewBindingAdapter.setDrawableStart(
                    tvStatus,
                    ContextCompat.getDrawable(context, R.drawable.ic_approved_check)
                )

                if (isDriverApproved) {
                    tvStatus.text = ContextCompat.getString(context, R.string.waiting_for_payment)
                } else {
                    tvStatus.text = ContextCompat.getString(context, R.string.approved)
                }
            }

            context.getString(R.string.confirmed) -> {
                TextViewBindingAdapter.setDrawableStart(
                    tvStatus,
                    ContextCompat.getDrawable(context, R.drawable.ic_confirm_check)
                )

                tvStatus.text = ContextCompat.getString(context, R.string.confirmed)
            }

            context.getString(R.string.completed) -> {
                TextViewBindingAdapter.setDrawableStart(
                    tvStatus,
                    ContextCompat.getDrawable(context, R.drawable.ic_confirm_check)
                )

                tvStatus.text = ContextCompat.getString(context, R.string.completed)
            }
        }
    }
    @SuppressLint("RestrictedApi")
    fun checkPickupStatus(isDriverApproved: Boolean, tvStatus: AppCompatTextView, passengerStatus: String?, driverStatus: String?) {
        val context = tvStatus.context
        if (isDriverApproved) {
            when (passengerStatus.toString()) {
                "WaitingToBePickedup" -> {
                    TextViewBindingAdapter.setDrawableStart(
                        tvStatus,
                        ContextCompat.getDrawable(context, R.drawable.ic_status_waiting)
                    )
                    tvStatus.text =
                        ContextCompat.getString(context, R.string.waiting_to_be_Picked_up)
                }

                "Pickedup" -> {
                    TextViewBindingAdapter.setDrawableStart(
                        tvStatus,
                        ContextCompat.getDrawable(context, R.drawable.ic_confirm_check)
                    )

                    tvStatus.text = ContextCompat.getString(context, R.string.pickup_marked)
                }

                "NotPickedup" -> {
                    TextViewBindingAdapter.setDrawableStart(
                        tvStatus,
                        ContextCompat.getDrawable(context, R.drawable.ic_status_cancelled)
                    )
                    tvStatus.text = ContextCompat.getString(context, R.string.not_Picked_up)
                }

            }
        } else {
            when (driverStatus.toString()) {
                "Pickedup" -> {
                    TextViewBindingAdapter.setDrawableStart(
                        tvStatus,
                        ContextCompat.getDrawable(context, R.drawable.ic_confirm_check)
                    )

                    tvStatus.text = ContextCompat.getString(context, R.string.pickup_marked)
                }

                "GoingToPickup" -> {
                    TextViewBindingAdapter.setDrawableStart(
                        tvStatus,
                        ContextCompat.getDrawable(context, R.drawable.ic_status_waiting)
                    )
                    tvStatus.text =
                        ContextCompat.getString(context, R.string.waiting_to_be_Picked_up)
                }

                "PassengerNotShowedup" -> {
                    TextViewBindingAdapter.setDrawableStart(
                        tvStatus,
                        ContextCompat.getDrawable(context, R.drawable.ic_status_cancelled)
                    )
                    tvStatus.text = ContextCompat.getString(context, R.string.passenger_not_showed)
                }
            }
        }
    }
}