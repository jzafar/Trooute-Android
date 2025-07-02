package com.travel.trooute.presentation.utils

import android.annotation.SuppressLint
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.databinding.adapters.TextViewBindingAdapter
import com.travel.trooute.R
import com.travel.trooute.data.model.Enums.PickUpPassengersStatus

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

            context.getString(R.string.pending_driver_payment) -> {
                TextViewBindingAdapter.setDrawableStart(
                    tvStatus,
                    ContextCompat.getDrawable(context, R.drawable.ic_approved_check)
                )
                if (isDriverApproved) {
                    tvStatus.text = ContextCompat.getString(context, R.string.pending_driver_side_payment_status)
                } else {
                    tvStatus.text = ContextCompat.getString(context, R.string.pending_driver_payment_status)
                }

            }
        }
    }
    @SuppressLint("RestrictedApi")
    fun checkPickupStatus(isDriverApproved: Boolean, tvStatus: AppCompatTextView, tvPickupStatusDetails: AppCompatTextView, passengerStatus: String?, driverStatus: String?) {
        val context = tvStatus.context
        when (passengerStatus.toString()) {
            PickUpPassengersStatus.NotSetYet.toString() -> {
                tvStatus.text =
                    ContextCompat.getString(context, R.string.waiting_to_be_Picked_up)
                TextViewBindingAdapter.setDrawableStart(
                    tvStatus,
                    ContextCompat.getDrawable(context, R.drawable.ic_status_waiting)
                )
            }

            PickUpPassengersStatus.DriverPickedup.toString() -> {
                TextViewBindingAdapter.setDrawableStart(
                    tvStatus,
                    ContextCompat.getDrawable(context, R.drawable.ic_confirm_check)
                )

                tvStatus.text = ContextCompat.getString(context, R.string.pickedup)
                tvPickupStatusDetails.text = ContextCompat.getString(context, R.string.pick_up_driver_details)
            }

            PickUpPassengersStatus.DriverNotShowedup.toString() -> {
                TextViewBindingAdapter.setDrawableStart(
                    tvStatus,
                    ContextCompat.getDrawable(context, R.drawable.ic_status_cancelled)
                )
                tvStatus.text = ContextCompat.getString(context, R.string.not_showed_up)
                tvPickupStatusDetails.text = ContextCompat.getString(context, R.string.not_showed_up_by_passenger_details)
            }

        }
    }
}