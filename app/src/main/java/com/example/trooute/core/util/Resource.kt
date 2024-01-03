package com.example.trooute.core.util

import android.util.Log
import org.json.JSONObject
import retrofit2.Response

private val TAG = "Resource"

sealed class Resource<out T : Any> {
    object LOADING : Resource<Nothing>()
    data class SUCCESS<out T : Any>(val data: T) : Resource<T>()
    data class ERROR(val message: String?) : Resource<Nothing>()
}

suspend fun <T : Any> safeApiCall(call: suspend () -> Response<T>): Resource<T> {
    try {
        val response = call.invoke()
        if (response.isSuccessful) {
            val body = response.body()
            body?.let {
                return Resource.SUCCESS(it)
            }
        } else if (response.code() == 400 || response.code() == 401) {
            val message = response.errorBody()?.string()?.let {
                JSONObject(it).getString("message")
            }
            return Resource.ERROR(message.toString())
        } else {
            val message = response.errorBody()?.string()?.let {
                JSONObject(it).getString("message")
            }
            return Resource.ERROR(message.toString())
        }
        return Resource.ERROR("Unexpected error occurred")
    } catch (e: Exception) {
        return Resource.ERROR("${e.message}")
    }
}