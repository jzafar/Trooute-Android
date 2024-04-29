package com.travel.trooute.presentation.ui.chat

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.travel.trooute.R
import com.travel.trooute.core.util.Constants.INBOX_COLLECTION_NAME
import com.travel.trooute.core.util.Constants.MESSAGE_USER_INFO
import com.travel.trooute.core.util.Resource
import com.travel.trooute.core.util.SharedPreferenceManager
import com.travel.trooute.data.model.chat.Inbox
import com.travel.trooute.data.model.chat.Users
import com.travel.trooute.databinding.FragmentInboxBinding
import com.travel.trooute.presentation.adapters.InboxAdapter
import com.travel.trooute.presentation.interfaces.AdapterItemClickListener
import com.travel.trooute.presentation.utils.setRVVertical
import com.travel.trooute.presentation.viewmodel.chatviewmodel.GetAllInboxViewModel
import com.faltenreich.skeletonlayout.Skeleton
import com.travel.trooute.core.util.BroadCastType
import com.travel.trooute.core.util.Constants
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class InboxFragment : Fragment(), AdapterItemClickListener {

    private val TAG = "InboxFragment"

    private lateinit var binding: FragmentInboxBinding
    private lateinit var skeleton: Skeleton
    private lateinit var inboxAdapter: InboxAdapter

    private val getAllInboxViewModel: GetAllInboxViewModel by viewModels()

    @Inject
    lateinit var sharedPreferenceManager: SharedPreferenceManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_inbox, container, false)
        inboxAdapter = InboxAdapter(this, sharedPreferenceManager)

        binding.apply {
            skeleton = skeletonLayout
            skeleton.showSkeleton()

            rvInbox.apply {
                this.setRVVertical()
                adapter = inboxAdapter
            }

//            getAllInboxViewModel.getAllInbox(sharedPreferenceManager.getAuthIdFromPref().toString())
            bindInboxObservers()
        }
        val lbm = LocalBroadcastManager.getInstance(requireContext())
        lbm.registerReceiver(receiver, IntentFilter(Constants.BROADCAST_INTENT))
        return binding.root
    }

    private var receiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent != null) {
                Log.i("tag","Receive notification")
                val  type = intent.getStringExtra(Constants.BROADCAST_TYPE)
                type?.let {
                    val broadCastType = BroadCastType.valueOf(it)
                    didReceiveNotification(broadCastType)
                }
            }
        }
    }

    private fun getInboxMessages() {
        getAllInboxViewModel.getAllInbox(sharedPreferenceManager.getAuthIdFromPref().toString())
    }
    override fun onResume() {
        super.onResume()
        getInboxMessages()
    }
    private fun bindInboxObservers() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                getAllInboxViewModel.getAllInboxState.collectLatest {
                    when (it) {
                        is Resource.ERROR -> {
                            Log.e(TAG, "bindInboxObservers: Error -> " + it.message.toString())
                            skeleton.showOriginal()
                            binding.rvInbox.isVisible = false
                            binding.tvNoInboxDataAvailable.isVisible = true
                        }

                        Resource.LOADING -> {
                            Log.e(TAG, "bindInboxObservers: loading...")
                        }

                        is Resource.SUCCESS -> {
                            Log.e(TAG, "bindInboxObservers: Success -> " + it.data)
                            if (it.data.isEmpty()) {
                                binding.rvInbox.isVisible = false
                                binding.tvNoInboxDataAvailable.isVisible = true
                            } else {
                                binding.rvInbox.isVisible = true
                                binding.tvNoInboxDataAvailable.isVisible = false
                                inboxAdapter.submitList(it.data)
                            }
                            skeleton.showOriginal()
                        }
                    }
                }
            }
        }
    }

    private fun didReceiveNotification(type: BroadCastType){
        when (type) {
            BroadCastType.CHAT -> {
                getInboxMessages()
            }
            else -> {}
        }
    }

    override fun onAdapterItemClicked(position: Int, data: Any) {
        if (data is Inbox) {
            startActivity(Intent(requireContext(), MessageActivity::class.java).apply {
                putExtra(INBOX_COLLECTION_NAME, data)
                Log.e(TAG, "onAdapterItemClicked: user data -> $data")
                data.users?.filter { users ->
                    users._id.toString() != sharedPreferenceManager.getAuthIdFromPref().toString()
                }?.map { filtered ->
                    putExtra(
                        MESSAGE_USER_INFO, Users(
                            _id = filtered._id,
                            name = data.user?.name,
                            photo = data.user?.photo
                        )
                    )
                }
            })
        }
    }
}