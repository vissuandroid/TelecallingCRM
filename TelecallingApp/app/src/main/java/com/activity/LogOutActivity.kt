package com.telecalling.crm.activity


import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.pixlcallcenterapp.requests.LoginRequest
import com.example.pixlcallcenterapp.responces.LoginResponse
import com.telecalling.crm.MainActivity

import com.telecalling.crm.R
import com.telecalling.crm.services.Api_Interface
import com.telecalling.crm.services.Client_Api
import com.telecalling.crm.services.PreferencesHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LogOutActivity : AppCompatActivity() {
    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var Changepassword: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_out)
        usernameEditText = findViewById(R.id.usernameEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        loginButton = findViewById(R.id.loginButton)
        Changepassword= findViewById(R.id.changePasswordButton)

        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR)
        window.statusBarColor = Color.WHITE
        window.navigationBarColor = Color.WHITE







        Changepassword.setOnClickListener {

        }







        loginButton.setOnClickListener {
            val email = usernameEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (validateInput(email, password)) {
                login(email, password)
            }
            if (validateInput(email, password)) {
                PreferencesHelper.saveLoginDetails(this, email, password)
                login(email, password)
            }
        }
    }

    private fun validateInput(email: String, password: String): Boolean {
        if (email.isEmpty()) {
            usernameEditText.error = "Username is required"
            return false
        }
        if (password.isEmpty()) {
            passwordEditText.error = "Password is required"
            return false
        }
        return true
    }

    private fun login(email: String, password: String) {
        val apiService = Client_Api.instance.create(Api_Interface::class.java)
        val call = apiService.login(LoginRequest(email, password))

        call.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    if (loginResponse?.success == true) {
                        Toast.makeText(this@LogOutActivity, "Login Successful", Toast.LENGTH_SHORT).show()
                        val userId = loginResponse.user_id.toString() // Assuming user_id is Int
                        PreferencesHelper.saveUserId(this@LogOutActivity, userId)
                        // Navigate to StatisticsActivity and pass user_id
                        val intent = Intent(this@LogOutActivity, MainActivity::class.java)
                        intent.putExtra("USER_ID", userId)
                        startActivity(intent)
                        finish() // Optional, if you don't want to keep LoginActivity in back stack
                    } else {
                        Toast.makeText(this@LogOutActivity, "Login Failed: ${loginResponse?.success}", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@LogOutActivity, "Login Failed", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Toast.makeText(this@LogOutActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}

