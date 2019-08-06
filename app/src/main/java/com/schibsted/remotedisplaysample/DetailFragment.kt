package com.schibsted.remotedisplaysample

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

class DetailFragment : Fragment() {
    private var adViewModel: AdViewModel? = null

    private var title: TextView? = null
    private var price: TextView? = null
    private var image: ImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        getViewArguments()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val fragmentView = inflater.inflate(R.layout.fragment_detail, container, false)
        title = fragmentView.findViewById(R.id.ad_title)
        price = fragmentView.findViewById(R.id.ad_price)
        image = fragmentView.findViewById(R.id.ad_image)
        return fragmentView
    }

    @SuppressLint("CheckResult")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adViewModel?.let {

            title?.text = it.title
            price?.text = it.price
            it.image?.let { imageUrl ->
                if (!imageUrl.isNullOrEmpty()) {
                    val options = RequestOptions()
                    options.centerCrop()
                    Glide.with(this)
                            .load(imageUrl)
                            .apply(options)
                            .into(image!!)
                }
            }
        }
    }

    private fun getViewArguments() {
        arguments?.let {
            adViewModel = AdViewModel(
                    it.getString(ARG_ID)!!,
                    it.getString(ARG_TITLE),
                    it.getString(ARG_PRICE),
                    it.getString(ARG_IMAGE)
            )
        }
    }

    companion object {
        private const val ARG_ID = "id"
        private const val ARG_TITLE = "title"
        private const val ARG_PRICE = "price"
        private const val ARG_IMAGE = "image"

        fun newInstance(ad: AdViewModel): DetailFragment {
            val fragment = DetailFragment()
            val args = Bundle()
            args.putString(ARG_ID, ad.id)
            args.putString(ARG_TITLE, ad.title)
            args.putString(ARG_PRICE, ad.price)
            args.putString(ARG_IMAGE, ad.image)
            fragment.arguments = args
            return fragment
        }
    }
}
