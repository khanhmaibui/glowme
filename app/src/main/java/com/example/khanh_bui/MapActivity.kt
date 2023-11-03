package com.example.khanh_bui

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MapActivity: AppCompatActivity() {
    private lateinit var manualSaveButton: Button
    private lateinit var manualCancelButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
        setSupportActionBar(findViewById(R.id.tool_bar))
        title = "Map"

        manualSaveButton = findViewById(R.id.manual_save_button)
        manualCancelButton = findViewById(R.id.manual_cancel_button)

        manualSaveButton.setOnClickListener() {
            finish()
        }
        manualCancelButton.setOnClickListener() {
            finish()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_main, menu)
        return true
    }
}