package com.pixl.crm.activity

import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager

class MyPhoneStateListener  : PhoneStateListener() {
    override fun onCallStateChanged(state: Int, phoneNumber: String?) {
        super.onCallStateChanged(state, phoneNumber)
        when (state) {
            TelephonyManager.CALL_STATE_IDLE -> {
                // Call ended
                showFeedbackPopup() // Show the popup when the call ends
            }
        }
    }

    private fun showFeedbackPopup() {

    }
}