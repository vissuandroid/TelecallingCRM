package com.example.pixlcallcenterapp.responces

data class Called_ResponseData(
    val id: Int,
    val last_updated: String,
    val name: String,
    val notes: Any,
    val number: String,
    val remarks: Any,
    val status: String,
    val user: Int
)