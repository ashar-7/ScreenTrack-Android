package com.se7en.screentrack.ui

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class AppsListItemDecoration: RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view)
        if (position == 0 || position == state.itemCount)
            with(outRect) {
                left = 8
                right = 8
            }
        else
            with(outRect) {
                left = 8
                right = 8
                top = 1
                bottom = 1
            }
    }
}
