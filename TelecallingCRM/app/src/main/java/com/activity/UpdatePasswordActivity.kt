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
import com.example.pixlcallcenterapp.requests.Changepassword_Request
import com.example.pixlcallcenterapp.responces.Changepassword_Response
import com.telecalling.crm.MainActivity
import com.telecalling.crm.R
import com.telecalling.crm.services.Api_Interface
import com.telecalling.crm.services.PreferencesHelper
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class UpdatePasswordActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_update_password)
        val password = findViewById<EditText>(R.id.newPassword)
        val confirm_password = findViewById<EditText>(R.id.confirmPassword)
        val submitButton = findViewById<Button>(R.id.submitButton)

        submitButton.setOnClickListener {
            val password = password.text.toString()
            val confirm_password = confirm_password.text.toString()

            if (password == confirm_password) {
                // Perform update password request using API
                updatePassword(password,confirm_password,)
            } else {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updatePassword(password: String, confirm_password: String) {
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

        val changepassworddata = Changepassword_Request(password,confirm_password)

        api.changepassword(changepassworddata).enqueue(object :
            retrofit2.Callback<Changepassword_Response> {
            override fun onResponse(call: retrofit2.Call<Changepassword_Response>, response: retrofit2.Response<Changepassword_Response>) {
                if (response.isSuccessful) {

                    val intent = Intent(this@UpdatePasswordActivity, MainActivity::class.java)
                    startActivity(intent)

                } else {
                    Log.e("API Error", "Response code: ${response.code()}, message: ${response.message()}")
                    // Show an error message to the user
                    Toast.makeText(this@UpdatePasswordActivity, "Failed to retrieve data", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: retrofit2.Call<Changepassword_Response>, t: Throwable) {
                // Handle failure
            }
        })
    }
}
