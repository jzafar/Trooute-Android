package com.example.trooute.presentation.ui.setting

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.databinding.adapters.TextViewBindingAdapter.setDrawableEnd
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.trooute.R
import com.example.trooute.core.util.Constants
import com.example.trooute.core.util.Constants.TROOUTE_TOPIC
import com.example.trooute.core.util.Resource
import com.example.trooute.core.util.SharedPreferenceManager
import com.example.trooute.databinding.FragmentSettingsBinding
import com.example.trooute.presentation.ui.auth.SignInActivity
import com.example.trooute.presentation.ui.auth.YourProfileActivity
import com.example.trooute.presentation.ui.main.BecomeDriverActivity
import com.example.trooute.presentation.ui.general.FrequentlyAskedQuestionsActivity
import com.example.trooute.presentation.ui.general.PrivacyPolicyActivity
import com.example.trooute.presentation.ui.general.TermsAndConditionsActivity
import com.example.trooute.presentation.ui.trip.SetUpYourTripActivity
import com.example.trooute.presentation.ui.trip.TripsHistoryActivity
import com.example.trooute.presentation.ui.wishlist.WishListActivity
import com.example.trooute.presentation.utils.Loader
import com.example.trooute.presentation.utils.ValueChecker.checkFloatValue
import com.example.trooute.presentation.utils.ValueChecker.checkLongValue
import com.example.trooute.presentation.utils.ValueChecker.checkStringValue
import com.example.trooute.presentation.utils.composeEmail
import com.example.trooute.presentation.utils.inviteFriend
import com.example.trooute.presentation.utils.loadImage
import com.example.trooute.presentation.utils.loadProfileImage
import com.example.trooute.presentation.utils.showErrorMessage
import com.example.trooute.presentation.utils.showSuccessMessage
import com.example.trooute.presentation.viewmodel.driverviewmodel.SwitchDriverModeViewModel
import com.example.trooute.presentation.viewmodel.notification.PushNotificationViewModel
import com.google.android.play.core.review.ReviewException
import com.google.android.play.core.review.ReviewManagerFactory
import com.google.android.play.core.review.model.ReviewErrorCode
import com.google.android.play.core.review.testing.FakeReviewManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SettingsFragment : Fragment() {

    private val TAG = "SettingsFragment"

    private lateinit var binding: FragmentSettingsBinding
    private lateinit var approved: String

    private val switchDriverModeViewModel: SwitchDriverModeViewModel by viewModels()
    private val pushNotificationViewModel: PushNotificationViewModel by viewModels()

    @Inject
    lateinit var sharedPreferenceManager: SharedPreferenceManager

    @Inject
    lateinit var loader: Loader

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_settings, container, false)
        if (!::approved.isInitialized) {
            approved = ContextCompat.getString(requireContext(), R.string.approved).lowercase()
        }

        binding.apply {
            includeUserDetailLayout.apply {
                ltCallInboxSection.isVisible = false
            }

            // Show switch button if driver is approved otherwise hide
            switchDriverMode.isVisible =
                sharedPreferenceManager.getDriverStatus()?.lowercase() == approved

            setUpViews(sharedPreferenceManager.driverMode())

            if (sharedPreferenceManager.getDriverStatus()?.lowercase() == approved) {
                switchDriverMode.isChecked = sharedPreferenceManager.driverMode()
//                switchDriverMode.setOnCheckedChangeListener { buttonView, isChecked ->
////                    setUpViews(isChecked)
////                    sharedPreferenceManager.saveDriverMode(isChecked)
//                    switchDriverModeViewModel.switchDriver()
//                    bindSwitchDriverModeObserver(isChecked)
//                }

                ltBecomeADriver.setOnClickListener {
                    switchDriverModeViewModel.switchDriver()
                    if (switchDriverMode.isChecked) bindSwitchDriverModeObserver(false)
                    else bindSwitchDriverModeObserver(true)
                }

                tvCreateNewTrip.setOnClickListener {
                    startActivity(Intent(requireContext(), SetUpYourTripActivity::class.java))
                }
            } else {
                ltBecomeADriver.setOnClickListener {
                    startActivity(Intent(requireContext(), BecomeDriverActivity::class.java))
                }

                switchNotification.isChecked = sharedPreferenceManager.getNotificationMode()
                ltNotification.setOnClickListener {
                    if (switchNotification.isChecked)
                        notificationTopic(false, false)
                    else
                        notificationTopic(true, false)
                }
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
        }

        return binding.root
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
                "Rating submitted successfully!"
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
        sharedPreferenceManager.getAuthModelFromPref().let { user ->
            binding.apply {
                includeUserDetailLayout.apply {
                    loadProfileImage(imgUserProfile, user?.photo.toString())
                    tvUserName.text = checkStringValue(
                        requireContext(), user?.name
                    )

                    if (sharedPreferenceManager.getDriverStatus() == approved) {
                        setDrawableEnd(
                            tvUserName,
                            ContextCompat.getDrawable(requireContext(), R.drawable.ic_verified_done)
                        )

                        tvBecomeADriver.text = ContextCompat.getString(
                            requireContext(), R.string.driver_mode
                        )
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
                            requireContext(), carDetails?.model
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