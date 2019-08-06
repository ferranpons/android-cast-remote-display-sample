package com.schibsted.remotedisplaysample

import android.content.Context
import android.os.Bundle
import android.view.Display
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.gms.cast.CastPresentation
import com.google.android.gms.cast.CastRemoteDisplayLocalService

class PresentationService : CastRemoteDisplayLocalService() {
    private var castPresentation: DetailPresentation? = null
    private var adViewModel: AdViewModel? = null

    override fun onCreatePresentation(display: Display) {
        dismissPresentation()
        castPresentation = DetailPresentation(this, display)

        try {
            castPresentation!!.show()
        } catch (ex: WindowManager.InvalidDisplayException) {
            dismissPresentation()
        }

    }

    override fun onDismissPresentation() {
        dismissPresentation()
        adViewModel = null
    }

    private fun dismissPresentation() {
        if (castPresentation != null) {
            castPresentation!!.dismiss()
            castPresentation = null
        }
    }

    fun setAdViewModel(ad: AdViewModel) {
        adViewModel = ad
        if (castPresentation != null) {
            castPresentation!!.updateAdDetail(ad)
        }
    }

    inner class DetailPresentation(context: Context, display: Display) : CastPresentation(context, display) {
        private lateinit var title: TextView
        private lateinit var price: TextView
        private lateinit var image: ImageView

        override fun onCreate(savedInstanceState: Bundle) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.presentation_detail)
            title = findViewById(R.id.ad_title)
            price = findViewById(R.id.ad_price)
            image = findViewById(R.id.ad_image)

            updateAdDetail(adViewModel)
        }

        fun updateAdDetail(adViewModel: AdViewModel?) {
            title.text = adViewModel!!.title
            price.text = adViewModel.price
            if (!adViewModel.image.isEmpty()) {
                val options = RequestOptions()
                options.centerCrop()
                Glide.with(context)
                        .load(adViewModel.image)
                        .apply(options)
                        .into(image)
            }
        }
    }
}