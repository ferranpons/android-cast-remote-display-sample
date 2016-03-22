package com.schibsted.remotedisplaysample;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.bumptech.glide.Glide;

public class DetailFragment extends Fragment {
  private static final String ARG_ID = "id";
  private static final String ARG_TITLE = "title";
  private static final String ARG_PRICE = "price";
  private static final String ARG_IMAGE = "image";
  private AdViewModel adViewModel;

  @Bind(R.id.ad_title) TextView title;
  @Bind(R.id.ad_price) TextView price;
  @Bind(R.id.ad_image) ImageView image;

  public static DetailFragment newInstance(AdViewModel ad) {
    DetailFragment fragment = new DetailFragment();
    Bundle args = new Bundle();
    args.putString(ARG_ID, ad.getId());
    args.putString(ARG_TITLE, ad.getTitle());
    args.putString(ARG_PRICE, ad.getPrice());
    args.putString(ARG_IMAGE, ad.getImage());
    fragment.setArguments(args);
    return fragment;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setHasOptionsMenu(true);
    getViewArguments();
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View fragmentView = inflater.inflate(R.layout.fragment_detail, container, false);
    ButterKnife.bind(this, fragmentView);
    title.setText(adViewModel.getTitle());
    price.setText(adViewModel.getPrice());
    if (!adViewModel.getImage().isEmpty()) {
      Glide.with(this)
          .load(adViewModel.getImage())
          .centerCrop()
          .placeholder(R.drawable.ic_cast_grey)
          .crossFade()
          .into(image);
    }
    return fragmentView;
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    ButterKnife.unbind(this);
  }

  private void getViewArguments() {
    if (getArguments() != null) {
      adViewModel =
          new AdViewModel(getArguments().getString(ARG_ID), getArguments().getString(ARG_TITLE),
              getArguments().getString(ARG_PRICE), getArguments().getString(ARG_IMAGE));
    }
  }
}
