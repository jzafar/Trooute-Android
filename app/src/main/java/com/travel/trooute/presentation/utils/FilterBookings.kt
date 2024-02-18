package com.travel.trooute.presentation.utils

import android.widget.Filter
import com.travel.trooute.data.model.bookings.response.BookingData
import com.travel.trooute.presentation.adapters.BookingsAdapter
import java.util.Locale

class FilterBookings(
    private val adapter: BookingsAdapter,
    private val bookings: ArrayList<BookingData>
) : Filter() {
    override fun performFiltering(searchingText: CharSequence?): FilterResults {
        val filterResults = FilterResults()

        if (searchingText != null) {
            // Filtering
            val query = searchingText.toString().trim().lowercase(Locale.getDefault()).split(" ")
            val filteredBookingList = ArrayList<BookingData>()
            for (booking in bookings) {
                if (query.any { search ->
                        booking._id?.lowercase(Locale.getDefault())?.contains(search) == true || booking.amount.toString().lowercase(Locale.getDefault())
                            .contains(search) || booking.note?.lowercase(Locale.getDefault())
                            ?.contains(search) == true
                    }){
                    filteredBookingList.add(booking)
                }
            }

            filterResults.apply {
                count = filteredBookingList.size
                values = filteredBookingList
            }
        } else {
            // Set original list
            filterResults.apply {
                count = bookings.size
                values = bookings
            }
        }

        return filterResults
    }

    override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
        // Passing data to adapter
        adapter.submitList(results?.values as ArrayList<BookingData>)
    }
}