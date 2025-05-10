package com.sysinteg.pawlly

import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Multipart
import retrofit2.http.Part
import com.squareup.moshi.Json
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Header
import android.content.Context
import com.sysinteg.pawlly.utils.Constants.PAWLLY_PREFS
import com.sysinteg.pawlly.utils.Constants.KEY_JWT_TOKEN
import android.util.Log
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit
import com.sysinteg.pawlly.PawllyApplication
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.http.Query
import retrofit2.http.DELETE
import retrofit2.Response
import com.sysinteg.pawlly.model.LostAndFound
import okhttp3.ResponseBody

// Data class for signup request
data class UserSignupRequest(
    val username: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val password: String,
    val address: String,
    val phoneNumber: String
)

// Data class for user response (fields you expect back)
data class UserResponse(
    @field:Json(name = "userId") val userId: Long? = null,
    @field:Json(name = "username") val username: String? = null,
    @field:Json(name = "firstName") val firstName: String? = null,
    @field:Json(name = "lastName") val lastName: String? = null,
    @field:Json(name = "email") val email: String? = null,
    @field:Json(name = "address") val address: String? = null,
    @field:Json(name = "phoneNumber") val phoneNumber: String? = null,
    @field:Json(name = "role") val role: String? = null,
    @field:Json(name = "profilePicture") val profilePicture: String? = null
)

data class LoginRequest(
    @field:Json(name = "email") val email: String,
    @field:Json(name = "password") val password: String
)

data class LoginResponse(
    @field:Json(name = "token") val token: String
)

data class GoogleSignInRequest(
    @field:Json(name = "email") val email: String,
    @field:Json(name = "name") val name: String,
    @field:Json(name = "googleId") val googleId: String
)

// Pet API models
data class PetResponse(
    val pid: Int,
    val name: String?,
    val breed: String?,
    val age: String?,
    val type: String?,
    val gender: String?,
    val description: String?,
    val status: String?,
    val userName: String?,
    val address: String?,
    val contactNumber: String?,
    val submissionDate: String?,
    val photo: String?,
    val photo1: String?,
    val photo1Thumb: String?,
    val photo2: String?,
    val photo3: String?,
    val photo4: String?,
    val weight: String?,
    val color: String?,
    val height: String?
)

typealias PetListResponse = List<PetResponse>

data class AddPetRequest(
    val name: String,
    val type: String,
    val breed: String,
    val age: String,
    val gender: String,
    val description: String,
    val photo: String, // base64 images joined by ||
    val status: String,
    val userName: String,
    val address: String,
    val contactNumber: String,
    val weight: String?,
    val color: String?,
    val height: String?
)

data class UpdatePetRequest(
    val name: String?,
    val breed: String?,
    val age: String?,
    val type: String?,
    val gender: String?,
    val description: String?,
    val status: String?,
    val userName: String?,
    val address: String?,
    val contactNumber: String?,
    val photo1: String?,
    val photo1Thumb: String?,
    val photo2: String?,
    val photo3: String?,
    val photo4: String?,
    val weight: String?,
    val color: String?,
    val height: String?
)

data class AdoptionApplicationRequest(
    @Json(name = "user_id")
    val userId: Long,
    @Json(name = "pet_id")
    val petId: Int,
    @Json(name = "pet_name")
    val petName: String,
    @Json(name = "household_type")
    val householdType: String?,
    @Json(name = "household_ownership")
    val householdOwnership: String?,
    @Json(name = "num_adults")
    val numAdults: Int?,
    @Json(name = "num_children")
    val numChildren: Int?,
    @Json(name = "other_pets")
    val otherPets: Boolean,
    @Json(name = "experience_with_pets")
    val experienceWithPets: String?,
    @Json(name = "daily_routine")
    val dailyRoutine: String?,
    @Json(name = "hours_alone_per_day")
    val hoursAlonePerDay: Int?,
    @Json(name = "reason_for_adoption")
    val reasonForAdoption: String?
)

data class AdoptionApplicationResponse(
    @Json(name = "id") val id: Long,
    @Json(name = "user_id") val userId: Long,
    @Json(name = "pet_id") val petId: Int,
    @Json(name = "pet_name") val petName: String? = null,
    @Json(name = "status") val status: String
)

data class DetailedAdoptionApplicationResponse(
    @Json(name = "id") val id: Int,
    @Json(name = "user_id") val userId: Long,
    @Json(name = "pet_id") val petId: Int,
    @Json(name = "pet_name") val petName: String? = null,
    @Json(name = "status") val status: String,
    @Json(name = "applicant_name") val applicantName: String? = null,
    @Json(name = "applicant_email") val applicantEmail: String? = null,
    @Json(name = "applicant_phone") val applicantPhone: String? = null,
    @Json(name = "applicant_address") val applicantAddress: String? = null,
    @Json(name = "created_at") val createdAt: String? = null,
    @Json(name = "reason_for_adoption") val reasonForAdoption: String? = null,
    @Json(name = "experience_with_pets") val experienceWithPets: String? = null,
    @Json(name = "household_type") val householdType: String? = null,
    @Json(name = "household_ownership") val householdOwnership: String? = null,
    @Json(name = "num_adults") val numAdults: Int? = null,
    @Json(name = "num_children") val numChildren: Int? = null,
    @Json(name = "daily_routine") val dailyRoutine: String? = null,
    @Json(name = "other_pets") val otherPets: Boolean? = null,
    @Json(name = "application_id") val applicationId: Int? = null
)

data class NotificationResponse(
    val notification_id: Long,
    val notification_title: String,
    val notification_description: String,
    val notification_date_and_time: String,
    val pet_id: Long?,
    val pet_name: String?
)

data class ImageUploadResponse(
    val url: String
)

// --- Logging interceptor must be top-level ---
val logging = HttpLoggingInterceptor().apply {
    level = HttpLoggingInterceptor.Level.BODY
}

interface UserApi {
    @Multipart
    @POST("users")
    suspend fun signUpWithProfilePicture(
        @Part("user") user: RequestBody,
        @Part profilePicture: MultipartBody.Part?
    ): UserResponse

    @POST("users")
    suspend fun signUp(@Body user: UserSignupRequest): UserResponse

    @POST("auth/login")
    suspend fun login(@Body loginRequest: LoginRequest): LoginResponse

    @POST("auth/oauth/google")
    suspend fun googleSignIn(@Body request: GoogleSignInRequest): LoginResponse

    @GET("users/me")
    suspend fun getMe(@Header("Authorization") token: String): UserResponse

    @Multipart
    @PUT("users/{id}")
    suspend fun updateUser(
        @Path("id") userId: Long,
        @Part("user") user: RequestBody,
        @Part profilePicture: MultipartBody.Part?
    ): UserResponse

    @Multipart
    @POST("pet/postpetrecord")
    suspend fun addPet(
        @Part("name") name: RequestBody,
        @Part("type") type: RequestBody,
        @Part("breed") breed: RequestBody,
        @Part("age") age: RequestBody,
        @Part("gender") gender: RequestBody,
        @Part("description") description: RequestBody,
        @Part photo1: MultipartBody.Part?,
        @Part photo2: MultipartBody.Part?,
        @Part photo3: MultipartBody.Part?,
        @Part photo4: MultipartBody.Part?,
        @Part("status") status: RequestBody,
        @Part("userName") userName: RequestBody,
        @Part("address") address: RequestBody,
        @Part("contactNumber") contactNumber: RequestBody,
        @Part("weight") weight: RequestBody?,
        @Part("color") color: RequestBody?,
        @Part("height") height: RequestBody?
    ): PetResponse

    @GET("pet/getAllPets")
    suspend fun getAllPets(): PetListResponse

    @GET("pet/byUserName/{userName}")
    suspend fun getPetsByUserName(@Path("userName") userName: String): PetListResponse

    @GET("pet/getPet/{id}")
    suspend fun getPetById(@Path("id") id: Int): PetResponse

    @PUT("pet/putPetDetails")
    suspend fun updatePetDetails(
        @retrofit2.http.Query("pid") pid: Int,
        @Body update: UpdatePetRequest
    ): PetResponse

    @DELETE("pet/deletePetDetails/{id}")
    suspend fun deletePet(@Path("id") id: Int): Response<Unit>

    @GET("users/byUsername/{username}")
    suspend fun getUserByUsername(@Path("username") username: String): UserResponse

    @POST("adoptions")
    suspend fun submitAdoptionApplication(@Body application: AdoptionApplicationRequest): retrofit2.Response<Unit>

    @GET("adoptions")
    suspend fun getAdoptionApplications(
        @Query("userId") userId: Long? = null,
        @Query("petId") petId: Int? = null
    ): List<AdoptionApplicationResponse>

    @GET("adoptions/user/{userId}")
    suspend fun getAdoptionApplicationsByUserId(@Path("userId") userId: Long): List<AdoptionApplicationResponse>

    @GET("adoptions/{id}")
    suspend fun getAdoptionApplicationById(@Path("id") id: Int): DetailedAdoptionApplicationResponse

    @PUT("adoptions/{id}")
    suspend fun updateAdoptionStatus(
        @Path("id") id: Int,
        @Body statusUpdate: Map<String, String>
    ): retrofit2.Response<Unit>

    @DELETE("adoptions/{id}")
    suspend fun deleteAdoptionApplication(@Path("id") id: Int): Response<Unit>

    @GET("users/{id}")
    suspend fun getUserById(@Path("id") userId: Long): UserResponse

    @GET("api/notifications/{id}")
    suspend fun getNotificationById(@Path("id") id: Long): NotificationResponse

    @GET("lostandfound")
    suspend fun getAllLostAndFoundReports(): Response<List<LostAndFound>>

    @GET("lostandfound/{creatorid}")
    suspend fun getLostAndFoundReportsByCreator(@Path("creatorid") creatorId: Int): Response<List<LostAndFound>>

    @POST("lostandfound")
    suspend fun createLostAndFoundReport(
        @Query("reporttype") reportType: String,
        @Query("petname") petName: String,
        @Query("datereported") dateReported: String,
        @Query("lastseen") lastSeen: String,
        @Query("description") description: String,
        @Query("creatorid") creatorId: Int,
        @Query("imagefile") imageFile: MultipartBody.Part?
    ): Response<ResponseBody>

    @PUT("lostandfound/{id}")
    suspend fun updateLostAndFoundReport(
        @Path("id") id: Int,
        @Query("reporttype") reportType: String,
        @Query("petname") petName: String,
        @Query("datereported") dateReported: String,
        @Query("lastseen") lastSeen: String,
        @Query("description") description: String,
        @Query("imagefile") imageFile: MultipartBody.Part? = null
    ): Response<Unit>

    @DELETE("lostandfound/{id}")
    suspend fun deleteLostAndFoundReport(@Path("id") id: Int): Response<Unit>

    @Multipart
    @POST("storage/upload")
    suspend fun uploadImage(@Part file: MultipartBody.Part): Response<ImageUploadResponse>
}

fun getAuthToken(context: Context): String {
    val prefs = context.getSharedPreferences(PAWLLY_PREFS, Context.MODE_PRIVATE)
    val token = prefs.getString(KEY_JWT_TOKEN, "") ?: ""
    return if (token.isNotEmpty()) "Bearer $token" else ""
}

val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

val retrofit: Retrofit = Retrofit.Builder()
    .baseUrl(ApiConfig.BASE_URL)
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .client(
        OkHttpClient.Builder()
            .addInterceptor(logging)
            .addInterceptor { chain ->
                val request = chain.request()
                val url = request.url.toString()
                // Skip adding token for login and signup endpoints
                if (!url.contains("/auth/login") && !url.contains("/auth/signup") && !url.contains("/auth/oauth/google")) {
                    val context = PawllyApplication.getAppContext()
                    val prefs = context.getSharedPreferences(PAWLLY_PREFS, Context.MODE_PRIVATE)
                    val token = prefs.getString(KEY_JWT_TOKEN, null)
                    if (token != null) {
                        val newRequest = request.newBuilder()
                            .header("Authorization", "Bearer $token")
                            .build()
                        Log.d("UserApi", "Adding Authorization header for URL: $url")
                        return@addInterceptor chain.proceed(newRequest)
                    } else {
                        Log.e("UserApi", "No JWT token found for authenticated request")
                    }
                }
                Log.d("UserApi", "Request URL: ${request.url}")
                Log.d("UserApi", "Request Headers: ${request.headers}")
                Log.d("UserApi", "Request Method: ${request.method}")
                try {
                    val response = chain.proceed(request)
                    Log.d("UserApi", "Response Code: ${response.code}")
                    Log.d("UserApi", "Response Headers: ${response.headers}")
                    Log.d("UserApi", "Response Message: ${response.message}")
                    response
                } catch (e: Exception) {
                    Log.e("UserApi", "Network Error: ${e.message}", e)
                    throw e
                }
            }
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build()
    )
    .build()

val userApi: UserApi = retrofit.create(UserApi::class.java) 