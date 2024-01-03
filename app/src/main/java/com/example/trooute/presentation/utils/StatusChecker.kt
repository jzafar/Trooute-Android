package com.example.trooute.presentation.utils

import android.annotation.SuppressLint
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.databinding.adapters.TextViewBindingAdapter
import com.example.trooute.R

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
                    tvStatus.text = ContextCompat.getString(context, R.string.waiting_for_approval)
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
}