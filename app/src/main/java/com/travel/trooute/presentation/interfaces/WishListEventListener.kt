package com.travel.trooute.presentation.interfaces

interface WishListEventListener {
    fun onWishListEventClick(position: Int, data: Any, added: Boolean)
}