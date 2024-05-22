package com.royputra.submissionstory.data.repo

import com.google.gson.Gson
import com.royputra.submissionstory.data.ResultState
import com.royputra.submissionstory.data.pref.UserModel
import com.royputra.submissionstory.data.pref.UserPreference
import com.royputra.submissionstory.data.retrofit.api.ApiService
import com.royputra.submissionstory.data.retrofit.response.ErrorResponse
import com.royputra.submissionstory.data.retrofit.response.LoginResponse
import com.royputra.submissionstory.data.retrofit.response.SignupResponse
import com.royputra.submissionstory.data.retrofit.response.StoryResponse
import kotlinx.coroutines.flow.Flow
import retrofit2.HttpException

class UserRepository private constructor(
    private val userPref: UserPreference,
    private val apiService: ApiService) {

    suspend fun saveSession(user: UserModel){
        userPref.saveSession(user)
    }

    fun getSession(): Flow<UserModel> {
        return userPref.getSession()
    }

    suspend fun signup(name: String, email: String, password: String): ResultState<SignupResponse> {
        ResultState.Loading
        return try {
            val response = apiService.signup(name, email, password)
            if (response.error == true){
                ResultState.Error(response.message ?: "Unknown Error")
            } else {
                ResultState.Success(response)
            }
        }catch (e: HttpException) {
            val jsonInString = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
            val errorMessage = errorBody.message
            ResultState.Error(errorMessage.toString())
        }
    }

    suspend fun login(email: String, password: String):LoginResponse {
        return apiService.login(email, password)
    }

    suspend fun getStory(token: String): StoryResponse {
        return apiService.getStory(token)
    }

    suspend fun logout() { userPref.logout() }

    suspend fun setAuth(user: UserModel) = userPref.saveSession(user)

    companion object {
        @Volatile
        private var instance: UserRepository? = null
        fun getInstance(
            userPreference: UserPreference,
            apiService: ApiService
        ): UserRepository =
            instance ?: synchronized(this) {
                instance ?: UserRepository(userPreference, apiService)
            }.also { instance = it }
    }
}