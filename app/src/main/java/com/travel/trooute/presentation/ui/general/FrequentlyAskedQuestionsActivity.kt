package com.travel.trooute.presentation.ui.general

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import com.travel.trooute.R
import com.travel.trooute.databinding.ActivityFrequentlyAskedQuestionsBinding
import com.travel.trooute.presentation.ui.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FrequentlyAskedQuestionsActivity : BaseActivity() {

    private val TAG = "FrequentlyAskedQuestions"

    private lateinit var binding: ActivityFrequentlyAskedQuestionsBinding

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_frequently_asked_questions)

        binding.apply {
            includeAppBar.apply {
                this.toolbarTitle.text = intent.getStringExtra("ToolBarTitle")
                this.filter.isVisible = false

                this.arrowBackPress.setOnClickListener {
                    finish()
                }
            }

            includeExpandText1.apply {
                tvExpandableHeader.text = getString(R.string.trooute_safty)
                tvExpandableBody.text = getString(R.string.trooute_safty_answer)
                setUpExpandableView(ltExpandableView, icExpandText, tvExpandableBody)
            }

            includeExpandText2.apply {
                tvExpandableHeader.text = getString(R.string.cancel_policy)
                tvExpandableBody.text = getString(R.string.cancel_policy_answer)
                setUpExpandableView(ltExpandableView, icExpandText, tvExpandableBody)
            }

            includeExpandText3.apply {
                tvExpandableHeader.text = getString(R.string.accident_event)
                tvExpandableBody.text =getString(R.string.accident_event_answer)
                setUpExpandableView(ltExpandableView, icExpandText, tvExpandableBody)
            }

            includeExpandText4.apply {
                tvExpandableHeader.text = getString(R.string.personal_info_secure)
                tvExpandableBody.text = getString(R.string.personal_info_secure_answer)
                setUpExpandableView(ltExpandableView, icExpandText, tvExpandableBody)
            }

            includeExpandText5.apply {
                tvExpandableHeader.text = getString(R.string.travel_companion)
                tvExpandableBody.text = getString(R.string.travel_companion_answer)
                setUpExpandableView(ltExpandableView, icExpandText, tvExpandableBody)
            }

            includeExpandText6.apply {
                tvExpandableHeader.text = getString(R.string.contact_customer)
                tvExpandableBody.text = getString(R.string.contact_customer_answer)
                setUpExpandableView(ltExpandableView, icExpandText, tvExpandableBody)
            }
        }
    }

    private fun setUpExpandableView(
        ltExpandableView: ConstraintLayout,
        icExpandText: AppCompatImageView,
        tvExpandableBody: AppCompatTextView
    ) {
        ltExpandableView.setOnClickListener {
            if (!tvExpandableBody.isVisible) {
                tvExpandableBody.isVisible = true
                icExpandText.setImageResource(R.drawable.ic_arrow_up)
            } else {
                tvExpandableBody.isVisible = false
                icExpandText.setImageResource(R.drawable.ic_arrow_down)
            }
        }
    }
}