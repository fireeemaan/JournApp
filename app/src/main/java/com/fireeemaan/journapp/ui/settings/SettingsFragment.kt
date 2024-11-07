package com.fireeemaan.journapp.ui.settings

import android.content.Intent
import androidx.fragment.app.viewModels
import android.os.Bundle
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

    private val viewModel: SettingsViewModel by viewModels {
        SettingsViewModelFactory.getInstance(
            SettingsPreferences.getInstance(requireContext().applicationContext.dataStore)
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        spinnerLang = binding.spinnerLanguage

        setupSpinner()

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().navigate(R.id.action_settingsFragment_to_listStoryFragment)
                }

            }
        )
    }

    private fun setupSpinner() {
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.lang_list,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerLang.adapter = adapter
        }

        val languageMap = mapOf(
            "English" to "en",
            "Indonesia" to "in"
        )

        var currentLang: String? = null

        viewModel.getLanguageSetting().observe(viewLifecycleOwner, Observer { language ->
            Log.e("BAHASA", "Sekarang Bahasa: $language")
            currentLang = language
            val position = resources.getStringArray(R.array.lang_list)
                .indexOf(languageMap.entries.find { it.value == language }?.key)
            if (position >= 0) {
                spinnerLang.setSelection(position)
            }
        })

        spinnerLang.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedLang = parent.getItemAtPosition(position).toString()
                val selectedLangCode = languageMap[selectedLang]
                if (selectedLangCode != currentLang) {
                    Log.e("BAHASA", "Kode Bahasa: $selectedLangCode - $selectedLang")
                    if (selectedLangCode != null) {
                        viewModel.saveLanguageSetting(selectedLangCode)
                        updateLanguage(selectedLangCode)
                    } else {
                        viewModel.saveLanguageSetting("en")
                        updateLanguage("en")
                    }
                    restart()
                }

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

        }
    }

    private fun updateLanguage(lang: String) {
        val locale = Locale(lang)
        Locale.setDefault(locale)

        val config = resources.configuration
        config.setLocale(locale)

        @Suppress("DEPRECATION")
        resources.updateConfiguration(config, resources.displayMetrics)
    }

    private fun restart() {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.restart_title)
            .setMessage(R.string.restart_message)
            .setPositiveButton(R.string.restart) { _, _ ->
                val intent =
                    requireActivity().packageManager.getLaunchIntentForPackage(requireActivity().packageName)
                intent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent!!)
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}