package com.example.khanh_bui.fragment

import android.content.Context
import android.content.Intent
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.khanh_bui.MapDisplayActivity
import com.example.khanh_bui.R
import com.example.khanh_bui.SingleManualInputActivity
import com.example.khanh_bui.database.ExerciseEntry

class HistoryListAdapter(private val context: Context, private var entryList: List<ExerciseEntry>) : BaseAdapter() {

    override fun getItem(position: Int): Any {
        return entryList[position]
    }

    override fun getItemId(position: Int): Long {
        return entryList[position].id
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = View.inflate(context, R.layout.layout_history_adapter, null)

        val title = view.findViewById(R.id.history_title) as TextView
        val content = view.findViewById(R.id.history_content) as TextView

        val entry = entryList[position]
        var titleInputType = ""
        var titleActivityType = ""

        //building texts for display
        when (entry.inputType) {
            0 -> {titleInputType = "Manual Entry"}
            1 -> {titleInputType = "GPS"}
            2 -> {titleInputType = "Automatic"}
        }

        //activity type
        if (entry.inputType == 2) {
            when (entry.activityType) {
                0 -> {titleActivityType = "Running"}
                1 -> {titleActivityType = "Walking"}
                2 -> {titleActivityType = "Standing"}
                3 -> {titleActivityType = "Unknown"}
                4 -> {titleActivityType = "Unknown"}
                5 -> {titleActivityType = "Unknown"}
                6 -> {titleActivityType = "Unknown"}
                7 -> {titleActivityType = "Unknown"}
                8 -> {titleActivityType = "Unknown"}
                9 -> {titleActivityType = "Unknown"}
                10 -> {titleActivityType = "Unknown"}
                11 -> {titleActivityType = "Unknown"}
                12 -> {titleActivityType = "Unknown"}
                13 -> {titleActivityType = "Other"}
            }
        } else {
            when (entry.activityType) {
                0 -> {titleActivityType = "Running"}
                1 -> {titleActivityType = "Walking"}
                2 -> {titleActivityType = "Standing"}
                3 -> {titleActivityType = "Cycling"}
                4 -> {titleActivityType = "Hiking"}
                5 -> {titleActivityType = "Downhill Skiing"}
                6 -> {titleActivityType = "Cross-Country Skiing"}
                7 -> {titleActivityType = "Snowboarding"}
                8 -> {titleActivityType = "Skating"}
                9 -> {titleActivityType = "Swimming"}
                10 -> {titleActivityType = "Mountain Biking"}
                11 -> {titleActivityType = "Wheelchair"}
                12 -> {titleActivityType = "Elliptical"}
                13 -> {titleActivityType = "Other"}
            }
        }

        //duration
        //format duration to show correct # decimal places depending on value
        var durationValue: String = if (entry.duration == 0.0) {
            "0secs" //no decimal if duration = 0
        } else if (entry.duration < 1) { //if duration < 1 minute, display in seconds
            val second = (entry.duration*60).toInt()
            "${second}secs"
        } else { //display in minutes and seconds otherwise
            val minute = (entry.duration).toInt()
            val second = ((entry.duration - minute)*60).toInt()
            "${minute}mins ${second}secs"
        }

        //get unit preference
        val sharedPreferences = context.getSharedPreferences("PREFERENCES", Context.MODE_PRIVATE)
        val unitPreferences = sharedPreferences.getString("UNIT_PREF", "")

        //distance
        //format distance to show correct # decimal places and unit depending on value & pref
        var distanceValue = ""
        distanceValue = if (unitPreferences == "Miles") {
            if (entry.distance == 0.0) "0 miles" //no decimal if distance = 0

            //no decimal if distance is whole number
            else if((entry.distance - (entry.distance).toInt()) == 0.0) "${entry.distance.toInt().toString()} miles"

            //2 decimals if distance otherwise
            else "${String.format("%.2f", entry.distance)} miles"
        } else {
            if (entry.distance == 0.0) "0 kilometers"
            else {
                val value = entry.distance*1.60934F //no decimal if distance = 0

                //no decimal if distance is whole number
                if (value - value.toInt() == 0.0) "${value.toInt().toString()} kilometers"

                //2 decimals if distance otherwise
                else "${String.format("%.2f", value)} kilometers"
            }
        }

        //set texts for display
        title.text = "${titleInputType}: ${titleActivityType}, ${entry.dateTime}"
        content.text = "${distanceValue}, ${durationValue}"

        view.setOnClickListener() {
            var intent: Intent? = null

            intent = if (entry.inputType == 0) {
                Intent(context, SingleManualInputActivity::class.java)
            } else {
                Intent(context, MapDisplayActivity::class.java)
            }

            //send index and activity type to other activities
            intent.putExtra("INDEX", entry.id)
            intent.putExtra("INPUT_TYPE", entry.inputType)
            intent.putExtra("ACTIVITY_TYPE", entry.activityType)

            context.startActivity(intent)
        }
        return view
    }

    override fun getCount(): Int {
        return entryList.size
    }

    fun replace(newCommentList: List<ExerciseEntry>){
        entryList = newCommentList
    }

}