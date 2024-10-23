package com.telecalling.crm.activity

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.pixl.crm.activity.OnboardingscreenActivity
import com.telecalling.crm.MainActivity
import com.telecalling.crm.R
import com.telecalling.crm.services.PreferencesHelper

class Welcome_Screen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_welcome_screen)

        // Find your ImageView for Glide to load the GIF
        val imageView = findViewById<ImageView>(R.id.splashImageView)
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR)
        window.statusBarColor = Color.WHITE
        window.navigationBarColor = Color.WHITE
        Glide.with(this)
            .asGif()
            .load(R.drawable.walk) // Replace with your GIF file
            .into(imageView) // Set the GIF into the ImageView

        // Delay for 2 seconds and then navigate to the appropriate activity
        Handler(Looper.getMainLooper()).postDelayed({

                navigateToLoginActivity()

        }, 2000) // 2 seconds delay
    }

    private fun navigateToLoginActivity() {
        val userId =  PreferencesHelper.getUserId(this)
        if (userId.isNullOrEmpty()) {
            // User ID is null or empty, navigate to LoginActivity
            val intent = Intent(this, OnboardingscreenActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            // User ID is not null or empty, navigate to HomeActivity
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun enableEdgeToEdge() {
        // Enable edge-to-edge UI flags
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                )
        window.statusBarColor = Color.TRANSPARENT
        window.navigationBarColor = Color.TRANSPARENT
    }
}
