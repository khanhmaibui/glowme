package com.example.khanh_bui

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.khanh_bui.database.ExerciseEntry
import com.example.khanh_bui.database.ExerciseEntryDatabase
import com.example.khanh_bui.database.ExerciseEntryDatabaseDao
import com.example.khanh_bui.database.ExerciseEntryRepository
import com.example.khanh_bui.database.ExerciseEntryViewModel
import com.example.khanh_bui.database.ExerciseEntryViewModelFactory
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import java.text.SimpleDateFormat
import java.util.Calendar

class MapDisplayActivity: AppCompatActivity(), OnMapReadyCallback {
    private lateinit var manualSaveButton: Button
    private lateinit var manualCancelButton: Button

    private lateinit var database: ExerciseEntryDatabase
    private lateinit var databaseDao: ExerciseEntryDatabaseDao
    private lateinit var repository: ExerciseEntryRepository
    private lateinit var exerciseEntryViewModel: ExerciseEntryViewModel
    private lateinit var viewModelFactory: ExerciseEntryViewModelFactory

    private lateinit var mapActivityType: TextView
    private lateinit var mapAverageSpeed: TextView
    private lateinit var mapCurrentSpeed: TextView
    private lateinit var mapClimb: TextView
    private lateinit var mapCalorie: TextView
    private lateinit var mapDistance: TextView

    private var activityType: Int = 0
    private var inputType: Int = 0
    private var dateTime = Calendar.getInstance()
    private var duration: Double = 0.0
    private var avgSpeed: Double = 0.0
    private var avgPace: Double = 0.0
    private var climb: Double = 0.0
    private var calorie: Double = 0.0
    private var distance: Double = 0.0

    private lateinit var mMap: GoogleMap
    private lateinit var mapViewModel: MapViewModel
    private lateinit var serviceIntent: Intent
    private var isBind = false
    private var mapCentered = false
    private lateinit var  markerOptions: MarkerOptions
    private lateinit var  polylineOptions: PolylineOptions
    private lateinit var  polylines: ArrayList<Polyline>
    private lateinit var locations: ArrayList<LatLng>
    private var endMarker : Marker? = null

    private var BIND_STATUS_KEY = "BIND_STATUS_KEY"
    private val PERMISSION_REQUEST_CODE = 0
    private var isEntry = false
    private var index: Long = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
        setSupportActionBar(findViewById(R.id.tool_bar))
        title = "Map"

        //load bind status key from saved instance
        if(savedInstanceState != null) {
            isBind = savedInstanceState.getBoolean(BIND_STATUS_KEY)
        }

        locations = ArrayList()
        manualSaveButton = findViewById(R.id.manual_save_button)
        manualCancelButton = findViewById(R.id.manual_cancel_button)

        //set intent and view model for map
        serviceIntent = Intent(this, TrackingService::class.java)
        mapViewModel = ViewModelProvider(this)[MapViewModel::class.java]

        //load map
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        //declare text views for data display
        mapActivityType = findViewById(R.id.map_type)
        mapAverageSpeed = findViewById(R.id.map_avg_speed)
        mapCurrentSpeed = findViewById(R.id.map_cur_speed)
        mapCalorie = findViewById(R.id.map_calorie)
        mapClimb = findViewById(R.id.map_climb)
        mapDistance = findViewById(R.id.map_distance)

        //set up database
        database = ExerciseEntryDatabase.getInstance(this)
        databaseDao = database.exerciseEntryDatabaseDao
        repository = ExerciseEntryRepository(databaseDao)
        viewModelFactory = ExerciseEntryViewModelFactory(repository)
        exerciseEntryViewModel = ViewModelProvider(this, viewModelFactory)[ExerciseEntryViewModel::class.java]

        //get data from FragmentStart or HistoryListAdapter
        val intentExtra = intent.extras
        if (intentExtra != null) {
            inputType = intentExtra.getInt("INPUT_TYPE")
            activityType = intentExtra.getInt("ACTIVITY_TYPE")
            isEntry = intentExtra.containsKey("INDEX")

            if (isEntry) { //if it is a saved entry (called from HistoryListAdapter)
                index = intentExtra.getLong("INDEX") //get index of entry
                manualSaveButton.visibility = View.GONE //delete save button
                manualCancelButton.visibility = View.GONE //delete cancel button

                if (inputType == 2) { //if input type is automatic
                    when (activityType) {
                        0 -> {mapActivityType.text = "Type: Running"}
                        1 -> {mapActivityType.text = "Type: Walking"}
                        2 -> {mapActivityType.text = "Type: Standing"}
                        3 -> {mapActivityType.text = "Type: Unknown"}
                        4 -> {mapActivityType.text = "Type: Unknown"}
                        5 -> {mapActivityType.text = "Type: Unknown"}
                        6 -> {mapActivityType.text = "Type: Unknown"}
                        7 -> {mapActivityType.text = "Type: Unknown"}
                        8 -> {mapActivityType.text = "Type: Unknown"}
                        9 -> {mapActivityType.text = "Type: Unknown"}
                        10 -> {mapActivityType.text = "Type: Unknown"}
                        11 -> {mapActivityType.text = "Type: Unknown"}
                        12 -> {mapActivityType.text = "Type: Unknown"}
                        13 -> {mapActivityType.text = "Type: Other"}
                    }
                } else { //if inputType is Manual or GPS
                    when (activityType) {
                        0 ->{mapActivityType.text = "Type: Running"}
                        1 ->{mapActivityType.text = "Type: Walking"}
                        2 ->{mapActivityType.text = "Type: Standing"}
                        3 ->{mapActivityType.text = "Type: Cycling"}
                        4 ->{mapActivityType.text = "Type: Hiking"}
                        5 ->{mapActivityType.text = "Type: Downhill Skiing"}
                        6 ->{mapActivityType.text = "Type: Cross-Country Skiing"}
                        7 ->{mapActivityType.text = "Type: Snowboarding"}
                        8 ->{mapActivityType.text = "Type: Skating"}
                        9 ->{mapActivityType.text = "Type: Swimming"}
                        10 ->{mapActivityType.text = "Type: Mountain Biking"}
                        11 ->{mapActivityType.text = "Type: Wheelchair"}
                        12 ->{mapActivityType.text = "Type: Elliptical"}
                        13 ->{mapActivityType.text = "Type: Other"}
                    }
                }
            } else { //if it is not a saved entry (called from FragmentStart)
                if (inputType == 2) { //if input type is automatic
                    mapViewModel.auto.observe(this) { //observe changes in activityType
                        val data = it.getString("AUTO_ACTIVITY_TYPE")
                        mapActivityType.text = "Type: $data"
                        when (data) {
                            "Running" -> {
                                activityType = 0
                            }

                            "Walking" -> {
                                activityType = 1
                            }

                            "Standing" -> {
                                activityType = 2
                            }

                            "Other" -> {
                                activityType = 13
                            }
                        }
                    }
                } else { //if inputType is Manual or GPS
                    when (activityType) {
                        0 ->{mapActivityType.text = "Type: Running"}
                        1 ->{mapActivityType.text = "Type: Walking"}
                        2 ->{mapActivityType.text = "Type: Standing"}
                        3 ->{mapActivityType.text = "Type: Cycling"}
                        4 ->{mapActivityType.text = "Type: Hiking"}
                        5 ->{mapActivityType.text = "Type: Downhill Skiing"}
                        6 ->{mapActivityType.text = "Type: Cross-Country Skiing"}
                        7 ->{mapActivityType.text = "Type: Snowboarding"}
                        8 ->{mapActivityType.text = "Type: Skating"}
                        9 ->{mapActivityType.text = "Type: Swimming"}
                        10 ->{mapActivityType.text = "Type: Mountain Biking"}
                        11 ->{mapActivityType.text = "Type: Wheelchair"}
                        12 ->{mapActivityType.text = "Type: Elliptical"}
                        13 ->{mapActivityType.text = "Type: Other"}
                    }
                }
            }
        }

        //save entry to database
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
            entry.locationList = locations
            entry.duration = duration
            entry.distance = distance
            entry.calorie = calorie
            entry.avgSpeed = avgSpeed
            entry.avgPace = avgPace
            entry.climb = climb

            //add entry to database
            exerciseEntryViewModel.insert(entry)

            //print message to UI with id of entry (+1 since starts with 0 in db)
            val id = exerciseEntryViewModel.getSize() + 1
            Toast.makeText(this, "Entry #$id saved.", Toast.LENGTH_SHORT).show()

            //stop service
            if (isBind)
            {
                applicationContext.unbindService(mapViewModel)
                stopService(serviceIntent)
                isBind = false
            }
            finish()
        }

        manualCancelButton.setOnClickListener() {

            //print message to UI
            Toast.makeText(this, "Entry discarded.", Toast.LENGTH_SHORT).show()

            //stop service
            if (isBind)
            {
                applicationContext.unbindService(mapViewModel)
                stopService(serviceIntent)
                isBind = false
            }
            finish()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        //set map
        mMap = googleMap
        mMap.mapType = GoogleMap.MAP_TYPE_NORMAL //set map type
        markerOptions = MarkerOptions()
        polylineOptions = PolylineOptions()
        polylineOptions.color(Color.BLACK) //set polyline colour
        polylines = ArrayList()

        val intentExtra = intent.extras
        if (intentExtra != null) {
            if (intentExtra.containsKey("INDEX")) { //if it is an entry (called from HistoryListAdapter)
                index = intentExtra.getLong("INDEX") //get index of entry
                exerciseEntryViewModel.allEntriesLiveData.observe(this) {
                    val entry = it.find { entry -> entry.id == index } //find the entry in list
                    if (entry != null) {
                        val bundle = Bundle()
                        //save data to use in displayMap
                        bundle.putString("LOCATIONS", convertLocationsToString(entry.locationList))
                        bundle.putDouble("DURATION", entry.duration)
                        bundle.putDouble("AVG_SPEED", entry.avgSpeed)
                        bundle.putDouble("AVG_PACE", entry.avgPace)
                        bundle.putDouble("CLIMB", entry.climb)
                        bundle.putDouble("CALORIE", entry.calorie)
                        bundle.putDouble("DISTANCE", entry.distance)
                        displayMap(bundle)
                    }
                }
            } else { //if it is not an entry (called from FragmentStart)
                checkPermission()
                mapViewModel.gps.observe(this) {
                    //observe changes in data (sent by TrackingService) and display on map
                    displayMap(it)
                }
            }
        }
    }

    private fun displayMap(bundle: Bundle) {
        locations = convertLocationsToLatLng(bundle.getString("LOCATIONS")!!)

        //if no location is recorded
        if (locations.isEmpty()) {
            //set marker in the middle of map
            mMap.addMarker(MarkerOptions().position(LatLng(0.0, 0.0)))

            //set text fields to be 0
            mapAverageSpeed.text = "Avg speed: 0 m/h"
            mapCurrentSpeed.text = "Cur speed: n/a"
            mapClimb.text = "Climb: 0 Miles"
            mapDistance.text = "Distance: 0 Miles"
            mapCalorie.text = "Calorie: 0"
        } else { //else get data from bundle
            duration = bundle.getDouble("DURATION")
            avgSpeed = bundle.getDouble("AVG_SPEED")
            avgPace = bundle.getDouble("AVG_PACE")
            climb = bundle.getDouble("CLIMB")
            calorie = bundle.getDouble("CALORIE")
            distance = bundle.getDouble("DISTANCE")

            if (avgPace.isInfinite() || avgPace.isNaN()) {
                avgPace = 0.0 //handle non-number cases
            }
            if (avgSpeed.isInfinite() || avgPace.isNaN()) {
                avgSpeed = 0.0 //handle non-number cases
            }

            val sharedPreferences = getSharedPreferences("PREFERENCES", Context.MODE_PRIVATE)
            val unitPreferences = sharedPreferences.getString("UNIT_PREF", "")

            //check and convert unit based on unit preferences
            if (unitPreferences == "Miles") {
                mapAverageSpeed.text = "Avg speed: ${String.format("%.1f", avgSpeed)} m/h"
                mapCurrentSpeed.text = "Cur speed: ${String.format("%.1f", avgPace)} m/h"
                mapClimb.text = "Climb: ${String.format("%.2f",climb)} Miles"
                mapDistance.text = "Distance: ${String.format("%.2f",distance)} Miles"
            } else { //convert to kilometers
                mapAverageSpeed.text = "Avg speed: ${String.format("%.1f", avgSpeed*1.60934F)} km/h"
                mapCurrentSpeed.text = "Cur speed: ${String.format("%.1f", avgPace*1.60934F)} km/h"
                mapClimb.text = "Climb: ${String.format("%.0f",climb*1.60934F)} Kilometers"
                mapDistance.text = "Distance: ${String.format("%.2f",distance*1.60934F)} Kilometers"
            }
            mapCalorie.text = "Calorie: ${String.format("%.0f",calorie)}"

            //if it is an entry, set cur speed to n/a
            val intentExtra = intent.extras
            if (intentExtra != null) {
                if (intentExtra.containsKey("INDEX")) {
                    mapCurrentSpeed.text = "Cur speed: n/a"
                }
            }

            //draw polyline
            polylineOptions = PolylineOptions()
            polylineOptions.addAll(locations)
            mMap.addPolyline(polylineOptions)

            //re-center map
            if (!mapCentered) {
                val latLng = locations.first()
                //zoom to start marker
                val cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 17f)
                mMap.animateCamera(cameraUpdate)
                //customize start marker
                markerOptions.position(latLng).title("Start location")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)) //set marker to green
                mMap.addMarker(markerOptions)
                mapCentered = true
            }

            //customize end marker
            val latLng = locations.lastOrNull()
            if (latLng != null) {
                endMarker?.remove()
                markerOptions.position(latLng).title("End location")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)) //set marker to red
                endMarker = mMap.addMarker(markerOptions)!!
            }
        }
    }

    //start TrackingService
    private fun startTrackingService() {
        applicationContext.startService(serviceIntent)
        if(!isBind) {
            applicationContext.bindService(serviceIntent, mapViewModel, Context.BIND_AUTO_CREATE)
            isBind = true
        }
    }

    //check if accessing device location is allowed
    private fun checkPermission() {
        if (Build.VERSION.SDK_INT < 23) return
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,
                                                                    Manifest.permission.ACCESS_COARSE_LOCATION), PERMISSION_REQUEST_CODE)
        else
            startTrackingService()
    }

    //convert locations from string to array to save in db
    private fun convertLocationsToLatLng(json: String) : ArrayList<LatLng> {
        val gson = Gson()
        val type: Type = object : TypeToken<List<LatLng>>() {}.type
        return gson.fromJson(json, type)
    }

    //convert locations from array to string to save in bundle
    private fun convertLocationsToString(array: ArrayList<LatLng>) : String {
        val gson = Gson()
        val listType: Type = object : TypeToken<List<LatLng>>() {}.type
        return gson.toJson(array, listType)
    }

    //inflate action bar activities
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_main, menu)

        //modify toolbar to include delete button
        menu.removeItem(R.id.action_settings)

        //if it is an entry, add delete button
        if(isEntry) {
            val buttonItem = menu.add(Menu.NONE, R.id.delete_button, Menu.NONE, "Delete")
            buttonItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)

            buttonItem.setOnMenuItemClickListener {item ->
                if (item.itemId == R.id.delete_button) {
                    exerciseEntryViewModel.delete(index)

                    //stop service
                    if (isBind)
                    {
                        applicationContext.unbindService(mapViewModel)
                        stopService(serviceIntent)
                        isBind = false
                    }
                    finish()
                    true
                } else {
                    false
                }
            }
        }
        return true
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(BIND_STATUS_KEY, isBind)
    }
}