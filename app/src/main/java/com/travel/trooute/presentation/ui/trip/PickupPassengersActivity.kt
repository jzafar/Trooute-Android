package com.travel.trooute.presentation.ui.trip

import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.travel.trooute.R
import com.travel.trooute.core.util.Constants
import com.travel.trooute.core.util.Constants.IN_PROGRESS
import com.travel.trooute.core.util.Constants.SCHEDULED
import com.travel.trooute.core.util.Resource
import com.travel.trooute.core.util.SharedPreferenceManager
import com.travel.trooute.data.model.Enums.PickUpPassengersStatus
import com.travel.trooute.data.model.chat.Users
import com.travel.trooute.data.model.common.User
import com.travel.trooute.data.model.trip.request.UpdatePickupStatusRequest
import com.travel.trooute.data.model.trip.response.Booking
import com.travel.trooute.data.model.trip.response.TripsData
import com.travel.trooute.databinding.ActivityPickupPassengersBinding
import com.travel.trooute.presentation.adapters.PickupPassengersAdapter
import com.travel.trooute.presentation.interfaces.PickUpPassengersEventListener
import com.travel.trooute.presentation.ui.chat.MessageActivity
import com.travel.trooute.presentation.utils.Loader
import com.travel.trooute.presentation.utils.WindowsManager.statusBarColor
import com.travel.trooute.presentation.utils.setRVVertical
import com.travel.trooute.presentation.utils.showErrorMessage
import com.travel.trooute.presentation.utils.showSuccessMessage
import com.travel.trooute.presentation.viewmodel.tripviewmodel.GetPickupPassengersViewModel
import com.travel.trooute.presentation.viewmodel.tripviewmodel.UpdatePickupStatusViewModel
import com.travel.trooute.presentation.viewmodel.tripviewmodel.UpdateTripStatusViewModel
import com.faltenreich.skeletonlayout.Skeleton
import com.faltenreich.skeletonlayout.createSkeleton
import com.travel.trooute.core.util.Constants.PickupStarted
import com.travel.trooute.data.model.common.Driver
import com.travel.trooute.presentation.ui.BaseActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class PickupPassengersActivity : BaseActivity(), PickUpPassengersEventListener {
    private val TAG = "PickupPassengersActivity"

    private lateinit var binding: ActivityPickupPassengersBinding
    @Inject
    lateinit var sharedPreferenceManager: SharedPreferenceManager
    @Inject
    lateinit var loader: Loader
    private lateinit var skeleton: Skeleton

    private var tripData: TripsData? = null
    private var tripID: String? = null
    private lateinit var pickupPassengersAdapter: PickupPassengersAdapter
    private val getPickupPassengersViewModel: GetPickupPassengersViewModel by viewModels()
    private val updatePickUpPassengersStatus: UpdatePickupStatusViewModel by viewModels()
    private val updateTripStatusViewModel: UpdateTripStatusViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        statusBarColor(R.color.white)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_pickup_passengers)

        tripID = intent.getStringExtra(Constants.TRIP_ID).toString()
        pickupPassengersAdapter = PickupPassengersAdapter(this, ::startMessaging, ::startCall, sharedPreferenceManager, applicationContext)
        sharedPreferenceManager.getAuthModelFromPref()


        binding.apply {
            includeAppBar.apply {
                this.toolbarTitle.text = getString(R.string.pickup_passengers)
                this.filter.isVisible = false

                this.arrowBackPress.setOnClickListener {
                    finish()
                }
            }
            rvPickupPassengers.apply {
                this.setRVVertical()
                adapter = pickupPassengersAdapter
            }
            skeleton = ltMainContent.createSkeleton()
            skeleton.showSkeleton()

            if (sharedPreferenceManager.driverMode()) {
                btnStartTrip.setOnClickListener {
                    val bookings = tripData?.bookings?.asReversed()
                    var allMarkedAsPickedUp = true
                    if (bookings != null) {
                        for (booking in bookings) {
                            if (booking.pickupStatus?.passengerStatus != PickUpPassengersStatus.DriverPickedup.toString() &&
                                booking.pickupStatus?.driverStatus != PickUpPassengersStatus.PassengerNotShowedup.toString()) {
                                allMarkedAsPickedUp = false
                            }
                        }
                    }
                    if (!allMarkedAsPickedUp) {
                        val eBuilder = AlertDialog.Builder(this@PickupPassengersActivity)
                        eBuilder.setTitle(getString(R.string.warning))
                        eBuilder.setMessage(getString(R.string.can_not_start_trip))
                        eBuilder.setPositiveButton(getString(R.string.ok), fun(_: DialogInterface, _: Int) {

                        })


                        eBuilder.create()
                        eBuilder.show()
                    } else {
                        updateTripStatusViewModel.updateTripStatus(tripID!!, IN_PROGRESS)
                        bindUpdateTripStatusObserver()
                    }


//                    sendNotification(START_BOOKING_TITLE, START_BOOKING_BODY, tripsData.bookings)
                }
            } else {
                ltButtonSection.isVisible = false
            }

        }
        val mainHandler = Handler(Looper.getMainLooper())
        mainHandler.post(object : Runnable {
            override fun run() {
                getPickupStatus()
                mainHandler.postDelayed(this, 5000)
            }
        })
        bindGetPickupStatusObserver()
    }



    private fun getPickupStatus() {
        tripID?.let { getPickupPassengersViewModel.getPickUpStatus(it) }
    }
    private fun bindGetPickupStatusObserver() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                getPickupPassengersViewModel.getPickupState.collect {
//                    loader.cancel()
                    when (it) {
                        is Resource.ERROR -> {
                            skeleton.showOriginal()
                            Log.e(
                                TAG, "bindGetPickupStatusObserver: Error -> " + it.message.toString()
                            )
                        }

                        Resource.LOADING -> {
//                            loader.show()
                        }

                        is Resource.SUCCESS -> {
                            skeleton.showOriginal()
                            it.data.data?.let { tripsData ->
                                tripData = tripsData
                                setUpViews(tripsData)
                            }

                        }

                        else -> {}
                    }
                }
            }
        }
    }
    private fun setUpViews(tripsData:TripsData) {
        pickupPassengersAdapter.submitList(tripsData.bookings)
        binding.apply {
            if (sharedPreferenceManager.driverMode()) {
                if (tripsData.status == IN_PROGRESS) {
                    finish()
//                    btnStartTrip.isVisible = false
//                    btnTripEnd.isVisible = true
                } else if (tripsData.status == PickupStarted) {
                    btnStartTrip.isVisible = true
                    btnTripEnd.isVisible = false
                } else {
                    ltButtonSection.isVisible = false
//                btnTripEnd.isVisible = false
                }
            }
        }

    }
    private fun startMessaging(user: User?) {
        user?.let {
            startActivity(
                Intent(this, MessageActivity::class.java).apply {
                    putExtra(
                        Constants.MESSAGE_USER_INFO, Users(
                            _id = it._id,
                            name = it.name,
                            photo = it.photo
                        )
                    )
                }
            )
        }
    }

    private fun startCall(phoneNumber: String?) {
        val uri = "tel:$phoneNumber"
        val intent = Intent(Intent.ACTION_DIAL)
        intent.setData(Uri.parse(uri))
        startActivity(intent)
    }

    override fun onMapButtonClick(data: Booking) {
        val gmmIntentUri = Uri.parse("http://maps.google.com/maps?daddr=" +
                data.pickupLocation?.location?.coordinates?.first() + "," +
                data.pickupLocation?.location?.coordinates?.last())
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        mapIntent.setPackage("com.google.android.apps.maps")
        if (mapIntent.resolveActivity(applicationContext.packageManager) != null) {
            startActivity(mapIntent)
        }
    }

    override fun onUpdateStatusButtonClick(data: Booking, status: PickUpPassengersStatus) {
        val request = data.trip?.let { data._id?.let { it1 ->
            data.pickupStatus?._id?.let { it2 ->
                UpdatePickupStatusRequest(it,
                    it1, status.toString(), it2
                )
            }
        } }

        if (request != null) {
            updatePickUpPassengersStatus.updatePickupStatus(request = request)
            bindUpdatePickupStatusObserver()
        }
    }

    private fun bindUpdatePickupStatusObserver(){
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                updatePickUpPassengersStatus.updateTripStatusState.collect {
                    loader.cancel()
                    when (it) {
                        is Resource.ERROR -> {
                            Log.e(
                                TAG, "bindUpdatePickupStatusObserver: Error -> " + it.message.toString()
                            )
                        }

                        Resource.LOADING -> {
                            loader.show()
                        }

                        is Resource.SUCCESS -> {
                            it.data.data?.let { tripsData ->
                                setUpViews(tripsData)
                            }
                            Toast(this@PickupPassengersActivity).showSuccessMessage(
                                this@PickupPassengersActivity, getString(R.string.status_updated_successfully)
                            )
                        }
                        else -> {}
                    }
                }
            }
        }
    }

    private fun bindUpdateTripStatusObserver() {
        lifecycleScope.launch {
            updateTripStatusViewModel.updateTripStatusState.collect {
                loader.cancel()
                when (it) {
                    is Resource.ERROR -> {
                        Toast(this@PickupPassengersActivity).showErrorMessage(
                            this@PickupPassengersActivity, it.message.toString()
                        )

                        Log.e(
                            TAG, "bindMarkTripCompletedObserver: Error -> " + it.message.toString()
                        )
                    }

                    Resource.LOADING -> {
                        loader.show()
                    }

                    is Resource.SUCCESS -> {

                        if (it.data.message == "trip_status_INPROGRESS") {
                            binding.apply {
                                btnTripEnd.isVisible = true
                                ltCancelStartTrip.isVisible = false
                            }
                        }

                        Log.i(
                            TAG,
                            "bindMarkTripCompletedObserver: success -> " + it.data.message.toString()
                        )

//                        if (toId?.isEmpty() == false || toId != null) {
////                            sendNotification(title.toString(), body.toString(), toId)
//                        }
                    }
                }
            }
        }
    }
}