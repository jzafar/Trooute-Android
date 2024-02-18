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
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FrequentlyAskedQuestionsActivity : AppCompatActivity() {

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
                tvExpandableHeader.text = "How does Trooute ensure user safety?"
                tvExpandableBody.text = "Trooute prioritizes user safety by implementing features such as user ratings, reviews, and optional verification badges. We encourage users to provide feedback on their experiences to maintain a safe and reliable community."
                setUpExpandableView(ltExpandableView, icExpandText, tvExpandableBody)
            }

            includeExpandText2.apply {
                tvExpandableHeader.text = "Can I cancel a ride, and what is the cancellation policy?"
                tvExpandableBody.text =
                    "Yes, you can cancel a ride. However, please note that no costs will be refunded for cancellations. It's important to review and understand our cancellation policy before confirming a ride."
                setUpExpandableView(ltExpandableView, icExpandText, tvExpandableBody)
            }

            includeExpandText3.apply {
                tvExpandableHeader.text = "What happens in the event of an accident or damage during a ride?"
                tvExpandableBody.text =
                    "Users (both drivers and passengers) are solely responsible for any accidents or damages that may occur during a ride. Trooute assumes no liability for such incidents. Users are encouraged to report any issues promptly through our support channels."
                setUpExpandableView(ltExpandableView, icExpandText, tvExpandableBody)
            }

            includeExpandText4.apply {
                tvExpandableHeader.text = "Is my personal information secure on Trooute?"
                tvExpandableBody.text =
                    "We take the security of user information seriously. Trooute employs industry-standard security measures to protect user data. For more details, please refer to our Privacy Policy."
                setUpExpandableView(ltExpandableView, icExpandText, tvExpandableBody)
            }

            includeExpandText5.apply {
                tvExpandableHeader.text = "Can I choose my travel companions based on preferences?"
                tvExpandableBody.text =
                    "Yes, allows users to set preferences, such as music preferences, smoking/non-smoking preferences, and other travel-related preferences. This information can help you find compatible travel companions."
                setUpExpandableView(ltExpandableView, icExpandText, tvExpandableBody)
            }

            includeExpandText6.apply {
                tvExpandableHeader.text = "How can I contact customer support?"
                tvExpandableBody.text =
                    "For any inquiries or assistance, you can reach our customer support team at trooute@outlook.com. We are here to help with any questions or concerns you may have."
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