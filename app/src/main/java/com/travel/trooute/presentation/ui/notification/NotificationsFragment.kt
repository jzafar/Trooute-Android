package com.travel.trooute.presentation.ui.notification

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.travel.trooute.R
import com.travel.trooute.databinding.FragmentNotificationsBinding
import com.travel.trooute.presentation.adapters.NotificationAdapter
import com.travel.trooute.presentation.utils.setRVVertical
import com.faltenreich.skeletonlayout.Skeleton
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NotificationsFragment : Fragment() {

    private val TAG = "Notifications"

    private lateinit var binding: FragmentNotificationsBinding
    private lateinit var skeleton: Skeleton

    private val notificationAdapter: NotificationAdapter by lazy {
        NotificationAdapter()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_notifications, container, false)

        binding.apply {
            skeleton = skeletonLayout
//            skeleton.showSkeleton()

            rvNotification.apply {
                this.setRVVertical()
                adapter = notificationAdapter
            }
        }

        return binding.root
    }
}