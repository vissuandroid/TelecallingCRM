package com.telecalling.crm.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.pixlcallcenterapp.requests.verif_Password_Request
import com.example.pixlcallcenterapp.responces.VerifyPasswordResponse
import com.telecalling.crm.R
import com.telecalling.crm.services.Api_Interface
import com.telecalling.crm.services.PreferencesHelper
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class VerifyPasswordActivity : AppCompatActivity() {
    private lateinit var passwordEditText: EditText
    private lateinit var submitButton: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verify_password)
        passwordEditText = findViewById(R.id.verifpasswordEditText)
        submitButton = findViewById(R.id.verifsubmitButton)

        // Set click listener for the submit button
        submitButton.setOnClickListener {
            val password = passwordEditText.text.toString()
            verifyPassword(password)
        }
    }

    private fun verifyPassword(password: String) {
        val accessToken = PreferencesHelper.getAccessToken(this)

        if (accessToken.isNullOrEmpty()) {
            // Handle case where access token is null or empty
            return
        }

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val original = chain.request()
                val requestBuilder = original.newBuilder()
                    .header("Authorization", "Bearer $accessToken")
                    .header("Content-Type", "application/json")
                val request = requestBuilder.build()
                chain.proceed(request)
            }
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("http://lead.bellapp.in/auth/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()

        val api = retrofit.create(Api_Interface::class.java)

        val verifypassworddata = verif_Password_Request(password)

        api.getveryfiypassword(verifypassworddata).enqueue(object :
            retrofit2.Callback<VerifyPasswordResponse> {
            override fun onResponse(call: retrofit2.Call<VerifyPasswordResponse>, response: retrofit2.Response<VerifyPasswordResponse>) {
                if (response.isSuccessful) {

                    val intent = Intent(this@VerifyPasswordActivity, UpdatePasswordActivity::class.java)
                    startActivity(intent)

                } else {
                    Log.e("API Error", "Response code: ${response.code()}, message: ${response.message()}")
                    // Show an error message to the user
                    Toast.makeText(this@VerifyPasswordActivity, "Failed to retrieve data", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: retrofit2.Call<VerifyPasswordResponse>, t: Throwable) {
                // Handle failure
            }
        })
    }
}
