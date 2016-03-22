package com.schibsted.remotedisplaysample;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.MediaRouteActionProvider;
import android.support.v7.media.MediaRouteSelector;
import android.support.v7.media.MediaRouter;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;
import com.google.android.gms.cast.CastDevice;
import com.google.android.gms.cast.CastMediaControlIntent;
import com.google.android.gms.cast.CastRemoteDisplayLocalService;
import com.google.android.gms.common.api.Status;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements CustomSimpleOnPageChangeListener.OnPageChangePosition {
  protected static final String INTENT_EXTRA_CAST_DEVICE = "CastDevice";
  private int currentPosition;
  private ScreenSlidePagerAdapter fragmentStatePagerAdapter;
  private MediaRouter mediaRouter;
  private MediaRouteSelector mediaRouteSelector;
  private CastDevice castDevice;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    List<AdViewModel> list = getAdViewModels();

    ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
    fragmentStatePagerAdapter =
        new ScreenSlidePagerAdapter(getSupportFragmentManager());
    fragmentStatePagerAdapter.addAds(list);
    CustomSimpleOnPageChangeListener customSimpleOnPageChangeListener =
        new CustomSimpleOnPageChangeListener(this);
    if (viewPager != null) {
      viewPager.setAdapter(fragmentStatePagerAdapter);
      viewPager.addOnPageChangeListener(customSimpleOnPageChangeListener);
    }

    setupMediaRouter();
  }

  @NonNull
  private List<AdViewModel> getAdViewModels() {
    AdViewModel adViewModel1 = new AdViewModel("0", "Solid Strike", "$3.333",
        "https://coresites-cdn.factorymedia.com/dirt_new/wp-content/uploads/2015/06/solid-strike.jpg");
    AdViewModel adViewModel2 = new AdViewModel("1", "YT Industries Tues", "$2.799",
        "https://coresites-cdn.factorymedia.com/dirt_new/wp-content/uploads/2015/06/yt-tues.jpg");
    AdViewModel adViewModel3 = new AdViewModel("2", "Transition TR450", "$3.750",
        "https://descensonuevoleon.files.wordpress.com/2009/08/tr450_weblarge3.jpg");
    AdViewModel adViewModel4 = new AdViewModel("3", "Lapierre DH", "$5.199",
        "https://coresites-cdn.factorymedia.com/dirt_new/wp-content/uploads/2015/06/lapierre-dh.jpg");
    AdViewModel adViewModel5 = new AdViewModel("4", "Specialized Demo", "$7.000",
        "https://coresites-cdn.factorymedia.com/dirt_new/wp-content/uploads/2015/06/specialized-demo.jpg");
    AdViewModel adViewModel6 = new AdViewModel("5", "Trek Session 9.9", "$7.000",
        "https://coresites-cdn.factorymedia.com/dirt_new/wp-content/uploads/2015/06/Trek-session-9.9.jpg");
    AdViewModel adViewModel7 = new AdViewModel("6", "Mondraker Summum Pro Team", "$5.799",
        "https://coresites-cdn.factorymedia.com/dirt_new/wp-content/uploads/2015/06/mondraker-summum-pro-team.jpg");
    AdViewModel adViewModel8 = new AdViewModel("7", "Intense 951 EVO", "$5.499",
        "https://coresites-cdn.factorymedia.com/dirt_new/wp-content/uploads/2015/06/intense-951-evo.jpg");
    AdViewModel adViewModel9 = new AdViewModel("8", "Giant Glory", "$4.749",
        "https://coresites-cdn.factorymedia.com/dirt_new/wp-content/uploads/2015/06/giant-glory.jpg");

    List<AdViewModel> list = new ArrayList<>();
    list.add(adViewModel1);
    list.add(adViewModel2);
    list.add(adViewModel3);
    list.add(adViewModel4);
    list.add(adViewModel5);
    list.add(adViewModel6);
    list.add(adViewModel7);
    list.add(adViewModel8);
    list.add(adViewModel9);
    return list;
  }

  @Override
  protected void onResume() {
    super.onResume();
    if (!isRemoteDisplaying()) {
      if (castDevice != null) {
        startCastService(castDevice);
      }
    }
  }

  @Override
  public void onDestroy() {
    if (mediaRouter != null) {
      mediaRouter.removeCallback(mMediaRouterCallback);
    }
    super.onDestroy();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.activity_main_actions, menu);

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && mediaRouteSelector != null) {
      MenuItem mediaRouteMenuItem = menu.findItem(R.id.action_cast);
      if (MenuItemCompat.getActionProvider(
          mediaRouteMenuItem) instanceof MediaRouteActionProvider) {
        MediaRouteActionProvider mediaRouteActionProvider =
            (MediaRouteActionProvider) MenuItemCompat.getActionProvider(mediaRouteMenuItem);
        mediaRouteActionProvider.setRouteSelector(mediaRouteSelector);
      }
    }
    return true;
  }

  private void setupMediaRouter() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
      mediaRouter = MediaRouter.getInstance(getApplicationContext());
      mediaRouteSelector = new MediaRouteSelector.Builder().addControlCategory(
          CastMediaControlIntent.categoryForCast(getString(R.string.app_cast_id))).build();
      if (isRemoteDisplaying()) {
        this.castDevice = CastDevice.getFromBundle(mediaRouter.getSelectedRoute().getExtras());
      } else {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
          castDevice = extras.getParcelable(INTENT_EXTRA_CAST_DEVICE);
        }
      }

      mediaRouter.addCallback(mediaRouteSelector, mMediaRouterCallback,
          MediaRouter.CALLBACK_FLAG_REQUEST_DISCOVERY);
    }
  }

  private boolean isRemoteDisplaying() {
    return CastRemoteDisplayLocalService.getInstance() != null;
  }

  private final MediaRouter.Callback mMediaRouterCallback = new MediaRouter.Callback() {
    @Override
    public void onRouteSelected(MediaRouter router, MediaRouter.RouteInfo info) {
      castDevice = CastDevice.getFromBundle(info.getExtras());
      Toast.makeText(getApplicationContext(),
          getString(R.string.cast_connected_to) + info.getName(), Toast.LENGTH_LONG).show();
      startCastService(castDevice);
    }

    @Override
    public void onRouteUnselected(MediaRouter router, MediaRouter.RouteInfo info) {
      if (isRemoteDisplaying()) {
        CastRemoteDisplayLocalService.stopService();
      }
      castDevice = null;
    }
  };

  private void startCastService(CastDevice castDevice) {
    Intent intent = new Intent(MainActivity.this, MainActivity.class);
    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
    PendingIntent notificationPendingIntent =
        PendingIntent.getActivity(MainActivity.this, 0, intent, 0);

    CastRemoteDisplayLocalService.NotificationSettings settings =
        new CastRemoteDisplayLocalService.NotificationSettings.Builder().setNotificationPendingIntent(
            notificationPendingIntent).build();

    CastRemoteDisplayLocalService.startService(MainActivity.this, PresentationService.class,
        getString(R.string.app_cast_id), castDevice, settings,
        new CastRemoteDisplayLocalService.Callbacks() {
          @Override
          public void onServiceCreated(CastRemoteDisplayLocalService service) {
            ((PresentationService) service).setAdViewModel(
                fragmentStatePagerAdapter.getAdAt(currentPosition));
          }

          @Override
          public void onRemoteDisplaySessionStarted(CastRemoteDisplayLocalService service) {
          }

          @Override
          public void onRemoteDisplaySessionError(Status errorReason) {
            initError();

            MainActivity.this.castDevice = null;
            MainActivity.this.finish();
          }
        });
  }

  private void initError() {
    Toast toast = Toast.makeText(getApplicationContext(), R.string.toast_connection_error,
        Toast.LENGTH_SHORT);
    if (mediaRouter != null) {
      mediaRouter.selectRoute(mediaRouter.getDefaultRoute());
    }
    toast.show();
  }

  @Override
  public void onCurrentPageChange(int position) {
    currentPosition = position;
    if (CastRemoteDisplayLocalService.getInstance() != null) {
      ((PresentationService) CastRemoteDisplayLocalService.getInstance()).setAdViewModel(
          fragmentStatePagerAdapter.getAdAt(position));
    }
  }

  private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
    private final List<AdViewModel> ads = new ArrayList<>();

    ScreenSlidePagerAdapter(FragmentManager fm) {
      super(fm);
    }

    @Override
    public Fragment getItem(int position) {
      return DetailFragment.newInstance(ads.get(position));
    }

    @Override
    public int getCount() {
      return ads.size();
    }

    public void addAds(List<AdViewModel> ads) {
      this.ads.addAll(ads);
      notifyDataSetChanged();
    }

    public AdViewModel getAdAt(int position) {
      return ads.get(position);
    }
  }
}
