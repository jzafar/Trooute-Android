package com.travel.trooute.domain.repository

import com.travel.trooute.core.util.Resource
import com.travel.trooute.data.model.auth.response.User
import com.travel.trooute.data.model.wishlist.WishListResponse

interface WishListRepository {
    suspend fun addToWishList(id: String?): Resource<User>
    suspend fun getMyWishList(): Resource<WishListResponse>
}