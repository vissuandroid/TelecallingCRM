package com.pixl.crm.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.google.firestore.v1.Cursor
import com.pixl.crm.response.ApiResponse
import com.telecalling.crm.MainActivity
//import com.telecalling.crm.MainActivity
import com.telecalling.crm.R
import com.telecalling.crm.services.Api_Interface
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File

class ProfileActivity : AppCompatActivity() {

    private lateinit var editTextEmail: EditText
    private lateinit var editTextCurrentPassword: EditText
    private lateinit var editTextNewPassword: EditText
    private lateinit var editTextConfirmPassword: EditText
    private lateinit var imageViewProfilePhoto: ImageView
    private lateinit var backbtn: ImageView
    private lateinit var progressBar: ProgressBar
    private var selectedImageUri: Uri? = null

    companion object {
        private const val PICK_IMAGE_REQUEST = 1
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        editTextEmail = findViewById(R.id.editTextEmail)
        editTextCurrentPassword = findViewById(R.id.editTextCurrentPassword)
        editTextNewPassword = findViewById(R.id.editTextNewPassword)
        editTextConfirmPassword = findViewById(R.id.editTextConfirmPassword)
        imageViewProfilePhoto = findViewById(R.id.editprofileImage)
        backbtn = findViewById(R.id.editprofile_backbutton_img)

//        progressBar = findViewById(R.id.progressBar)  // Initialize progressBar

        findViewById<Button>(R.id.buttonChoosePhoto).setOnClickListener {
            chooseImage()
        }
        backbtn.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        findViewById<Button>(R.id.buttonUpdateProfile).setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://app.telecallingcrm.com"))
            startActivity(intent)
            if (validateInputs()) {
                updateProfile()

            }
        }
    }

    private fun chooseImage() {
        val intent = Intent().apply {
            type = "image/*"
            action = Intent.ACTION_GET_CONTENT
        }
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            selectedImageUri = data.data!!
            Glide.with(this).load(selectedImageUri).into(imageViewProfilePhoto)
        }
    }

    private fun validateInputs(): Boolean {
        val email = editTextEmail.text.toString().trim()
        val currentPassword = editTextCurrentPassword.text.toString().trim()
        val newPassword = editTextNewPassword.text.toString().trim()
        val confirmPassword = editTextConfirmPassword.text.toString().trim()

        if (email.isEmpty()) {
            editTextEmail.error = "Email is required"
            editTextEmail.requestFocus()
            return false
        }

        if (currentPassword.isEmpty()) {
            editTextCurrentPassword.error = "Current password is required"
            editTextCurrentPassword.requestFocus()
            return false
        }

        if (newPassword.isEmpty()) {
            editTextNewPassword.error = "New password is required"
            editTextNewPassword.requestFocus()
            return false
        }

        if (confirmPassword.isEmpty()) {
            editTextConfirmPassword.error = "Confirm password is required"
            editTextConfirmPassword.requestFocus()
            return false
        }

        if (newPassword != confirmPassword) {
            editTextConfirmPassword.error = "Passwords do not match"
            editTextConfirmPassword.requestFocus()
            return false
        }

        if (selectedImageUri == null) {
            Toast.makeText(this, "Profile photo is required", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    private fun updateProfile() {
        val email = editTextEmail.text.toString().trim()
        val currentPassword = editTextCurrentPassword.text.toString().trim()
        val newPassword = editTextNewPassword.text.toString().trim()
        val confirmPassword = editTextConfirmPassword.text.toString().trim()

//        progressBar.visibility = View.VISIBLE

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val original = chain.request()
                val requestBuilder = original.newBuilder()
                    .header("Content-Type", "multipart/form-data")
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

        val file = File(getRealPathFromURI(selectedImageUri!!))
        val requestFile = RequestBody.create("image/jpeg".toMediaTypeOrNull(), file)
        val body = MultipartBody.Part.createFormData("photo", file.name, requestFile)

        val requestBodyEmail = RequestBody.create("text/plain".toMediaTypeOrNull(), email)
        val requestBodyCurrentPassword = RequestBody.create("text/plain".toMediaTypeOrNull(), currentPassword)
        val requestBodyNewPassword = RequestBody.create("text/plain".toMediaTypeOrNull(), newPassword)
        val requestBodyConfirmPassword = RequestBody.create("text/plain".toMediaTypeOrNull(), confirmPassword)

        val call = apiService.updateProfile(
            requestBodyEmail,
            requestBodyCurrentPassword,
            requestBodyNewPassword,
            requestBodyConfirmPassword,
            body
        )

        call.enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
//                progressBar.visibility = View.GONE

                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        Toast.makeText(this@ProfileActivity, responseBody.message, Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@ProfileActivity, "Empty response body", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://app.telecallingcrm.com"))
                    startActivity(intent)
                    Toast.makeText(this@ProfileActivity, "Failed to update profile: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
//                progressBar.visibility = View.GONE
                Toast.makeText(this@ProfileActivity, "Failed to update profile: ${t.message}", Toast.LENGTH_SHORT).show()
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://app.telecallingcrm.com"))
                startActivity(intent)
            }
        })
    }

    @SuppressLint("Range")
    private fun getRealPathFromURI(uri: Uri): String {
        var result = ""
        val cursor: android.database.Cursor? = contentResolver.query(uri, null, null, null, null)
        if (cursor != null) {
            cursor.moveToFirst()
            val idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
            result = cursor.getString(idx)
            cursor.close()
        }
        return result
    }
}