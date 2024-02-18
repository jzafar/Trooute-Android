package com.travel.trooute.data.datasource.network

import com.travel.trooute.core.util.URL.ADD_TO_WISH_LIST
import com.travel.trooute.core.util.URL.GET_MY_WISH_LIST
import com.travel.trooute.data.model.auth.response.User
import com.travel.trooute.data.model.wishlist.WishListResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface WishListAPI {
    @POST("$ADD_TO_WISH_LIST/{id}/add-to-wish-list")
    suspend fun addToWishList(@Path("id") id: String?): Response<User>

    @GET(GET_MY_WISH_LIST)
    suspend fun getMyWishList(): Response<WishListResponse>
}