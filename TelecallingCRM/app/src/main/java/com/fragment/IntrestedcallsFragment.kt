package com.pixl.crm.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
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
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.navigation.NavigationView
import com.pixl.crm.activity.ViewinfoActivity
import com.telecalling.crm.NavigationCommunicator
import com.telecalling.crm.R
import com.telecalling.crm.activity.LoginActivity
import com.telecalling.crm.adapter.IntrestescallAdapter
import com.telecalling.crm.request.IntrestedcallsListRequest
import com.telecalling.crm.response.Instrested_calls_list_Response
import com.telecalling.crm.services.Api_Interface
import com.telecalling.crm.services.PreferencesHelper
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class IntrestedcallsFragment : Fragment(), IntrestescallAdapter.IntrestedCallClickListener, NavigationView.OnNavigationItemSelectedListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: IntrestescallAdapter
    private lateinit var progressBar: ProgressBar
    private lateinit var hamburgerIcon: ImageView
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var powerLogout: ImageView
    private lateinit var shimmerFrameLayout: ShimmerFrameLayout
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
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_intrestedcalls, container, false)
        shimmerFrameLayout = view.findViewById(R.id.shimmer_view_container)
        drawerLayout = view.findViewById(R.id.leadsdrawer_layout)
        hamburgerIcon = view.findViewById(R.id.leadshamburger_icon)
        powerLogout = view.findViewById(R.id.leadspowericon)
        navigationView = view.findViewById(R.id.navigation_view)
        navigationView.setNavigationItemSelectedListener(this)

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

        setupViews(view)
        setStatusBarColor()
        fetchPendingNumbers()

        return view
    }

    private fun setupViews(view: View) {
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        progressBar = view.findViewById(R.id.progressBar)
    }

    private fun fetchPendingNumbers() {
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
                    .header("Content-Type", "application/x-www-form-urlencoded")
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

        val requestBodyData = IntrestedcallsListRequest(
            action = "login",
            email = email,
            password = password
        )

        val call = apiService.getintrestedNumbers(requestBodyData)
        call.enqueue(object : Callback<Instrested_calls_list_Response> {
            override fun onResponse(
                call: Call<Instrested_calls_list_Response>,
                response: Response<Instrested_calls_list_Response>
            ) {
                // Ensure the fragment is attached
                if (!isAdded) return

                // Stop shimmer effect and show RecyclerView
                shimmerFrameLayout.stopShimmer()
                shimmerFrameLayout.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE

                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        val numbers = responseBody.phone_numbers ?: emptyList()
                        adapter = IntrestescallAdapter(requireContext(), this@IntrestedcallsFragment, numbers)
                        recyclerView.adapter = adapter
                    } else {
                        showSafeToast("Empty response body")
                    }
                } else {
                    showSafeToast("Failed to fetch numbers: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<Instrested_calls_list_Response>, t: Throwable) {
                // Ensure the fragment is attached
                if (!isAdded) return

                // Stop shimmer effect and show RecyclerView
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
        followUpDate: String,
        calledStatus: String,
        callStatus: String,
        dateAdded: String,
        dealStatus: String,
        dealAmount: Double,
        totalCalls: Int,
        leadStageId: Int,
        leadStageStatus: String,
        id: Int,



    ) {
        val intent = Intent(requireContext(), ViewinfoActivity::class.java).apply {

            putExtra("NUMBER", number)
            putExtra("NAME", name)
            putExtra("REMARKS", remarks)
            putExtra("FOLLOW_UP_DATE", followUpDate)
            putExtra("CALLED_STATUS", calledStatus)
            putExtra("CALL_STATUS", callStatus)
            putExtra("DATE_ADDED", dateAdded)
            putExtra("DEAL_STATUS", dealStatus)
            putExtra("DEAL_AMOUNT", dealAmount)
            putExtra("TOTAL_CALLS", totalCalls)
            putExtra("LEAD_STAGE_ID", leadStageId)
            putExtra("LEAD_STAGE_STATUS", leadStageStatus)
            putExtra("ID", id)
        }
        startActivity(intent)
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

