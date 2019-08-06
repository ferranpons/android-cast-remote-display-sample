package com.schibsted.remotedisplaysample;

import androidx.viewpager.widget.ViewPager;

public class CustomSimpleOnPageChangeListener extends ViewPager.SimpleOnPageChangeListener {
  private OnPageChangePosition onPageChangePosition;

  public CustomSimpleOnPageChangeListener(OnPageChangePosition onPageChangePosition) {
    this.onPageChangePosition = onPageChangePosition;
  }

  @Override
  public void onPageSelected(int position) {
    super.onPageSelected(position);
    onPageChangePosition.onCurrentPageChange(position);
  }

  interface OnPageChangePosition {
    void onCurrentPageChange(int position);
  }
}
