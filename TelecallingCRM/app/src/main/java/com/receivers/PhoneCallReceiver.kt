package com.pixl.crm.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager
import androidx.localbroadcastmanager.content.LocalBroadcastManager

class PhoneCallReceiver : BroadcastReceiver() {



        override fun onReceive(context: Context, intent: Intent) {
            val state = intent.getStringExtra(TelephonyManager.EXTRA_STATE)

            if (state == TelephonyManager.EXTRA_STATE_IDLE) {
                // Call ended
                val callEndedIntent = Intent("CALL_ENDED_ACTION")
                LocalBroadcastManager.getInstance(context).sendBroadcast(callEndedIntent)
            }
        }

}