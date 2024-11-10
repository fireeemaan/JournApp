package com.fireeemaan.journapp.ui.story.add

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.fireeemaan.journapp.R
import com.fireeemaan.journapp.data.Result
import com.fireeemaan.journapp.data.datastore.TokenDataStore
import com.fireeemaan.journapp.data.datastore.dataStore
import com.fireeemaan.journapp.databinding.ActivityAddStoryBinding
import com.fireeemaan.journapp.ui.button.JournButton
import com.fireeemaan.journapp.ui.story.StoryActivity
import com.fireeemaan.journapp.ui.story.StoryViewModelFactory
import com.fireeemaan.journapp.ui.story.camera.CameraActivity
import com.fireeemaan.journapp.utils.Utils.uriToFile
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.switchmaterial.SwitchMaterial
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody

class AddStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddStoryBinding
    private lateinit var btnSendStory: JournButton
    private lateinit var edDescription: EditText
    private lateinit var swLocation: SwitchMaterial
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private var lat: Double? = null
    private var lon: Double? = null

    private val addStoryViewModel: AddStoryViewModel by viewModels {
        StoryViewModelFactory.getInstance(
            applicationContext,
            TokenDataStore.getInstance(applicationContext.dataStore)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        btnSendStory = binding.buttonAdd
        edDescription = binding.edAddDescription
        swLocation = binding.swLocation

        val imgUri =
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                intent.getParcelableExtra(EXTRA_IMAGE_URI, Uri::class.java)
            } else {
                @Suppress("DEPRECATION")
                intent.getParcelableExtra(EXTRA_IMAGE_URI)
            }

        Glide.with(this).load(imgUri).into(binding.ivAddPhoto)

        swLocation.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                requestPermission()
            } else {
                lat = null
                lon = null
            }
        }

        btnSendStory.setOnClickListener {
            if (imgUri != null) {
                addStory(edDescription.text.toString(), imgUri, lat, lon)
            } else {
                Toast.makeText(this, "Image not found.", Toast.LENGTH_SHORT).show()
            }
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                intent = Intent(this@AddStoryActivity, CameraActivity::class.java)
                startActivity(intent)
                finish()
            }

        })

    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (!isGranted) {
            swLocation.isChecked = false
            Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getUserLocation() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation
                .addOnSuccessListener(this) { location ->
                    if (location != null) {
                        lat = location.latitude
                        lon = location.longitude
                    } else {
                        requestPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
                    }
                }
        }


    }

    private fun requestPermission() {
        if (ContextCompat.checkSelfPermission(
                this.applicationContext, android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            getUserLocation()
        } else {
            requestPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun addStory(
        description: String,
        imageUri: Uri,
        lat: Double? = null,
        lon: Double? = null
    ) {
        showLoading(true)

        val imageFile = uriToFile(imageUri, this)

        val descriptionBody = description.toRequestBody("text/plain".toMediaType())

        var latBody: RequestBody? = null
        var lonBody: RequestBody? = null

        if (lat != null && lon != null) {
            latBody = lat.toString().toRequestBody("text/plain".toMediaType())
            lonBody = lon.toString().toRequestBody("text/plain".toMediaType())
        }

        val photoFile = imageFile.asRequestBody("image/*".toMediaType())
        val photoPart = MultipartBody.Part.createFormData("photo", imageFile.name, photoFile)

        addStoryViewModel.addStory(photoPart, descriptionBody, latBody, lonBody)
        addStoryViewModel.addStoryResponse.observe(this) { result ->
            when (result) {
                is Result.Loading -> {
                    showLoading(true)
                }

                is Result.Success -> {
                    showLoading(false)
                    Toast.makeText(this, "Story uploaded.", Toast.LENGTH_SHORT).show()
                    toListStory()
                }

                is Result.Error -> {
                    showLoading(false)
                    Toast.makeText(this, "Error: ${result.error}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun toListStory() {
        val intent = Intent(this@AddStoryActivity, StoryActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun showLoading(isLoading: Boolean) {
        with(binding) {
            btnSendStory.isEnabled = !isLoading
            btnSendStory.isClickable = !isLoading
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            btnSendStory.text = if (isLoading) "" else getString(R.string.take_photo)
        }
    }


    companion object {
        const val EXTRA_IMAGE_URI = "extra_image_uri"
    }
}