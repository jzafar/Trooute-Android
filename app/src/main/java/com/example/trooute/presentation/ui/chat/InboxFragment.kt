package com.example.trooute.presentation.ui.chat

import android.content.Intent
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
import com.example.trooute.R
import com.example.trooute.core.util.Constants
import com.example.trooute.core.util.Constants.INBOX_COLLECTION_NAME
import com.example.trooute.core.util.Constants.MESSAGE_USER_INFO
import com.example.trooute.core.util.Resource
import com.example.trooute.core.util.SharedPreferenceManager
import com.example.trooute.data.model.chat.Inbox
import com.example.trooute.data.model.chat.Users
import com.example.trooute.databinding.FragmentInboxBinding
import com.example.trooute.presentation.adapters.InboxAdapter
import com.example.trooute.presentation.interfaces.AdapterItemClickListener
import com.example.trooute.presentation.utils.setRVVertical
import com.example.trooute.presentation.viewmodel.chatviewmodel.GetAllInboxViewModel
import com.faltenreich.skeletonlayout.Skeleton
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
        inboxAdapter = InboxAdapter(this)

        binding.apply {
            skeleton = skeletonLayout
            skeleton.showSkeleton()

            rvInbox.apply {
                this.setRVVertical()
                adapter = inboxAdapter
            }

            getAllInboxViewModel.getAllInbox(sharedPreferenceManager.getAuthIdFromPref().toString())
            bindInboxObservers()
        }

        return binding.root
    }

    private fun bindInboxObservers() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
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