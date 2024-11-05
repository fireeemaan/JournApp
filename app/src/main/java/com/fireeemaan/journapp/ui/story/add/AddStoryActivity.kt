package com.fireeemaan.journapp.ui.story.add

import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.fireeemaan.journapp.R
import com.fireeemaan.journapp.databinding.ActivityAddStoryBinding

class AddStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddStoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val imgUri =
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                intent.getParcelableExtra(EXTRA_IMAGE_URI, Uri::class.java)
            } else {
                @Suppress("DEPRECATION")
                intent.getParcelableExtra(EXTRA_IMAGE_URI)
            }

        Glide.with(this).load(imgUri).into(binding.ivAddPhoto)
    }

    companion object {
        const val EXTRA_IMAGE_URI = "extra_image_uri"
    }
}