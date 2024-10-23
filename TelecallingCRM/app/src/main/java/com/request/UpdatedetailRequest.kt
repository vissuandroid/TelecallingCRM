package com.telecalling.crm.request

data class UpdatedetailRequest(
    val email: String,
    val password: String,
    val id: String,
    val follow_up_date: String,
    val remarks: String,
    val name: String,
    val call_status: String,
    val lead_stage_id: Int
)
