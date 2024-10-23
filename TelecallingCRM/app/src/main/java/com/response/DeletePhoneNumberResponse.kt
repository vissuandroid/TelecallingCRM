package com.pixl.crm.response

import com.google.gson.annotations.SerializedName

data class DeletePhoneNumberResponse(
    @SerializedName("message")
    val message: String
)

