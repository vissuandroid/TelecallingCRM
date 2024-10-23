package com.pixl.crm.fragment

import android.app.DatePickerDialog
import android.content.Intent
import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.telecalling.crm.R
import com.telecalling.crm.activity.Intrestedcalls_Activity
import com.telecalling.crm.request.UpdatedetailRequest
import com.telecalling.crm.response.UpdatedetailsResponse
import com.telecalling.crm.services.Api_Interface
import com.telecalling.crm.services.PreferencesHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
class UpdateInfoBottomSheetFragment(
    private val listener: OnUpdateInfoListener,
    private val userId: String,
    private val email: String?,
    private val password: String?,
    private val name: String?,
    private val follwupdate: String?,
    private val remarks: String?,
    private val leadstatus: String?
) : BottomSheetDialogFragment() {

    private lateinit var companyNameEditText: EditText
    private lateinit var datePickerEditText: EditText
    private lateinit var remarksEditText: EditText
    private lateinit var optionsRadioGroup: RadioGroup
    private lateinit var leadStatusRadioGroup: RadioGroup
    private lateinit var submitButton: Button

    interface OnUpdateInfoListener {
        fun onUpdateInfo(
            companyName: String,
            selectedDate: String,
            remarks: String,
            option: String,
            leadStatus: String
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_update_info_bottom_sheet, container, false)

        // Initialize views
        companyNameEditText = view.findViewById(R.id.companynameedittext)
        datePickerEditText = view.findViewById(R.id.datePickerEditText)
        remarksEditText = view.findViewById(R.id.remarksEditText)
        optionsRadioGroup = view.findViewById(R.id.optionsRadioGroup)
        leadStatusRadioGroup = view.findViewById(R.id.leadStatusRadioGroup)
        submitButton = view.findViewById(R.id.updateButton)

        // Set initial values for EditText fields
        companyNameEditText.setText(name?.takeIf { it.isNotBlank() } ?: "Name Not Specified")
        datePickerEditText.setText(follwupdate ?: "")
        remarksEditText.setText(remarks ?: "")

        // Make EditText fields editable (enabled by default, so no need to set)
        // The following lines are not necessary as EditText is enabled by default
        // companyNameEditText.isEnabled = true
        // datePickerEditText.isEnabled = true
        // remarksEditText.isEnabled = true

        datePickerEditText.setOnClickListener {
            showDatePickerDialog(datePickerEditText)
        }

        submitButton.setOnClickListener {
            val companyName = companyNameEditText.text.toString().trim()
            val selectedDate = datePickerEditText.text.toString().trim()
            val remarks = remarksEditText.text.toString().trim()
            val selectedOptionId = optionsRadioGroup.checkedRadioButtonId
            val option = view.findViewById<RadioButton>(selectedOptionId)?.text.toString().trim()
            val selectedLeadStatusId = leadStatusRadioGroup.checkedRadioButtonId
            val leadStatus = view.findViewById<RadioButton>(selectedLeadStatusId)?.text.toString().trim()

            if (validateInputs(companyName, selectedDate, remarks, option, leadStatus)) {
                updateInfoOnServer(
                    companyName,
                    selectedDate,
                    remarks,
                    option,
                    leadStatus
                )
            }
        }

        return view
    }

    private fun showDatePickerDialog(editText: EditText) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, selectedYear, selectedMonth, selectedDay ->
                val formattedDate =
                    String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay)
                editText.setText(formattedDate)
            }, year, month, day
        )
        datePickerDialog.show()
    }

    private fun validateInputs(
        companyName: String,
        selectedDate: String,
        remarks: String,
        option: String,
        leadStatus: String
    ): Boolean {
        return when {
            // Check if an option is selected
            optionsRadioGroup.checkedRadioButtonId == -1 -> {
                Toast.makeText(context, "Please select an option (Interested or Not Interested)", Toast.LENGTH_SHORT).show()
                false
            }
            else -> true
        }
    }

    private fun updateInfoOnServer(
        companyName: String,
        selectedDate: String,
        remarks: String,
        option: String,
        leadStatus: String
    ) {
        // Ensure we have both email and password
        val userEmail = email ?: PreferencesHelper.getEmail(requireContext())
        val userPassword = password ?: PreferencesHelper.getPassword(requireContext())

        if (userEmail.isNullOrEmpty() || userPassword.isNullOrEmpty()) {
            Toast.makeText(context, "Email or Password is missing", Toast.LENGTH_SHORT).show()
            return
        }

        val leadStatusCode = when (leadStatus.lowercase()) {
            "hot" -> 12
            "cold" -> 10
            "warm" -> 11
            else -> 0
        }

        // Create request body with email, password, and other details
        val requestBodyData = UpdatedetailRequest(
            email = userEmail,
            password = userPassword,
            id = userId,
            follow_up_date = selectedDate,
            remarks = remarks,
            name = companyName,
            call_status = option,
            lead_stage_id = leadStatusCode
        )

        Log.d("requestBodyData", "requestBodyData: $requestBodyData")

        val retrofit = Retrofit.Builder()
            .baseUrl("https://app.telecallingcrm.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(Api_Interface::class.java)
        val call = apiService.updateintrestedNumberDetails(requestBodyData)

        call.enqueue(object : Callback<UpdatedetailsResponse> {
            override fun onResponse(
                call: Call<UpdatedetailsResponse>,
                response: Response<UpdatedetailsResponse>
            ) {
                if (response.isSuccessful) {
                    Toast.makeText(context, "Updated successfully", Toast.LENGTH_SHORT).show()
                    val intent = Intent(requireContext(), Intrestedcalls_Activity::class.java)
                    startActivity(intent)
                    Log.d("updateint", "response: ${response.body()}")
                    listener.onUpdateInfo(companyName, selectedDate, remarks, option, leadStatus)
                    dismiss()
                } else {
                    val errorMessage = response.errorBody()?.string() ?: "Unknown error"
                    Toast.makeText(context, "Failed to update: $errorMessage", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<UpdatedetailsResponse>, t: Throwable) {
                Toast.makeText(context, "Failed to update: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
