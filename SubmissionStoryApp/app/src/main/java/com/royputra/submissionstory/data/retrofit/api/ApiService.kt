package com.royputra.submissionstory.data.retrofit.api

import com.royputra.submissionstory.data.retrofit.response.FileUploadResponse
import com.royputra.submissionstory.data.retrofit.response.LoginResponse
import com.royputra.submissionstory.data.retrofit.response.SignupResponse
import com.royputra.submissionstory.data.retrofit.response.StoryResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiService {

    @FormUrlEncoded
    @POST("register")
    suspend fun signup(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): SignupResponse

    @FormUrlEncoded
    @POST("login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): LoginResponse

    @Multipart
    @POST("stories")
    suspend fun uploadStory(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
    ): FileUploadResponse

    @GET("stories")
    suspend fun getStory(
        @Header("Authorization") token: String,
    ) : StoryResponse
}