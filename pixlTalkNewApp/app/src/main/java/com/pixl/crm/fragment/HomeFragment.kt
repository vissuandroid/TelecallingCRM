package com.telecalling.crm
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.pixlcallcenterapp.responces.UserHomescreenResponse
import com.telecalling.crm.request.UserDeyailsRequest
import com.telecalling.crm.services.Api_Interface
import com.telecalling.crm.services.PreferencesHelper
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class HomeFragment : Fragment() {

//    private var username: TextView? = null
//    private var nameText: TextView? = null
//    private var callLabel: TextView? = null
//    private var callCount: TextView? = null
//    private var startimg: ImageView? = null
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        val view = inflater.inflate(R.layout.fragment_home, container, false)
//        username = view.findViewById(R.id.username)
//        nameText = view.findViewById(R.id.nameText)
//        callLabel = view.findViewById(R.id.callLabel)
//        callCount = view.findViewById(R.id.callCount)
//        startimg = view.findViewById(R.id.centerImage)
//        callApiAndUpdateUI()
//        return view
//    }
//
//    private fun callApiAndUpdateUI() {
//        val email = context?.let { PreferencesHelper.getEmail(it) }
//        val password = context?.let { PreferencesHelper.getPassword(it) }
//
//        val okHttpClient = OkHttpClient.Builder()
//            .addInterceptor { chain ->
//                val originalRequest = chain.request()
//                val requestBuilder = originalRequest.newBuilder()
//                val request = requestBuilder.build()
//                chain.proceed(request)
//            }
//            .build()
//
//        val retrofit = Retrofit.Builder()
//            .baseUrl("https://app.telecallingcrm.com/")
//            .addConverterFactory(GsonConverterFactory.create())
//            .client(okHttpClient)
//            .build()
//
//        val apiService = retrofit.create(Api_Interface::class.java)
//
//        if (email != null && password != null) {
//            val request = UserDeyailsRequest(email, password)
//
//            apiService.Userdetailsdetails(request).enqueue(object : Callback<UserHomescreenResponse> {
//                override fun onResponse(
//                    call: Call<UserHomescreenResponse>,
//                    response: Response<UserHomescreenResponse>
//                ) {
//                    if (response.isSuccessful) {
//                        val userResponse = response.body()
//                        if (userResponse != null) {
//                            nameText?.text = userResponse.username
//                            callCount?.text = userResponse.pending_count.toString()
//                        } else {
//                            showToast("User response data is null")
//                        }
//                    } else {
//                        showToast("Failed to fetch user details: ${response.code()} - ${response.message()}")
//                    }
//                }
//
//                override fun onFailure(call: Call<UserHomescreenResponse>, t: Throwable) {
//                    showToast("Failed to fetch user details: ${t.message}")
//                }
//            })
//        }
//    }
//
//    private fun showToast(message: String) {
//        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
//    }
//
//    override fun onDestroyView() {
//        super.onDestroyView()
//        username = null
//        nameText = null
//        callLabel = null
//        callCount = null
//        startimg = null
//    }
}

