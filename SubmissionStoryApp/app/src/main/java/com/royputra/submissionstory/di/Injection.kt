package com.royputra.submissionstory.di

import android.content.Context
import com.royputra.submissionstory.data.pref.UserPreference
import com.royputra.submissionstory.data.pref.dataStore
import com.royputra.submissionstory.data.repo.UserRepository
import com.royputra.submissionstory.data.retrofit.api.ApiConfig
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object Injection {
    fun provideRepository(context: Context): UserRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val user = runBlocking { pref.getSession().first() }
        val apiService = ApiConfig.getApiService(user.token)
        return UserRepository.getInstance(pref, apiService)
    }
}