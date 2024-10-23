package com.telecalling.crm.response

data class Instrested_calls_list_Response(
    val pagination: Pagination,
    val phone_numbers: List<PhoneNumberX>
)