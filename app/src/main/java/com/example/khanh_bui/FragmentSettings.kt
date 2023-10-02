package com.example.khanh_bui

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat

class FragmentSettings : PreferenceFragmentCompat()  {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
    }
}