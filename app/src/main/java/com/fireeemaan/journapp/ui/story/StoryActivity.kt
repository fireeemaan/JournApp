package com.fireeemaan.journapp.ui.story

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.menu.MenuBuilder
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.fireeemaan.journapp.R
import com.fireeemaan.journapp.data.datastore.TokenDataStore
import com.fireeemaan.journapp.data.datastore.dataStore
import com.fireeemaan.journapp.databinding.ActivityStoryBinding
import com.fireeemaan.journapp.ui.main.MainActivity
import com.fireeemaan.journapp.ui.story.add.AddStoryViewModel
import com.fireeemaan.journapp.ui.story.list.ListStoryFragmentDirections
import kotlin.math.log

class StoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStoryBinding

    private val viewModel: StoryViewModel by viewModels {
        StoryViewModelFactory.getInstance(
            applicationContext,
            TokenDataStore.getInstance(applicationContext.dataStore)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
        val navController = navHostFragment?.findNavController()

        val storyId = intent.getStringExtra("story_id")
        if (storyId != null) {
            val action =
                ListStoryFragmentDirections.actionListStoryFragmentToDetailStoryFragment(storyId)
            navController?.navigate(action)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onMenuOpened(featureId: Int, menu: Menu): Boolean {
        return super.onMenuOpened(featureId, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                findNavController(R.id.nav_host_fragment)
                    .navigate(R.id.action_listStoryFragment_to_settingsFragment)
                true
            }

            R.id.action_logout -> {
                confirmLogout()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun confirmLogout() {
        AlertDialog.Builder(this)
            .setTitle(R.string.logout)
            .setMessage(R.string.logout_message)
            .setPositiveButton(R.string.yes) { dialog, _ ->
                dialog.dismiss()
                logout()
            }
            .setNegativeButton(R.string.no) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun logout() {
        viewModel.clearToken()

        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}