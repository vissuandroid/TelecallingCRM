package com.pixl.crm.request

data class DeletePhoneNumberRequest(
    val email: String,
    val password: String,
    val phone_number_id: Int
)
