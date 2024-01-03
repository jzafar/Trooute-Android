package com.example.trooute.presentation.utils

import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.example.trooute.R
import com.example.trooute.core.util.URL.BASE_URL
import com.google.android.material.imageview.ShapeableImageView

fun loadProfileImage(imageView: View, url: String?) {
    when (imageView) {
        is ShapeableImageView -> {
            Glide.with(imageView.context)
                .load("$BASE_URL/files/$url")
                .centerCrop()
                .placeholder(R.drawable.profile_place_holder)
                .into(imageView)
        }

        is ImageView -> {
            Glide.with(imageView.context)
                .load("$BASE_URL/files/$url")
                .centerCrop()
                .placeholder(R.drawable.profile_place_holder)
                .into(imageView)
        }
    }
}

fun loadImage(imageView: View, url: String?) {
    when (imageView) {
        is ShapeableImageView -> {
            Glide.with(imageView.context)
                .load("$BASE_URL/files/$url")
                .centerCrop()
                .placeholder(R.drawable.place_holder)
                .into(imageView)
        }

        is ImageView -> {
            Glide.with(imageView.context)
                .load("$BASE_URL/files/$url")
                .centerCrop()
                .placeholder(R.drawable.place_holder)
                .into(imageView)
        }
    }
}