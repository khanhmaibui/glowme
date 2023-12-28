package com.example.khanh_bui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Spinner
import androidx.fragment.app.Fragment
import com.example.khanh_bui.ManualInputActivity
import com.example.khanh_bui.MapDisplayActivity
import com.example.khanh_bui.R
class FragmentStart : Fragment() {
    private lateinit var startButton: Button
    private lateinit var inputType: Spinner
    private lateinit var activityType: Spinner
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_start, container, false)

        startButton = view.findViewById(R.id.start_activity)
        inputType = view.findViewById(R.id.input_type)
        activityType = view.findViewById(R.id.activity_type)

        startButton.setOnClickListener() {
            var intent: Intent? = null
            activity?.let{
                when (inputType.selectedItemPosition) {
                    0 -> intent = Intent(it, ManualInputActivity::class.java)
                    1 -> intent = Intent(it, MapDisplayActivity::class.java)
                    2 -> intent = Intent(it, MapDisplayActivity::class.java)
                }

                //for other activities
                intent?.putExtra("INPUT_TYPE", inputType.selectedItemPosition)
                intent?.putExtra("ACTIVITY_TYPE", activityType.selectedItemPosition)
                it.startActivity(intent)
            }
        }

        return view
    }
}