package com.travel.trooute.data.repository

import android.util.Log
import com.travel.trooute.core.util.Constants.IS_EXIST
import com.travel.trooute.core.util.Constants.LAST_MESSAGE_FIELD_NAME
import com.travel.trooute.core.util.Constants.MESSAGE_COLLECTION_NAME
import com.travel.trooute.core.util.Constants.MESSAGE_FIELD_NAME
import com.travel.trooute.core.util.Constants.NAME_FIELD_NAME
import com.travel.trooute.core.util.Constants.PHOTO_FIELD_NAME
import com.travel.trooute.core.util.Constants.SEEN_FIELD_NAME
import com.travel.trooute.core.util.Constants.SENDER_ID_FIELD_NAME
import com.travel.trooute.core.util.Constants.TIMESTAMP_FIELD_NAME
import com.travel.trooute.core.util.Resource
import com.travel.trooute.data.model.chat.Inbox
import com.travel.trooute.data.model.chat.Message
import com.travel.trooute.data.model.chat.Users
import com.travel.trooute.di.hilt_module.InboxCollection
import com.travel.trooute.domain.repository.ChatRepository
import com.google.firebase.Timestamp
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class ChatRepositoryImpl @Inject constructor(
    @InboxCollection private val inboxCollection: CollectionReference,
) : ChatRepository {

    override fun getAllInbox(userId: String?): Flow<Resource<List<Inbox>>> = callbackFlow {
        // Registers callback to fireStore, which will be called on new events
        val subscription = inboxCollection
            .whereEqualTo("${userId.toString()}.$IS_EXIST", true)
            .orderBy(TIMESTAMP_FIELD_NAME, Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                Log.e("getAllInbox", "snapshot isEmpty ${snapshot?.isEmpty}")
                Log.e("getAllInbox", "error ${error?.message}")
                if (snapshot?.isEmpty == true) {
                    trySend(Resource.ERROR(error?.message))
                    return@addSnapshotListener
                }
                // Sends events to the flow! Consumers will get the new events
                try {
                    snapshot?.let { documents ->
                        val inbox = mutableListOf<Inbox>()
                        for (document in documents) {
                            inbox.add(getInbox(userId, document))
                        }

                        trySend(Resource.SUCCESS(inbox))
                    }
                } catch (exception: Throwable) {
                    // Event couldn't be sent to the flow
                    trySend(Resource.ERROR(exception.message))
                }
            }

        // The callback inside awaitClose will be executed when the flow is
        // either closed or cancelled.
        // In this case, remove the callback from FireStore
        awaitClose {
            subscription.remove()
            channel.close()
        }
    }

    private fun getInbox(userId: String?, document: QueryDocumentSnapshot?): Inbox {
        Log.e("getUser", "getInbox: " + getUser(userId.toString(), document))
        return Inbox(
            user = getUser(userId.toString(), document),
            users = getUsers(document),
            lastMessage = document?.get(LAST_MESSAGE_FIELD_NAME) as String,
            timestamp = document.get(TIMESTAMP_FIELD_NAME) as Timestamp?
        )
    }

//    private fun getUser(uID: String, document: QueryDocumentSnapshot?): Users {
//        val userMapData = document?.get(uID) as Map<String, Any>?
//        return Users(
//            _id = uID,
//            name = userMapData?.get(NAME_FIELD_NAME)?.toString(),
//            photo = userMapData?.get(PHOTO_FIELD_NAME)?.toString(),
//            seen = userMapData?.get(SEEN_FIELD_NAME) as Boolean
//        )
//    }

    private fun getUser(uID: String, document: QueryDocumentSnapshot?): Users {
        val userMapData = document?.data as Map<String, Any>?

        val senderId = userMapData
            ?.filterKeys { key -> key != uID && key != LAST_MESSAGE_FIELD_NAME && key != TIMESTAMP_FIELD_NAME } // Exclude specific keys
            ?.values
            ?.firstOrNull() // Get the first (and presumably only) entry's value

        return if (senderId is Map<*, *>) {
            Users(
                _id = uID,
                name = senderId["name"]?.toString(),
                photo = senderId["image"]?.toString(),
                seen = senderId["seen"] as? Boolean ?: false
            )
        } else {
            // Handle the case when senderId is not a Map
            // You may return a default Users object or handle it as needed
            Users(_id = uID, name = null, photo = null, seen = false)
        }
    }

    private fun getUsers(document: QueryDocumentSnapshot?): List<Users> {
        val usersList = mutableListOf<Users>()

        // Get all the data from the document
        val documentData = document?.data

        if (documentData != null) {
            // Iterate over the keys in the document data
            for (key in documentData.keys) {
                // Check if the value associated with the key is a hashmap
                if (documentData[key] is Map<*, *>) {
                    // Assuming it's the nested hashmap, access its data
                    val userMapData = documentData[key] as Map<String, Any>?

                    // Extract the relevant fields
                    val name = userMapData?.get(NAME_FIELD_NAME)?.toString()
                    val photo = userMapData?.get(PHOTO_FIELD_NAME)?.toString()
                    val seen = userMapData?.get(SEEN_FIELD_NAME) as Boolean

                    usersList.add(
                        Users(
                            _id = key, // Use the key as the user ID
                            name = name,
                            photo = photo,
                            seen = seen
                        )
                    )
                }
            }
        }
        return usersList
    }

    override fun getAllMessages(
        senderId: String?,
        receiverId: String?
    ): Flow<Resource<List<Message>>> = callbackFlow {
        Log.e("getAllMessages", "senderId: $senderId")
        Log.e("getAllMessages", "receiverId: $receiverId")
        inboxCollection
//            .where(Filter.and(
//                Filter.equalTo("$senderId.$IS_EXIST", true),
//                Filter.equalTo("$receiverId.$IS_EXIST", true)
//            ))
            .whereEqualTo("$senderId.$IS_EXIST", true)
            .whereEqualTo("$receiverId.$IS_EXIST", true)
//            .get()
            .addSnapshotListener { querySnapshot, error ->
                if (querySnapshot?.isEmpty == true) {
                    return@addSnapshotListener
                }

                if (querySnapshot != null) {
                    Log.e("getAllMessages", "ref -> " + querySnapshot.documents[0])
                }

                // Document already exists, add the message to it
                querySnapshot?.documents?.get(0)
                    ?.reference
                    ?.collection(MESSAGE_COLLECTION_NAME)
                    ?.orderBy(TIMESTAMP_FIELD_NAME, Query.Direction.ASCENDING)
                    ?.addSnapshotListener { snapshot, error ->
                        if (snapshot == null) {
                            return@addSnapshotListener
                        }
                        // Sends events to the flow! Consumers will get the new events
                        try {
                            snapshot.let { documents ->
                                val message = mutableListOf<Message>()
                                for (document in documents) {
                                    message.add(getMessages(document))
                                }

                                trySend(Resource.SUCCESS(message))
                            }
                        } catch (exception: Throwable) {
                            // Event couldn't be sent to the flow
                            trySend(Resource.ERROR(exception.localizedMessage))
                        }
                    }
            }

        // The callback inside awaitClose will be executed when the flow is
        // either closed or cancelled.
        // In this case, remove the callback from FireStore
        awaitClose {
            channel.close()
        }
    }

    private fun getMessages(document: QueryDocumentSnapshot?): Message {
        return Message(
            senderId = document?.getString(SENDER_ID_FIELD_NAME),
            message = document?.getString(MESSAGE_FIELD_NAME),
            timestamp = document?.getTimestamp(TIMESTAMP_FIELD_NAME),
        )
    }

    override fun updateSeenStatus(
        currentUID: String?, receiverID: String?, isSeen: Boolean
    ): Flow<Resource<Boolean>> = callbackFlow {
        try {
            trySend(Resource.LOADING)
            inboxCollection
                .whereEqualTo("$currentUID.$IS_EXIST", true)
                .whereEqualTo("$receiverID.$IS_EXIST", true)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    if (querySnapshot.isEmpty) {
                        return@addOnSuccessListener
                    }

                    // Document already exists, add the message to it
                    val inboxDocRef = querySnapshot.documents[0].reference

                    inboxDocRef.update(
                        "$currentUID.$SEEN_FIELD_NAME", isSeen
                    ).addOnCompleteListener {
                        if (it.isSuccessful) {
                            trySend(Resource.SUCCESS(true))
                        } else {
                            trySend(Resource.ERROR(it.exception?.localizedMessage))
                        }
                    }
                }

            awaitClose {
                channel.close()
            }
        } catch (exception: Exception) {
            trySend(Resource.ERROR("Seen Status Updating Failed due to ${exception.localizedMessage}"))
        }
    }

    override fun sendMessage(
        currentUser: Users?,
        inbox: Inbox,
        message: Message
    ): Flow<Resource<Boolean>> = callbackFlow {
        try {
            trySend(Resource.LOADING)

            // Map inbox data
            val currentUserID = currentUser?._id.toString()
            val receiverID = inbox.user?._id.toString()

            val inboxDocumentRef = inboxCollection.document()

            val senderData = hashMapOf(
                IS_EXIST to true,
                PHOTO_FIELD_NAME to currentUser?.photo,
                NAME_FIELD_NAME to currentUser?.name,
                SEEN_FIELD_NAME to currentUser?.seen
            )

            Log.e("sendMessage", "isCurrentUser seen: " + currentUser?.seen)
            Log.e("sendMessage", "isReceiver seen: " + inbox.user?.seen)

            val receiverData = hashMapOf(
                IS_EXIST to true,
                PHOTO_FIELD_NAME to inbox.user?.photo,
                NAME_FIELD_NAME to inbox.user?.name,
                SEEN_FIELD_NAME to inbox.user?.seen
            )

            Log.e("sendMessage", "senderData : " + senderData)
            Log.e("sendMessage", "receiverData : " + receiverData)

            inboxCollection
                .whereEqualTo("$currentUserID.$IS_EXIST", true)
                .whereEqualTo("$receiverID.$IS_EXIST", true)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    if (querySnapshot.isEmpty) {
                        // No document exists, create a new one
                        val inboxHashMap = hashMapOf<String, Any>()
                        inboxHashMap[currentUserID] = senderData
                        inboxHashMap[receiverID] = receiverData
                        inboxHashMap[LAST_MESSAGE_FIELD_NAME] = inbox.lastMessage ?: ""
                        inboxHashMap[TIMESTAMP_FIELD_NAME] = inbox.timestamp ?: ""

                        inboxDocumentRef.set(inboxHashMap, SetOptions.merge())
                            .addOnSuccessListener {
                                trySend(Resource.SUCCESS(true))
                                val messageDocument =
                                    inboxDocumentRef.collection(MESSAGE_COLLECTION_NAME)
                                        .document()
                                val messageHashMap = hashMapOf(
                                    MESSAGE_FIELD_NAME to message.message,
                                    SENDER_ID_FIELD_NAME to message.senderId,
                                    TIMESTAMP_FIELD_NAME to message.timestamp
                                )
                                messageDocument.set(messageHashMap)
                                    .addOnSuccessListener {
                                        trySend(Resource.SUCCESS(true))
                                    }
                                    .addOnFailureListener {
                                        trySend(Resource.ERROR("Message Sending Failed due to ${it.localizedMessage}"))
                                    }

                                // Close the channel after the message is sent
                                channel.close()
                            }
                            .addOnFailureListener {
                                trySend(Resource.ERROR("Creating inbox failed due to ${it.localizedMessage}"))
                                // Close the channel if there is an error
                                channel.close()
                            }
                    } else {
                        // Document already exists, add the message to it
                        val inboxDocRef = querySnapshot.documents[0].reference

                        inboxDocRef.update(
                            receiverID, receiverData,
                            LAST_MESSAGE_FIELD_NAME, inbox.lastMessage,
                            TIMESTAMP_FIELD_NAME, inbox.timestamp
                        )

                        val messageDocument =
                            inboxDocRef.collection(MESSAGE_COLLECTION_NAME).document()
                        val messageHashMap = hashMapOf(
                            MESSAGE_FIELD_NAME to message.message,
                            SENDER_ID_FIELD_NAME to message.senderId,
                            TIMESTAMP_FIELD_NAME to message.timestamp
                        )
                        messageDocument.set(messageHashMap)
                            .addOnSuccessListener {
                                trySend(Resource.SUCCESS(true))
                                // Close the channel after the message is sent
                                channel.close()
                            }
                            .addOnFailureListener {
                                trySend(Resource.ERROR("Message Sending Failed due to ${it.localizedMessage}"))
                                // Close the channel if there is an error
                                channel.close()
                            }
                    }
                }
                .addOnFailureListener {
                    trySend(Resource.ERROR("Error checking inbox: ${it.localizedMessage}"))
                    // Close the channel if there is an error
                    channel.close()
                }

            awaitClose {
                channel.close()
            }
        } catch (exception: Exception) {
            trySend(Resource.ERROR("Message Sending Failed due to ${exception.localizedMessage}"))
            // Close the channel if there is an error
            channel.close()
        }
    }
}