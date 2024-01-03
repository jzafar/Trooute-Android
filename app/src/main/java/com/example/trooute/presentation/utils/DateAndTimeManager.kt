package com.example.trooute.presentation.utils

import android.util.Log
import android.widget.CalendarView
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.FragmentActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class DateAndTimeManager(private val fragmentActivity: FragmentActivity) {
    fun initializeCalendar(calendarView: CalendarView, callBack: (String) -> Unit) {
        val calendar = Calendar.getInstance()
        val currentYear = calendar.get(Calendar.YEAR)
        val currentMonth = calendar.get(Calendar.MONTH)
        val currentDay = calendar.get(Calendar.DAY_OF_MONTH)

        callBack(formatDate(currentYear, currentMonth, currentDay))

        calendarView.setOnDateChangeListener { view, year, month, dayOfMonth ->
            callBack(formatDate(year, month, dayOfMonth))
        }
    }

    private fun formatDate(year: Int, month: Int, day: Int): String {
        val calendar = Calendar.getInstance()
        calendar.set(year, month, day)
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }

    fun initializeTimePicker(
        view: AppCompatTextView,
        field: TextInputEditText,
        callBack: (String) -> Unit
    ): String {
        var pickedHour: Int
        var pickedMinute: Int

        // Get the current time
        val calendar = Calendar.getInstance()
        val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
        val currentMinute = calendar.get(Calendar.MINUTE)

        // Initialize the TextView with the current time
        var time = formatTime(currentHour, currentMinute)
        view.text = time
        field.setText(time)
        callBack(formatTime(currentHour, currentMinute))

        // instance of MDC time picker
        val materialTimePicker: MaterialTimePicker = MaterialTimePicker.Builder()
            // set the title for the alert dialog
            .setTitleText("SELECT YOUR TIMING")
            // set the default hour for the
            // dialog when the dialog opens
            .setHour(currentHour)
            // set the default minute for the
            // dialog when the dialog opens
            .setMinute(currentMinute)
            // Set theme
//                .setTheme(R.style.TimePickerTheme)
            // set the time format
            // according to the region
            .setTimeFormat(TimeFormat.CLOCK_12H)
            .build()

        // Set an OnClickListener on the TextView to show the TimePicker dialog
        view.setOnClickListener {
            materialTimePicker.show(fragmentActivity.supportFragmentManager, "MainActivity")
        }

        // on clicking the positive button of the time picker
        // dialog update the TextView accordingly
        materialTimePicker.addOnPositiveButtonClickListener {
            pickedHour = materialTimePicker.hour
            pickedMinute = materialTimePicker.minute
            time = formatTime(hour = pickedHour, minute = pickedMinute)
            view.text = time
            field.setText(time)
            callBack(formatTime(hour = pickedHour, minute = pickedMinute))
        }

        return time
    }

    private fun formatTime(
        hour: Int,
        minute: Int
    ): String {
        // Format the time as desired (e.g., "03:00 AM")
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, minute)

        val dateFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }
}