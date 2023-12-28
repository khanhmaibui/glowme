package com.example.khanh_bui.fragment

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.example.khanh_bui.R

class FragmentSettings : PreferenceFragmentCompat()  {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {

        //save distance unit preferences
        val manager: PreferenceManager = getPreferenceManager()
        manager.sharedPreferencesName = "PREFERENCES"

        setPreferencesFromResource(R.xml.preferences, rootKey)
    }
}