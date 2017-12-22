package com.schibsted.remotedisplaysample;

import android.content.Context;
import android.os.Bundle;
import android.view.Display;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.cast.CastPresentation;
import com.google.android.gms.cast.CastRemoteDisplayLocalService;

public class PresentationService extends CastRemoteDisplayLocalService {
  private DetailPresentation castPresentation;
  private AdViewModel adViewModel;

  @Override
  public void onCreatePresentation(Display display) {
    dismissPresentation();
    castPresentation = new DetailPresentation(this, display);

    try {
      castPresentation.show();
    } catch (WindowManager.InvalidDisplayException ex) {
      dismissPresentation();
    }
  }

  @Override
  public void onDismissPresentation() {
    dismissPresentation();
    adViewModel = null;
  }

  private void dismissPresentation() {
    if (castPresentation != null) {
      castPresentation.dismiss();
      castPresentation = null;
    }
  }

  public void setAdViewModel(AdViewModel ad) {
    adViewModel = ad;
    if (castPresentation != null) {
      castPresentation.updateAdDetail(ad);
    }
  }

  public class DetailPresentation extends CastPresentation {
    @BindView(R.id.ad_title) public TextView title;
    @BindView(R.id.ad_price) public TextView price;
    @BindView(R.id.ad_image) public ImageView image;

    public DetailPresentation(Context context, Display display) {
      super(context, display);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.presentation_detail);
      ButterKnife.bind(this);
      updateAdDetail(adViewModel);
    }

    public void updateAdDetail(AdViewModel adViewModel) {
      title.setText(adViewModel.getTitle());
      price.setText(adViewModel.getPrice());
      if (!adViewModel.getImage().isEmpty()) {
        RequestOptions options = new RequestOptions();
        options.centerCrop();
        Glide.with(getContext())
            .load(adViewModel.getImage())
            .apply(options)
            .into(image);
      }
    }

    @Override
    public void onDetachedFromWindow() {
      super.onDetachedFromWindow();
    }
  }
}