package com.schibsted.remotedisplaysample

import androidx.viewpager.widget.ViewPager

class CustomSimpleOnPageChangeListener(private val onPageChangePosition: OnPageChangePosition) : ViewPager.SimpleOnPageChangeListener() {

    override fun onPageSelected(position: Int) {
        super.onPageSelected(position)
        onPageChangePosition.onCurrentPageChange(position)
    }

    interface OnPageChangePosition {
        fun onCurrentPageChange(position: Int)
    }
}
