package com.pixl.crm.response

data class FollowupResponse(
    val follow_up_count: Int,
    val follow_ups: List<FollowupnumberResponse>
)

data class FollowupnumberResponse(
    val staff_name: String,
    val person_name: String,
    val number: String,
    val remarks: String,
    val follow_up_date: String,
    val lead_stage_status : String
)