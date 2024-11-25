package com.telecalling.crm
import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.pixl.crm.fragment.Follwupsfragment
import com.pixl.crm.fragment.IntrestedcallsFragment
import com.pixl.crm.fragment.ToppersFragment
import com.telecalling.crm.fragment.CallsFragment
import android.content.Intent
import android.provider.Settings
import android.net.Uri
import android.util.Log


class MainActivity : AppCompatActivity(), NavigationCommunicator {
    private val PERMISSION_REQUEST_CODE = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Check and request permissions
        if (arePermissionsGranted()) {
            setupUI()
        } else {
            requestPermissions()
        }
    }


//    private fun registerNetworkCallback() {
//        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            connectivityManager.registerDefaultNetworkCallback(object : ConnectivityManager.NetworkCallback() {
//                override fun onAvailable(network: Network) {
//                    super.onAvailable(network)
//                    // Network is available
//                    Log.d("NetworkCallback", "Network is available")
//                    println("Network is available")
//                }
//
//                override fun onLost(network: Network) {
//                    super.onLost(network)
//                    // Network is lost
//                    Log.d("NetworkCallback", "Network is lost")
//                    println("Network is lost")
//                    startActivity(Intent(this@MainActivity, NoInternetActivity::class.java))
//                }
//            })
//        } else {
//            Log.d("NetworkCallback1", "Network is lost1")
//            // Fallback to BroadcastReceiver for devices below Lollipop
//            val filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
//            registerReceiver(networkChangeReceiver, filter)
//        }
//    }



    private fun arePermissionsGranted(): Boolean {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.READ_CALL_LOG, Manifest.permission.CALL_PHONE),
            PERMISSION_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                setupUI() // Permissions granted, setup the UI
            } else {
                Toast.makeText(this, "Permissions are required to use this app", Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri = Uri.fromParts("package", packageName, null)
                intent.data = uri
                startActivity(intent)
                finish()
            }
        }
    }


    private fun setupUI() {
        // Initialize BottomNavigationView
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.setItemIconTintList(null)

        // Set initial fragment
        loadFragment(CallsFragment())

        // Set color for navigation items
        val colorStateList = ContextCompat.getColorStateList(this, R.color.baseappcolor)
        bottomNavigationView.itemIconTintList = colorStateList
        bottomNavigationView.itemTextColor = colorStateList

        // Set up BottomNavigationView listener
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_calls -> loadFragment(CallsFragment())
                R.id.nav_topper -> loadFragment(ToppersFragment())
                R.id.nav_Lead -> loadFragment(IntrestedcallsFragment())
                R.id.nav_Followups -> loadFragment(Follwupsfragment())
                else -> loadFragment(HomeFragment())
            }
            true
        }

        // Set up the status bar color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = ContextCompat.getColor(this, R.color.status_bar_color)
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }


    override fun onNavigate(tag: String) {
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        when (tag) {
            "home" -> {
                bottomNavigationView.selectedItemId = R.id.nav_calls
                loadFragment(CallsFragment())
            }
            "followup" -> {
                bottomNavigationView.selectedItemId = R.id.nav_Followups
                loadFragment(Follwupsfragment())
            }
            "toppers" -> {
                bottomNavigationView.selectedItemId = R.id.nav_topper
                loadFragment(ToppersFragment())
            }
            "deal" -> {
                bottomNavigationView.selectedItemId = R.id.nav_Lead
                loadFragment(IntrestedcallsFragment())
            }
            "leads" -> {
                bottomNavigationView.selectedItemId = R.id.nav_Lead
                loadFragment(IntrestedcallsFragment())
                // Handle leads fragment if applicable
            }
        }
    }
//
//    private val networkChangeReceiver = NetworkChangeReceiver { isConnected ->
//        if (isConnected) {
//
//        } else {
//            Log.d("No Internet","No Internet connection")
//            startActivity(Intent(this, NoInternetActivity::class.java))
//        }
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        // Unregister the receiver
//        unregisterReceiver(networkChangeReceiver)
//    }





}

interface NavigationCommunicator {
    fun onNavigate(tag: String)
}

