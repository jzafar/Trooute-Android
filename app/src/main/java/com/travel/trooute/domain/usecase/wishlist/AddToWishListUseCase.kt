package com.travel.trooute.domain.usecase.wishlist

import com.travel.trooute.core.util.Resource
import com.travel.trooute.data.model.auth.response.User
import com.travel.trooute.domain.repository.WishListRepository
import javax.inject.Inject

class AddToWishListUseCase @Inject constructor(private val wishListRepository: WishListRepository) {
    suspend operator fun invoke(id: String?): Resource<User> {
        return wishListRepository.addToWishList(id = id)
    }
}