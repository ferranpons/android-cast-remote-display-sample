package com.schibsted.remotedisplaysample

import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import androidx.appcompat.app.AppCompatActivity
import androidx.mediarouter.media.MediaRouteSelector
import androidx.mediarouter.media.MediaRouter
import android.view.Menu
import android.widget.Toast
import com.google.android.gms.cast.CastDevice
import com.google.android.gms.cast.CastMediaControlIntent
import com.google.android.gms.cast.CastRemoteDisplayLocalService
import com.google.android.gms.cast.framework.CastButtonFactory
import com.google.android.gms.common.api.Status
import java.util.ArrayList

class MainActivity : AppCompatActivity(), CustomSimpleOnPageChangeListener.OnPageChangePosition {
    private var currentPosition: Int = 0
    private var fragmentStatePagerAdapter: ScreenSlidePagerAdapter? = null
    private var mediaRouter: MediaRouter? = null
    private var castDevice: CastDevice? = null

    private val isRemoteDisplaying: Boolean
        get() = CastRemoteDisplayLocalService.getInstance() != null

    private val mMediaRouterCallback = object : MediaRouter.Callback() {
        override fun onRouteSelected(router: MediaRouter?, info: MediaRouter.RouteInfo) {
            castDevice = CastDevice.getFromBundle(info.extras)
            Toast.makeText(applicationContext,
                    getString(R.string.cast_connected_to) + info.name, Toast.LENGTH_LONG).show()
            startCastService(castDevice)
        }

        override fun onRouteUnselected(router: MediaRouter?, info: MediaRouter.RouteInfo?) {
            if (isRemoteDisplaying) {
                CastRemoteDisplayLocalService.stopService()
            }
            castDevice = null
        }
    }

    private val adViewModels: List<AdViewModel>
        get() {
            val adViewModel1 = AdViewModel("0", "Solid Strike", "$3.333",
                    "https://coresites-cdn.factorymedia.com/dirt_new/wp-content/uploads/2015/06/solid-strike.jpg")
            val adViewModel2 = AdViewModel("1", "YT Industries Tues", "$2.799",
                    "https://coresites-cdn.factorymedia.com/dirt_new/wp-content/uploads/2015/06/yt-tues.jpg")
            val adViewModel3 = AdViewModel("2", "Transition TR450", "$3.750",
                    "https://descensonuevoleon.files.wordpress.com/2009/08/tr450_weblarge3.jpg")
            val adViewModel4 = AdViewModel("3", "Lapierre DH", "$5.199",
                    "https://coresites-cdn.factorymedia.com/dirt_new/wp-content/uploads/2015/06/lapierre-dh.jpg")
            val adViewModel5 = AdViewModel("4", "Specialized Demo", "$7.000",
                    "https://coresites-cdn.factorymedia.com/dirt_new/wp-content/uploads/2015/06/specialized-demo.jpg")
            val adViewModel6 = AdViewModel("5", "Trek Session 9.9", "$7.000",
                    "https://coresites-cdn.factorymedia.com/dirt_new/wp-content/uploads/2015/06/Trek-session-9.9.jpg")
            val adViewModel7 = AdViewModel("6", "Mondraker Summum Pro Team", "$5.799",
                    "https://coresites-cdn.factorymedia.com/dirt_new/wp-content/uploads/2015/06/mondraker-summum-pro-team.jpg")
            val adViewModel8 = AdViewModel("7", "Intense 951 EVO", "$5.499",
                    "https://coresites-cdn.factorymedia.com/dirt_new/wp-content/uploads/2015/06/intense-951-evo.jpg")
            val adViewModel9 = AdViewModel("8", "Giant Glory", "$4.749",
                    "https://coresites-cdn.factorymedia.com/dirt_new/wp-content/uploads/2015/06/giant-glory.jpg")

            val list = ArrayList<AdViewModel>()
            list.add(adViewModel1)
            list.add(adViewModel2)
            list.add(adViewModel3)
            list.add(adViewModel4)
            list.add(adViewModel5)
            list.add(adViewModel6)
            list.add(adViewModel7)
            list.add(adViewModel8)
            list.add(adViewModel9)
            return list
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val list = adViewModels

        val viewPager = findViewById<ViewPager>(R.id.pager)
        fragmentStatePagerAdapter = ScreenSlidePagerAdapter(supportFragmentManager)
        fragmentStatePagerAdapter?.addAds(list)
        val customSimpleOnPageChangeListener = CustomSimpleOnPageChangeListener(this)
        if (viewPager != null) {
            viewPager.adapter = fragmentStatePagerAdapter
            viewPager.addOnPageChangeListener(customSimpleOnPageChangeListener)
        }

        setupMediaRouter()
    }

    override fun onResume() {
        super.onResume()
        if (!isRemoteDisplaying) {
            if (castDevice != null) {
                startCastService(castDevice)
            }
        }
    }

    public override fun onDestroy() {
        if (mediaRouter != null) {
            mediaRouter!!.removeCallback(mMediaRouterCallback)
        }
        super.onDestroy()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.activity_main_actions, menu)
        CastButtonFactory.setUpMediaRouteButton(applicationContext, menu, R.id.action_cast)
        return true
    }

    private fun setupMediaRouter() {
        mediaRouter = MediaRouter.getInstance(applicationContext)
        val mediaRouteSelector = MediaRouteSelector.Builder().addControlCategory(
                CastMediaControlIntent.categoryForCast(getString(R.string.app_cast_id))).build()
        if (isRemoteDisplaying) {
            this.castDevice = CastDevice.getFromBundle(mediaRouter!!.selectedRoute.extras)
        } else {
            val extras = intent.extras
            if (extras != null) {
                castDevice = extras.getParcelable(INTENT_EXTRA_CAST_DEVICE)
            }
        }

        mediaRouter!!.addCallback(mediaRouteSelector, mMediaRouterCallback,
                MediaRouter.CALLBACK_FLAG_REQUEST_DISCOVERY)
    }

    private fun startCastService(castDevice: CastDevice?) {
        val intent = Intent(this@MainActivity, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        val notificationPendingIntent = PendingIntent.getActivity(this@MainActivity, 0, intent, 0)

        val settings = CastRemoteDisplayLocalService.NotificationSettings.Builder().setNotificationPendingIntent(
                notificationPendingIntent).build()

        CastRemoteDisplayLocalService.startService(this@MainActivity, PresentationService::class.java,
                getString(R.string.app_cast_id), castDevice!!, settings,
                object : CastRemoteDisplayLocalService.Callbacks {
                    override fun onServiceCreated(service: CastRemoteDisplayLocalService) {
                        (service as PresentationService).setAdViewModel(
                                fragmentStatePagerAdapter!!.getAdAt(currentPosition))
                    }

                    override fun onRemoteDisplaySessionStarted(service: CastRemoteDisplayLocalService) {}

                    override fun onRemoteDisplaySessionError(errorReason: Status) {
                        initError()

                        this@MainActivity.castDevice = null
                        this@MainActivity.finish()
                    }

                    override fun onRemoteDisplaySessionEnded(castRemoteDisplayLocalService: CastRemoteDisplayLocalService) {}
                })
    }

    private fun initError() {
        val toast = Toast.makeText(applicationContext, R.string.toast_connection_error,
                Toast.LENGTH_SHORT)
        mediaRouter?.let {
            it.selectRoute(it.defaultRoute)
        }
        toast.show()
    }

    override fun onCurrentPageChange(position: Int) {
        currentPosition = position
        fragmentStatePagerAdapter?.let {
            if (CastRemoteDisplayLocalService.getInstance() != null) {
                (CastRemoteDisplayLocalService.getInstance() as PresentationService).setAdViewModel(it.getAdAt(position))
            }
        }
    }

    private inner class ScreenSlidePagerAdapter internal constructor(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {
        private val ads = ArrayList<AdViewModel>()

        override fun getItem(position: Int): Fragment {
            return DetailFragment.newInstance(ads[position])
        }

        override fun getCount(): Int {
            return ads.size
        }

        internal fun addAds(ads: List<AdViewModel>) {
            this.ads.addAll(ads)
            notifyDataSetChanged()
        }

        fun getAdAt(position: Int): AdViewModel {
            return ads[position]
        }
    }

    companion object {
        private const val INTENT_EXTRA_CAST_DEVICE = "CastDevice"
    }
}
