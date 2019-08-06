package com.schibsted.remotedisplaysample;

import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.Objects;

public class DetailFragment extends Fragment {
  private static final String ARG_ID = "id";
  private static final String ARG_TITLE = "title";
  private static final String ARG_PRICE = "price";
  private static final String ARG_IMAGE = "image";
  private AdViewModel adViewModel;

  private TextView title;
  private TextView price;
  private ImageView image;

  static DetailFragment newInstance(AdViewModel ad) {
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
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View fragmentView = inflater.inflate(R.layout.fragment_detail, container, false);
    title = fragmentView.findViewById(R.id.ad_title);
    price = fragmentView.findViewById(R.id.ad_price);
    image = fragmentView.findViewById(R.id.ad_image);
    return fragmentView;
  }

  @SuppressLint("CheckResult")
  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    adViewModel.getTitle();
    title.setText(adViewModel.getTitle());
    adViewModel.getPrice();
    price.setText(adViewModel.getPrice());
    if (!adViewModel.getImage().isEmpty()) {
      RequestOptions options = new RequestOptions();
      options.centerCrop();
      Glide.with(this)
          .load(adViewModel.getImage())
          .apply(options)
          .into(image);
    }
  }

  private void getViewArguments() {
    if (getArguments() != null) {
      adViewModel =
          new AdViewModel(
                  Objects.requireNonNull(getArguments().getString(ARG_ID)),
                  Objects.requireNonNull(getArguments().getString(ARG_TITLE)),
                  Objects.requireNonNull(getArguments().getString(ARG_PRICE)),
                  Objects.requireNonNull(getArguments().getString(ARG_IMAGE))
          );
    }
  }
}
