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
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.khanh_bui.database.ExerciseEntry
import com.example.khanh_bui.database.ExerciseEntryDatabase
import com.example.khanh_bui.database.ExerciseEntryDatabaseDao
import com.example.khanh_bui.database.ExerciseEntryRepository
import com.example.khanh_bui.database.ExerciseEntryViewModel
import com.example.khanh_bui.database.ExerciseEntryViewModelFactory
import java.util.Calendar
import androidx.activity.viewModels
import java.text.SimpleDateFormat

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
    private lateinit var manualSaveButton: Button
    private lateinit var manualCancelButton: Button

    private var activityType: Int = 0
    private var inputType: Int = 0
    private var dateTime = Calendar.getInstance()
    private var duration: Double = 0.0
    private var distance: Double = 0.0
    private var calories: Double = 0.0
    private var heartRate: Double = 0.0
    private var comment: String = ""

    //remove
    private lateinit var entry: ExerciseEntry
    private lateinit var database: ExerciseEntryDatabase
    private lateinit var databaseDao: ExerciseEntryDatabaseDao
    private lateinit var repository: ExerciseEntryRepository
    private lateinit var exerciseEntryViewModel: ExerciseEntryViewModel
    private lateinit var viewModelFactory: ExerciseEntryViewModelFactory

    private val entryViewModel: EntryViewModel by viewModels() //create a VM instance
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manual_input)
        setSupportActionBar(findViewById(R.id.tool_bar))

        //get activity type from fragment start
        val intentExtra = intent.extras
        if (intentExtra != null) {
            inputType = intentExtra.getInt("INPUT_TYPE")
            activityType = intentExtra.getInt("ACTIVITY_TYPE")
        }

        manualSaveButton = findViewById(R.id.manual_save_button)
        manualCancelButton = findViewById(R.id.manual_cancel_button)
        manualInputListView = findViewById(R.id.manual_input_list_view)

        //observe changes in entry
        entryViewModel.calendarViewModel.observe(this) {
            dateTime = it
        }
        entryViewModel.durationViewModel.observe(this) {
            duration = it
        }
        entryViewModel.distanceViewModel.observe(this) {
            distance = it
        }
        entryViewModel.caloriesViewModel.observe(this) {
            calories = it
        }
        entryViewModel.heartRateViewModel.observe(this) {
            heartRate = it
        }
        entryViewModel.commentViewModel.observe(this) {
            comment = it
        }

        //set up database
        database = ExerciseEntryDatabase.getInstance(this)
        databaseDao = database.exerciseEntryDatabaseDao
        repository = ExerciseEntryRepository(databaseDao)
        viewModelFactory = ExerciseEntryViewModelFactory(repository)
        exerciseEntryViewModel = ViewModelProvider(this, viewModelFactory)[ExerciseEntryViewModel::class.java]

        val arrayAdapter: ArrayAdapter<String> = ArrayAdapter<String>(this,
            android.R.layout.simple_list_item_1, MANUAL_ITEM_LIST)
        manualInputListView.adapter = arrayAdapter
        manualInputListView.setOnItemClickListener { _: AdapterView<*>, _: View, position: Int, _: Long ->
            when (position) {
                0 -> {
                    val datePickerDialog = DatePickerDialog(
                        this, { _, year, month, dayOfMonth ->
                            dateTime.set(Calendar.YEAR, year)
                            dateTime.set(Calendar.MONTH, month)
                            dateTime.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                            entryViewModel.calendarViewModel.value = dateTime
                              },
                        dateTime.get(Calendar.YEAR),
                        dateTime.get(Calendar.MONTH),
                        dateTime.get(Calendar.DAY_OF_MONTH)
                    )
                    datePickerDialog.show()
                }
                1 -> {
                    val timePickerDialog = TimePickerDialog(
                        this, { _, hour, minute ->
                            dateTime.set(Calendar.HOUR_OF_DAY, hour)
                            dateTime.set(Calendar.MINUTE, minute)
                            entryViewModel.calendarViewModel.value = dateTime
                              },
                        dateTime.get(Calendar.HOUR_OF_DAY),
                        dateTime.get(Calendar.MINUTE), true
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

        //save (insert into database)
        manualSaveButton.setOnClickListener() {
            val dateTimeFormat = SimpleDateFormat("HH:mm:ss MMM dd yyyy") //format for storing datetime

            val entry = ExerciseEntry()
            entry.inputType = inputType
            when(activityType) {
                0 -> {entry.activityType = 0}
                1 -> {entry.activityType = 1}
                2 -> {entry.activityType = 2}
                3 -> {entry.activityType = 3}
                4 -> {entry.activityType = 4}
                5 -> {entry.activityType = 5}
                6 -> {entry.activityType = 6}
                7 -> {entry.activityType = 7}
                8 -> {entry.activityType = 8}
                9 -> {entry.activityType = 9}
                10 -> {entry.activityType = 10}
                11 -> {entry.activityType = 11}
                12 -> {entry.activityType = 12}
                13 -> {entry.activityType = 13}
            }
            entry.dateTime = dateTimeFormat.format(dateTime.time) //store datetime with proper formatting
            entry.duration = duration
            entry.distance = distance
            entry.calorie = calories
            entry.heartRate = heartRate
            entry.comment = comment

            exerciseEntryViewModel.insert(entry)
            val id = exerciseEntryViewModel.getSize() + 1
            Toast.makeText(this, "Entry #$id saved.", Toast.LENGTH_SHORT).show()
            finish()
        }

        manualCancelButton.setOnClickListener() {
            Toast.makeText(this, "Entry discarded.", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    //inflate action bar activities
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_main, menu)

        //modify toolbar to not include setting button
        menu.removeItem(R.id.action_settings)

        return true
    }
}