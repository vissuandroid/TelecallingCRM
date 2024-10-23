package com.example.pixlcallcenterapp.responces


data class LoginData(
    val access: String,
    val expiry_time: Long,
    val refresh: String
)