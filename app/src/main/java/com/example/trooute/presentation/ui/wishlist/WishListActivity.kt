package com.example.trooute.presentation.ui.wishlist

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.trooute.R
import com.example.trooute.core.util.Resource
import com.example.trooute.data.model.wishlist.Message
import com.example.trooute.databinding.ActivityWishListBinding
import com.example.trooute.presentation.adapters.WishListAdapter
import com.example.trooute.presentation.interfaces.WishListEventListener
import com.example.trooute.presentation.utils.setRVVertical
import com.example.trooute.presentation.viewmodel.wishlistviewmodel.AddToWishListViewModel
import com.example.trooute.presentation.viewmodel.wishlistviewmodel.GetMyWishListViewModel
import com.faltenreich.skeletonlayout.Skeleton
import com.faltenreich.skeletonlayout.applySkeleton
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class WishListActivity : AppCompatActivity(), WishListEventListener {

    private val TAG = "WishListActivity"

    private lateinit var binding: ActivityWishListBinding
    private lateinit var wishListAdapter: WishListAdapter
    private lateinit var rvSkeleton: Skeleton

    private var wishList: MutableList<Message> = mutableListOf()

    private val getWishListViewModel: GetMyWishListViewModel by viewModels()
    private val addToWishListViewModel: AddToWishListViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_wish_list)
        wishListAdapter = WishListAdapter(this)

        binding.apply {
            includeAppBar.apply {
                this.toolbarTitle.text = intent.getStringExtra("ToolBarTitle")
                this.filter.isVisible = false

                this.arrowBackPress.setOnClickListener {
                    finish()
                }
            }

            rvWishList.apply {
                setRVVertical()
                adapter = wishListAdapter
                rvSkeleton = this.applySkeleton(R.layout.rv_trips_item)
                rvSkeleton.showSkeleton()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        getWishListViewModel.getMyWishList()
        bindGetWishListObservers()
    }

    @SuppressLint("RepeatOnLifecycleWrongUsage")
    private fun bindGetWishListObservers() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                getWishListViewModel.getMyWishListState.collect {
                    when (it) {
                        is Resource.ERROR -> {
                            rvSkeleton.showOriginal()
                            Log.e(
                                TAG,
                                "bindGetWishListObservers: Error -> " + it.message.toString()
                            )
                        }

                        Resource.LOADING -> {

                        }

                        is Resource.SUCCESS -> {
                            Log.e(TAG, "bindGetWishListObservers: Success -> " + it.data)

                            if (it.data.message?.isEmpty() == true) {
                                binding.rvWishList.isVisible = false
                                binding.tvNoDataAvailable.isVisible = true
                            } else {
                                binding.rvWishList.isVisible = true
                                binding.tvNoDataAvailable.isVisible = false
                                it.data.message?.let { message ->
                                    wishList = message.toMutableList()
                                    Log.e(TAG, "bindGetWishListObservers: wishList -> $wishList")
                                    wishListAdapter.submitList(message)
                                }
                            }

                            rvSkeleton.showOriginal()
                        }
                    }
                }
            }
        }
    }

    override fun onWishListEventClick(position: Int, data: Any) {
        if (data is Message) {
            if (wishList.isNotEmpty()) {
                wishList.removeAt(position)
                wishListAdapter.submitList(wishList)
                if (wishListAdapter.itemCount == 0) {
                    binding.rvWishList.isVisible = false
                    binding.tvNoDataAvailable.isVisible = true
                } else {
                    binding.rvWishList.isVisible = true
                    binding.tvNoDataAvailable.isVisible = false
                }
                addToWishListViewModel.addToWishList(data._id)
                binAddToWishListObserver()
            }
        }
    }

    private fun binAddToWishListObserver() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                addToWishListViewModel.addToWishListState.collect {
                    when (it) {
                        is Resource.ERROR -> {
                            Log.e(
                                TAG,
                                "binAddToWishListObserver: error -> " + it.message.toString()
                            )
                        }

                        Resource.LOADING -> {

                        }

                        is Resource.SUCCESS -> {
                            Log.e(TAG, "binAddToWishListObserver: success -> " + it.data)
                        }
                    }
                }
            }
        }
    }
}