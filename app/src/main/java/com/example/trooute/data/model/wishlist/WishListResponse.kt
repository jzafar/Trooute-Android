package com.example.trooute.data.model.wishlist

data class WishListResponse(
    val `data`: List<Message>?,
    val message: String?,
    val success: Boolean
)