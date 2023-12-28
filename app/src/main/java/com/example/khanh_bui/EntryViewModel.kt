package com.example.khanh_bui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.Calendar

class EntryViewModel : ViewModel() {
    var calendarViewModel = MutableLiveData<Calendar>()
    var durationViewModel = MutableLiveData<Double>()
    var distanceViewModel = MutableLiveData<Double>()
    var caloriesViewModel = MutableLiveData<Double>()
    var heartRateViewModel = MutableLiveData<Double>()
    var commentViewModel = MutableLiveData<String>()
}