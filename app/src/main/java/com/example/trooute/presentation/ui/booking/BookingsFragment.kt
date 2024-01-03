package com.example.trooute.presentation.ui.booking

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.trooute.R
import com.example.trooute.core.util.Constants.BOOKING_ID
import com.example.trooute.core.util.Constants.MESSAGE_USER_INFO
import com.example.trooute.core.util.Resource
import com.example.trooute.core.util.SharedPreferenceManager
import com.example.trooute.data.model.bookings.response.BookingData
import com.example.trooute.data.model.chat.Users
import com.example.trooute.data.model.common.Driver
import com.example.trooute.data.model.common.User
import com.example.trooute.databinding.FragmentBookingsBinding
import com.example.trooute.presentation.adapters.BookingsAdapter
import com.example.trooute.presentation.interfaces.AdapterItemClickListener
import com.example.trooute.presentation.ui.chat.MessageActivity
import com.example.trooute.presentation.utils.Loader
import com.example.trooute.presentation.utils.setRVVertical
import com.example.trooute.presentation.viewmodel.bookingviewmodel.GetBookingViewModel
import com.faltenreich.skeletonlayout.Skeleton
import com.faltenreich.skeletonlayout.applySkeleton
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class BookingsFragment : Fragment(), AdapterItemClickListener {

    private val TAG = "BookingsFragment"

    private lateinit var binding: FragmentBookingsBinding
    private lateinit var skeleton: Skeleton
    private lateinit var bookingsAdapter: BookingsAdapter

    private val getBookingViewModel: GetBookingViewModel by viewModels()

    @Inject
    lateinit var loader: Loader

    @Inject
    lateinit var sharedPreferenceManager: SharedPreferenceManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_bookings, container, false)
        bookingsAdapter = BookingsAdapter(
            this@BookingsFragment,
            sharedPreferenceManager,
            ::startMessaging
        )

        binding.apply {
            includeSearchView.searchingText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {

                }

                override fun onTextChanged(
                    string: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int
                ) {
                    val query = string.toString().trim()
                    // Pass to adapter
                    bookingsAdapter.filter.filter(query)
                }

                override fun afterTextChanged(s: Editable?) {

                }
            })

            rvBooking.apply {
                setRVVertical()
                adapter = bookingsAdapter
                skeleton = this.applySkeleton(R.layout.rv_bookings_item)
                skeleton.showSkeleton()
            }
        }
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        getBookingViewModel.getBooking()
        callGetBookingApi()
    }

    @SuppressLint("RepeatOnLifecycleWrongUsage")
    private fun callGetBookingApi() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                getBookingViewModel.getBookingState.collect {
                    when (it) {
                        is Resource.ERROR -> {
                            skeleton.showOriginal()
                            binding.tvBookingNotAvailable.isVisible = true
                            binding.rvBooking.isVisible = false
                            Log.e(TAG, "callGetBookingApi: Error -> " + it.message.toString())
                        }

                        Resource.LOADING -> {

                        }

                        is Resource.SUCCESS -> {
                            Log.e(TAG, "callGetBookingApi: success -> " + it.data)
                            if (it.data.data.isNullOrEmpty() || it.data.data.isEmpty()) {
                                binding.tvBookingNotAvailable.isVisible = true
                                binding.rvBooking.isVisible = false
                            } else {
                                binding.tvBookingNotAvailable.isVisible = false
                                binding.rvBooking.isVisible = true
                                if (::bookingsAdapter.isInitialized) {
                                    bookingsAdapter.submitList(it.data.data.reversed())
                                }
                                bookingsAdapter.originalList =
                                    it.data.data as ArrayList<BookingData>
                            }

                            skeleton.showOriginal()
                        }
                    }
                }
            }
        }
    }

    override fun onAdapterItemClicked(position: Int, data: Any) {
        if (data is BookingData) {
            startActivity(
                Intent(requireContext(), BookingDetailActivity::class.java).apply {
                    putExtra(BOOKING_ID, data._id)
                }
            )
        }
    }

    private fun startMessaging(user: User?) {
        user?.let {
            startActivity(
                Intent(requireContext(), MessageActivity::class.java).apply {
                    putExtra(
                        MESSAGE_USER_INFO, Users(
                            _id = it._id,
                            name = it.name,
                            photo = it.photo
                        )
                    )
                }
            )
        }
    }
}