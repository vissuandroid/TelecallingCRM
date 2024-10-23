package com.pixl.crm.activity

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.pixl.crm.fragment.UpdateInfoBottomSheetFragment

import com.telecalling.crm.R
import com.telecalling.crm.activity.Intrestedcalls_Activity
import com.telecalling.crm.services.PreferencesHelper



class ViewinfoActivity : AppCompatActivity(), UpdateInfoBottomSheetFragment.OnUpdateInfoListener {
    private lateinit var backButton: ImageView
    private lateinit var whatsappButton: ImageView
    private lateinit var callButton: ImageView
    private lateinit var smsButton: ImageView
    private lateinit var updateInfoTextView: TextView
    private lateinit var statusImageView: ImageView
    private var number: String = ""
    private var name: String = "Not specified" // Set default name here
    private var remarks: String = "No Remarks"
    private var followUpDate: String = "No Date"
    private var leadStatus: String = "No Status"
    private var calledStatus: String = ""
    private var callStatus: String = ""
    private var dateAdded: String = ""
    private var dealStatus: String = ""
    private var dealAmount: Double = 0.0
    private var totalCalls: Int = 0
    private var leadStageId: Int = 0
    private var leadStageStatus: String = ""
    private var id: String = "" // Changed to String for consistency

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_viewinfo)

        // Initialize the views
        backButton = findViewById(R.id.followbackbutton_img)
        whatsappButton = findViewById(R.id.whatsapp_icon)
        callButton = findViewById(R.id.call_icon)
        smsButton = findViewById(R.id.sms_icon)
        updateInfoTextView = findViewById(R.id.updateTextView)
        statusImageView = findViewById(R.id.infostatusicon) // Initialize the status icon

        // Retrieve data from intent with default values
        number = intent.getStringExtra("NUMBER") ?: ""
        name = intent.getStringExtra("NAME")?.takeIf { it.isNotEmpty() } ?: "Not specified"
        remarks = intent.getStringExtra("REMARKS") ?: "No Remarks"
        followUpDate = intent.getStringExtra("FOLLOW_UP_DATE") ?: "No Date"
        leadStatus = intent.getStringExtra("LEAD_STATUS") ?: "No Status"
        calledStatus = intent.getStringExtra("CALLED_STATUS") ?: ""
        callStatus = intent.getStringExtra("CALL_STATUS") ?: ""
        dateAdded = intent.getStringExtra("DATE_ADDED") ?: ""
        dealStatus = intent.getStringExtra("DEAL_STATUS") ?: ""
        dealAmount = intent.getDoubleExtra("DEAL_AMOUNT", 0.0)
        totalCalls = intent.getIntExtra("TOTAL_CALLS", 0)
        leadStageId = intent.getIntExtra("LEAD_STAGE_ID", 0)
        leadStageStatus = intent.getStringExtra("LEAD_STAGE_STATUS") ?: ""
        id = intent.getIntExtra("ID", 0).toString()

        // Display the data
        findViewById<TextView>(R.id.infotextViewname).text = name
        findViewById<TextView>(R.id.infotextViewNumber).text = number
        findViewById<TextView>(R.id.infotextViewCreatedOn).text = "Created On \n $dateAdded"
        findViewById<TextView>(R.id.infotextViewNextFollowup).text = "Next followup \n $followUpDate"
        findViewById<TextView>(R.id.inforemarks).text = remarks

        // Set the image resource based on leadStageStatus
        when (leadStageStatus.toLowerCase()) {
            "hot" -> statusImageView.setImageResource(R.drawable.hotstatus)
            "cold" -> statusImageView.setImageResource(R.drawable.coldimg)
            "warm" -> statusImageView.setImageResource(R.drawable.warm)
            else -> statusImageView.setImageResource(R.drawable.hotstatus) // Default image if status doesn't match
        }

        backButton.setOnClickListener {
            val intent = Intent(this@ViewinfoActivity, Intrestedcalls_Activity::class.java)
            startActivity(intent)
            finish()
        }

        whatsappButton.setOnClickListener {
            if (number.isNotEmpty()) {
                val url = "https://wa.me/$number"
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(url)
                try {
                    startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    Toast.makeText(this, "WhatsApp not installed.", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Call button functionality
        callButton.setOnClickListener {
            if (number.isNotEmpty()) {
                val callIntent = Intent(Intent.ACTION_DIAL)
                callIntent.data = Uri.parse("tel:$number")
                startActivity(callIntent)
            }
        }

        // SMS button functionality
        smsButton.setOnClickListener {
            if (number.isNotEmpty()) {
                val smsIntent = Intent(Intent.ACTION_SENDTO)
                smsIntent.data = Uri.parse("smsto:$number")
                startActivity(smsIntent)
            }
        }

        // Show bottom sheet when "Update Info" is clicked
        updateInfoTextView.setOnClickListener {
            showBottomSheet(id, name, followUpDate, remarks, leadStatus)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = ContextCompat.getColor(this, R.color.status_bar_color)
        }
    }

    private fun showBottomSheet(
        id: String,
        name: String,
        follwupdate: String,
        remarks: String,
        leadstatus: String
    ) {
        val email = PreferencesHelper.getEmail(this)
        val password = PreferencesHelper.getPassword(this)

        Log.d("ShowBottomSheet", "The ID is: $id")

        // Retrieve the id from SharedPreferences if needed
        // val userId = PreferencesHelper.getUserId(this)?.toString() ?: id // Convert to String if necessary

        // Correctly initialize the fragment and pass required parameters
        if (email != null && password != null) {
            val bottomSheetFragment = UpdateInfoBottomSheetFragment(
                listener = this, // Pass the activity as the listener
                userId = id, // Ensure userId is a String
                email = email,
                password = password,
                name = name,
                follwupdate = follwupdate,
                remarks = remarks,
                leadstatus = leadstatus
            )
            bottomSheetFragment.show(supportFragmentManager, bottomSheetFragment.tag)
        } else {
            Toast.makeText(this, "Failed to load email or password.", Toast.LENGTH_SHORT).show()
        }
    }

    // Handle the update information here
    override fun onUpdateInfo(
        companyName: String,
        selectedDate: String,
        remarks: String,
        option: String,
        leadStatus: String
    ) {
        // Use the updated information as needed, e.g., call your API here
        Toast.makeText(
            this,
            "Updating info:\n$companyName\n$selectedDate\n$remarks\n$option\n$leadStatus",
            Toast.LENGTH_LONG
        ).show()

        updateInfoOnServer(companyName, selectedDate, remarks, option, leadStatus)
    }

    private fun updateInfoOnServer(
        companyName: String,
        selectedDate: String,
        remarks: String,
        option: String,
        leadStatus: String
    ) {
        // For demonstration purposes, we'll just log the data
        println("API call to update info with data: $companyName, $selectedDate, $remarks, $option, $leadStatus")
    }
}





