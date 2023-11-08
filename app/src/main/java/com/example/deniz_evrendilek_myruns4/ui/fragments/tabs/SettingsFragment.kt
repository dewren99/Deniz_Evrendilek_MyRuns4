package com.example.deniz_evrendilek_myruns4.ui.fragments.tabs

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.preference.CheckBoxPreference
import androidx.preference.EditTextPreference
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.example.deniz_evrendilek_myruns4.R
import com.example.deniz_evrendilek_myruns4.constants.PreferenceConstants.UNIT_PREFERENCE_DEFAULT
import com.example.deniz_evrendilek_myruns4.constants.PreferenceConstants.UNIT_PREFERENCE_IMPERIAL
import com.example.deniz_evrendilek_myruns4.constants.PreferenceConstants.UNIT_PREFERENCE_KEY
import com.example.deniz_evrendilek_myruns4.constants.PreferenceConstants.UNIT_PREFERENCE_METRIC
import com.example.deniz_evrendilek_myruns4.ui.viewmodel.ExerciseEntryViewModel
import com.example.deniz_evrendilek_myruns4.ui.viewmodel.ExerciseEntryViewModelFactory

class SettingsFragment : PreferenceFragmentCompat() {
    private lateinit var profilePreference: Preference
    private lateinit var privacyPreference: CheckBoxPreference
    private lateinit var unitPreference: ListPreference
    private lateinit var commentsPreference: EditTextPreference
    private lateinit var webpagePreference: Preference
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var unitPreferences: Array<String>
    private lateinit var exerciseEntryViewModelFactory: ExerciseEntryViewModelFactory
    private lateinit var exerciseEntryViewModel: ExerciseEntryViewModel

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
        unitPreferences = resources.getStringArray(R.array.unit_preference)
        exerciseEntryViewModelFactory = ExerciseEntryViewModelFactory(requireActivity())
        exerciseEntryViewModel = ViewModelProvider(
            requireActivity(), exerciseEntryViewModelFactory
        )[ExerciseEntryViewModel::class.java]

        setupAccountPreferences()
        setupAdditionalSettings()
        setupMisc()
    }

    private fun setupAccountPreferences() {
        profilePreference =
            preferenceManager.findPreference("profile")
                ?: throw NoSuchFieldError("profilePreference preference not found")
        privacyPreference =
            preferenceManager.findPreference("privacy")
                ?: throw NoSuchFieldError("privacyPreference preference not found")

        profilePreference.setOnPreferenceClickListener {
            navigateToProfile()
            true
        }
        privacyPreference.setOnPreferenceChangeListener { _, newValue ->
            true
        }
    }

    private fun navigateToProfile() {
        val navHostFragment = requireActivity().supportFragmentManager.findFragmentById(
            R.id.nav_host_fragment
        ) as NavHostFragment
        navHostFragment.navController.navigate(R.id.profileFragment)
    }

    private fun setupAdditionalSettings() {
        unitPreference =
            preferenceManager.findPreference("unit_preference")
                ?: throw NoSuchFieldError("Unit preference not found")
        commentsPreference = preferenceManager.findPreference("comments") ?: throw NoSuchFieldError(
            "Comments preference not found"
        )

        unitPreference.setOnPreferenceChangeListener { _, newValue ->
            val editor = sharedPreferences.edit()
            var unit = UNIT_PREFERENCE_DEFAULT
            when (newValue) {
                unitPreferences[0] -> unit = UNIT_PREFERENCE_METRIC
                unitPreferences[1] -> unit = UNIT_PREFERENCE_IMPERIAL
            }
            editor.putString(UNIT_PREFERENCE_KEY, unit)
            exerciseEntryViewModel.setUnitPreference(unit)
            editor.apply()
            true
        }
        commentsPreference.setOnPreferenceChangeListener { _, newValue ->
            true
        }

    }

    private fun setupMisc() {
        webpagePreference =
            preferenceManager.findPreference("webpage")
                ?: throw NoSuchFieldError("Webpage preference not found")

        webpagePreference.setOnPreferenceClickListener {
            val intentOpenWebpage = Intent(Intent.ACTION_VIEW)
            intentOpenWebpage.data = Uri.parse("https://www.sfu.ca/computing.html")
            startActivity(intentOpenWebpage)
            true
        }
    }
}