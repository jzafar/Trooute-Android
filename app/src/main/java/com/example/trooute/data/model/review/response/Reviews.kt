package com.example.trooute.data.model.review.response
import android.os.Parcelable
import com.example.trooute.data.model.common.User
import kotlinx.parcelize.Parcelize

@Parcelize
data class Reviews (
    val __v: Long? = null ?: 0,
    val _id: String? = null ?: "0",
    val user: User? = null,
    val targetType: String? = null,
    val target: User? = null,
    val comment: String? = null,
    val rating: Long? = null ?: 0
) : Parcelable

