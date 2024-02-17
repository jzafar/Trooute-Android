package com.example.trooute.presentation.adapters

import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.trooute.R
import com.example.trooute.core.util.SharedPreferenceManager
import com.example.trooute.data.model.Enums.PickUpPassengersStatus
import com.example.trooute.data.model.common.User
import com.example.trooute.data.model.trip.response.Booking
import com.example.trooute.databinding.RvPickupPassengersItemBinding
import com.example.trooute.presentation.interfaces.PickUpPassengersEventListener
import com.example.trooute.presentation.utils.ValueChecker
import com.example.trooute.presentation.utils.loadProfileImage
import com.example.trooute.presentation.utils.StatusChecker.checkPickupStatus
import com.example.trooute.presentation.utils.ValueChecker.checkStringValue


class PickupPassengersAdapter(private val adapterItemClickListener: PickUpPassengersEventListener,
                              private val startMessaging: (User?) -> Unit,
                              private val startCall: (User?) -> Unit,
                              private val sharedPreferenceManager: SharedPreferenceManager,
                              private val applicationContext: Context,
) :
    ListAdapter<Booking, PickupPassengersAdapter.ViewHolder>(DiffCallback()) {
    inner class ViewHolder(private val binding: RvPickupPassengersItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindViews(currentItem: Booking?) {
            currentItem?.let { item ->
                binding.apply {
                    checkPickupStatus(sharedPreferenceManager.driverMode(),tvPickupStatus,
                        item.pickupStatus?.passengerStatus, item.pickupStatus?.driverStatus)

                    includeUserDetail.apply {
                        loadProfileImage(imgUserProfile, item.user?.photo)
                        tvUserName.text = ValueChecker.checkStringValue(
                            tvUserName.context,
                            item.user?.name
                        )
                        var genderStr = ValueChecker.checkStringValue(
                            gender.context, item.user?.gender
                        )

                        if (genderStr.equals(gender.context.getString(R.string.not_provided))){
                            gender.isVisible = false
                        }
                        else {
                            gender.text = genderStr
                        }

                        tvAvgRating.text = ValueChecker.checkFloatValue(
                            item.user?.reviewsStats?.avgRating
                        )
                        tvTotalReviews.text = "(${
                            ValueChecker.checkLongValue(item.user?.reviewsStats?.totalReviews)
                        })"

                        messageIcon.setOnClickListener {
                            startMessaging.invoke(item.user)
                        }

                        callIcon.setOnClickListener {
                            startCall.invoke(item.user)
                        }
                    }

                    tvAddress.text = checkStringValue( tvAddress.context, item.pickupLocation?.address)

                    btnMarkedPickup.setOnClickListener{
                        if(!sharedPreferenceManager.driverMode()) {
                            val eBuilder = AlertDialog.Builder(applicationContext)
                            eBuilder.setTitle("Warning")
                            eBuilder.setMessage("Once you Makred as pickup, you can't change status after that.")
                            eBuilder.setPositiveButton("Ok", fun(_: DialogInterface, _: Int) {
                                adapterItemClickListener.onUpdateStatusButtonClick(data = currentItem, status = PickUpPassengersStatus.Pickedup)

                            })

                            eBuilder.setNegativeButton("Cancel"){
                                    _, _ -> eBuilder.setCancelable(true)
                            }

                            eBuilder.create()
                            eBuilder.show()

                        } else {
                            adapterItemClickListener.onUpdateStatusButtonClick(data = currentItem, status = PickUpPassengersStatus.Pickedup)
                        }

                    }
                    btnNotifyPassenger.setOnClickListener{
                        adapterItemClickListener.onUpdateStatusButtonClick(data = currentItem, status = PickUpPassengersStatus.GoingToPickup)
                    }

                    tvMapButton.setOnClickListener {
                        adapterItemClickListener.onMapButtonClick(data = currentItem)
                    }
                    btnPassengerNotShowedUp.setOnClickListener {
                        adapterItemClickListener.onUpdateStatusButtonClick(data = currentItem, status = PickUpPassengersStatus.PassengerNotShowedup)
                    }

                }


            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DataBindingUtil.inflate<RvPickupPassengersItemBinding>(
            LayoutInflater.from(parent.context), R.layout.rv_pickup_passengers_item, parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bindViews(currentItem)
    }

    class DiffCallback :
        DiffUtil.ItemCallback<Booking>() {
        override fun areItemsTheSame(
            oldItem: Booking,
            newItem: Booking
        ) = oldItem._id == newItem._id

        override fun areContentsTheSame(
            oldItem: Booking,
            newItem: Booking
        ) = oldItem == newItem
    }
}