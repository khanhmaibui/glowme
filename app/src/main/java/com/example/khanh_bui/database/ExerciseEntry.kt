package com.example.khanh_bui.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

@TypeConverters(MapConvert::class)
@Entity(tableName = "entry_table")
data class ExerciseEntry (

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L,

    @ColumnInfo(name = "inputType_column")
    var inputType: Int = 0,

    @ColumnInfo(name = "activityType_column")
    var activityType: Int = 0,

    @ColumnInfo(name = "dateTime_column")
    var dateTime: String = "",

    @ColumnInfo(name = "duration_column")
    var duration: Double = 0.0,

    @ColumnInfo(name = "distance_column")
    var distance: Double = 0.0,

    @ColumnInfo(name = "avgPace_column")
    var avgPace: Double = 0.0,

    @ColumnInfo(name = "avgSpeed_column")
    var avgSpeed: Double = 0.0,

    @ColumnInfo(name = "calorie_column")
    var calorie: Double = 0.0,

    @ColumnInfo(name = "climb_column")
    var climb: Double = 0.0,

    @ColumnInfo(name = "heartRate_column")
    var heartRate: Double = 0.0,

    @ColumnInfo(name = "comment_column")
    var comment: String = "",

    @ColumnInfo(name = "location_column")
    var locationList: ArrayList<LatLng> = ArrayList()
)

class MapConvert
{
    //convert locations from string to array
    @TypeConverter
    fun convertLocationsToLatLng(json: String) : ArrayList<LatLng> {
        val gson = Gson()
        val type: Type = object : TypeToken<List<LatLng>>() {}.type
        return gson.fromJson(json, type)
    }

    //convert locations from array to string
    @TypeConverter
    fun convertLocationsToString(array: ArrayList<LatLng>) : String {
        val gson = Gson()
        val type: Type = object : TypeToken<List<LatLng>>() {}.type
        return gson.toJson(array, type)
    }
}