package com.example.khanh_bui

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Binder
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.IllegalStateException
import java.lang.reflect.Type
import java.util.concurrent.ArrayBlockingQueue

class TrackingService: Service(), LocationListener, SensorEventListener {
    private val CHANNEL_ID = "CHANNEL_ID"
    private lateinit var notificationManager: NotificationManager
    private val NOTIFICATION_ID = 888

    private lateinit var locationManager: LocationManager
    private lateinit var locations: ArrayList<LatLng>

    private lateinit var currentLocation: Location

    private var msgHandler: Handler? = null
    private lateinit var myBinder: MyBinder

    private var durationTotalInMinutes: Double = 0.0
    private var durationInSeconds: Double = 0.0
    private var durationTotal: Double = 0.0
    private var avgSpeed: Double = 0.0
    private var avgPace: Double = 0.0
    private var climb: Double = 0.0
    private var calorie: Double = 0.0
    private var distance: Double = 0.0
    private var beginTime: Long = 0L
    private var endTime: Long = 0L
    private var currentTime: Long = 0L
    private var stop = false

    private var x: Double = 0.0
    private var y: Double = 0.0
    private var z: Double = 0.0
    private lateinit var sensorManager: SensorManager
    private lateinit var mAccBuffer: ArrayBlockingQueue<Double>

    companion object {
        val MSG_INT_VALUE = 0
        val AUTO_MSG_INT_VALUE = 1
    }

    override fun onCreate() {
        super.onCreate()
        showNotification()

        //for duration update
        beginTime = System.currentTimeMillis()
        endTime = 0

        //create binder
        myBinder = MyBinder()

        //for location update
        locations = ArrayList()
        initLocationManager()

        //set buffer size to ACCELEROMETER_BUFFER_CAPACITY
        mAccBuffer = ArrayBlockingQueue<Double>(Globals.ACCELEROMETER_BUFFER_CAPACITY)

        //start sensor
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        val sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)

        //run thread for checking activity type
        Thread {
            checkActivityType()
        }.start()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return myBinder
    }

    inner class MyBinder : Binder() {
        fun setMsgHandler(msgHandler: Handler) {
            this@TrackingService.msgHandler = msgHandler
        }
    }

    override fun onUnbind(intent: Intent?): Boolean {
        msgHandler = null
        return true
    }

    override fun onLocationChanged(location: Location) {
        currentTime = System.currentTimeMillis()

        //get latlng of new location, add it to location array
        val lat = location.latitude
        val lng = location.longitude
        val latLng = LatLng(lat, lng)
        locations.add(latLng)

        //if there exists a previous location
        if (::currentLocation.isInitialized) {
            durationTotal = ((currentTime - beginTime) / 1000).toDouble() //total in seconds
            durationTotalInMinutes = durationTotal / 60 //total in minutes
            durationInSeconds = ((currentTime - endTime) / 1000).toDouble() //in seconds
            distance += currentLocation.distanceTo(location) * 0.00062137F //in miles
            avgPace =
                (currentLocation.distanceTo(location) * 0.00062137F) / (durationInSeconds / 3600) // in miles/h
            avgSpeed = distance / (durationTotal / 3600) //in miles/h
            calorie = (distance * 100) // 100 calorie per mile
            climb = (currentLocation.altitude - location.altitude) * 0.00062137F //in miles
        }
        endTime = System.currentTimeMillis() //save endTime for next location update (calculate avgPace)
        currentLocation = location //set current location to be the new location

        //update data for displayMap
        sendingData()
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        cleanupTasks()
        stopSelf()
    }

    override fun onDestroy() {
        super.onDestroy()
        cleanupTasks()
    }

    //for classifying activity type
    override fun onSensorChanged(event: SensorEvent?) {
        if (event != null && event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            x = (event.values[0] / SensorManager.GRAVITY_EARTH).toDouble()
            y = (event.values[1] / SensorManager.GRAVITY_EARTH).toDouble()
            z = (event.values[2] / SensorManager.GRAVITY_EARTH).toDouble()
            val magnitude = Math.sqrt(x * x + y * y + z * z)

            try {
                //inserts if there is space
                mAccBuffer.add(magnitude)
            } catch (e: IllegalStateException) {
                //if not, create new buffer with double space
                val newBuf = ArrayBlockingQueue<Double>(mAccBuffer.size * 2)
                mAccBuffer.drainTo(newBuf)
                mAccBuffer = newBuf
                mAccBuffer.add(magnitude)
            }
        }
    }

    //get device's latest location through GPS
    private fun initLocationManager() {
        try {
            locationManager = getSystemService(LOCATION_SERVICE) as LocationManager

            //check if device GPS is enabled
            try {
                locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            } catch (e: Exception) {
                println("GPS is not enabled")
            }

            //get the latest location of device
            val location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            if (location != null)
                //update data using new location
                onLocationChanged(location)

            //get new location updates
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, this)
        } catch (e: SecurityException) { }
    }

    private fun showNotification() {

        //create notification
        val notificationBuilder: NotificationCompat.Builder =
            NotificationCompat.Builder(this, CHANNEL_ID)
        notificationBuilder.setSmallIcon(R.drawable.rocket)
        notificationBuilder.setContentTitle("MyRuns")
        notificationBuilder.setContentText("Recording your path now")

        //set on-click behavior for notification
        val intent = Intent(this, MapDisplayActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        notificationBuilder.setContentIntent(pendingIntent)

        //build notification
        val notification = notificationBuilder.build()

        //start notification
        notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= 26) { //create notification channel if sdk>=26
            val notificationChannel = NotificationChannel(
                CHANNEL_ID,
                "CHANNEL_NAME",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(notificationChannel)
        }
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    //for sending data through view model to MapDisplay
    private fun sendingData() {
        try {
            if (msgHandler != null) {
                val bundle = Bundle()
                //save to bundle
                bundle.putString("LOCATIONS", convertLocationsToString(locations))
                bundle.putDouble("DURATION", durationTotalInMinutes)
                bundle.putDouble("AVG_SPEED", avgSpeed)
                bundle.putDouble("AVG_PACE", avgPace)
                bundle.putDouble("CLIMB", climb)
                bundle.putDouble("CALORIE", calorie)
                bundle.putDouble("DISTANCE", distance)

                //send data to MapDisplay
                val message = msgHandler!!.obtainMessage()
                message.data = bundle
                message.what = MSG_INT_VALUE
                msgHandler!!.sendMessage(message)
            }
        } catch (t: Throwable) { }
    }

    //for classifying activity type
    private fun checkActivityType() {
        var blockSize = 0
        val fft = FFT(Globals.ACCELEROMETER_BLOCK_CAPACITY)
        val accBlock = DoubleArray(Globals.ACCELEROMETER_BLOCK_CAPACITY)
        val im = DoubleArray(Globals.ACCELEROMETER_BLOCK_CAPACITY)
        var max: Double
        //for saving data points, max is the max value of accelerometer block
        val frequencyData = ArrayList<Double>(Globals.ACCELEROMETER_BLOCK_CAPACITY)

        while (true) {
            try {
                accBlock[blockSize++] = mAccBuffer.take().toDouble()

                //if reaching buffer size
                if (blockSize == Globals.ACCELEROMETER_BLOCK_CAPACITY) {
                    blockSize = 0
                    max = .0

                    //find the max value
                    for (`val` in accBlock) {
                        if (max < `val`) {
                            max = `val`
                        }
                    }
                    fft.fft(accBlock, im)
                    for (i in accBlock.indices) {
                        val magnitude = Math.sqrt(accBlock[i] * accBlock[i] + im[i] * im[i])
                        im[i] = .0
                        //add data point to data array
                        frequencyData.add(magnitude)
                    }
                    frequencyData.add(max) //append max after frequency component

                    //get data from weka classifier and send activity type to MapDisplay
                    when (WekaClassifier.classify(frequencyData.toArray()).toInt()) {
                        0 -> {
                            sendingActivityType("Standing")
                        }

                        1 -> {
                            sendingActivityType("Walking")
                        }

                        2 -> {
                            sendingActivityType("Running")
                        }

                        else -> {
                            sendingActivityType("Other")
                        }
                    }

                    //done with data, clear
                    frequencyData.clear()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            if (stop) {
                break
            }
        }
    }

    //for sending classified activity type to MapDisplay
    private fun sendingActivityType(activityType: String) {
        try {
            if (msgHandler != null) {
                //save to bundle
                val bundle = Bundle()
                bundle.putString("AUTO_ACTIVITY_TYPE", activityType)

                //send data to MapDisplay
                val message = msgHandler!!.obtainMessage()
                message.data = bundle
                message.what = AUTO_MSG_INT_VALUE
                msgHandler!!.sendMessage(message)
            }
        } catch (t: Throwable) { }
    }

    //convert location array to string to save to bundle
    private fun convertLocationsToString(array: ArrayList<LatLng>): String {
        val gson = Gson()
        val type: Type = object : TypeToken<List<LatLng>>() {}.type
        return gson.toJson(array, type)
    }

    private fun cleanupTasks() {
        //cancel notification service
        notificationManager.cancel(NOTIFICATION_ID)

        //cancel location service
        if (locationManager != null) {
            locationManager.removeUpdates(this)
        }

        //clear all recorded locations
        locations.clear()

        //delete msgHandler
        msgHandler = null

        //unregister sensor listener, stop sensor
        sensorManager.unregisterListener(this)
        stop = true
    }
}