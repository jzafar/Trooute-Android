package com.example.trooute.data.repository

import com.example.trooute.core.util.Resource
import com.example.trooute.core.util.safeApiCall
import com.example.trooute.data.datasource.network.WishListAPI
import com.example.trooute.data.model.common.BaseResponse
import com.example.trooute.data.model.wishlist.WishListResponse
import com.example.trooute.domain.repository.WishListRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class WishListRepositoryImpl @Inject constructor(
    private val wishListAPI: WishListAPI,
    private val ioDispatcher: CoroutineDispatcher
) : WishListRepository {
    override suspend fun addToWishList(id: String?): Resource<BaseResponse> {
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