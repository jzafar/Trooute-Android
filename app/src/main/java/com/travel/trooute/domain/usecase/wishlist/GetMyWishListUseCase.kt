package com.travel.trooute.domain.usecase.wishlist

import com.travel.trooute.core.util.Resource
import com.travel.trooute.data.model.wishlist.WishListResponse
import com.travel.trooute.domain.repository.WishListRepository
import javax.inject.Inject

class GetMyWishListUseCase @Inject constructor(private val wishListRepository: WishListRepository) {
    suspend operator fun invoke(): Resource<WishListResponse> {
        return wishListRepository.getMyWishList()
    }
}