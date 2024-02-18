package com.travel.trooute.core.util

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

object UploadMultipart {
    // Use this function if u r passing string in addFormDataPart of MultipartBody
    fun stringRequestBody(str: String?): RequestBody {
        return str!!.toRequestBody("text/plain".toMediaTypeOrNull())
    }

    // Use this function if u r passing image file as a MultipartBody
    fun imgFileMultipartBody(fileName: String, file: File?): MultipartBody.Part {
        return MultipartBody.Part.createFormData(fileName, file?.name, imgRequestBody(file))
    }

    // Use this function if u r passing file in addFormDataPart of MultipartBody
    fun imgRequestBody(file: File?): RequestBody {
        return file!!.asRequestBody("image/*".toMediaTypeOrNull())
    }
}