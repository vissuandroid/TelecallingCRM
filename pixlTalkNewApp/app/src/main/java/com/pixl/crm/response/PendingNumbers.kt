package com.telecalling.crm.response

data class PendingNumbers(
    val id: Int,
    val number: String,
    val status: String,
    val user: Int,
    val last_updated: String,
    val name: String,
    val notes: String?,
    val remarks: String?




)
