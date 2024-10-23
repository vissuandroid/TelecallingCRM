package com.example.pixlcallcenterapp.responces

data class Intrested_CallsResponse(
    val `data`: List<LeadResponse_from_Intrested>,
    val settings: Intrested_Resp_Settings
)