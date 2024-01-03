package com.example.trooute.presentation.utils

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.trooute.R

// Help Link -> https://stackoverflow.com/questions/28531996/android-recyclerview-gridlayoutmanager-column-spacing#:~:text=In%20your%20source%20code%2C%20add,new%20ItemOffsetDecoration(context%2C%20R.
fun RecyclerView.setRVGrid(spanCount:Int) {
    layoutManager = GridLayoutManager(this.context, spanCount)
    val spacingInPixels = resources.getDimensionPixelSize(R.dimen.rv_grid_space)
    val includeEdge = false
    this.addItemDecoration(
        GridSpacingItemDecoration(
            spanCount,
            spacingInPixels,
            includeEdge
        )
    )
}

class GridSpacingItemDecoration(
    private val spanCount: Int,
    private val spacing: Int,
    private val includeEdge: Boolean
) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view) // item position
        val column = position % spanCount // item column
        if (includeEdge) {
            outRect.left =
                spacing - column * spacing / spanCount // spacing - column * ((1f / spanCount) * spacing)
            outRect.right =
                (column + 1) * spacing / spanCount // (column + 1) * ((1f / spanCount) * spacing)
            if (position < spanCount) { // top edge
                outRect.top = spacing
            }
            outRect.bottom = spacing // item bottom
        } else {
            outRect.left = column * spacing / spanCount // column * ((1f / spanCount) * spacing)
            outRect.right =
                spacing - (column + 1) * spacing / spanCount // spacing - (column + 1) * ((1f /    spanCount) * spacing)
            if (position >= spanCount) {
                outRect.top = spacing // item top
            }
        }
    }
}

fun RecyclerView.setRVVertical() {
    this.layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, false)
    val spacingInPixels = resources.getDimensionPixelSize(R.dimen.rv_Vertical_space)
    val verticalSpacingDecoration = VerticalSpacingItemDecoration(spacingInPixels)
    this.addItemDecoration(verticalSpacingDecoration)
}

class VerticalSpacingItemDecoration(
    private val spacing: Int,
    private val includeTopEdge: Boolean = true,
    private val includeBottomEdge: Boolean = true
) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view)
        if (includeTopEdge && position == 0) {
            outRect.top = spacing
        }

        if (includeBottomEdge) {
            outRect.bottom = spacing
        }
    }
}

fun RecyclerView.setRVHorizontal() {
    this.layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.HORIZONTAL, false)
    val spacingInPixels = resources.getDimensionPixelSize(R.dimen.rv_horizontal_space)
    val horizontalSpacingDecoration = HorizontalSpacingItemDecoration(spacingInPixels)
    this.addItemDecoration(horizontalSpacingDecoration)
}

class HorizontalSpacingItemDecoration(
    private val spacing: Int,
    private val includeStartEdge: Boolean = true,
    private val includeEndEdge: Boolean = true
) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view)
//        if (includeStartEdge && position == 0) {
//            outRect.left = spacing
//        }

        if (includeEndEdge) {
            outRect.right = spacing
        }

        // Skip the first item
        if (position != 0) {
            outRect.left = spacing
        }
    }
}

fun RecyclerView.setRVOverlayHorizontal() {
    this.layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.HORIZONTAL, false)
    val spacingInPixels = resources.getDimensionPixelSize(R.dimen.rv_minus_horizontal_space)
    val horizontalOverlaySpacingItemDecoration = HorizontalOverlaySpacingItemDecoration(spacingInPixels)
    this.addItemDecoration(horizontalOverlaySpacingItemDecoration)
}

class HorizontalOverlaySpacingItemDecoration(
    private val spacing: Int,
    private val includeStartEdge: Boolean = true,
    private val includeEndEdge: Boolean = true
) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view)
//        if (includeStartEdge && position == 0) {
//            outRect.left = spacing
//        }

//        if (includeEndEdge) {
//            outRect.right = spacing
//        }

        // Skip the first item
        if (position != 0) {
            outRect.left = spacing
        }
    }
}