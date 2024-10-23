package com.pixl.crm.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.navigation.NavigationView
import com.pixl.crm.adapter.LeaderboardAdapter
import com.pixl.crm.response.LeaderboardResponse
import com.telecalling.crm.MainActivity
import com.telecalling.crm.NavigationCommunicator
import com.telecalling.crm.*
import com.telecalling.crm.activity.LoginActivity
import com.telecalling.crm.services.Api_Interface
import com.telecalling.crm.services.PreferencesHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ToppersFragment : Fragment(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var shimmerFrameLayout: ShimmerFrameLayout
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: LeaderboardAdapter
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var powerlogout: ImageView
    private var navigationCommunicator: NavigationCommunicator? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is NavigationCommunicator) {
            navigationCommunicator = context
        } else {
            throw RuntimeException("$context must implement NavigationCommunicator")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_toppers, container, false)

        powerlogout = view.findViewById(R.id.topperspowericon)

        val navigationView: NavigationView = view.findViewById(R.id.navigation_view)
        navigationView.setNavigationItemSelectedListener(this)

        powerlogout.setOnClickListener {
            showLogoutConfirmationDialog()
        }

        drawerLayout = view.findViewById(R.id.toppersdrawer_layout)
        val hamburgerIcon: ImageView = view.findViewById(R.id.hamburger_icon)

        hamburgerIcon.setOnClickListener {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START)
            } else {
                drawerLayout.openDrawer(GravityCompat.START)
            }
        }

        shimmerFrameLayout = view.findViewById(R.id.shimmer_view_container)
        recyclerView = view.findViewById(R.id.recyclerViewTopper)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Start shimmer effect
        shimmerFrameLayout.startShimmer()

        fetchLeaderboard()
        return view
    }

    private fun showLogoutConfirmationDialog() {
        AlertDialog.Builder(requireContext()).apply {
            setTitle("Logout")
            setMessage("Do you want to log out?")
            setPositiveButton("Yes") { dialog, _ ->
                // Clear all stored data in SharedPreferences
                PreferencesHelper.clearLoginDetails(requireContext())
                PreferencesHelper.clearTokens(requireContext())

                // Redirect to the login screen
                redirectToLogin()

                // Dismiss the dialog
                dialog.dismiss()
            }
            setNegativeButton("No") { dialog, _ ->
                // Dismiss the dialog
                dialog.dismiss()
            }
            create().show()
        }
    }

    private fun redirectToLogin() {
        val loginIntent = Intent(context, LoginActivity::class.java)

        loginIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(loginIntent)
    }

    private fun fetchLeaderboard() {
        val apiService = Retrofit.Builder()
            .baseUrl("https://app.telecallingcrm.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(Api_Interface::class.java)

        val params = mapOf("email" to "ayesha@pixl.in", "password" to "123456")

        apiService.getLeaderboard(params).enqueue(object : Callback<LeaderboardResponse> {
            override fun onResponse(
                call: Call<LeaderboardResponse>,
                response: Response<LeaderboardResponse>
            ) {
                if (response.isSuccessful) {
                    val leaderboard = response.body()?.leaderboard ?: emptyList()
                    adapter = LeaderboardAdapter(leaderboard)
                    recyclerView.adapter = adapter

                    // Stop shimmer effect and show RecyclerView
                    shimmerFrameLayout.stopShimmer()
                    shimmerFrameLayout.visibility = View.GONE
                    recyclerView.visibility = View.VISIBLE
                } else {
                    Toast.makeText(requireContext(), "Failed to load data", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            override fun onFailure(call: Call<LeaderboardResponse>, t: Throwable) {
                Toast.makeText(
                    requireContext(),
                    "Failed to load data: ${t.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nv_home -> navigationCommunicator?.onNavigate("home")
            R.id.nv_followup -> navigationCommunicator?.onNavigate("followup")
            R.id.nv_toppers -> navigationCommunicator?.onNavigate("toppers")
            R.id.nv_deal -> navigationCommunicator?.onNavigate("deal")
            R.id.nv_leads -> navigationCommunicator?.onNavigate("leads")
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onDetach() {
        super.onDetach()
        navigationCommunicator = null
    }
}


