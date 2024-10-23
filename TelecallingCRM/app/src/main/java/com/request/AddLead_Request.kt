package com.pixl.crm.request

data class AddLead_Request(
    val email: String,
    val password: String,
    val number: String,
    val name: String,
    val follow_up_date: String,
    val remarks: String,
    val lead_stage_id: Int,
    val call_status: String,
    val called_status: String
)