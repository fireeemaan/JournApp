package com.fireeemaan.journapp.ui.maps

import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.fireeemaan.journapp.R
import com.fireeemaan.journapp.data.Result
import com.fireeemaan.journapp.data.datastore.TokenDataStore
import com.fireeemaan.journapp.data.datastore.dataStore
import com.fireeemaan.journapp.database.story.StoryEntity

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.fireeemaan.journapp.databinding.ActivityMapsBinding
import com.fireeemaan.journapp.ui.story.StoryViewModelFactory
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    private val viewModel: MapsViewModel by viewModels {
        StoryViewModelFactory.getInstance(
            applicationContext,
            TokenDataStore.getInstance(applicationContext.dataStore)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
            }
        })
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            getMyLocation()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap


//        val sydney = LatLng(-34.0, 151.0)
//        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))

        getMyLocation()
        setMapStyle()
        observeData()
    }

    private val boundsBuilder = LatLngBounds.Builder()

    private fun observeData() {
        viewModel.getStoriesWithLocation().observe(this) { result ->
            when (result) {
                is Result.Success -> {
                    val stories = result.data
                    stories.forEach { story ->
                        addMarker(story)
                    }
                }

                is Result.Error -> {
                    Toast.makeText(this, "Error: ${result.error}", Toast.LENGTH_SHORT).show()
                }

                else -> {}
            }
        }
    }

    private fun addMarker(story: StoryEntity) {

        createCustomMarker(this, story.photoUrl) { customMarkerIcon ->
            val latLng = LatLng(story.lat!!, story.lon!!)
            val marker = mMap.addMarker(
                MarkerOptions()
                    .position(latLng)
                    .icon(customMarkerIcon)
                    .title(story.name)
                    .snippet(story.description)
            )
            marker?.showInfoWindow()
            boundsBuilder.include(latLng)

            val bounds: LatLngBounds = boundsBuilder.build()
            mMap.animateCamera(
                CameraUpdateFactory.newLatLngBounds(
                    bounds,
                    resources.displayMetrics.widthPixels,
                    resources.displayMetrics.heightPixels,
                    300
                )
            )
        }
    }

    private fun createCustomMarker(
        context: Context,
        photoUrl: String,
        callback: (BitmapDescriptor) -> Unit
    ) {
        val markerView = LayoutInflater.from(context).inflate(R.layout.custom_marker_layout, null)
        val ivMarker = markerView.findViewById<ImageView>(R.id.iv_marker)

        Glide.with(context)
            .asBitmap()
            .load(photoUrl)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {

                    ivMarker.setImageBitmap(resource)

                    markerView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
                    markerView.layout(0, 0, markerView.measuredWidth, markerView.measuredHeight)

                    val bitmap = Bitmap.createBitmap(
                        markerView.measuredWidth,
                        markerView.measuredHeight,
                        Bitmap.Config.ARGB_8888
                    )
                    val canvas = Canvas(bitmap)
                    markerView.draw(canvas)

                    callback(BitmapDescriptorFactory.fromBitmap(bitmap))
                }

                override fun onLoadCleared(placeholder: Drawable?) {}

            })


    }

    private fun setMapStyle() {
        try {
            val success =
                mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))
            if (!success) {
                Log.e(TAG, "setMapStyle: Style parsing failed.")
            }
        } catch (e: Resources.NotFoundException) {
            Log.e(TAG, "setMapStyle: Can't find style: $e")
        }
    }


    private fun getMyLocation() {
        if (ContextCompat.checkSelfPermission(
                this.applicationContext, android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
        } else {
            requestPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    companion object {
        private const val TAG = "MapsActivity"
    }


}