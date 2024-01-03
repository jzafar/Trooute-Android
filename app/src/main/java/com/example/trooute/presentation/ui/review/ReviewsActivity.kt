package com.example.trooute.presentation.ui.review

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import com.example.trooute.R
import com.example.trooute.databinding.ActivityReviewsBinding
import com.example.trooute.presentation.adapters.ReviewsAdapter
import com.example.trooute.presentation.utils.WindowsManager.statusBarColor
import com.example.trooute.presentation.utils.setRVVertical
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ReviewsActivity : AppCompatActivity() {

    private val TAG = "ReviewsActivity"

    private lateinit var binding: ActivityReviewsBinding

    private val reviewsAdapter: ReviewsAdapter by lazy {
        ReviewsAdapter()
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        statusBarColor(R.color.white)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_reviews)

        binding.apply {
            includeAppBar.apply {
                this.toolbarTitle.text = "Driver’s Reviews"
                this.filter.isVisible = false

                this.arrowBackPress.setOnClickListener {
                    finish()
                }
            }

            rvReviews.apply {
                setRVVertical()
                adapter = reviewsAdapter
            }
        }
    }
}