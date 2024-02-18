package com.travel.trooute.presentation.utils

import android.annotation.SuppressLint
import android.content.Context
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import com.travel.trooute.R
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.*

object Utils {
    @SuppressLint("SimpleDateFormat")
    fun formatDateTime(context: Context, view: AppCompatTextView, inputDateTime: String?) {
        view.text = try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
            val date = inputDateTime?.let { inputFormat.parse(it) }

            val outputFormat = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.US)
            outputFormat.format(date as Date)
        } catch (e: Exception) {
            e.printStackTrace()
            ContextCompat.getString(context, R.string.not_provided)
        }
    }

    fun getCurrentTimestamp(): Timestamp {
        return Timestamp.now()
    }

    fun convertTimeStampToDate(timestamp: Timestamp): String {
        val date = timestamp.toDate() // Convert FireStore Timestamp to Date
        val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
        return sdf.format(date)
    }

    fun getSubString(subString: String?): String {
        return subString?.substring(0, 10).toString().uppercase() // Print 1 to 10 characters
    }
}