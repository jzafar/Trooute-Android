package com.travel.trooute.core.util

object Constants {
    const val PRICE_SIGN = "€"
    const val EMAIL = "Email"
    const val TRIP_ID = "Trip Id"
    const val GET_TRIP_DETAIL = "Get Trip Detail"
    const val BOOKING_ID = "Booking Id"
    const val CREATE_BOOKING_REQUEST = "Create Booking Request"
    const val TOTAL_AMOUNT = "Total Amount"
    const val PLATFORM_FEE_PRICE: Double = 1.0
    const val WEIGHT_SIGN = "kg"
    const val PLACES_START_LAT_LNG = "Google places start lat long"
    const val PLACES_DESTINATION_LAT_LNG = "Google places destination lat long"
    const val SEARCH_TRIPS_DATA = "searchTripsData"
    const val MAX_PASSENGERS = 10
    const val USER_ID = "user_id"

    // Trip Status
    const val SCHEDULED = "Scheduled"
    const val IN_PROGRESS = "In Progress"
    const val COMPLETED = "Completed"
    const val CANCELED = "Canceled"
    const val PickupStarted = "PickupStarted"

    // FireStore constant
    const val INBOX_COLLECTION_NAME = "TroouteInbox"
    const val MESSAGE_COLLECTION_NAME = "Message"

    const val MESSAGE_FIELD_NAME = "message"
    const val SENDER_FIELD_NAME = "sender"
    const val RECEIVER_FIELD_NAME = "receiver"
    const val SENDER_ID_FIELD_NAME = "senderId"
    const val RECEIVER_ID_FIELD_NAME = "receiverId"
    const val IS_EXIST = "isExist"
    const val PHOTO_FIELD_NAME = "image"
    const val NAME_FIELD_NAME = "name"
    const val SEEN_FIELD_NAME = "seen"
    const val FCM_TOKEN_FIELD_NAME = "fcmToken"
    const val DOCUMENT_ID_FIELD_NAME = "documentId"
    const val DOCUMENT_INBOX_ID_FIELD_NAME = "inboxDocumentId"
    const val LAST_MESSAGE_FIELD_NAME = "lastMessage"
    const val TIMESTAMP_FIELD_NAME = "timestamp"

    const val MESSAGE_USER_INFO = "UserInfo"

    // FCM
    const val FCM_BASE_URL = "https://fcm.googleapis.com"
    const val SERVER_KEY = "AAAAsRKMUMQ:APA91bGTTdh3bQM6oORh38BawUcjBYokEyDT_j4tFkb2scXMMM7QqFRx6OE8yKTrjs9C5l_hHI3OelhaHDVnSg0LUtl0XQhWa5nPLUJ-Ul046k6w4gqIjElov_c_ycjKxCuUaO_qNaKk"
    const val CONTENT_TYPE = "application/json"

    const val TOPIC = "/topics/"
    const val TROOUTE_TOPIC = "trooute_topic_"

    const val TONE = "Tri-tone"
    const val MUTABLE_CONTENT = true

    const val CHANNEL_ID = "TroouteChannel"
    const val CHANNEL_DESCRIPTION = "TroouteChannelDescription"

    const val TRIP_BOOKED_TITLE = "Trip Booked"
    const val TRIP_BOOKED_BODY = "Great news, Your trip is booked by "

    const val BOOKED_CANCELLED_TITLE = "Booking cancelled"
    const val BOOKED_CANCELLED_BODY = "Sorry, Your trip is cancelled by "

    const val PICKUP_PASSENGERS_STARTED = "Pickup passengers started"
    const val PICKUP_PASSENGERS_STARTED_BODY = "Driver has started pickup passengers"

    const val MAKE_PAYMENT_TITLE = "Booking payment"
    const val MAKE_PAYMENT_BODY = "Get ready for an amazing journey, Payment received for your trip from "

    const val ACCEPT_BOOKING_TITLE = "Booking accepted"
    const val ACCEPT_BOOKING_BODY = "Great news, Your trip is all set and accepted by "

    const val START_BOOKING_TITLE = "Booking started"
    const val START_BOOKING_BODY = "Enjoy every moment of your trip. Your adventure has started by "

    const val TRIP_COMPLETED_TITLE = "Trip completed"
    const val TRIP_COMPLETED_BODY = "Congratulations! Your trip has successfully come to a memorable end by "

    const val WISH_LIST_CHECKER_CODE = 1
    const val INTENT_IS_TRIP_WISH_LISTED = "IS_TRIP_WISH_LISTED"

    const val TERMS_CONDITIONS = "https://trooute.com/terms-and-conditions.html"
    const val PRIVACY_POLICY = "https://www.google.com"

    const val BROADCAST_INTENT = "broadcast_intent"
    const val BROADCAST_TYPE = "broadcast_type"
}

enum class BroadCastType(val type: String) {
    FETCH_ME("fetch_me"),
    CHAT("chat"),
    BOOKINGS("bookings")
}