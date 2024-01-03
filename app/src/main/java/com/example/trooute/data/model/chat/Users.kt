package com.example.trooute.data.model.chat

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Users(
    val _id: String? = null,
    val name: String? = null ?: "No name",
    val photo: String? = null ?: "",
    val seen: Boolean = false
) : Parcelable
