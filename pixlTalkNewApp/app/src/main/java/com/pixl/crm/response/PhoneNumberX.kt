package com.telecalling.crm.response

data class PhoneNumberX(
    val id: Int,
    val number: String,
    val staffId: Int,
    val date_added: String,
    val callStatus: String,
    val calledStatus: String,
    val follow_up_date: String?,
    val remarks: String?,
    val dealStatus: String,
    val dealAmount: Double?,  // Use Double? for nullable amount
    val totalCalls: Int,
    val leadStageId: Int?,
    val name: String?,
    val lead_stage_status: String?



)