package com.example.trooute.core.util

import android.content.Context
import android.content.SharedPreferences
import com.example.trooute.data.model.auth.response.User
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class SharedPreferenceManager @Inject constructor(@ApplicationContext context: Context) {

    companion object {
        const val SHARED_PREF_FILE = "SharedPref"
        const val PREF_ONBOARDING_STATE = "OnBoardingState"
        const val PREF_AUTH_TOKEN = "AuthToken"
        const val PREF_MAKE_PAYMENT_UID = "MakePaymentUID"
        const val PREF_AUTH_ID = "AuthID"
        const val PREF_AUTH_DRIVER = "IsAuthDriver"
        const val PREF_DRIVER_MODE = "DriverMode"
        const val PREF_NOTIFICATION_MODE = "NotificationMode"
        const val PREF_AUTH_DATA = "AuthData"
    }

    private val sharedPreferences = context.getSharedPreferences(
        SHARED_PREF_FILE, Context.MODE_PRIVATE
    )
    private val editor: SharedPreferences.Editor = sharedPreferences.edit()
    private val gson = Gson()

    fun saveOnBoardingState(value: Boolean) {
        editor.putBoolean(PREF_ONBOARDING_STATE, value)
        editor.apply()
    }

    fun getOnBoardingState(): Boolean {
        return sharedPreferences.getBoolean(PREF_ONBOARDING_STATE, false)
    }

    fun saveAuthTokenInPref(token: String?) {
        editor.putString(PREF_AUTH_TOKEN, token)
        editor.apply()
    }

    fun getAuthTokenFromPref(): String? {
        return sharedPreferences.getString(PREF_AUTH_TOKEN, null)
    }

    fun saveMakePaymentUserId(id: String?){
        editor.putString(PREF_MAKE_PAYMENT_UID, id)
        editor.apply()
    }

    fun getMakePaymentUserIdFromPref(): String? {
        return sharedPreferences.getString(PREF_MAKE_PAYMENT_UID, null)
    }

    fun saveAuthIdInPref(id: String?) {
        editor.putString(PREF_AUTH_ID, id)
        editor.apply()
    }

    fun getAuthIdFromPref(): String? {
        return sharedPreferences.getString(PREF_AUTH_ID, "AuthID")
    }

    fun saveIsDriverStatus(value: String?) {
        editor.putString(PREF_AUTH_DRIVER, value)
        editor.apply()
    }

    fun getDriverStatus(): String? {
        return sharedPreferences.getString(PREF_AUTH_DRIVER, "")
    }

    fun saveDriverMode(value: Boolean) {
        editor.putBoolean(PREF_DRIVER_MODE, value)
        editor.apply()
    }

    fun driverMode(): Boolean {
        return sharedPreferences.getBoolean(PREF_DRIVER_MODE, false)
    }

    fun saveNotificationMode(value: Boolean) {
        editor.putBoolean(PREF_NOTIFICATION_MODE, value)
        editor.apply()
    }

    fun getNotificationMode(): Boolean {
        return sharedPreferences.getBoolean(PREF_NOTIFICATION_MODE, false)
    }

    fun saveAuthModelInPref(model: User?) {
        editor.putString(PREF_AUTH_DATA, gson.toJson(model)).apply()
    }

    fun getAuthModelFromPref(): User? {
        val json = sharedPreferences.getString(PREF_AUTH_DATA, null) ?: return null
        return gson.fromJson(json, User::class.java)
    }
}