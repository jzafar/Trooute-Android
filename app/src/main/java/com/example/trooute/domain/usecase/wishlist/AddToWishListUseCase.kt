package com.example.trooute.domain.usecase.wishlist

import com.example.trooute.core.util.Resource
import com.example.trooute.data.model.auth.response.User
import com.example.trooute.data.model.common.BaseResponse
import com.example.trooute.data.model.wishlist.WishListResponse
import com.example.trooute.domain.repository.WishListRepository
import javax.inject.Inject

class AddToWishListUseCase @Inject constructor(private val wishListRepository: WishListRepository) {
    suspend operator fun invoke(id: String?): Resource<User> {
        return wishListRepository.addToWishList(id = id)
    }
}