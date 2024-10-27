package com.travel.trooute.presentation.ui.review

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
import com.travel.trooute.R
import com.travel.trooute.core.util.Constants
import com.travel.trooute.core.util.Resource
import com.travel.trooute.data.model.review.response.Reviews
import com.travel.trooute.databinding.ActivityReviewsBinding
import com.travel.trooute.presentation.adapters.ReviewsAdapter
import com.travel.trooute.presentation.utils.Loader
import com.travel.trooute.presentation.utils.ValueChecker
import com.travel.trooute.presentation.utils.WindowsManager.statusBarColor
import com.travel.trooute.presentation.utils.loadProfileImage
import com.travel.trooute.presentation.utils.setRVVertical
import com.travel.trooute.presentation.viewmodel.reviewviewmodel.GetReviewsViewModel
import com.faltenreich.skeletonlayout.Skeleton
import com.faltenreich.skeletonlayout.applySkeleton
import com.faltenreich.skeletonlayout.createSkeleton
import com.travel.trooute.presentation.ui.BaseActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ReviewsActivity : BaseActivity() {

    private val TAG = "ReviewsActivity"

    private lateinit var binding: ActivityReviewsBinding
    private val getReviewsViewModel: GetReviewsViewModel by viewModels()
    private lateinit var skeleton: Skeleton
    private lateinit var rvSkeleton: Skeleton
    @Inject
    lateinit var loader: Loader
    private lateinit var reviewsAdapter: ReviewsAdapter

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        statusBarColor(R.color.white)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_reviews)
        val userId = intent.getStringExtra(Constants.USER_ID).toString()
        reviewsAdapter = ReviewsAdapter()
        binding.apply {
            includeAppBar.apply {
                this.toolbarTitle.text = getString(R.string.reviews)
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
                rvSkeleton = this.applySkeleton(R.layout.rv_reviews_item)
                rvSkeleton.showSkeleton()
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
                            Log.i(TAG, "bindGetReviewsObserver: Success -> " + it.data)
                            it.data.data?.let { reviewsData ->
                                populateData(reviewsData)
                            }
                            rvSkeleton.showOriginal()
                            skeleton.showOriginal()
                        }
                    }
                }
            }
        }
    }

    private fun populateData(reviewsData: List<Reviews>) {
        val firstReview = reviewsData.first()
        binding.includeUserDetail.apply {
            loadProfileImage(imgUserProfile, firstReview.target?.photo.toString())
            tvUserName.text = ValueChecker.checkStringValue(
                this@ReviewsActivity, firstReview.target?.name
            )
            var genderStr = ValueChecker.checkStringValue(
                this@ReviewsActivity, firstReview.target?.gender
            )

            if (genderStr.equals(getString(R.string.not_provided))){
                gender.isVisible = false
            }
            else {
                gender.text = genderStr
            }

            tvAvgRating.text =
                ValueChecker.checkFloatValue(firstReview.target?.reviewsStats?.avgRating)
            tvTotalReviews.text = "(${
                ValueChecker.checkLongValue(
                    firstReview.target?.reviewsStats?.totalReviews
                )
            })"

        if(reviewsData.size  > 0 && firstReview.user != null) {
            binding.noReviews.isVisible = false
            binding.rvReviews.isVisible = true
            reviewsAdapter.submitList(reviewsData)
        } else {
            binding.noReviews.isVisible = true
            binding.rvReviews.isVisible = false
        }

        }
    }
}