package com.pixl.crm.fragment

import FollowupAdapter
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.navigation.NavigationView
import com.pixl.crm.activity.ViewinfoActivity
import com.pixl.crm.request.FollowupRequest
import com.pixl.crm.response.FollowupResponse
import com.telecalling.crm.MainActivity
import com.telecalling.crm.NavigationCommunicator
import com.telecalling.crm.R
import com.telecalling.crm.activity.LoginActivity
import com.telecalling.crm.services.Api_Interface
import com.telecalling.crm.services.PreferencesHelper
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class Follwupsfragment : Fragment(), FollowupAdapter.CallClickListener, NavigationView.OnNavigationItemSelectedListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: FollowupAdapter
    private lateinit var shimmerFrameLayout: ShimmerFrameLayout
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var hamburgerIcon: ImageView
    private lateinit var powerLogout: ImageView
    private lateinit var navigationView: NavigationView
    private lateinit var progressBar: ProgressBar
    private var navigationCommunicator: NavigationCommunicator? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is NavigationCommunicator) {
            navigationCommunicator = context
        } else {
            throw RuntimeException("$context must implement NavigationCommunicator")
        }
    }

    override fun onDetach() {
        super.onDetach()
        navigationCommunicator = null
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_follwupsfragment, container, false)

        shimmerFrameLayout = view.findViewById(R.id.shimmer_view_containerf)
        drawerLayout = view.findViewById(R.id.followupdrawer_layout)
        hamburgerIcon = view.findViewById(R.id.followuphamburger_icon)
        powerLogout = view.findViewById(R.id.followuppowericon)
        navigationView = view.findViewById(R.id.navigation_view)

        powerLogout.setOnClickListener {
            showLogoutConfirmationDialog()
        }

        hamburgerIcon.setOnClickListener {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START)
            } else {
                drawerLayout.openDrawer(GravityCompat.START)
            }
        }

        navigationView.setNavigationItemSelectedListener(this)

        setupViews(view)
        fetchFollowupNumbers()
        setStatusBarColor()

        return view
    }

    private fun setupViews(view: View) {
        recyclerView = view.findViewById(R.id.notintreastedrecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)
        progressBar = view.findViewById(R.id.progressBar)
    }

    private fun fetchFollowupNumbers() {
        val email = PreferencesHelper.getEmail(requireContext())
        val password = PreferencesHelper.getPassword(requireContext())

        if (email.isNullOrEmpty() || password.isNullOrEmpty()) {
            showSafeToast("Email or Password is missing")
            return
        }

        // Start shimmer effect and hide RecyclerView while loading data
        shimmerFrameLayout.startShimmer()
        shimmerFrameLayout.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE

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
                shimmerFrameLayout.stopShimmer()
                shimmerFrameLayout.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE

                if (!isAdded) return
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        val followupNumbers = responseBody.follow_ups
                        if (followupNumbers != null && followupNumbers.isNotEmpty()) {
                            adapter = FollowupAdapter(requireContext(), followupNumbers, this@Follwupsfragment)
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
                shimmerFrameLayout.stopShimmer()
                shimmerFrameLayout.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
                showSafeToast("Failed to fetch numbers: ${t.message}")
            }
        })
    }

    private fun showLogoutConfirmationDialog() {
        AlertDialog.Builder(requireContext()).apply {
            setTitle("Logout")
            setMessage("Do you want to log out?")
            setPositiveButton("Yes") { dialog, _ ->
                PreferencesHelper.clearLoginDetails(requireContext())
                PreferencesHelper.clearTokens(requireContext())
                redirectToLogin()
                dialog.dismiss()
            }
            setNegativeButton("No") { dialog, _ ->
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

    private fun setStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            activity?.window?.statusBarColor = ContextCompat.getColor(requireContext(), R.color.status_bar_color)
        }
    }

    private fun showSafeToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
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
        follow_up_date: String,
        lead_stage_status: String
    ) {
        // Handle info click
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
}
