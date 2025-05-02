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

    @PUT("users/{id}")
    suspend fun updateUser(
        @Path("id") userId: Long,
        @Body user: RequestBody
    ): UserResponse
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
    .client(OkHttpClient.Builder()
        .addInterceptor { chain ->
            val request = chain.request()
            val url = request.url.toString()
            
            // Skip adding token for login and signup endpoints
            if (!url.contains("/auth/login") && !url.contains("/users")) {
                val context = PawllyApplication.getAppContext()
                val token = getAuthToken(context)
                if (token.isNotEmpty()) {
                    val newRequest = request.newBuilder()
                        .header("Authorization", token)
                        .build()
                    return@addInterceptor chain.proceed(newRequest)
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
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build())
    .build()

val userApi: UserApi = retrofit.create(UserApi::class.java) 