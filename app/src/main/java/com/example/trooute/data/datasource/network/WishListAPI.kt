package com.example.trooute.data.datasource.network

import com.example.trooute.core.util.URL.ADD_TO_WISH_LIST
import com.example.trooute.core.util.URL.GET_MY_WISH_LIST
import com.example.trooute.data.model.auth.response.User
import com.example.trooute.data.model.common.BaseResponse
import com.example.trooute.data.model.wishlist.WishListResponse
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