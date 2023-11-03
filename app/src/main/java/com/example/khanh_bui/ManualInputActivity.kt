package com.example.khanh_bui

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import java.util.Calendar

class ManualInputActivity : AppCompatActivity() {
    private var MANUAL_ITEM_LIST = arrayOf(
        "Date",
        "Time",
        "Duration",
        "Distance",
        "Calories",
        "Heart Rate",
        "Comment"
    )
    private lateinit var manualInputListView: ListView
    private val calendar = Calendar.getInstance()
    private lateinit var manualSaveButton: Button
    private lateinit var manualCancelButton: Button
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manual_input)
        setSupportActionBar(findViewById(R.id.tool_bar))

        manualSaveButton = findViewById(R.id.manual_save_button)
        manualCancelButton = findViewById(R.id.manual_cancel_button)
        manualInputListView = findViewById(R.id.manual_input_list_view)

        
        val arrayAdapter: ArrayAdapter<String> = ArrayAdapter<String>(this,
            android.R.layout.simple_list_item_1, MANUAL_ITEM_LIST)
        manualInputListView.adapter = arrayAdapter
        manualInputListView.setOnItemClickListener { _: AdapterView<*>, _: View, position: Int, _: Long ->
            when (position) {
                0 -> {
                    val datePickerDialog = DatePickerDialog(
                        this, { _, year, month, dayOfMonth ->
                            calendar.set(Calendar.YEAR, year)
                            calendar.set(Calendar.MONTH, month)
                            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)},
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)
                    )
                    datePickerDialog.show()
                }
                1 -> {
                    val timePickerDialog = TimePickerDialog(
                        this, { _, hour, minute ->
                            calendar.set(Calendar.HOUR_OF_DAY, hour)
                            calendar.set(Calendar.MINUTE, minute)
                        },
                        calendar.get(Calendar.HOUR_OF_DAY),
                        calendar.get(Calendar.MINUTE), true
                    )
                    timePickerDialog.show()
                }
                2 -> {
                    val myDialog = ManualActivityDialog()
                    val bundle = Bundle()
                    bundle.putInt(ManualActivityDialog.DIALOG_KEY, ManualActivityDialog.DURATION_DIALOG)
                    myDialog.arguments = bundle
                    myDialog.show(supportFragmentManager, "my dialog")
                }
                3 -> {
                    val myDialog = ManualActivityDialog()
                    val bundle = Bundle()
                    bundle.putInt(ManualActivityDialog.DIALOG_KEY, ManualActivityDialog.DISTANCE_DIALOG)
                    myDialog.arguments = bundle
                    myDialog.show(supportFragmentManager, "my dialog")
                }
                4 -> {
                    val myDialog = ManualActivityDialog()
                    val bundle = Bundle()
                    bundle.putInt(ManualActivityDialog.DIALOG_KEY, ManualActivityDialog.CALORIES_DIALOG)
                    myDialog.arguments = bundle
                    myDialog.show(supportFragmentManager, "my dialog")
                }
                5 -> {
                    val myDialog = ManualActivityDialog()
                    val bundle = Bundle()
                    bundle.putInt(ManualActivityDialog.DIALOG_KEY, ManualActivityDialog.HEART_RATE_DIALOG)
                    myDialog.arguments = bundle
                    myDialog.show(supportFragmentManager, "my dialog")
                }
                6 -> {
                    val myDialog = ManualActivityDialog()
                    val bundle = Bundle()
                    bundle.putInt(ManualActivityDialog.DIALOG_KEY, ManualActivityDialog.COMMENT_DIALOG)
                    myDialog.arguments = bundle
                    myDialog.show(supportFragmentManager, "my dialog")
                }
            }
        }

        manualSaveButton.setOnClickListener() {
            finish()
        }
        manualCancelButton.setOnClickListener() {
            Toast.makeText(this, "Entry discarded.", Toast.LENGTH_SHORT).show()
            finish()
        }
        

    }

    //Inflate action bar activities
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_main, menu)
        return true
    }
}