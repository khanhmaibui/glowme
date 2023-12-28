package com.example.khanh_bui

import android.content.ComponentName
import android.content.ServiceConnection
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.Message
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MapViewModel: ViewModel(), ServiceConnection {
    private var myMessageHandler: MyMessageHandler = MyMessageHandler(Looper.getMainLooper())
    private val _gps = MutableLiveData<Bundle>()
    val gps: LiveData<Bundle>
        get() {
            return _gps
        }

    private val _auto = MutableLiveData<Bundle>()
    val auto: LiveData<Bundle>
        get() {
            return _auto
        }

    override fun onServiceConnected(name: ComponentName?, iBinder: IBinder?) {
        val tempBinder = iBinder as TrackingService.MyBinder
        tempBinder.setMsgHandler(myMessageHandler)
    }

    override fun onServiceDisconnected(name: ComponentName?) {

    }

    inner class MyMessageHandler(looper: Looper): Handler(looper) {
        override fun handleMessage(msg: Message) {
            if (msg.what == TrackingService.MSG_INT_VALUE) {
                _gps.value = msg.data
            } else if (msg.what == TrackingService.AUTO_MSG_INT_VALUE) {
                _auto.value = msg.data
            }
        }
    }
}