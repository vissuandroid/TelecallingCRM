package com.telecalling.crm.activity

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.pixl.crm.R
import com.telecalling.crm.R
import com.telecalling.crm.response.UpdateCallStatusResponse
import com.telecalling.crm.services.Api_Interface
import com.telecalling.crm.services.Client_Api
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PhonenumberupdateActivity : AppCompatActivity() {
    private lateinit var phoneNumberEditText: EditText
    private lateinit var callStatusSpinner: Spinner
    private lateinit var updateButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_phonenumberupdate)

        phoneNumberEditText = findViewById(R.id.phoneNumberEditText)
        callStatusSpinner = findViewById(R.id.callStatusSpinner)
        updateButton = findViewById(R.id.updateButton)

        // Set up the spinner
        val callStatusOptions = arrayOf("Pending", "Interested", "Not Interested")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, callStatusOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        callStatusSpinner.adapter = adapter

        updateButton.setOnClickListener {
            val phoneNumber = phoneNumberEditText.text.toString().trim()
            val callStatus = callStatusSpinner.selectedItem.toString()

            if (validateInput(phoneNumber)) {
//                updateCallStatus(phoneNumber, callStatus)
            }
        }
    }

    private fun validateInput(phoneNumber: String): Boolean {
        if (phoneNumber.isEmpty()) {
            phoneNumberEditText.error = "Phone number is required"
            return false
        }
        return true
    }














//    private fun updateCallStatus(phoneNumber: String, callStatus: String) {
//        val apiService = Client_Api.instance.create(Api_Interface::class.java)
//        val call = apiService.updateCallStatus(phoneNumber, callStatus)
//
//        call.enqueue(object : Callback<UpdateCallStatusResponse> {
//            override fun onResponse(call: Call<UpdateCallStatusResponse>, response: Response<UpdateCallStatusResponse>) {
//                if (response.isSuccessful) {
//                    val updateResponse = response.body()
//                    if (updateResponse != null) {
//                        Toast.makeText(this@PhonenumberupdateActivity, updateResponse.success, Toast.LENGTH_SHORT).show()
//                    } else {
//                        Toast.makeText(this@PhonenumberupdateActivity, "Unexpected response", Toast.LENGTH_SHORT).show()
//                    }
//                } else {
//                    Toast.makeText(this@PhonenumberupdateActivity, "Update failed", Toast.LENGTH_SHORT).show()
//                }
//            }
//
//            override fun onFailure(call: Call<UpdateCallStatusResponse>, t: Throwable) {
//                Toast.makeText(this@PhonenumberupdateActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
//            }
//        })
//    }

}