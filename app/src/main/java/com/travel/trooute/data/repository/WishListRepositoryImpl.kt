package com.travel.trooute.data.repository

import com.travel.trooute.core.util.Resource
import com.travel.trooute.core.util.safeApiCall
import com.travel.trooute.data.datasource.network.WishListAPI
import com.travel.trooute.data.model.auth.response.User
import com.travel.trooute.data.model.wishlist.WishListResponse
import com.travel.trooute.domain.repository.WishListRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class WishListRepositoryImpl @Inject constructor(
    private val wishListAPI: WishListAPI,
    private val ioDispatcher: CoroutineDispatcher
) : WishListRepository {
    override suspend fun addToWishList(id: String?): Resource<User> {
        return withContext(ioDispatcher) {
            safeApiCall {
                wishListAPI.addToWishList(id = id)
            }
        }
    }

    override suspend fun getMyWishList(): Resource<WishListResponse> {
        return withContext(ioDispatcher) {
            safeApiCall {
                wishListAPI.getMyWishList()
            }
        }
    }
}