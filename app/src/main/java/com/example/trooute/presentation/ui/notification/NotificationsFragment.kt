package com.example.trooute.presentation.ui.notification

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.trooute.R
import com.example.trooute.databinding.FragmentNotificationsBinding
import com.example.trooute.presentation.adapters.NotificationAdapter
import com.example.trooute.presentation.utils.setRVVertical
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