package com.royputra.submissionstory.view.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.royputra.submissionstory.data.ResultState
import com.royputra.submissionstory.data.repo.UserRepository
import com.royputra.submissionstory.data.retrofit.response.ErrorResponse
import com.royputra.submissionstory.data.retrofit.response.SignupResponse
import kotlinx.coroutines.launch
import retrofit2.HttpException

class SignupViewModel (private val userRepo: UserRepository): ViewModel() {
    private val _registrationResult = MutableLiveData<ResultState<SignupResponse>>()
    val registrationResult: LiveData<ResultState<SignupResponse>> get() = _registrationResult

    fun signup(name: String, email: String, password: String){
        viewModelScope.launch {
            try {
                _registrationResult.value = ResultState.Loading
                val result = userRepo.signup(name, email, password)
                _registrationResult.postValue(result)
            } catch (e: Exception){
                _registrationResult.postValue(ResultState.Error("${e.message}"))
            }
        }
    }
}