package com.fireeemaan.journapp.ui.settings

import android.content.Intent
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.fireeemaan.journapp.R
import com.fireeemaan.journapp.data.datastore.SettingsPreferences
import com.fireeemaan.journapp.data.datastore.dataStore
import com.fireeemaan.journapp.databinding.FragmentSettingsBinding
import java.util.Locale
import kotlin.math.log

private const val s = "Restart Required"

class SettingsFragment : Fragment() {

    private lateinit var spinnerLang: Spinner

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupAction()


        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().navigate(R.id.action_settingsFragment_to_listStoryFragment)
                }
            })

    }

    private fun setupAction() {
        binding.btnLanguage.setOnClickListener {
            startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}