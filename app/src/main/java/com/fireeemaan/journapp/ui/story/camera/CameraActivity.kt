package com.fireeemaan.journapp.ui.story.camera

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.OrientationEventListener
import android.view.Surface
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import com.fireeemaan.journapp.R
import com.fireeemaan.journapp.databinding.ActivityCameraBinding
import com.fireeemaan.journapp.ui.button.JournButton
import com.fireeemaan.journapp.ui.story.StoryActivity
import com.fireeemaan.journapp.ui.story.add.AddStoryActivity
import com.fireeemaan.journapp.utils.Utils.compressImage
import com.fireeemaan.journapp.utils.Utils.createCustomTempFile
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCameraBinding

    private var imageCapture: ImageCapture? = null
    private lateinit var cameraExecutor: ExecutorService

    private lateinit var btnTakePhoto: JournButton
    private lateinit var previewView: PreviewView
    private lateinit var fabGallery: FloatingActionButton

    private val activityResultLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        )
        { permissions ->
            var permissionGranted = true
            permissions.entries.forEach {
                if (it.key in REQUIRED_PERMISSIONS && !it.value)
                    permissionGranted = false
            }
            if (!permissionGranted) {
                Toast.makeText(
                    baseContext,
                    "Permission request denied",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                startCamera()
            }
        }

    private val orientationEventListener by lazy {
        object : OrientationEventListener(this) {
            override fun onOrientationChanged(orientation: Int) {
                if (orientation == ORIENTATION_UNKNOWN) {
                    return
                }

                val rotation = when (orientation) {
                    in 45 until 135 -> Surface.ROTATION_270
                    in 135 until 225 -> Surface.ROTATION_180
                    in 225 until 315 -> Surface.ROTATION_90
                    else -> Surface.ROTATION_0
                }

                imageCapture?.targetRotation = rotation
            }

        }
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        uri?.let {
            val intent = Intent(this@CameraActivity, AddStoryActivity::class.java)
            intent.putExtra(AddStoryActivity.EXTRA_IMAGE_URI, uri)
            addStoryActivityLauncher.launch(intent)
        } ?: run {
            Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show()
        }
    }

    private val addStoryActivityLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)

        previewView = binding.previewView
        btnTakePhoto = binding.btnTakePhoto
        fabGallery = binding.fabGallery

        if (allPermissionGranted()) {
            startCamera()
        } else {
            requestPermission()
        }

        btnTakePhoto.setOnClickListener {
            takePhoto()
        }

        fabGallery.setOnClickListener {
            startGallery()
        }

        cameraExecutor = Executors.newSingleThreadExecutor()

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                intent = Intent(this@CameraActivity, StoryActivity::class.java)
                startActivity(intent)
                finish()
            }

        })
    }

    override fun onResume() {
        super.onResume()
        showLoading(false)
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder()
                .build()
                .also {
                    it.surfaceProvider = previewView.surfaceProvider
                }

            imageCapture = ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .build()

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()

                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture
                )

                orientationEventListener.enable()
            } catch (e: Exception) {
                Log.e("CameraX", "Something went wrong!")
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun takePhoto() {
        showLoading(true)
        val imageCapture = imageCapture ?: return

        val photoFile = createCustomTempFile(application)

        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    compressImage(photoFile, maxSizeKB = 500)

                    showLoading(false)

                    val intent = Intent(this@CameraActivity, AddStoryActivity::class.java)
                    intent.putExtra(AddStoryActivity.EXTRA_IMAGE_URI, Uri.fromFile(photoFile))
                    startActivity(intent)
                    finish()
                }


                override fun onError(exception: ImageCaptureException) {
                    Log.e("Camera", "Error: ${exception.message}")
                }
            })
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private fun allPermissionGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission() {
        activityResultLauncher.launch(REQUIRED_PERMISSIONS)
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
        orientationEventListener.disable()
    }

    private fun showLoading(isLoading: Boolean) {
        with(binding) {
            btnTakePhoto.isEnabled = !isLoading
            btnTakePhoto.isClickable = !isLoading
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            btnTakePhoto.text = if (isLoading) "" else getString(R.string.take_photo)
        }
    }

    companion object {
        private val REQUIRED_PERMISSIONS = mutableListOf(
            Manifest.permission.CAMERA,
        ).apply {
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        }.toTypedArray()
    }
}