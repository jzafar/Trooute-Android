package com.travel.trooute.presentation.ui.setting

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.databinding.adapters.TextViewBindingAdapter.setDrawableEnd
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.travel.trooute.R
import com.travel.trooute.core.util.Constants
import com.travel.trooute.core.util.Constants.TROOUTE_TOPIC
import com.travel.trooute.core.util.Resource
import com.travel.trooute.core.util.SharedPreferenceManager
import com.travel.trooute.databinding.FragmentSettingsBinding
import com.travel.trooute.presentation.ui.auth.SignInActivity
import com.travel.trooute.presentation.ui.auth.YourProfileActivity
import com.travel.trooute.presentation.ui.main.BecomeDriverActivity
import com.travel.trooute.presentation.ui.general.FrequentlyAskedQuestionsActivity
import com.travel.trooute.presentation.ui.trip.SetUpYourTripActivity
import com.travel.trooute.presentation.ui.trip.TripsHistoryActivity
import com.travel.trooute.presentation.ui.wishlist.WishListActivity
import com.travel.trooute.presentation.utils.Loader
import com.travel.trooute.presentation.utils.ValueChecker
import com.travel.trooute.presentation.utils.ValueChecker.checkFloatValue
import com.travel.trooute.presentation.utils.ValueChecker.checkLongValue
import com.travel.trooute.presentation.utils.ValueChecker.checkStringValue
import com.travel.trooute.presentation.utils.composeEmail
import com.travel.trooute.presentation.utils.inviteFriend
import com.travel.trooute.presentation.utils.loadImage
import com.travel.trooute.presentation.utils.loadProfileImage
import com.travel.trooute.presentation.utils.showErrorMessage
import com.travel.trooute.presentation.utils.showSuccessMessage
import com.travel.trooute.presentation.viewmodel.authviewmodel.GetMeVM
import com.travel.trooute.presentation.viewmodel.driverviewmodel.SwitchDriverModeViewModel
import com.travel.trooute.presentation.viewmodel.notification.PushNotificationViewModel
import com.google.android.play.core.review.ReviewException
import com.google.android.play.core.review.ReviewManagerFactory
import com.google.android.play.core.review.model.ReviewErrorCode
import com.travel.trooute.presentation.ui.review.ReviewsActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SettingsFragment : Fragment() {

    private val TAG = "SettingsFragment"

    private lateinit var binding: FragmentSettingsBinding
//    private lateinit var approved: String

    private val switchDriverModeViewModel: SwitchDriverModeViewModel by viewModels()
    private val pushNotificationViewModel: PushNotificationViewModel by viewModels()
    private val getMeViewModel: GetMeVM by viewModels()
    @Inject
    lateinit var sharedPreferenceManager: SharedPreferenceManager

    @Inject
    lateinit var loader: Loader

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_settings, container, false)

        binding.apply {
            includeUserDetailLayout.apply {
                ltCallInboxSection.isVisible = false
                ltComments.setOnClickListener {
                    startActivity(Intent(requireContext(), ReviewsActivity::class.java).apply {
                        putExtra(Constants.USER_ID, sharedPreferenceManager.getAuthIdFromPref())

                    })
                }
                userReviews.setOnClickListener {
                    startActivity(
                        Intent(
                            requireContext(),
                            YourProfileActivity::class.java
                        )
                    )
                }
            }

            // Show switch button if driver is approved otherwise hide
            switchDriverMode.isVisible =
                sharedPreferenceManager.getDriverStatus()?.lowercase() == "approved"

            setUpViews(sharedPreferenceManager.driverMode())

            if (sharedPreferenceManager.getDriverStatus()?.lowercase() == "approved") {
                switchDriverMode.isChecked = sharedPreferenceManager.driverMode()
                ltBecomeADriver.setOnClickListener {
                    switchDriverModeViewModel.switchDriver()
                    if (switchDriverMode.isChecked) bindSwitchDriverModeObserver(false)
                    else bindSwitchDriverModeObserver(true)
                }

                tvCreateNewTrip.setOnClickListener {
                    var user = sharedPreferenceManager.getAuthModelFromPref()
                    if (user?.stripeConnectedAccountId != null) {
                        startActivity(Intent(requireContext(), SetUpYourTripActivity::class.java))
                    } else {
                        showConnectStripeAccountAlertAtCreateTrip()
                    }

                }
            } else if (sharedPreferenceManager.getDriverStatus()?.lowercase() == "pending") {
                tvBecomeADriver.text = requireContext().getString(R.string.become_a_driver) + " " + getString(R.string.reques_pending)
                ltBecomeADriver.setOnClickListener {
                    startActivity(Intent(requireContext(), BecomeDriverActivity::class.java))
                }
            } else if (sharedPreferenceManager.getDriverStatus()?.lowercase() == "rejected") {
                tvBecomeADriver.text = requireContext().getString(R.string.become_a_driver) + " " + getString(R.string.request_rejected)
                ltBecomeADriver.setOnClickListener {
                    startActivity(Intent(requireContext(), BecomeDriverActivity::class.java))
                }
            } else {
                ltBecomeADriver.setOnClickListener {
                    startActivity(Intent(requireContext(), BecomeDriverActivity::class.java))
                }
            }

            switchNotification.isChecked = sharedPreferenceManager.getNotificationMode()
            ltNotification.setOnClickListener {
                if (switchNotification.isChecked)
                    notificationTopic(false, false)
                else
                    notificationTopic(true, false)
            }

            tvTripHistory.setOnClickListener {
                startActivity(Intent(requireContext(), TripsHistoryActivity::class.java))
            }
            tvYourProfile.setOnClickListener {
                startActivity(
                    Intent(
                        requireContext(),
                        YourProfileActivity::class.java
                    )
                )
            }

            tvInviteFriend.setOnClickListener {
                requireContext().inviteFriend()
            }

            tvWishlist.isVisible = !sharedPreferenceManager.driverMode()
            tvWishlist.setOnClickListener {
                startActivity(
                    Intent(
                        requireContext(),
                        WishListActivity::class.java
                    ).putExtra("ToolBarTitle", tvWishlist.text.toString())
                )
            }

            tvFrequentlyAskedQuestions.setOnClickListener {
                startActivity(
                    Intent(
                        requireContext(),
                        FrequentlyAskedQuestionsActivity::class.java
                    ).putExtra("ToolBarTitle", tvFrequentlyAskedQuestions.text.toString())
                )
            }

            giveUsFeedBack.setOnClickListener {
                val manager = ReviewManagerFactory.create(requireContext())
                val request = manager.requestReviewFlow()
                request.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // We got the ReviewInfo object
                        val reviewInfo = task.result
                        val flow = manager.launchReviewFlow(requireActivity(), reviewInfo)
                        flow.addOnCompleteListener { _ ->
                            Log.i("GoogleReview", "" + flow.isSuccessful)
                            // The flow has finished. The API does not indicate whether the user
                            // reviewed or not, or even whether the review dialog was shown. Thus, no
                            // matter the result, we continue our app flow.
                        }
                    } else {
                        // There was some problem, log or handle the error code.
                        @ReviewErrorCode val reviewErrorCode = (task.getException() as ReviewException).errorCode
                        Log.i("GoogleReview", "" + reviewErrorCode)
                    }
                }
//                showRatingDialog()
            }

            termsAndConditions.setOnClickListener {
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(Constants.TERMS_CONDITIONS))
                startActivity(browserIntent)
//                startActivity(
//                    Intent(
//                        requireContext(),
//                        TermsAndConditionsActivity::class.java
//                    ).putExtra("ToolBarTitle", termsAndConditions.text.toString())
//                )
            }

            privacyPolicy.setOnClickListener {
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(Constants.PRIVACY_POLICY))
                startActivity(browserIntent)
//                startActivity(
//                    Intent(
//                        requireContext(),
//                        PrivacyPolicyActivity::class.java
//                    ).putExtra("ToolBarTitle", privacyPolicy.text.toString())
//                )
            }

            tvReportProblem.setOnClickListener {
                requireContext().composeEmail()
            }

            ltLogout.setOnClickListener {
                notificationTopic(false, true)
            }

            includeVehicleInfoLayout.editCarInfo.setOnClickListener {
                startActivity(Intent(requireContext(), BecomeDriverActivity::class.java))
            }
        }

        return binding.root
    }

    private fun showConnectStripeAccountAlertAtCreateTrip() {
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.error))
            .setMessage(getString(R.string.connect_stripe_create_account))
            .setPositiveButton(getString(R.string.ok)) { _, _ ->

            }
            .create()
            .show()
    }
    @SuppressLint("MissingInflatedId")
    private fun showRatingDialog() {
        val builder = AlertDialog.Builder(
            requireContext(), R.style.CustomAlertDialog
        ).create()
        val view = layoutInflater.inflate(R.layout.dialog_rating_bar, null)
        val button_submit = view.findViewById<TextView>(R.id.dialogSubmit_button)
        val button_not_now = view.findViewById<TextView>(R.id.dialogDismiss_button)
        builder.setView(view)
        button_submit.setOnClickListener {
            Toast(requireContext()).showSuccessMessage(
                requireContext(),
                getString(R.string.ratting_submitted)
            )
            builder.dismiss()
        }
        button_not_now.setOnClickListener {
            builder.dismiss()
        }
        builder.setCanceledOnTouchOutside(false)
        builder.show()
    }

    private fun notificationTopic(subscribe: Boolean, isLogout: Boolean) {
        Log.e(TAG, "notificationTopic: subscribe -> $subscribe")
        if (subscribe)
            pushNotificationViewModel.subscribeTopic(
                "$TROOUTE_TOPIC${
                    sharedPreferenceManager.getAuthIdFromPref()
                }"
            )
        else
            pushNotificationViewModel.unsubscribeTopic(
                "${TROOUTE_TOPIC}${sharedPreferenceManager.getAuthIdFromPref()}"
            )

        bindTopicObserver(subscribe, isLogout)
    }

    private fun bindTopicObserver(subscribe: Boolean, isLogout: Boolean) {
        pushNotificationViewModel.topicState.onEach {
            when (it) {
                is Resource.ERROR -> {
                    Log.e(TAG, "bindTopicObserver: Error -> ${it.message}")
                }

                Resource.LOADING -> {
                    Log.e(TAG, "bindTopicObserver: Loading...")
                }

                is Resource.SUCCESS -> {
                    Log.e(TAG, "bindTopicObserver: subscribe -> $subscribe")
                    Log.e(TAG, "bindTopicObserver: isLogout -> $isLogout")

                    if (isLogout) {
//                        Toast(requireContext()).showSuccessMessage(
//                            requireContext(),
//                            "Logout successfully!"
//                        )

                        sharedPreferenceManager.saveAuthIdInPref(null)
                        sharedPreferenceManager.saveAuthTokenInPref(null)
                        sharedPreferenceManager.saveAuthModelInPref(null)
                        sharedPreferenceManager.saveIsDriverStatus(null)
                        startActivity(Intent(requireContext(), SignInActivity::class.java))
                        activity?.finish()
                    } else {
                        Log.e(TAG, "bindTopicObserver: subscribe----> $subscribe")
                        binding.switchNotification.isChecked = subscribe
                        sharedPreferenceManager.saveNotificationMode(subscribe)
                    }
                }
            }
        }.launchIn(lifecycleScope)
    }

    private fun setUpViews(isInDriverMode: Boolean) {
        binding.apply {
            if (isInDriverMode) {
                includeVehicleInfoLayout.vehicleInfoRoot.isVisible = true
                includeVehicleInfoLayout.editCarInfo.isVisible = true
                tvWishlist.isVisible = false
                tvCreateNewTrip.isVisible = true
            } else {
                includeVehicleInfoLayout.vehicleInfoRoot.isVisible = false
                includeVehicleInfoLayout.editCarInfo.isVisible = false
                tvWishlist.isVisible = true
                tvCreateNewTrip.isVisible = false
            }
        }
    }

    @SuppressLint("SetTextI18n", "RestrictedApi")
    override fun onResume() {
        super.onResume()
        refreshData()
        getMeViewModel.getMe()
        bindGetMeApi()
    }

    private fun refreshData(){
        sharedPreferenceManager.getAuthModelFromPref().let { user ->
            binding.apply {
                includeUserDetailLayout.apply {
                    loadProfileImage(imgUserProfile, user?.photo.toString())
                    tvUserName.text = checkStringValue(
                        requireContext(), user?.name
                    )
                    var genderStr = ValueChecker.checkStringValue(
                        requireContext(), user?.gender
                    )

                    if (genderStr.equals(getString(R.string.not_provided))){
                        gender.isVisible = false
                    }
                    else {
                        gender.text = genderStr
                    }

                    if (sharedPreferenceManager.getDriverStatus() == "approved") {
                        setDrawableEnd(
                            tvUserName,
                            ContextCompat.getDrawable(requireContext(), R.drawable.ic_verified_done)
                        )

                        tvBecomeADriver.text = ContextCompat.getString(
                            requireContext(), R.string.driver_mode
                        )
                    } else if(sharedPreferenceManager.getDriverStatus() == "pending") {
                        tvBecomeADriver.text = requireContext().getString(R.string.become_a_driver) + " " + getString(R.string.reques_pending)
                    } else if (sharedPreferenceManager.getDriverStatus()?.lowercase() == "rejected") {
                        tvBecomeADriver.text = requireContext().getString(R.string.become_a_driver) + " " + getString(R.string.request_rejected)
                        ltBecomeADriver.setOnClickListener {
                            startActivity(Intent(requireContext(), BecomeDriverActivity::class.java))
                        }
                    } else {
                        setDrawableEnd(tvUserName, null)

                        tvBecomeADriver.text = ContextCompat.getString(
                            requireContext(), R.string.become_a_driver
                        )
                    }

                    user?.reviewsStats.let { reviewsStats ->
                        tvAvgRating.text = checkFloatValue(reviewsStats?.avgRating)
                        tvTotalReviews.text = "(${
                            checkLongValue(reviewsStats?.totalReviews)
                        })"
                    }
                }

                includeVehicleInfoLayout.apply {
                    user?.carDetails.let { carDetails ->
                        loadImage(imgVehicleProfile, carDetails?.photo.toString())
                        tvVehicleModel.text = checkStringValue(
                            requireContext(), carDetails?.make + " " + carDetails?.model
                        )
                        tvVehicleYear.text = checkLongValue(carDetails?.year)
                        tvVehicleColor.text = checkStringValue(requireContext(), carDetails?.color)
                        carDetails?.reviewsStats.let { reviewsStats ->
                            tvVehicleAvgRating.text = checkFloatValue(reviewsStats?.avgRating)
                            tvVehicleTotalReviews.text = "(${
                                checkLongValue(reviewsStats?.totalReviews)
                            })"
                        }
                        tvVehicleRegistrationNumber.text = checkStringValue(
                            requireContext(), carDetails?.registrationNumber
                        )
                    }
                }
            }
        }
    }
    @SuppressLint("RepeatOnLifecycleWrongUsage")
    private fun bindGetMeApi() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                getMeViewModel.getMeState.collect {
                    when (it) {
                        is Resource.ERROR -> {

                            Log.e(TAG, "getMe: Error -> " + it.message.toString())
                        }

                        Resource.LOADING -> {

                        }

                        is Resource.SUCCESS -> {
                            it.data.data?.let { user ->
                                sharedPreferenceManager.saveIsDriverStatus(user.isApprovedDriver)
                                sharedPreferenceManager.saveDriverMode(user.driverMode)
                                sharedPreferenceManager.updateUserInPref(user)
                                refreshData()
                            }
                            Log.i(TAG, "getMe: success -> " + it.data)

                        }
                    }
                }
            }
        }
    }

    private fun bindSwitchDriverModeObserver(isChecked: Boolean) {
        lifecycleScope.launch {
            switchDriverModeViewModel.switchDriverState.collect {
                loader.cancel()
                when (it) {
                    is Resource.ERROR -> {
                        Toast(requireContext()).showErrorMessage(
                            requireContext(), it.message.toString()
                        )
                    }

                    Resource.LOADING -> {
                        loader.show()
                    }

                    is Resource.SUCCESS -> {
                        Toast(requireContext()).showSuccessMessage(
                            requireContext(), it.data.message.toString()
                        )

                        binding.switchDriverMode.isChecked = isChecked
                        setUpViews(isChecked)
                        sharedPreferenceManager.saveDriverMode(isChecked)

                    }
                }
            }
        }
    }
}