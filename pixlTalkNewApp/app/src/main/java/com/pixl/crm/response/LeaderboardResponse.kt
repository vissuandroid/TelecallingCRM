package com.pixl.crm.response

data class LeaderboardResponse(
    val success: Boolean,
    val user_id: Int,
    val role_id: Int,


    val leaderboard: List<LeaderboardItem>
)

data class LeaderboardItem(
    val staff_name: String,
    val staff_photo: String,
    val lead_count: Int
)