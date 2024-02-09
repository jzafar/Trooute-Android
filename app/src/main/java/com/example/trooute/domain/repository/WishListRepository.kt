package com.example.trooute.domain.repository

import com.example.trooute.core.util.Resource
import com.example.trooute.data.model.auth.response.User
import com.example.trooute.data.model.wishlist.WishListResponse

interface WishListRepository {
    suspend fun addToWishList(id: String?): Resource<User>
    suspend fun getMyWishList(): Resource<WishListResponse>
}