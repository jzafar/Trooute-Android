package com.travel.trooute.presentation.utils

import android.annotation.SuppressLint
import android.content.Context
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import com.travel.trooute.R
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

object Utils {
    @SuppressLint("SimpleDateFormat")
    fun formatDateTime(context: Context, view: AppCompatTextView, inputDateTime: String?) {
        view.text = try {
//            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",)
//            inputFormat.timeZone = TimeZone.getTimeZone("UTC")
//            val date = inputDateTime?.let { inputFormat.parse(it) }
            val instant = Instant.parse(inputDateTime)
            val outputFormat = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a")
                                .withZone(ZoneId.of(getDeviceTimeZoneId()))
            outputFormat.format(instant)
        } catch (e: Exception) {
            e.printStackTrace()
            ContextCompat.getString(context, R.string.not_provided)
        }
    }

    private fun getDeviceTimeZoneId(): String {
        val zoneId = ZoneId.systemDefault()
        return zoneId.id // e.g., "America/New_York"
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

    fun combineDateAndTime(dateString: String, timeString: String): String? {
        try {
            // Define date and time formatters
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault()) // "11:11 PM" format

            // Parse the date and time strings
            val date = dateFormat.parse(dateString)
            val time = timeFormat.parse(timeString)

            // Combine date and time into a Calendar
            val calendar = Calendar.getInstance()
            if (date != null) {
                calendar.time = date
            }
            val timeCalendar = Calendar.getInstance()
            if (time != null) {
                timeCalendar.time = time
            }
            calendar.set(Calendar.HOUR_OF_DAY, timeCalendar.get(Calendar.HOUR_OF_DAY))
            calendar.set(Calendar.MINUTE, timeCalendar.get(Calendar.MINUTE))
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)

            // Format as UTC date-time string
            val outputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
            outputFormat.timeZone = TimeZone.getTimeZone("UTC") // Set to UTC

            return outputFormat.format(calendar.time)
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }
}