package com.example.khanh_bui.fragment

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.example.khanh_bui.R

class FragmentSettings : PreferenceFragmentCompat()  {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
    }
}