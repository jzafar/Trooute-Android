package com.travel.trooute.presentation.ui.trip

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.travel.trooute.R
import com.travel.trooute.core.util.Constants.TRIP_ID
import com.travel.trooute.core.util.Resource
import com.travel.trooute.core.util.SharedPreferenceManager
import com.travel.trooute.data.model.trip.response.TripsData
import com.travel.trooute.databinding.ActivityTripsHistoryBinding
import com.travel.trooute.presentation.adapters.TripsHistoryAdapter
import com.travel.trooute.presentation.interfaces.AdapterItemClickListener
import com.travel.trooute.presentation.utils.WindowsManager.statusBarColor
import com.travel.trooute.presentation.utils.setRVVertical
import com.travel.trooute.presentation.utils.showErrorMessage
import com.travel.trooute.presentation.viewmodel.tripviewmodel.DriverTripsHistoryViewModel
import com.faltenreich.skeletonlayout.Skeleton
import com.faltenreich.skeletonlayout.applySkeleton
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class TripsHistoryActivity : AppCompatActivity(), AdapterItemClickListener {

    private val TAG = "TripsHistory"

    private lateinit var binding: ActivityTripsHistoryBinding
    private lateinit var skeleton: Skeleton
    private lateinit var tripsHistoryAdapter: TripsHistoryAdapter
    private var authModelInfo: com.travel.trooute.data.model.auth.response.User? = null
    @Inject
    lateinit var sharedPreferenceManager: SharedPreferenceManager
    private val driverTripsHistoryViewModel: DriverTripsHistoryViewModel by viewModels()

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        statusBarColor(R.color.white)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_trips_history)
        authModelInfo = sharedPreferenceManager.getAuthModelFromPref()
        binding.apply {
            includeAppBar.apply {
                this.toolbarTitle.text = getString(R.string.trip_history)
                this.filter.isVisible = false

                this.arrowBackPress.setOnClickListener {
                    finish()
                }
            }

            rvTripHistory.apply {
                setRVVertical()
                tripsHistoryAdapter = TripsHistoryAdapter(this@TripsHistoryActivity, sharedPreferenceManager)
                adapter = tripsHistoryAdapter
                skeleton = this.applySkeleton(R.layout.rv_trip_detail_completed_item)
                skeleton.showSkeleton()
            }

            driverTripsHistoryViewModel.tripsHistory()
            bindDriverTripsHistoryObserver()
        }
    }

    private fun bindDriverTripsHistoryObserver() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                driverTripsHistoryViewModel.driverTripsHistoryState.collect {
                    when (it) {
                        is Resource.ERROR -> {
                            Toast(this@TripsHistoryActivity).showErrorMessage(
                                this@TripsHistoryActivity,
                                it.message.toString()
                            )
                            skeleton.showOriginal()
                        }

                        Resource.LOADING -> {

                        }

                        is Resource.SUCCESS -> {
                            if (it.data.data?.isEmpty() == true || it.data.data == null) {
                                binding.rvTripHistory.isVisible = false
                                binding.tvNoTripsHistoryAvailable.isVisible = true
                            } else {
                                binding.rvTripHistory.isVisible = true
                                binding.tvNoTripsHistoryAvailable.isVisible = false
                                tripsHistoryAdapter.submitList(it.data.data.reversed())
                            }
                            skeleton.showOriginal()
                        }
                    }
                }
            }
        }
    }

    override fun onAdapterItemClicked(position: Int, data: Any) {
        if (data is TripsData) {
            startActivity(
                Intent(this, TripDetailCompletedActivity::class.java).apply {
                    putExtra(TRIP_ID, data._id)
                }
            )
        }
    }
}