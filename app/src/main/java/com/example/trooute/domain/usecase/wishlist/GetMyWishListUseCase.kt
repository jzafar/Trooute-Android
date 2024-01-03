package com.example.trooute.domain.usecase.wishlist

import com.example.trooute.core.util.Resource
import com.example.trooute.data.model.wishlist.WishListResponse
import com.example.trooute.domain.repository.WishListRepository
import javax.inject.Inject

class GetMyWishListUseCase @Inject constructor(private val wishListRepository: WishListRepository) {
    suspend operator fun invoke(): Resource<WishListResponse> {
        return wishListRepository.getMyWishList()
    }
}