package com.travel.trooute.presentation.ui.wishlist

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.travel.trooute.R
import com.travel.trooute.core.util.Constants
import com.travel.trooute.core.util.Resource
import com.travel.trooute.data.model.wishlist.Message
import com.travel.trooute.databinding.ActivityWishListBinding
import com.travel.trooute.presentation.adapters.WishListAdapter
import com.travel.trooute.presentation.interfaces.AdapterItemClickListener
import com.travel.trooute.presentation.interfaces.WishListEventListener
import com.travel.trooute.presentation.ui.trip.TripDetailActivity
import com.travel.trooute.presentation.utils.WindowsManager.statusBarColor
import com.travel.trooute.presentation.utils.setRVVertical
import com.travel.trooute.presentation.utils.showSuccessMessage
import com.travel.trooute.presentation.viewmodel.wishlistviewmodel.AddToWishListViewModel
import com.travel.trooute.presentation.viewmodel.wishlistviewmodel.GetMyWishListViewModel
import com.faltenreich.skeletonlayout.Skeleton
import com.faltenreich.skeletonlayout.applySkeleton
import com.travel.trooute.presentation.ui.BaseActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class WishListActivity : BaseActivity(), WishListEventListener, AdapterItemClickListener {

    private val TAG = "WishListActivity"

    private lateinit var binding: ActivityWishListBinding
    private lateinit var wishListAdapter: WishListAdapter
    private lateinit var rvSkeleton: Skeleton

    private var wishList: MutableList<Message> = mutableListOf()

    private val getWishListViewModel: GetMyWishListViewModel by viewModels()
    private val addToWishListViewModel: AddToWishListViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        statusBarColor(R.color.white)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_wish_list)
        wishListAdapter = WishListAdapter(this, this)

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

                            if (it.data.data?.isEmpty() == true) {
                                binding.rvWishList.isVisible = false
                                binding.tvNoDataAvailable.isVisible = true
                            } else {
                                binding.rvWishList.isVisible = true
                                binding.tvNoDataAvailable.isVisible = false
                                it.data.data?.let { data ->
                                    wishList = data.toMutableList()
                                    Log.e(TAG, "bindGetWishListObservers: wishList -> $wishList")
                                    wishListAdapter.submitList(data)
                                }
                            }

                            rvSkeleton.showOriginal()
                        }
                    }
                }
            }
        }
    }

    override fun onWishListEventClick(position: Int, data: Any, added: Boolean) {
        if (data is Message) {
            addToWishListViewModel.addToWishList(data._id)
            binAddToWishListObserver(added)
//            if (wishList.isNotEmpty()) {
//                wishList.removeAt(position)
//                wishListAdapter.submitList(wishList)
//                if (wishListAdapter.itemCount == 0) {
//                    binding.rvWishList.isVisible = false
//                    binding.tvNoDataAvailable.isVisible = true
//                } else {
//                    binding.rvWishList.isVisible = true
//                    binding.tvNoDataAvailable.isVisible = false
//                }
//                addToWishListViewModel.addToWishList(data._id)
//                binAddToWishListObserver(added)
//            }
        }
    }

    override fun onAdapterItemClicked(position: Int, data: Any) {
        if (data is Message) {
            startActivity(Intent(this, TripDetailActivity::class.java).apply {
                putExtra(Constants.TRIP_ID, data._id)
            })
        }
    }

    private fun binAddToWishListObserver(added: Boolean) {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                addToWishListViewModel.addToWishListState.collect {
                    when (it) {
                        is Resource.ERROR -> {
                            Log.i(
                                TAG,
                                "binAddToWishListObserver: error -> " + it.message.toString()
                            )
                        }

                        Resource.LOADING -> {

                        }

                        is Resource.SUCCESS -> {
                            Log.i(TAG, "binAddToWishListObserver: success -> " + it.data)
                            if (added) {
                                Toast(this@WishListActivity).showSuccessMessage(
                                    this@WishListActivity,
                                    getString(R.string.wish_list_added)
                                )
                            } else {
                                Toast(this@WishListActivity).showSuccessMessage(
                                    this@WishListActivity,
                                    getString(R.string.wish_list_removed)
                                )
                                getWishListViewModel.getMyWishList()
                            }
                        }
                    }
                }
            }
        }
    }
}