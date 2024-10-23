package com.telecalling.crm.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pixlcallcenterapp.requests.IntrestedcallsRequest
import com.example.pixlcallcenterapp.responces.Intrested_CallsResponse
import com.example.pixlcallcenterapp.responces.LeadResponse_from_Intrested
import com.telecalling.crm.R
import com.telecalling.crm.adapter.IntrestescallAdapter
import com.telecalling.crm.services.Api_Interface
import com.telecalling.crm.services.PreferencesHelper
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class Pending_Calls_Activity : AppCompatActivity() {
    private lateinit var statusSpinner: Spinner
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: IntrestescallAdapter
    private lateinit var pendingbackimge: ImageView
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pending_calls)

        pendingbackimge= findViewById(R.id.pendingbackbutton_img)
        pendingbackimge.setOnClickListener {

            onBackPressed()

        }
        statusSpinner = findViewById(R.id.pending_dateOptionsSpinner)
        recyclerView = findViewById(R.id.pendingrecyclerView)

        recyclerView.layoutManager = LinearLayoutManager(this)

        val periodOptions = arrayOf("this_week", "last_week", "this_month", "last_month", "last_30", "last_3_months")
        statusSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, periodOptions)

//        adapter = IntrestescallAdapter(emptyList()) // Initialize the adapter here
        recyclerView.adapter = adapter

        statusSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val period = periodOptions[position]
//                callApi(period, "pending")
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

//    private fun callApi(period: String, status: String) {
//        val accessToken = PreferencesHelper.getAccessToken(this)
//
//        if (accessToken.isNullOrEmpty()) {
//            // Handle case where access token is null or empty
//            return
//        }
//
//        val okHttpClient = OkHttpClient.Builder()
//            .addInterceptor { chain ->
//                val original = chain.request()
//                val requestBuilder = original.newBuilder()
//                    .header("Authorization", "Bearer $accessToken")
//                    .header("Content-Type", "application/json")
//                val request = requestBuilder.build()
//                chain.proceed(request)
//            }
//            .build()
//
//        val retrofit = Retrofit.Builder()
//            .baseUrl("http://lead.bellapp.in/api/")
//            .addConverterFactory(GsonConverterFactory.create())
//            .client(okHttpClient)
//            .build()
//
//        val api = retrofit.create(Api_Interface::class.java)
//
//        val interestedcallsdata = IntrestedcallsRequest(period, status)
//
//        api.getLeadHistory(interestedcallsdata).enqueue(object : Callback<Intrested_CallsResponse> {
//            override fun onResponse(call: Call<Intrested_CallsResponse>, response: Response<Intrested_CallsResponse>) {
//                if (response.isSuccessful) {
//                    val responseData = response.body()
//                    if (responseData != null) {
//                        val leadList = responseData.data?.map {
//                            LeadResponse_from_Intrested(it.id, it.number, it.name, it.status)
//                        } ?: listOf()
//                        runOnUiThread {
//                            if (leadList.isEmpty()) {
//                                Toast.makeText(this@Pending_Calls_Activity, "No Leads Found", Toast.LENGTH_SHORT).show()
//                            }
//                            adapter.setData(leadList)
//                        }
//                    } else {
//                        // Handle case where response body is null
//                    }
//                } else {
//                    // Handle unsuccessful response
//                }
//            }
//
//            override fun onFailure(call: Call<Intrested_CallsResponse>, t: Throwable) {
//                // Handle failure
//            }
//        })
//    }
}