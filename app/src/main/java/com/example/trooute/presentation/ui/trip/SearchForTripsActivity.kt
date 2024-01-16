package com.example.trooute.presentation.ui.trip

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import com.example.trooute.R
import com.example.trooute.core.util.Constants
import com.example.trooute.core.util.SharedPreferenceManager
import com.example.trooute.data.model.trip.response.TripsData
import com.example.trooute.databinding.ActivitySearchForTripsBinding
import com.example.trooute.presentation.adapters.TripsAdapter
import com.example.trooute.presentation.interfaces.AdapterItemClickListener
import com.example.trooute.core.util.Constants.TRIP_ID
import com.example.trooute.presentation.utils.WindowsManager.statusBarColor
import com.example.trooute.presentation.utils.setRVVertical
import com.faltenreich.skeletonlayout.Skeleton
import com.faltenreich.skeletonlayout.applySkeleton
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@Suppress("INFERRED_TYPE_VARIABLE_INTO_POSSIBLE_EMPTY_INTERSECTION")
@AndroidEntryPoint
class SearchForTripsActivity : AppCompatActivity(), AdapterItemClickListener {

    private val TAG = "SearchForTrips"

    private lateinit var binding: ActivitySearchForTripsBinding
    private lateinit var tripsAdapter: TripsAdapter
    private lateinit var skeleton: Skeleton
    private lateinit var rvSkeleton: Skeleton

    private var tripsData: List<TripsData> = emptyList()


    @Inject
    lateinit var sharedPreferenceManager: SharedPreferenceManager

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        statusBarColor(R.color.white)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_search_for_trips)
        tripsAdapter = TripsAdapter(
            sharedPreferenceManager = sharedPreferenceManager, adapterItemClickListener = this
        )
        val extras = intent.extras
        val trips = if(SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            extras?.getParcelableArrayList(Constants.SEARCH_TRIPS_DATA, TripsData::class.java)
        }
        else{
            extras?.getParcelableArrayList<TripsData>(Constants.SEARCH_TRIPS_DATA)
        }

        if (trips != null){
            tripsData = trips.toList()
        }


        binding.rvTrips.isVisible = true
        binding.tvNoTripsAvailable.isVisible = false
        binding.includeTripDestinationLayout.root.visibility = View.GONE


        binding.apply {
            includeAppBar.apply {
                this.toolbarTitle.text = "Search results for Trip"
                this.filter.isVisible = false

                this.arrowBackPress.setOnClickListener {
                    finish()
                }
            }

            skeleton = skeletonLayout
            skeleton.showSkeleton()

            rvTrips.apply {
                setRVVertical()
                adapter = tripsAdapter
                rvSkeleton = this.applySkeleton(R.layout.rv_trips_item)
                rvSkeleton.showSkeleton()


            }
            Handler().postDelayed({
                skeleton.showOriginal()
               rvSkeleton.showOriginal()
                tripsAdapter.submitList(tripsData)
            }, 1000)
        }
    }


    override fun onAdapterItemClicked(position: Int, data: Any) {
        if (data is TripsData) {
            startActivity(Intent(this, TripDetailActivity::class.java).apply {
                putExtra(TRIP_ID, data._id)
            })
        }
    }

}