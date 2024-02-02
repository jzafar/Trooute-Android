package com.example.trooute.presentation.ui.review

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.trooute.R
import com.example.trooute.core.util.Constants
import com.example.trooute.core.util.Resource
import com.example.trooute.databinding.ActivityReviewsBinding
import com.example.trooute.presentation.adapters.ReviewsAdapter
import com.example.trooute.presentation.utils.Loader
import com.example.trooute.presentation.utils.WindowsManager.statusBarColor
import com.example.trooute.presentation.utils.setRVVertical
import com.example.trooute.presentation.viewmodel.bookingviewmodel.GetBookingDetailsViewModel
import com.example.trooute.presentation.viewmodel.reviewviewmodel.GetReviewsViewModel
import com.faltenreich.skeletonlayout.Skeleton
import com.faltenreich.skeletonlayout.createSkeleton
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ReviewsActivity : AppCompatActivity() {

    private val TAG = "ReviewsActivity"

    private lateinit var binding: ActivityReviewsBinding
    private val getReviewsViewModel: GetReviewsViewModel by viewModels()
    private lateinit var skeleton: Skeleton
    @Inject
    lateinit var loader: Loader

    private val reviewsAdapter: ReviewsAdapter by lazy {
        ReviewsAdapter()
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        statusBarColor(R.color.white)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_reviews)
        val userId = intent.getStringExtra(Constants.USER_ID).toString()
        binding.apply {
            includeAppBar.apply {
                this.toolbarTitle.text = "Reviews"
                this.filter.isVisible = false

                this.arrowBackPress.setOnClickListener {
                    finish()
                }
            }
            skeleton = ltRoot.createSkeleton()
            skeleton.showSkeleton()

            rvReviews.apply {
                setRVVertical()
                adapter = reviewsAdapter
            }

            getReviewsViewModel.getReviews(userId)
            bindGetReviewsObserver()
        }
    }

    private fun bindGetReviewsObserver() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {

                getReviewsViewModel.getReviewsState.collect {
                    when (it) {
                        is Resource.ERROR -> {
                            skeleton.showOriginal()
                            Log.e(
                                TAG,
                                "bindGetReviewsObserver: Error -> " + it.message.toString()
                            )
                        }

                        Resource.LOADING -> {

                        }

                        is Resource.SUCCESS -> {
                            Log.e(TAG, "bindGetReviewsObserver: Success -> " + it.data)
                            it.data.data?.let { reviewsData ->
                            }

                            skeleton.showOriginal()
                        }
                    }
                }
            }
        }
    }
}