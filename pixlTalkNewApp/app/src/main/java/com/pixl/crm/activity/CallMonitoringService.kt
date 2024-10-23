package com.pixl.crm.activity

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import android.util.Log

class CallMonitoringService : Service() {

    private lateinit var telephonyManager: TelephonyManager
    private lateinit var phoneStateListener: PhoneStateListener
    private var callStartTime: Long = 0L

    override fun onCreate() {
        super.onCreate()

        telephonyManager = getSystemService(TELEPHONY_SERVICE) as TelephonyManager
        phoneStateListener = object : PhoneStateListener() {
            override fun onCallStateChanged(state: Int, incomingNumber: String?) {
                when (state) {
                    TelephonyManager.CALL_STATE_OFFHOOK -> {
                        // Call started
                        callStartTime = System.currentTimeMillis()
                    }
                    TelephonyManager.CALL_STATE_IDLE -> {
                        // Call ended
                        if (callStartTime > 0) {
                            val callDuration = System.currentTimeMillis() - callStartTime
                            // Broadcast the call duration to the fragment
                            val intent = Intent("com.yourapp.CALL_ENDED")
                            intent.putExtra("callDuration", callDuration)
                            sendBroadcast(intent)
                            callStartTime = 0L
                            stopSelf()  // Stop the service once the call has ended
                        }
                    }
                }
            }
        }

        telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE)
    }
}