package com.telecalling.crm.fragment
//import android.app.Fragment

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.provider.CallLog
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.airbnb.lottie.LottieAnimationView
import com.example.pixlcallcenterapp.responces.FeedbackUpdateResponse
import com.example.pixlcallcenterapp.responces.Numberlist_Response
import com.example.pixlcallcenterapp.responces.UserHomescreenResponse
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.navigation.NavigationView
import com.pixl.crm.MainActivity
import com.pixl.crm.activity.FollowupActivity
import com.pixl.pixltalknewapp.R
import com.telecalling.crm.NetworkUtils.isNetworkAvailable
import com.telecalling.crm.NoInternetActivity
import com.telecalling.crm.activity.Intrestedcalls_Activity
import com.telecalling.crm.activity.LoginActivity
import com.telecalling.crm.adapter.PhoneNumbersAdapter
import com.telecalling.crm.request.UserDeyailsRequest
import com.telecalling.crm.response.PhoneNumber
import com.telecalling.crm.response.StaffDashboardResponse
import com.telecalling.crm.response.Statistics
import com.telecalling.crm.services.Api_Interface
import com.telecalling.crm.services.PreferencesHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import pl.droidsonroids.gif.GifImageView
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class CallsFragment : Fragment(),NavigationView.OnNavigationItemSelectedListener {
    private lateinit var shimmerFrameLayout: ShimmerFrameLayout
    private lateinit var startNowButton: Button
    private lateinit var callsinquetxt: TextView
    private lateinit var pauseResumeButton: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var countdownTimer: CountDownTimer
    private lateinit var completedcounttxt: TextView
    private lateinit var pendingcounttxt: TextView
    //    private lateinit var nodatalistimg: GifImageView
    private lateinit var lottieAnimationView: LottieAnimationView


    private lateinit var intreastedcounttxt: TextView
    private lateinit var notintreastedcounttxt: TextView
    private lateinit var compltedcall: LinearLayoutCompat
    private lateinit var pendingcall: LinearLayoutCompat
    private lateinit var intrestedcall: LinearLayoutCompat
    private lateinit var followups: LinearLayoutCompat
    private var nameText: TextView? = null
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var hamburgerIcon: ImageView
    private var currentIndex = 0
    private lateinit var navigationView: NavigationView
    private var isPaused = false
    private lateinit var powerlogout: ImageView
    private lateinit var phoneNumbersAdapter: PhoneNumbersAdapter
    private val RECORD_AUDIO_PERMISSION_REQUEST_CODE = 1002
    private var navigationCommunicator: MainActivity.NavigationCommunicator? = null
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var progressBar: ProgressBar
    private lateinit var profileImageView: de.hdodenhof.circleimageview.CircleImageView
    private lateinit var changePhotoIcon: ImageView
    private val PICK_IMAGE_REQUEST = 1
    private var callDuration: Long = 0L
    private lateinit var telephonyManager: TelephonyManager
    private var feedbackPopupShown = false
    private val REQUEST_READ_PHONE_STATE = 1001
    private var callduration: String = ""


    private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.READ_CALL_LOG)
    private val REQUEST_CODE_PERMISSIONS = 1001


    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context is MainActivity.NavigationCommunicator) {
            navigationCommunicator = context
        } else {
            throw RuntimeException("$context must implement NavigationCommunicator")
        }
    }
    companion object {
        private const val CALL_PHONE_PERMISSION_REQUEST_CODE = 1003
        private const val CALL_DELAY_SECONDS = 1L

    }

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_calls, container, false)
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_PHONE_STATE)
            != PackageManager.PERMISSION_GRANTED) {
            // If not granted, request the permission
            ActivityCompat.requestPermissions(requireActivity(),
                arrayOf(Manifest.permission.READ_PHONE_STATE),
                REQUEST_READ_PHONE_STATE)
        } else {
            // If granted, register the phone state listener
            registerPhoneStateListener()
        }
        shimmerFrameLayout = view.findViewById(R.id.shimmer_view_containers)
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout)

        // Set up the refresh listener using lambda
        swipeRefreshLayout.setOnRefreshListener {
            refreshContent()
        }
        // Start Shimmer
        shimmerFrameLayout.startShimmer()
        // Start Shimmer

        drawerLayout = view.findViewById(R.id.callsdrawer_layout)
        hamburgerIcon = view.findViewById(R.id.callshamburger_icon)
        powerlogout = view.findViewById(R.id.callspowericon)
        progressBar = view.findViewById(R.id.progressBar)
        navigationView = view.findViewById(R.id.callsnav_view)
        callsinquetxt  = view.findViewById(R.id.queTextView)
//        nodatalistimg  = view.findViewById(R.id.jsonImageView)
        lottieAnimationView = view.findViewById(R.id.jsonImageView)

        navigationView.setNavigationItemSelectedListener(this)

        powerlogout.setOnClickListener {
            showLogoutConfirmationDialog()

        }
        val headerView = navigationView.getHeaderView(0)
        profileImageView = headerView.findViewById(R.id.profileImage)
        changePhotoIcon = headerView.findViewById(R.id.changePhotoIcon)

        // Set click listener to change photo
        changePhotoIcon.setOnClickListener {
            openImagePicker()
        }

        hamburgerIcon.setOnClickListener {

            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {

                drawerLayout.closeDrawer(GravityCompat.START)

            } else {

                drawerLayout.openDrawer(GravityCompat.START)
            }

        }

        val userId = PreferencesHelper.getUserId(requireContext())
        nameText = view.findViewById(R.id.nameText)

        callApiAndUpdateUI()
        if (userId != null) {
            fetchPendingNumbers(userId)
            fetchStaffDashboard(userId)
        } else {
            showSafeToast("User ID is null or empty.")
        }
        initializeViews(view)
        setupRecyclerView()
        return view
    }

    private fun registerPhoneStateListener() {
        val telephonyManager = requireContext().getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        val phoneStateListener = object : PhoneStateListener() {
            override fun onCallStateChanged(state: Int, phoneNumber: String?) {
                super.onCallStateChanged(state, phoneNumber)
                // Handle call state changes here
            }
        }
        telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_CODE_PERMISSIONS -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted, proceed with the action
                    registerPhoneStateListener()
                } else {
                    // Permission denied, handle accordingly
                    Toast.makeText(requireContext(), "Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        telephonyManager = context?.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE)
    }


    private fun openImagePicker() {

        val intent = Intent(Intent.ACTION_PICK).apply {
            type = "image/*"
        }
        startActivityForResult(intent, PICK_IMAGE_REQUEST)

    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == AppCompatActivity.RESULT_OK) {
            data?.data?.let { uri ->

            }
        }
    }

    private fun refreshContent() {
        swipeRefreshLayout.isRefreshing = true
        progressBar.visibility = ProgressBar.VISIBLE

        println("refreshContent Called")

        // Launch a coroutine to perform API calls
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Call API to update UI
                callApiAndUpdateUI()

                // Retrieve user ID and fetch staff dashboard if ID is valid
                val userId = PreferencesHelper.getUserId(requireContext())
                if (userId != null) {
                    fetchStaffDashboard(userId)
                }

                // Update UI on the main thread after successful API calls
                withContext(Dispatchers.Main) {
                    // Stop the refreshing animation
                    swipeRefreshLayout.isRefreshing = false
                    progressBar.visibility = ProgressBar.GONE
                }
            } catch (e: Exception) {
                // Handle any exceptions
                withContext(Dispatchers.Main) {
                    // Show an error message or handle the error appropriately
                    showToast("Failed to refresh data")
                    swipeRefreshLayout.isRefreshing = false
                    progressBar.visibility = ProgressBar.GONE
                }
            }
        }
    }

    private fun showLogoutConfirmationDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_logout_confirmation, null)

        val alertDialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        val buttonYes = dialogView.findViewById<Button>(R.id.dialog_button_yes)
        val buttonNo = dialogView.findViewById<Button>(R.id.dialog_button_no)

        buttonYes.setOnClickListener {
            // Clear all stored data in SharedPreferences
            PreferencesHelper.clearLoginDetails(requireContext())
            PreferencesHelper.clearTokens(requireContext())

            // Redirect to the login screen
            redirectToLogin()

            // Dismiss the dialog
            alertDialog.dismiss()
        }

        buttonNo.setOnClickListener {
            // Dismiss the dialog
            alertDialog.dismiss()
        }

        alertDialog.show()
    }


    private fun redirectToLogin() {
        val loginIntent = Intent(context, LoginActivity::class.java)
        loginIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(loginIntent)
    }

    private fun callApiAndUpdateUI() {
        val email = context?.let { PreferencesHelper.getEmail(it) }
        val password = context?.let { PreferencesHelper.getPassword(it) }

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val originalRequest = chain.request()
                val requestBuilder = originalRequest.newBuilder()
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

        if (email != null && password != null) {
            val request = UserDeyailsRequest(email, password)

            apiService.Userdetailsdetails(request).enqueue(object :
                retrofit2.Callback<UserHomescreenResponse> {
                override fun onResponse(
                    call: Call<UserHomescreenResponse>,
                    response: Response<UserHomescreenResponse>
                ) {
                    if (response.isSuccessful) {
                        val userResponse = response.body()
                        if (userResponse != null) {
                            nameText?.text = userResponse.username
//                                callCount?.text = userResponse.pending_count.toString()
                        } else {
                            showToast("User response data is null")
                        }
                    } else {
                        showToast("Failed to fetch user details: ${response.code()} - ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<UserHomescreenResponse>, t: Throwable) {
                    showToast("Failed to fetch user details: ${t.message}")
                }
            })
        }

    }

    private fun showToast(message: String) {

    }

    private fun initializeViews(view: View) {
        startNowButton = view.findViewById(R.id.startNowButton)
        pauseResumeButton = view.findViewById(R.id.pauseResumeButton)
        recyclerView = view.findViewById(R.id.phoneNumbersRecyclerView)
        completedcounttxt = view.findViewById(R.id.completedcallscount_txt)
        pendingcounttxt = view.findViewById(R.id.pendingcallscount_txt)
        intreastedcounttxt = view.findViewById(R.id.intrestedcallscount_txt)
        notintreastedcounttxt = view.findViewById(R.id.notintreastedcallscount_txt)
        intrestedcall = view.findViewById(R.id.canceldelivery_Lay)
        followups = view.findViewById(R.id.returndelivery_Lay)
        startNowButton.setOnClickListener { startCallingProcess() }
        pauseResumeButton.setOnClickListener { togglePauseResume() }
        pauseResumeButton.visibility = View.GONE


        intrestedcall.setOnClickListener {
            val intent = Intent(activity, Intrestedcalls_Activity::class.java)
            startActivity(intent)
        }
        followups.setOnClickListener {
            val intent = Intent(activity, FollowupActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupRecyclerView() {
        phoneNumbersAdapter = PhoneNumbersAdapter(mutableListOf())
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = phoneNumbersAdapter
    }

    private fun fetchPendingNumbers(userId: String) {
        // Check network connectivity
        if (!isNetworkAvailable(requireContext())) {
            val intent = Intent(activity, NoInternetActivity::class.java)
            startActivity(intent)
            showSafeToast("No internet connection. Please check your network settings.")
            return
        }

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .header("Cache-Control", "no-cache")
                    .build()
                chain.proceed(request)
            }
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://app.telecallingcrm.com/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api = retrofit.create(Api_Interface::class.java)

        api.getPendingNumbers(userId).enqueue(object : retrofit2.Callback<Numberlist_Response> {
            override fun onResponse(call: Call<Numberlist_Response>, response: Response<Numberlist_Response>) {
                if (response.isSuccessful) {
                    val numbers = response.body()?.data?.mapNotNull { item ->
                        item.name?.let {
                            PhoneNumber(item.id, item.number, it)
                        }
                    } ?: listOf()

                    phoneNumbersAdapter.updatePhoneNumbers(numbers)
                    startNowButton.visibility = if (numbers.isEmpty()) View.GONE else View.VISIBLE
                    callsinquetxt.visibility = if (numbers.isEmpty()) View.GONE else View.VISIBLE

                    pauseResumeButton.visibility = View.GONE
                    if (numbers.isEmpty()) {
                        showJsonGifImage()
                    } else {
                        phoneNumbersAdapter.updatePhoneNumbers(numbers)
                        startNowButton.visibility = View.VISIBLE
                        callsinquetxt.visibility = View.VISIBLE
                        pauseResumeButton.visibility = View.GONE

//                        nodatalistimg.visibility = View.GONE
                        recyclerView.visibility = View.VISIBLE
                    }

                } else {
                    showSafeToast("Failed to fetch numbers")
                }
                shimmerFrameLayout.stopShimmer()
                shimmerFrameLayout.visibility = View.GONE
            }

            override fun onFailure(call: Call<Numberlist_Response>, t: Throwable) {
                showSafeToast("Failed to fetch numbers: ${t.message}")
            }
        })
    }


    private fun showJsonGifImage() {
        recyclerView.visibility = View.GONE
        startNowButton.visibility = View.GONE
        callsinquetxt.visibility = View.GONE

        // Show the ImageView and load your GIF
        lottieAnimationView.visibility = View.VISIBLE
//        Glide.with(requireContext())


        if (isAdded()) {
//            Glide.with(requireContext())
//                .asGif()
//                .load(R.drawable.nodatagif)
//                .into(nodatalistimg)

        }
    }

    private fun fetchStaffDashboard(userId: String) {
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .header("Cache-Control", "no-cache")
                    .build()
                chain.proceed(request)
            }
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://app.telecallingcrm.com/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()


        val api = retrofit.create(Api_Interface::class.java)

        api.getStaffDashboard(userId).enqueue(object : retrofit2.Callback<StaffDashboardResponse> {
            override fun onResponse(call: Call<StaffDashboardResponse>, response: Response<StaffDashboardResponse>) {
                if (response.isSuccessful) {
                    val staffDashboardResponse = response.body()
                    if (staffDashboardResponse != null && staffDashboardResponse.statistics != null) {
                        displayStatistics(staffDashboardResponse.statistics)
                    } else {
                        showSafeToast("Received empty statistics")
                    }
                } else {
                    showSafeToast("Failed to fetch staff dashboard: ${response.message()}")
                }
                // Stop Shimmer
                shimmerFrameLayout.stopShimmer()
                shimmerFrameLayout.visibility = View.GONE
            }

            override fun onFailure(call: Call<StaffDashboardResponse>, t: Throwable) {
                showSafeToast("Failed to fetch staff dashboard: ${t.message}")

            }
        })
    }

    private fun displayStatistics(statistics: Statistics) {
        Log.d("Statistics", "Statistics object: $statistics")
        completedcounttxt.text = statistics.called_count?.toString() ?: "0"
        pendingcounttxt.text = statistics.pending_calls?.toString() ?: "0"
        intreastedcounttxt.text = statistics.interested?.toString() ?: "0"
        notintreastedcounttxt.text = statistics.follow_up_count?.toString() ?: "0"

        // Logging for verification
        Log.d("Statistics", "Completed Calls: ${statistics.called_count}")
        Log.d("Statistics", "Pending Calls: ${statistics.pending_calls}")
        Log.d("Statistics", "Interested: ${statistics.interested}")
        Log.d("Statistics", "Not Interested: ${statistics.follow_up_count}")
    }

    private fun startCallingProcess() {
        currentIndex = 0
        isPaused = false
        startNowButton.visibility = View.GONE
        pauseResumeButton.visibility = View.VISIBLE
        pauseResumeButton.text = "Pause"
        scheduleNextCall()
    }

    private fun togglePauseResume() {
        if (::countdownTimer.isInitialized) {
            isPaused = !isPaused
            pauseResumeButton.text = if (isPaused) "Resume" else "Pause"
            if (!isPaused) {
                scheduleNextCall()
            } else {
                countdownTimer.cancel()
//                timerTextView.text = "Paused"
            }
        } else {
            Log.e("CallsFragment", "countdownTimer has not been initialized")
        }
    }

    private fun scheduleNextCall() {
        if (currentIndex < phoneNumbersAdapter.itemCount && !isPaused) {
            if (::countdownTimer.isInitialized) {
                countdownTimer.cancel()
            }

            countdownTimer = object : CountDownTimer(CALL_DELAY_SECONDS * 1000, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                }
                override fun onFinish() {
                    startCallingPhoneNumbers()
                }
            }

            countdownTimer.start()
        }
    }

    private var currentPhoneNumberId: Int? = null


    private val phoneStateListener = object : PhoneStateListener() {
        override fun onCallStateChanged(state: Int, phoneNumber: String?) {
            when (state) {
                TelephonyManager.CALL_STATE_IDLE -> {
                    Handler(Looper.getMainLooper()).postDelayed({
                        if(currentIndex!=0){
                            showFeedbackPopup()
                        }
                    },1L * 1000)
                    scheduleNextCall()
                }
                TelephonyManager.CALL_STATE_OFFHOOK -> {
                    // Call is active
                }
                TelephonyManager.CALL_STATE_RINGING -> {
                    // Incoming call
                }
            }
        }
    }


    private fun startCallingPhoneNumbers() {
        if (currentIndex < phoneNumbersAdapter.itemCount && !isPaused) {
            val phoneNumber = phoneNumbersAdapter.phoneNumbers[currentIndex]
            currentPhoneNumberId = phoneNumber.id
            val callIntent = Intent(Intent.ACTION_CALL).apply {
                data = Uri.parse("tel:${phoneNumber.number}")
            }
            // Check for CALL_PHONE permission
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {
                // If not granted, request the permission
                ActivityCompat.requestPermissions(requireActivity(),
                    arrayOf(Manifest.permission.READ_PHONE_STATE),
                    REQUEST_READ_PHONE_STATE)
            } else {
                // If granted, register the phone state listener
                registerPhoneStateListener()
            }
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.CALL_PHONE), CALL_PHONE_PERMISSION_REQUEST_CODE)
            } else {
                try {
                    requireActivity().startActivity(callIntent)
                    currentIndex++

                    // If you reach the end of the list, reset the index
                    if (currentIndex >= phoneNumbersAdapter.itemCount) {
                        currentIndex = 0
                    }

                } catch (e: SecurityException) {
                    showSafeToast("Permission issue: Cannot make the call.")
                    Log.e("MessagesFragment", "SecurityException during calling: ${e.message}")
                } catch (e: Exception) {
                    showSafeToast("Unable to make the call. Please check the phone number.")
                    Log.e("MessagesFragment", "Exception during calling: ${e.message}")
                }
            }
        }
    }

    private fun showFeedbackPopup() {
        // Use StringBuilder to construct the feedback message
        val sb = StringBuilder()
        val contactsUri = CallLog.Calls.CONTENT_URI

        Log.v("get duration", "called")
        val cursor: Cursor? = requireContext().contentResolver.query(
            contactsUri, null, null, null,
            "${CallLog.Calls.DATE} DESC"
        )

        cursor?.use {
            if (it.moveToFirst()) {
                val numberIndex = it.getColumnIndex(CallLog.Calls.NUMBER)
                val durationIndex = it.getColumnIndex(CallLog.Calls.DURATION)
                val phNumber = it.getString(numberIndex)
                val callDuration = it.getString(durationIndex)
//                sb.append("Call Details : \n")
                sb.append("Duration: $callDuration seconds")
            }
        }
        val result = sb.toString()
        callduration=result
        Log.v("result", result)
//        Toast.makeText(requireContext(), result, Toast.LENGTH_SHORT).show()
        if (feedbackPopupShown) return // Prevent showing the popup again until a new call ends

        feedbackPopupShown = true


        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.feedbackoption_layout, null)
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setTitle("")

        val alertDialog = alertDialogBuilder.create()
        alertDialog.setCancelable(false)  // Prevent dialog from being canceled when touching outside
        alertDialog.show()


        // Find and set the call duration TextView in the dialog
        val callDurationTextView = dialogView.findViewById<TextView>(R.id.callDurationTextView)
        callDurationTextView.text = "$callduration"

        val submitButton = dialogView.findViewById<Button>(R.id.submitFeedbackButton)
        val feedbackOptionsRadioGroup = dialogView.findViewById<RadioGroup>(R.id.feedbackOptionsRadioGroup)

        submitButton.setOnClickListener {
            feedbackPopupShown = false
            val selectedOptionId = feedbackOptionsRadioGroup.checkedRadioButtonId

            if (selectedOptionId != -1) {
                // Get the selected call status
                val callStatus = when (selectedOptionId) {
                    R.id.option_notlifting -> "not_lifting"
                    R.id.option_notinterested -> "not_interested"
                    R.id.option_interested -> "interested"
                    R.id.option_incorrect -> "notcorrect_number"
                    else -> "unknown"
                }

                // Ensure that there is a valid call status before sending feedback
                if (callStatus.isNotEmpty()) {
                    currentPhoneNumberId?.let { id ->
                        sendFeedbackToApi(id, callStatus,callduration)
                    }
                    alertDialog.dismiss() // Dismiss the dialog after successful feedback submission
                } else {
                    Toast.makeText(requireContext(), "Feedback cannot be empty", Toast.LENGTH_SHORT).show()
                }
            } else {
                // Show toast if no option is selected
                Toast.makeText(requireContext(), "Please select an option", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun sendFeedbackToApi(id: Int, callStatus: String,duration:String) {
        val context = requireContext()

        val email = PreferencesHelper.getEmail(context)
        val password = PreferencesHelper.getPassword(context)

        if (email != null && password != null) {
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

            val api = retrofit.create(Api_Interface::class.java)

            api.updateCallStatus(id, callStatus, email, password,duration).enqueue(object :
                retrofit2.Callback<FeedbackUpdateResponse> {
                override fun onResponse(call: Call<FeedbackUpdateResponse>, response: Response<FeedbackUpdateResponse>) {
                    if (response.isSuccessful) {
                        val userId = PreferencesHelper.getUserId(requireContext())
                        if (userId != null) {
                            fetchPendingNumbers(userId)
                            fetchStaffDashboard(userId)
                        }
                        showSafeToast("Updated successfully.")


                    } else {
                        showSafeToast("Failed to update leads.")
                    }
                }

                override fun onFailure(call: Call<FeedbackUpdateResponse>, t: Throwable) {
                    showSafeToast("Failed to update leads.")


                }
            })
        } else {
            showSafeToast("User not logged in.")
        }
    }


    private fun showSafeToast(message: String) {
        context?.let { ctx ->
            Toast.makeText(ctx, message, Toast.LENGTH_LONG).show()
        } ?: run {
            Log.e("MessagesFragment", "Context is null. Unable to show toast message.")
        }
    }

    override fun onPause() {
        super.onPause()
        isPaused = true
        if (::countdownTimer.isInitialized) {
            countdownTimer.cancel()
        }
        pauseResumeButton.text = "Resume"
    }
    override fun onDestroy() {
        super.onDestroy()
        telephonyManager?.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE)
    }
    override fun onDestroyView() {
        super.onDestroyView()
        if (::countdownTimer.isInitialized) {
            countdownTimer.cancel()
        }
    }

    override fun onNavigationItemSelected(p0: MenuItem): Boolean {

        when (p0.itemId) {
            R.id.nv_home -> navigationCommunicator?.onNavigate("home")
            R.id.nv_followup -> navigationCommunicator?.onNavigate("followup")
            R.id.nv_toppers -> navigationCommunicator?.onNavigate("toppers")
            R.id.nv_deal -> navigationCommunicator?.onNavigate("deal")
            R.id.nv_leads -> navigationCommunicator?.onNavigate("leads")
            R.id.nv_changepass -> navigationCommunicator?.onNavigate("Change Password")
            R.id.nv_reports -> navigationCommunicator?.onNavigate("My Reports")

        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

}