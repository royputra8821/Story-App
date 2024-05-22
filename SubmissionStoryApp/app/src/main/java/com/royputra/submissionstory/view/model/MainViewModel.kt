package com.royputra.submissionstory.view.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.royputra.submissionstory.data.pref.UserModel
import com.royputra.submissionstory.data.repo.UserRepository
import com.royputra.submissionstory.data.retrofit.response.ErrorResponse
import com.royputra.submissionstory.data.retrofit.response.ListStoryItem
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import retrofit2.HttpException

class MainViewModel (private val repo: UserRepository): ViewModel() {
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading
    private val _storyList = MutableLiveData<List<ListStoryItem>>()
    val storyList: LiveData<List<ListStoryItem>> get() = _storyList

    private val _message = MutableLiveData<String>()
    val message: LiveData<String> get() = _message
    suspend fun getAllStory(){
        try {
            val token = repo.getSession().first().token
            val story = repo.getStory("Bearer $token")
            val message = story.message
            _isLoading.value = false
            _storyList.value = story.listStory
            _message.value = message!!
        } catch (e: HttpException){
            val jsonInString = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
            val errorMessage = errorBody.message
            _isLoading.value = false
            _message.value = errorMessage ?: "Unknown Error"
        }
    }

    fun getSession(): LiveData<UserModel>{
        return repo.getSession().asLiveData()
    }

    fun logout(){
        viewModelScope.launch {
            repo.logout()
        }
    }
}