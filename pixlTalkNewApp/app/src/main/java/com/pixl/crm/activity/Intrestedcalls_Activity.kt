package com.telecalling.crm.activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pixl.crm.MainActivity
import com.pixl.crm.activity.AddLeadsActivity
import com.pixl.crm.activity.ViewinfoActivity
import com.pixl.crm.request.DeletePhoneNumberRequest
import com.pixl.crm.response.DeletePhoneNumberResponse
import com.pixl.pixltalknewapp.R

import com.telecalling.crm.adapter.IntrestescallAdapter
import com.telecalling.crm.request.IntrestedcallsListRequest
import com.telecalling.crm.request.UpdatedetailRequest
import com.telecalling.crm.response.Instrested_calls_list_Response
import com.telecalling.crm.response.UpdatedetailsResponse
import com.telecalling.crm.services.Api_Interface
import com.telecalling.crm.services.PreferencesHelper
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class Intrestedcalls_Activity : AppCompatActivity(),IntrestescallAdapter.IntrestedCallClickListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: IntrestescallAdapter
    private lateinit var intrestedbackimge: ImageView
    private lateinit var progressBar: ProgressBar
    private lateinit var addbutton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView( R.layout.activity_intrestedcalls)
        setupViews()
        fetchPendingNumbers()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window: Window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = ContextCompat.getColor(this, R.color.status_bar_color)
        }
    }


    private fun setupViews() {
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        intrestedbackimge = findViewById(R.id.intrested_backbtn)
        progressBar = findViewById(R.id.progressBar)
        addbutton= findViewById(R.id.button_add_leads)
        intrestedbackimge.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
        addbutton.setOnClickListener {
            val intent = Intent(this, AddLeadsActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
    }

    private fun fetchPendingNumbers() {
        val email = PreferencesHelper.getEmail(this)
        val password = PreferencesHelper.getPassword(this)

        if (email.isNullOrEmpty() || password.isNullOrEmpty()) {
            showSafeToast("Email or Password is missing")
            return
        }

        progressBar.visibility = View.VISIBLE

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val original = chain.request()
                val requestBuilder = original.newBuilder()
                    .header("Content-Type", "application/x-www-form-urlencoded")
                val request = requestBuilder.build()
                chain.proceed(request)
            }
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://app.telecallingcrm.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()

        val apiService = retrofit.create(Api_Interface::class.java)

        val requestBodyData = IntrestedcallsListRequest(
            action = "login",
            email = email,
            password = password
        )

        val call = apiService.getintrestedNumbers(requestBodyData)
        call.enqueue(object : Callback<Instrested_calls_list_Response> {
            override fun onResponse(
                call: Call<Instrested_calls_list_Response>,
                response: Response<Instrested_calls_list_Response>
            ) {
                progressBar.visibility = View.GONE

                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        Log.d("interestedapi", "response: ${response.body()}")

                        val numbers = responseBody.phone_numbers ?: emptyList()
                        adapter = IntrestescallAdapter(this@Intrestedcalls_Activity, this@Intrestedcalls_Activity, numbers)
                        recyclerView.adapter = adapter
                    } else {
                        showSafeToast("Empty response body")
                    }
                } else {
                    showSafeToast("Failed to fetch numbers: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<Instrested_calls_list_Response>, t: Throwable) {
                progressBar.visibility = View.GONE
                showSafeToast("Failed to fetch numbers: ${t.message}")
            }
        })
    }

//    override fun onSaveButtonClick(
//        position: Int,
//        id: Int,
//        selectedDate: String,
//        remarks: String,
//        name: String?,
//        call_status: String
//    ) {
//        try {
//            val phoneNumberInt = id.toUInt()
//            if (phoneNumberInt == null) {
//                showSafeToast("Invalid phone number ID")
//                return
//            }
//
//            val email = PreferencesHelper.getEmail(this)
//            val password = PreferencesHelper.getPassword(this)
//
//            // Default values for fields
//            val defaultName = "Client"
//            val defaultDate = "2024-01-01"  // Example default date
//            val defaultRemarks = "No remarks provided"
//
//            // Check if email and password are missing
//            if (email.isNullOrEmpty() || password.isNullOrEmpty()) {
//                showSafeToast("Email or Password is missing")
//                return
//            }
//
//            // If call_status is "interested", ensure all fields are provided
//            if (call_status == "interested" && (selectedDate.isBlank() || remarks.isBlank() || name.isNullOrBlank())) {
//                showSafeToast("Missing required fields for interested status")
//                return
//            }
//
//
//            // Use provided values or default values based on call_status
//            val finalName = if (call_status == "not-interested") defaultName else name ?: defaultName
//            val finalDate = if (call_status == "not-interested") defaultDate else if (selectedDate.isBlank()) defaultDate else selectedDate
//            val finalRemarks = if (call_status == "not-interested") defaultRemarks else if (remarks.isBlank()) defaultRemarks else remarks
//
//            val requestBodyData = UpdatedetailRequest(
//                email = email,
//                password = password,
//                id = id,
//                follow_up_date = finalDate,
//                remarks = finalRemarks,
//                name = finalName,
//                call_status = call_status
//            )
//
//            val okHttpClient = OkHttpClient.Builder()
//                .addInterceptor { chain ->
//                    val request = chain.request().newBuilder()
//                        .header("Cache-Control", "no-cache")
//                        .build()
//                    chain.proceed(request)
//                }
//                .build()
//
//            val retrofit = Retrofit.Builder()
//                .baseUrl("https://app.telecallingcrm.com/")
//                .client(okHttpClient)
//                .addConverterFactory(GsonConverterFactory.create())
//                .build()
//
//            val apiService = retrofit.create(Api_Interface::class.java)
//            val call = apiService.updateintrestedNumberDetails(requestBodyData)
//
//            call.enqueue(object : Callback<UpdatedetailsResponse> {
//                override fun onResponse(
//                    call: Call<UpdatedetailsResponse>,
//                    response: Response<UpdatedetailsResponse>
//                ) {
//                    if (response.isSuccessful) {
//                        showSafeToast("Updated successfully")
//                        fetchPendingNumbers()  // Call fetchPendingNumbers here to refresh the list
//                    } else {
//                        val errorMessage = response.errorBody()?.string() ?: "Unknown error"
//                        showSafeToast("Failed to update: $errorMessage")
//                    }
//                }
//
//                override fun onFailure(call: Call<UpdatedetailsResponse>, t: Throwable) {
//                    showSafeToast("Failed to update: ${t.message}")
//                }
//            })
//        } catch (e: NumberFormatException) {
//            showSafeToast("Invalid phone number ID")
//        }
//    }



//
//    override fun onDeleteButtonClick(position: Int, id: Int) {
//        val email = PreferencesHelper.getEmail(this)
//        val password = PreferencesHelper.getPassword(this)
//
//        if (email.isNullOrEmpty() || password.isNullOrEmpty()) {
//            showSafeToast("Email or Password is missing")
//            return
//        }
//
//        progressBar.visibility = View.VISIBLE
//
//        val okHttpClient = OkHttpClient.Builder()
//            .addInterceptor { chain ->
//                val original = chain.request()
//                val requestBuilder = original.newBuilder()
//                    .header("Content-Type", "application/json")
//                    .method(original.method, original.body)
//                val request = requestBuilder.build()
//                chain.proceed(request)
//            }
//            .build()
//
//        val retrofit = Retrofit.Builder()
//            .baseUrl("https://app.telecallingcrm.com/")
//            .client(okHttpClient)
//            .addConverterFactory(GsonConverterFactory.create())
//            .build()
//
//        val apiService = retrofit.create(Api_Interface::class.java)
//
//        val requestBody = DeletePhoneNumberRequest(
//            email = email,
//            password = password,
//            phone_number_id = id
//        )
//
//        val call = apiService.deletePhoneNumber(requestBody)
//        call.enqueue(object : Callback<DeletePhoneNumberResponse> {
//            override fun onResponse(
//                call: Call<DeletePhoneNumberResponse>,
//                response: Response<DeletePhoneNumberResponse>
//            ) {
//                progressBar.visibility = View.GONE
//
//                if (response.isSuccessful) {
//                    showSafeToast("Deleted successfully")
//                    fetchPendingNumbers()
//                } else {
//                    val errorMessage = response.errorBody()?.string() ?: "Unknown error"
//                    showSafeToast("Failed to delete: $errorMessage")
//                }
//            }
//
//            override fun onFailure(call: Call<DeletePhoneNumberResponse>, t: Throwable) {
//                progressBar.visibility = View.GONE
//                showSafeToast("Failed to delete: ${t.message}")
//            }
//        })
//    }

//    override fun onCallButtonClick(phoneNumber: String) {
//        val intent = Intent(Intent.ACTION_DIAL)
//        intent.data = Uri.parse("tel:$phoneNumber")
//        startActivity(intent)
//    }


    private fun showSafeToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onCallButtonClick(number: String) {
        val callIntent = Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse("tel:$number")
        }
        startActivity(callIntent)
    }

    override fun onWhatsAppButtonClick(number: String) {
        val formattedNumber = if (number.startsWith("+91")) {
            number
        } else {
            "+91$number"
        }

        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse("https://api.whatsapp.com/send?phone=$formattedNumber")
        }
        startActivity(intent)
    }

    override fun onInfoClick(
        number: String,
        name: String,
        remarks: String,
        followUpDate: String,
        calledStatus: String,
        callStatus: String,
        dateAdded: String,
        dealStatus: String,
        dealAmount: Double,
        totalCalls: Int,
        leadStageId: Int,
        leadStageStatus: String,
        id: Int,
    ) {

        val intent = Intent(this, ViewinfoActivity::class.java).apply {
            putExtra("NUMBER", number)
            putExtra("NAME", name)
            putExtra("REMARKS", remarks)
            putExtra("FOLLOW_UP_DATE", followUpDate)
            putExtra("CALLED_STATUS", calledStatus)
            putExtra("CALL_STATUS", callStatus)
            putExtra("DATE_ADDED", dateAdded)
            putExtra("DEAL_STATUS", dealStatus)
            putExtra("DEAL_AMOUNT", dealAmount)
            putExtra("TOTAL_CALLS", totalCalls)
            putExtra("LEAD_STAGE_ID", leadStageId)
            putExtra("LEAD_STAGE_STATUS", leadStageStatus)
            putExtra("ID", id)
        }

        startActivity(intent)



    }
}



