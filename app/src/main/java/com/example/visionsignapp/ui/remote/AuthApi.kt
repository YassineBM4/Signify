package com.example.visionsignapp.ui.remote

import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface AuthApi {
    //User section
    @POST("auth/login")
    fun login(@Body user: LoginRequest): Call<LoginResponse>
    @POST("auth/signup")
    fun signUp(@Body user: User): Call<User>
    @POST("auth/forgot-password")
    fun sendPasswordResetEmail(@Body forgotPasswordDto: PasswordResetRequest): Call<Void>
    @POST("auth/verify-reset-code")
    fun verifyResetCode(@Body verifyOtpDto: VerifyOtpDto): Call<Void>
    @PUT("auth/reset-password/{email}")
    suspend fun resetPassword(@Path("email") email: String, @Body resetPasswordDto: ResetPasswordDto): Response<Void>
    @GET("auth/profile")
    suspend fun getProfile(@Header("Authorization") authHeader: String): Response<User>
    @PUT("auth/update-profile")
    suspend fun updateProfile(@Header("Authorization") authHeader: String, @Body editProfileDto: EditProfileDto): Response<User>
    @PUT("auth/change-password")
    suspend fun changePassword(@Header("Authorization") authHeader: String, @Body changePasswordDto: ChangePasswordDto): Response<User>
    @PUT("auth/update-icon")
    suspend fun updateIcon(@Header("Authorization") authHeader: String, @Body editIconDto: EditIconDto): Response<User>
    @PUT("auth/update-lock")
    suspend fun updateCategoryLock(@Header("Authorization") authHeader: String, @Body changeLockDto: ChangeLockDto): Response<User>
    @PUT("auth/award-gems")
    suspend fun awardGems(@Header("Authorization") authHeader: String, @Body awardGemsDto: AwardGemsDto): Response<AwardGemsResponse>
    @GET("auth/fetchShop")
    suspend fun fetchShop(): Response<List<Shop>>
    @POST("auth/create-payment-intent")
    suspend fun createPaymentIntent(@Body params: PaymentIntentRequest): Response<Map<String, String>>
    @PUT("auth/buyGems")
    suspend fun buyGems(@Header("Authorization") authHeader: String, @Body addGemsDto: AddGemsDto): Response<AwardGemsResponse>

    //Category section
    @GET("category/fetchCategory")
    suspend fun getCategory(): Response<CategoriesResponse>


    //Content section
    @GET("content/fetchContents/{categoryId}")
    suspend fun getContents(@Path("categoryId") categoryId: String): Response<ContentsResponse>

    //Quizz section
    @GET("quizz/fetchQuizzes/{categoryId}")
    suspend fun getQuizzes(@Path("categoryId") categoryId: String): Response<QuizzesResponse>


}

//User section
data class VerifyOtpDto(
    val email: String,
    val resetCode: String
)
data class PasswordResetRequest(
    val email: String
)
data class ResetPasswordDto(
    val newPassword: String
)
data class LoginRequest(
    val email: String,
    val password: String
)

data class User(
    @SerializedName("icon") val icon: String?,
    @SerializedName("fullName") val fullName: String?,
    @SerializedName("email") val email: String?,
    @SerializedName("password") val password: String?,
    @SerializedName("gems") val gems: Int?,
    @SerializedName("level") val level: Int?,
    @SerializedName("levelProgress") val levelProgress: Int?,
    @SerializedName("categoriesProgress") val categoriesProgress: List<CategoryProgress>?
)

data class Shop(
    @SerializedName("scPrice") val scPrice: Int?,
    @SerializedName("realPrice") val realPrice: Int?,
    @SerializedName("scImage") val scImage: String?
)

data class LoginResponse(
    @SerializedName("payload") val payload: User,
    @SerializedName("token") val token: String
)
data class EditProfileDto(
    val newName: String,
    val newEmail: String
)
data class ChangePasswordDto(
    val oldPassword: String,
    val newPassword: String
)
data class EditIconDto(
    val newIcon: String
)

data class ChangeLockDto(
    @SerializedName("categoryId") val categoryId: String,
    @SerializedName("isLockedStatus") val isLockedStatus: Boolean
)

data class AwardGemsDto(
    @SerializedName("correctAnswersCount") val correctAnswersCount: Int,
    @SerializedName("expEarned") val expEarned: Int,
    @SerializedName("catProgress") val catProgress: Int,
    @SerializedName("categoryId") val categoryId: String
)

data class AwardGemsResponse(
    @SerializedName("user") val user: User
)

data class PaymentIntentRequest(
    @SerializedName("amount") val amount: Int,
    @SerializedName("currency") val currency: String
)

data class AddGemsDto(
    @SerializedName("earnedGems") val earnedGems: Int
)
//Category section
data class Category(
    @SerializedName("_id") val catId: String?,
    @SerializedName("catName") val catName: String?,
    @SerializedName("catIcon") val catIcon: String?,
    @SerializedName("catDescription") val catDescription: String?,
    @SerializedName("catPrice") val catPrice: Int?,
    @SerializedName("isLocked") var isLocked: Boolean?
)

data class CategoryProgress(
    @SerializedName("category") val category: Category,
    @SerializedName("progress") val progress: Int?,
    @SerializedName("isLocked") val isLocked: Boolean?
)

data class CategoriesResponse(
    @SerializedName("category") val categories: List<Category>
)

//Content section
data class Content(
    @SerializedName("contentName") val contentName: String?,
    @SerializedName("contentImage") val contentImage: String?,
)

data class ContentsResponse(
    @SerializedName("content") val contents: List<Content>
)

// Quiz section
data class Quizz(
    @SerializedName("quizzQuestion") val quizzQuestion: String?,
    @SerializedName("quizzType") val quizzType: String?, // "single" or "multiple"
    @SerializedName("quizzImage1") val quizzImage1: String?,
    @SerializedName("quizzImage2") val quizzImage2: String?,
    @SerializedName("quizzImage3") val quizzImage3: String?,
    @SerializedName("quizzImage4") val quizzImage4: String?,
    @SerializedName("reponseCorrecte") val reponseCorrecte: String? // Correct answer
)

data class QuizzesResponse(
    @SerializedName("quizz") val quizzes: List<Quizz>
)
