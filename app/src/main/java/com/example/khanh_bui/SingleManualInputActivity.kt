package com.example.khanh_bui

import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.khanh_bui.database.ExerciseEntryDatabase
import com.example.khanh_bui.database.ExerciseEntryDatabaseDao
import com.example.khanh_bui.database.ExerciseEntryRepository
import com.example.khanh_bui.database.ExerciseEntryViewModel
import com.example.khanh_bui.database.ExerciseEntryViewModelFactory

class SingleManualInputActivity: AppCompatActivity() {

    private lateinit var database: ExerciseEntryDatabase
    private lateinit var databaseDao: ExerciseEntryDatabaseDao
    private lateinit var repository: ExerciseEntryRepository
    private lateinit var exerciseEntryViewModel: ExerciseEntryViewModel
    private lateinit var viewModelFactory: ExerciseEntryViewModelFactory

    private var index: Long = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_single_manual_input)
        setSupportActionBar(findViewById(R.id.tool_bar))

        //find views for each attribute
        val inputType = findViewById<EditText>(R.id.manual_input_type)
        val activityType = findViewById<EditText>(R.id.manual_activity_type)
        val dateTime = findViewById<EditText>(R.id.manual_date_time)
        val duration = findViewById<EditText>(R.id.manual_duration)
        val distance = findViewById<EditText>(R.id.manual_distance)
        val calories = findViewById<EditText>(R.id.manual_calories)
        val heartRate = findViewById<EditText>(R.id.manual_heart_rate)

        //set up database
        database = ExerciseEntryDatabase.getInstance(this)
        databaseDao = database.exerciseEntryDatabaseDao
        repository = ExerciseEntryRepository(databaseDao)
        viewModelFactory = ExerciseEntryViewModelFactory(repository)
        exerciseEntryViewModel = ViewModelProvider(this, viewModelFactory)[ExerciseEntryViewModel::class.java]

        //get index and activity type from HistoryListAdapter
        //input type should always be Manual Entry
        val intentExtra = intent.extras
        if (intentExtra != null) {
            index = intentExtra.getLong("INDEX") //get index of entry
            val getActivityType = intentExtra.getInt("ACTIVITY_TYPE")
            exerciseEntryViewModel.allEntriesLiveData.observe(this) {
                val entry = it.find { entry -> entry.id == index} //find the entry in list

                //set text for display
                if (entry != null) {
                    //input type
                    inputType.setText("Manual Entry")

                    //activity type
                    when(getActivityType) {
                        0 ->{activityType.setText("Running")}
                        1 ->{activityType.setText("Walking")}
                        2 ->{activityType.setText("Standing")}
                        3 ->{activityType.setText("Cycling")}
                        4 ->{activityType.setText("Hiking")}
                        5 ->{activityType.setText("Downhill Skiing")}
                        6 ->{activityType.setText("Cross-Country Skiing")}
                        7 ->{activityType.setText("Snowboarding")}
                        8 ->{activityType.setText("Skating")}
                        9 ->{activityType.setText("Swimming")}
                        10 ->{activityType.setText("Mountain Biking")}
                        11 ->{activityType.setText("Wheelchair")}
                        12 ->{activityType.setText("Elliptical")}
                        13 ->{activityType.setText("Other")}
                    }

                    //duration
                    //format duration to show correct # decimal places depending on value
                    if (entry.duration == 0.0) { //no decimal if duration = 0
                        duration.setText("0secs")
                    } else if (entry.duration < 1) { //if duration < 1 minute, display in seconds
                        val second = entry.duration*60
                        duration.setText(second.toString() + "secs")
                    } else { //displays in minutes and seconds otherwise
                        val minute = (entry.duration).toInt()
                        val second = ((entry.duration - minute)*60).toInt()
                        duration.setText(minute.toString() + "mins " + second.toString() + "secs")
                    }

                    //get unit preference
                    val sharedPreferences = getSharedPreferences("PREFERENCES", Context.MODE_PRIVATE)
                    val unitPreferences = sharedPreferences.getString("UNIT_PREF", "")

                    //distance
                    //format distance to show correct # decimal places and unit depending on value & pref
                    var distanceValue = 0.0
                    if (unitPreferences == "Miles") {
                        if (entry.distance == 0.0) distance.setText("0 miles") //no decimal if distance = 0

                        //no decimal if distance is whole number
                        else if((entry.distance - entry.distance.toInt()) == 0.0) distance.setText(entry.distance.toInt().toString() + " miles")

                        //2 decimals if distance otherwise
                        else distance.setText(String.format("%.2f", entry.distance) + " miles")
                    } else {
                        if (entry.distance == 0.0) distance.setText("0 kilometers")
                        else {
                            distanceValue = entry.distance*1.60934 //no decimal if distance = 0

                            //no decimal if distance is whole number
                            if (distanceValue - distanceValue.toInt() == 0.0) distance.setText(distanceValue.toInt().toString() + " kilometers")

                            //2 decimals if distance otherwise
                            distance.setText(String.format("%.2f", distanceValue) + " kilometers")
                        }

                    }

                    //datetime
                    dateTime.setText(entry.dateTime)

                    //calories
                    //no decimal if calorie is whole number
                    if (entry.calorie - entry.calorie.toInt() == 0.0) calories.setText(entry.calorie.toInt().toString() + " cals")
                    //2 decimals otherwise
                    else calories.setText(String.format("%.2f", entry.calorie) + " cals")

                    //heart rate
                    //no decimal if heartRate is whole number
                    if (entry.heartRate - entry.heartRate.toInt() == 0.0) heartRate.setText(entry.heartRate.toInt().toString() + " bpm")
                    //2 decimals otherwise
                    else heartRate.setText(String.format("%.2f", entry.heartRate) + " bpm")
                }
            }
        }
    }

    //inflate action bar activities
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_main, menu)

        //modify toolbar to include delete button
        menu.removeItem(R.id.action_settings)
        val buttonItem = menu.add(Menu.NONE, R.id.delete_button, Menu.NONE, "Delete")
        buttonItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)

        buttonItem.setOnMenuItemClickListener {item ->
            if (item.itemId == R.id.delete_button) {
                exerciseEntryViewModel.delete(index)
                finish()
                true
            } else {
                false
            }
        }
        return true
    }

}