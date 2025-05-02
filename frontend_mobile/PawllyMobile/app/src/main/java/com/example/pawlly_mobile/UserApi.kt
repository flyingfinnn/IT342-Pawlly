package com.example.pawlly_mobile

import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

// Data class for signup request
data class UserSignupRequest(
    val username: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val password: String,
    val address: String,
    val phoneNumber: String,
    val profilePicture: String // base64 string
)

// Data class for user response (fields you expect back)
data class UserResponse(
    val userId: Long?,
    val username: String?,
    val firstName: String?,
    val lastName: String?,
    val email: String?,
    val address: String?,
    val phoneNumber: String?,
    val role: String?,
    val profilePicture: String?,
    val createdAt: String?
)

interface UserApi {
    @POST("users")
    suspend fun signUp(@Body user: UserSignupRequest): UserResponse
}

// Retrofit setup using ApiConfig.BASE_URL
val retrofit: Retrofit = Retrofit.Builder()
    .baseUrl(ApiConfig.BASE_URL)
    .addConverterFactory(MoshiConverterFactory.create())
    .build()

val userApi: UserApi = retrofit.create(UserApi::class.java) 