package com.fireeemaan.journapp.ui.story

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.fireeemaan.journapp.R
import com.fireeemaan.journapp.databinding.ActivityStoryBinding

class StoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityStoryBinding.inflate(layoutInflater)

        setContentView(binding.root)
    }
}