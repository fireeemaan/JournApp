package com.fireeemaan.journapp.ui.story.add

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
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
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody

class AddStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddStoryBinding
    private lateinit var btnSendStory: JournButton
    private lateinit var edDescription: EditText

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

        val imgUri =
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                intent.getParcelableExtra(EXTRA_IMAGE_URI, Uri::class.java)
            } else {
                @Suppress("DEPRECATION")
                intent.getParcelableExtra(EXTRA_IMAGE_URI)
            }

        Glide.with(this).load(imgUri).into(binding.ivAddPhoto)

        btnSendStory.setOnClickListener {
            if (imgUri != null) {
                addStory(edDescription.text.toString(), imgUri)
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

    private fun addStory(description: String, imageUri: Uri) {
        showLoading(true)

        val imageFile = uriToFile(imageUri, this)

        val descriptionBody = description.toRequestBody("text/plain".toMediaType())

        val photoFile = imageFile.asRequestBody("image/*".toMediaType())
        val photoPart = MultipartBody.Part.createFormData("photo", imageFile.name, photoFile)

        addStoryViewModel.addStory(photoPart, descriptionBody)
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