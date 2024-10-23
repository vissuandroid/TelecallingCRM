package com.pixl.crm.activity

import FollowupAdapter

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.pixl.crm.request.FollowupRequest
import com.pixl.crm.response.FollowupResponse
import com.pixl.crm.response.FollowupnumberResponse
import com.telecalling.crm.MainActivity
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

class FollowupActivity : AppCompatActivity(), FollowupAdapter.CallClickListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: FollowupAdapter
    private lateinit var followbackimge: ImageView
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notintrestedcalls)
        setupViews()
        fetchFollowupNumbers()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = ContextCompat.getColor(this, R.color.status_bar_color)
        }
    }

    private fun setupViews() {
        recyclerView = findViewById(R.id.notintreastedrecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        followbackimge = findViewById(R.id.notintrestedbackbutton_img)
        progressBar = findViewById(R.id.progressBarfollow)

        followbackimge.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun fetchFollowupNumbers() {
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
                    .header("Content-Type", "application/json")
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

        val requestBodyData = FollowupRequest(
            email = email,
            password = password
        )

        val call = apiService.getFollowupNumbers(requestBodyData)
        call.enqueue(object : Callback<FollowupResponse> {
            override fun onResponse(call: Call<FollowupResponse>, response: Response<FollowupResponse>) {
                progressBar.visibility = View.GONE

                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        val followupNumbers = responseBody.follow_ups
                        if (followupNumbers != null && followupNumbers.isNotEmpty()) {
                            adapter = FollowupAdapter(this@FollowupActivity, followupNumbers, this@FollowupActivity)
                            recyclerView.adapter = adapter
                        } else {
                            showSafeToast("No follow-up numbers found")
                        }
                    } else {
                        showSafeToast("Empty response body")
                    }
                } else {
                    showSafeToast("Failed to fetch numbers: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<FollowupResponse>, t: Throwable) {
                progressBar.visibility = View.GONE
                showSafeToast("Failed to fetch numbers: ${t.message}")
            }
        })
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

        name: String,
        number: String,
        follow_up_date: String,
        remarks: String,
        lead_stage_status: String
    ) {
        val intent = Intent(this, ViewinfoActivity::class.java).apply {

            putExtra("NAME", name)
            putExtra("NUMBER", number)
            putExtra("FOLLOW_UP_DATE", follow_up_date)
            putExtra("REMARKS", remarks)
            putExtra("LEAD_STATUS", lead_stage_status)
        }
        startActivity(intent)
    }




    private fun showSafeToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}



