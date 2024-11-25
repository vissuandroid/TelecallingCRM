package com.pixl.crm.activity

import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Color
import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.MalformedJsonException
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import com.pixl.crm.request.AddLead_Request
import com.pixl.crm.response.AddLeadResponse
import com.pixl.pixltalknewapp.R

import com.telecalling.crm.activity.Intrestedcalls_Activity
import com.telecalling.crm.adapter.IntrestescallAdapter
import com.telecalling.crm.services.Api_Interface
import com.telecalling.crm.services.PreferencesHelper
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Locale

class AddLeadsActivity : AppCompatActivity() {

    private lateinit var nameEditText: EditText
    private lateinit var phoneNumberEditText: EditText
    private lateinit var datePickerEditText: EditText
    private lateinit var remarksEditText: EditText
    private lateinit var leadStatusRadioGroup: RadioGroup
    private lateinit var updateButton: Button
    private lateinit var onbackview: ImageView

    private lateinit var apiService: Api_Interface
    private lateinit var preferenceHelper: PreferencesHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_leads)

        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR)
        window.statusBarColor = Color.WHITE
        window.navigationBarColor = Color.WHITE


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window: Window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = ContextCompat.getColor(this, R.color.status_bar_color)
        }

        // Initialize views
        nameEditText = findViewById(R.id.nameEditText)
        phoneNumberEditText = findViewById(R.id.phoneNumberEditText)
        datePickerEditText = findViewById(R.id.datePickerEditText)
        remarksEditText = findViewById(R.id.remarksEditText)
        leadStatusRadioGroup = findViewById(R.id.leadStatusRadioGroup)
        updateButton = findViewById(R.id.updateButton)
        onbackview = findViewById(R.id.backArrowImageView)

        // Initialize Retrofit with logging interceptor
        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)
        val httpClient = OkHttpClient.Builder()
        httpClient.addInterceptor(logging)

        val retrofit = Retrofit.Builder()
            .baseUrl("https://app.telecallingcrm.com/") // Replace with your API base URL
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClient.build())
            .build()

        apiService = retrofit.create(Api_Interface::class.java)

        // Initialize PreferencesHelper
        preferenceHelper = PreferencesHelper

        // Set click listener for the submit button
        updateButton.setOnClickListener {
            submitLead()
        }

        // Set click listener for the date picker
        datePickerEditText.setOnClickListener {
            showDatePicker()
        }
        onbackview.setOnClickListener {
            val intent = Intent(this@AddLeadsActivity, Intrestedcalls_Activity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun submitLead() {
        // Collect data from input fields
        val name = nameEditText.text.toString().trim()
        val phoneNumber = phoneNumberEditText.text.toString().trim()
        val followUpDate = datePickerEditText.text.toString().trim()
        Log.d("DatePicker", "Selected date: $followUpDate")
        val remarks = remarksEditText.text.toString().trim()

        if (name.isEmpty() || phoneNumber.isEmpty() || followUpDate.isEmpty() || remarks.isEmpty()) {
            Toast.makeText(this, "Please fill in all the fields", Toast.LENGTH_SHORT).show()
            return
        }

        // Get email and password from PreferencesHelper
        val email = preferenceHelper.getEmail(this)
        val password = preferenceHelper.getPassword(this)

        if (email.isNullOrEmpty() || password.isNullOrEmpty()) {
            Toast.makeText(this, "User credentials are missing. Please log in.", Toast.LENGTH_SHORT).show()
            return
        }

        // Optional: Convert the date format if needed by the API
        val formattedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(
            SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).parse(followUpDate)
        )

        // Create a Lead object
        val lead = AddLead_Request(
            email = email,
            password = password,
            number = phoneNumber,
            name = name,
            follow_up_date = formattedDate,
            remarks = remarks,
            lead_stage_id = 11,
            call_status = "Interested",
            called_status = "Called"
        )

        // Make the API call
        apiService.addlead(lead).enqueue(object : Callback<AddLeadResponse> {
            override fun onResponse(call: Call<AddLeadResponse>, response: Response<AddLeadResponse>) {
                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    Toast.makeText(this@AddLeadsActivity, apiResponse?.message ?: "Lead added successfully!", Toast.LENGTH_SHORT).show()
                } else {
                    // Log the error for debugging
                    val errorBody = response.errorBody()?.string()
                    Log.e("AddLeadsActivity", "Failed to add lead. Response body: $errorBody")
                    Toast.makeText(this@AddLeadsActivity, "Failed to add lead", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<AddLeadResponse>, t: Throwable) {
                // Log the error message for debugging
                if (t is MalformedJsonException) {
                    Log.e("AddLeadsActivity", "Malformed JSON exception: ${t.message}")
                } else {
                    Log.e("AddLeadsActivity", "Network failure: ${t.message}")
                }
                Toast.makeText(this@AddLeadsActivity, "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this,
            { _, selectedYear, selectedMonth, selectedDay ->
                val date = String.format("%02d-%02d-%d", selectedDay, selectedMonth + 1, selectedYear)
                datePickerEditText.setText(date)
            }, year, month, day)

        datePickerDialog.show()
    }
}




