package com.telecalling.crm
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.lottie.LottieAnimationView
import android.widget.Toast


class NoInternetActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_no_network)

        val lottieAnimationView: LottieAnimationView = findViewById(R.id.lottieAnimationView)
         lottieAnimationView.playAnimation()
        val retryButton: Button = findViewById(R.id.retryButton)

        retryButton.setOnClickListener {
            if (isInternetAvailable()) {
                // Restart the MainActivity or the previous activity
                startActivity(Intent(this, MainActivity::class.java))
            } else {
                // Show a toast or keep the user on the same screen
                showToast("No internet connection. Please try again.")
            }
        }
    }

    private fun isInternetAvailable(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetwork
        val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
        return networkCapabilities != null && networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
