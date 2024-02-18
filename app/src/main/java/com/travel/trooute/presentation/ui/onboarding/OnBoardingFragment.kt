package com.travel.trooute.presentation.ui.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.travel.trooute.R
import com.travel.trooute.databinding.FragmentOnBoardingBinding


class OnBoardingFragment : Fragment() {

    private val TAG = "OnBoardingFragment"

    private lateinit var onBoardingTitle: String
    private lateinit var onBoardingDesc: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            onBoardingTitle = requireArguments().getString(ARG_PARAM1).toString()
            onBoardingDesc = requireArguments().getString(ARG_PARAM2).toString()
        }
    }


    private lateinit var binding: FragmentOnBoardingBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_on_boarding, container, false)

        binding.apply {
            title = onBoardingTitle
            description = onBoardingDesc
        }

        return binding.root
    }

    companion object {
        // The fragment initialization parameters, e.g. ARG_ITEM_NUMBER
        private const val ARG_PARAM1 = "param1"
        private const val ARG_PARAM2 = "param2"

        fun newInstance(
            title: String?,
            desc: String?
        ): OnBoardingFragment {
            val args = Bundle()
            val fragment = OnBoardingFragment()

            args.putString(ARG_PARAM1, title)
            args.putString(ARG_PARAM2, desc)

            fragment.arguments = args
            return fragment
        }
    }
}