package com.schibsted.remotedisplaysample;

import android.support.v4.view.ViewPager;

public class CustomSimpleOnPageChangeListener extends ViewPager.SimpleOnPageChangeListener {
  OnPageChangePosition onPageChangePosition;

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
