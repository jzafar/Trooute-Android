package com.travel.trooute.presentation.ui.chat

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.IntentCompat
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.travel.trooute.R
import com.travel.trooute.core.util.Constants.MESSAGE_USER_INFO
import com.travel.trooute.core.util.Constants.MUTABLE_CONTENT
import com.travel.trooute.core.util.Constants.TONE
import com.travel.trooute.core.util.Constants.TOPIC
import com.travel.trooute.core.util.Constants.TROOUTE_TOPIC
import com.travel.trooute.core.util.Resource
import com.travel.trooute.core.util.SharedPreferenceManager
import com.travel.trooute.data.model.auth.response.User
import com.travel.trooute.data.model.chat.Inbox
import com.travel.trooute.data.model.chat.Message
import com.travel.trooute.data.model.chat.Users
import com.travel.trooute.data.model.notification.NotificationRequest
import com.travel.trooute.databinding.ActivityMessageBinding
import com.travel.trooute.di.hilt_module.InboxCollection
import com.travel.trooute.presentation.adapters.ChatAdapter
import com.travel.trooute.presentation.utils.Utils.getCurrentTimestamp
import com.travel.trooute.presentation.utils.ValueChecker.checkStringValue
import com.travel.trooute.presentation.utils.WindowsManager.statusBarColor
import com.travel.trooute.presentation.utils.messageBoxIsEmpty
import com.travel.trooute.presentation.utils.setRVVertical
import com.travel.trooute.presentation.viewmodel.chatviewmodel.MessageViewModel
import com.travel.trooute.presentation.viewmodel.notification.PushNotificationViewModel
import com.faltenreich.skeletonlayout.Skeleton
import com.google.firebase.firestore.CollectionReference
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MessageActivity : AppCompatActivity() {

    private val TAG = "MessageActivity"

    private lateinit var binding: ActivityMessageBinding
    private lateinit var chatAdapter: ChatAdapter
    private lateinit var skeleton: Skeleton

    private var messageReceiverInfo: Users? = null
    private var authModelInfo: User? = null
    private var isMessageSend: Boolean = false

    private val messageViewModel: MessageViewModel by viewModels()
    private val pushNotificationViewModel: PushNotificationViewModel by viewModels()

    @Inject
    lateinit var sharedPreferenceManager: SharedPreferenceManager

    @Inject
    @InboxCollection
    lateinit var inboxCollectionRef: CollectionReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        statusBarColor(R.color.white)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_message)
        chatAdapter = ChatAdapter(sharedPreferenceManager)
        authModelInfo = sharedPreferenceManager.getAuthModelFromPref()

        messageReceiverInfo = IntentCompat.getParcelableExtra(
            intent,
            MESSAGE_USER_INFO,
            Users::class.java
        )

        binding.apply {
            includeAppBar.apply {
                this.toolbarTitle.text = checkStringValue(
                    this@MessageActivity, messageReceiverInfo?.name
                )
                this.filter.isVisible = false

                this.arrowBackPress.setOnClickListener {
                    finish()
                }
            }

            skeleton = skeletonLayout
            skeleton.showSkeleton()

            rvMessage.apply {
                setRVVertical()
                adapter = chatAdapter
            }

            Log.e(TAG, "onCreate: sender data -> " + authModelInfo)
            Log.e(TAG, "onCreate: receiver data -> " + messageReceiverInfo)

            // Make message as read
            markAsRead()

            messageViewModel.getAllMessages(
                senderId = authModelInfo?._id.toString(),
                receiverId = messageReceiverInfo?._id.toString()
            )
            bindGetAllMessageObserver()

            imgSendMessage.setOnClickListener {
                if (!messageBoxIsEmpty(etMessage)) {
                    messageViewModel.sendMessage(
                        currentUser = Users(
                            _id = authModelInfo?._id.toString(),
                            name = authModelInfo?.name.toString(),
                            photo = authModelInfo?.photo.toString(),
                            seen = true
                        ),
                        inbox = Inbox(
                            user = Users(
                                _id = messageReceiverInfo?._id.toString(),
                                name = messageReceiverInfo?.name.toString(),
                                photo = messageReceiverInfo?.photo.toString(),
                                seen = false
                            ),
                            lastMessage = etMessage.text.toString(),
                            timestamp = getCurrentTimestamp()
                        ),
                        message = Message(
                            senderId = authModelInfo?._id.toString(),
                            message = etMessage.text.toString(),
                            timestamp = getCurrentTimestamp()
                        )
                    )
                    isMessageSend = true
                }
            }

            bindMessageObserver()
        }
    }

    private fun markAsRead() {
        messageViewModel.updateSeenStatus(
            authModelInfo?._id.toString(),
            messageReceiverInfo?._id.toString(),
            true
        )
    }
    private fun bindGetAllMessageObserver() {
        binding.etMessage.setText("")
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                messageViewModel.allMessagesState.collect {
                    when (it) {
                        is Resource.ERROR -> {
                            Log.e(
                                TAG,
                                "bindGetAllMessageObserver: Error -> " + it.message.toString()
                            )

                            skeleton.showOriginal()
                        }

                        Resource.LOADING -> {

                        }

                        is Resource.SUCCESS -> {
                            Log.e(TAG, "bindGetAllMessageObserver: Success -> " + it.data)

                            chatAdapter.submitList(it.data)
                            scrollRVToBottom(it.data)

                            skeleton.showOriginal()
                        }
                    }
                }
            }
        }
    }

    private fun scrollRVToBottom(messagesList: List<Message>?) {
        binding.rvMessage.apply {
            if (messagesList?.isNotEmpty() == true) {
                // Keep RecyclerView always scroll bottom
                smoothScrollToPosition(messagesList.size - 1)
            }
        }
    }

    private fun bindMessageObserver() {
        lifecycleScope.launch {
            messageViewModel.messageState.collectLatest {
                when (it) {
                    is Resource.ERROR -> {
                        Log.e(TAG, "bindMessageObserver: Error -> " + it.message.toString())
                    }

                    Resource.LOADING -> {

                    }

                    is Resource.SUCCESS -> {
                        Log.e(TAG, "bindMessageObserver: Success -> " + it.data)

                        if (isMessageSend) {
                            sendNotification()
                        }
                    }
                }
            }
        }
    }

    private fun sendNotification() {
        Log.e(TAG, "sendNotification: title-> " + messageReceiverInfo?.name.toString())
        Log.e(TAG, "sendNotification: body-> " + binding.etMessage.text.toString())
        pushNotificationViewModel.sendMessageNotification(
            NotificationRequest(
                notification = NotificationRequest.Notification(
                    title = messageReceiverInfo?.name.toString(),
                    body = binding.etMessage.text.toString(),
                    mutable_content = MUTABLE_CONTENT,
                    sound = TONE
                ),
                to = "$TOPIC$TROOUTE_TOPIC${messageReceiverInfo?._id.toString()}",
                data = NotificationRequest.Data(dl = "chat")
            )
        )

        bindSendMessageNotificationObserver()
    }

    private fun bindSendMessageNotificationObserver() {
        isMessageSend = false
        pushNotificationViewModel.sendNotificationState.onEach { state ->
            binding.etMessage.setText("")
            when (state) {
                is Resource.ERROR -> {
                    Log.e(TAG, "bindSendMessageNotificationObserver: Error -> ${state.message}")
                }

                Resource.LOADING -> {
                    Log.e(TAG, "bindSendMessageNotificationObserver: Loading...")
                }

                is Resource.SUCCESS -> {
                    Log.e(TAG, "bindSendMessageNotificationObserver: Success -> ${state.data}")
                }
            }
        }.launchIn(lifecycleScope)
    }
}