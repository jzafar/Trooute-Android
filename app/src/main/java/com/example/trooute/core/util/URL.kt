package com.example.trooute.core.util

object URL {
    // BASE URL
//    const val BASE_URL = "http://192.168.18.85:4000"

//    const val BASE_URL = "http://localhost:4000"

    const val BASE_URL = "https://backened.trooute.com"

    // AUTH END POINT
    const val LOGIN_END_POINT = "/api/v1/users/login"
    const val SIGNUP_END_POINT = "/api/v1/users/signup"
    const val FORGOT_PASSWORD_END_POINT = "/api/v1/users/forgotPassword"
    const val EMAIL_VERIFICATION_END_POINT = "/api/v1/users/verify/email"
    const val RESEND_EMAIL_VERIFICATION_END_POINT = "/api/v1/users/resend-token"
    const val UPDATE_PROFILE_END_POINT = "api/v1/users/updateMe"
    const val UPDATE_MY_PASSWORD_END_POINT = "api/v1/users/updateMyPassword"
    const val GET_ME_END_POINT = "api/v1/users/me"

    // TRIPS END POINT
    const val CREATE_TRIPS_END_POINT = "/api/v1/trips"
    const val GET_TRIPS_END_POINT = "/api/v1/trips"
    const val GET_TRIPS_DETAILS_END_POINT = "/api/v1/trips"
    const val TRIPS_HISTORY_END_POINT = "/api/v1/trips/trips-history"
    const val UPDATE_TRIP_STATUS = "api/v1/trips/update-trip-status"

    // BOOKINGS END POINT
    const val CREATE_BOOKING_END_POINT = "/api/v1/bookings"
    const val GET_BOOKING_END_POINT = "/api/v1/bookings"
    const val GET_BOOKING_DETAILS_END_POINT = "/api/v1/bookings"
    const val APPROVE_BOOKING_END_POINT = "/api/v1/bookings"
    const val CONFIRM_BOOKING_END_POINT = "/api/v1/bookings"
    const val CANCEL_BOOKING_END_POINT = "/api/v1/bookings"
    const val COMPLETE_BOOKING_END_POINT = "/api/v1/bookings"

    // REVIEW END POINT
    const val REVIEW_END_POINT = "/api/v1/review"
    const val GET_REVIEW_END_POINT = "/api/v1/review/"

    // DRIVER END POINT
    const val UPLOAD_DRIVER_END_POINT = "/api/v1/users/driver/upload-driver-details"
    const val SWITCH_DRIVER_END_POINT = "/api/v1/users/driver/switch-to-driver-mode"
    const val GET_DRIVERS_REQUESTS_END_POINT = "/api/v1/users/driver/get-drivers-requests"
    const val APPROVE_DRIVER_END_POINT = "/api/v1/users/driver"
    const val UPDATE_CAR_INFO_END_POINT = "/api/v1/users/driver/update-car-info"

    // WISH LIST POINT
    const val ADD_TO_WISH_LIST = "api/v1/trips"
    const val GET_MY_WISH_LIST = "api/v1/users/get-my-wishlist"
}