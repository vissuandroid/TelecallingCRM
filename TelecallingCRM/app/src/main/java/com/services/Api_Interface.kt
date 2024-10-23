package com.telecalling.crm.services

import com.example.pixlcallcenterapp.requests.Changepassword_Request
import com.example.pixlcallcenterapp.requests.FeedbackData
import com.example.pixlcallcenterapp.requests.IntrestedcallsRequest
import com.example.pixlcallcenterapp.requests.LoginRequest
import com.example.pixlcallcenterapp.requests.verif_Password_Request
import com.example.pixlcallcenterapp.responces.Changepassword_Response
import com.example.pixlcallcenterapp.responces.FeedbackUpdateResponse
import com.example.pixlcallcenterapp.responces.Intrested_CallsResponse
import com.example.pixlcallcenterapp.responces.LeadStatisticsResponse
import com.example.pixlcallcenterapp.responces.LoginResponse
import com.example.pixlcallcenterapp.responces.Numberlist_Response
import com.example.pixlcallcenterapp.responces.UserHomescreenResponse
import com.example.pixlcallcenterapp.responces.VerifyPasswordResponse
import com.pixl.crm.request.AddLead_Request
import com.pixl.crm.request.DeletePhoneNumberRequest
import com.pixl.crm.request.FollowupRequest
import com.pixl.crm.response.AddLeadResponse
import com.pixl.crm.response.ApiResponse
import com.pixl.crm.response.DeletePhoneNumberResponse
import com.pixl.crm.response.FollowupResponse
import com.pixl.crm.response.LeaderboardResponse
import com.telecalling.crm.request.IntrestedcallsListRequest
import com.telecalling.crm.request.UpdatedetailRequest
import com.telecalling.crm.request.UserDeyailsRequest
import com.telecalling.crm.response.Instrested_calls_list_Response
import com.telecalling.crm.response.StaffDashboardResponse
import com.telecalling.crm.response.UpdatedetailsResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface Api_Interface {


    @POST("login_api.php")
        fun login(@Body request: LoginRequest): Call<LoginResponse>

//    @GET("get_pending_numbers.php")
//    fun getPendingNumbers(@Query("user_id") userId: String): Call<NumberlistResponse>


    @GET("get_pending_numbers.php")
    fun getPendingNumbers(@Query("user_id") userId: String): Call<Numberlist_Response>

    @POST("interestedapi.php")
    fun getintrestedNumbers(@Body requestBody: IntrestedcallsListRequest): Call<Instrested_calls_list_Response>

    @GET("staff_dashboard_api.php")
    fun getStaffDashboard(
        @Query("user_id") userId: String
    ): Call<StaffDashboardResponse>

    @FormUrlEncoded
    @POST("update_call_status_api.php")
    fun updateCallStatus(
        @Field("id") id: Int,
        @Field("call_status") callStatus: String,
        @Field("email") email: String,
        @Field("password") password: String,
        @Field("duration") duration: String

    ): Call<FeedbackUpdateResponse>

    @POST("updateint.php")
    fun updateintrestedNumberDetails(@Body requestBody: UpdatedetailRequest): Call<UpdatedetailsResponse>

    @POST("listfollowupsapi.php")
    fun getFollowupNumbers(@Body request: FollowupRequest): Call<FollowupResponse>

    @POST("leaderboard.php")
    fun getLeaderboard(
        @Body params: Map<String, String>
    ): Call<LeaderboardResponse>

    @Multipart
    @POST("api-profile.php")
    fun updateProfile(
        @Part("email") email: RequestBody,
        @Part("current_password") currentPassword: RequestBody,
        @Part("new_password") newPassword: RequestBody,
        @Part("confirm_password") confirmPassword: RequestBody,
        @Part photo: MultipartBody.Part
    ): Call<ApiResponse>
//    @Headers("Content-Type: application/json") // Set the content type if required
//    @POST("api/leads") // Specify the endpoint relative to the base URL
//    fun numberslist(): Call<Numberlist_Response>
//    @FormUrlEncoded
//    @POST("update_call_status_api.php")
//    fun updateCallStatus(@Body feedbackData: FeedbackData): Call<FeedbackUpdateResponse>

//
//    @POST("auth/login")
//    fun login(@Body request: LoginRequest): Call<LoginResponse>
@POST("api_pending_count.php")
fun Userdetailsdetails(
    @Body request: UserDeyailsRequest
): Call<UserHomescreenResponse>

    @POST("add_lead_api.php") // Replace with the correct endpoint for your API
    fun addlead(@Body lead: AddLead_Request): Call<AddLeadResponse>







    @POST("api/lead-update")
    fun updateLead(@Body feedbackData: FeedbackData): Call<FeedbackUpdateResponse>

    @GET("lead-statistics")
    fun getLeadStatistics(): Call<LeadStatisticsResponse>
    @POST("lead-history")
    fun getLeadHistory(@Body interestedcallsdata: IntrestedcallsRequest): Call<Intrested_CallsResponse>
    @POST("verify-password")
    fun getveryfiypassword(@Body verifypassworddata: verif_Password_Request): Call<VerifyPasswordResponse>
    @POST("change-password")
    fun changepassword(@Body changepassworddata: Changepassword_Request): Call<Changepassword_Response>
    @HTTP(method = "DELETE", path = "deletenumberapi.php", hasBody = true)
    fun deletePhoneNumber(@Body body: DeletePhoneNumberRequest): Call<DeletePhoneNumberResponse>

}
//    fun getLeadHistory(@Query("period") period: String, @Query("status") status: String): Call<Intrested_CallsResponse>
