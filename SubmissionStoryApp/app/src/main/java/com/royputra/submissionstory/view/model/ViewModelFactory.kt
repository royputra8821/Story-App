package com.royputra.submissionstory.view.model

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.royputra.submissionstory.data.repo.UserRepository
import com.royputra.submissionstory.di.Injection

class ViewModelFactory (private val repo: UserRepository): ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <i : ViewModel> create(modelClass: Class<i>): i {
        return when{
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(repo) as i
            }
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(repo) as i
            }
            modelClass.isAssignableFrom(SignupViewModel::class.java) -> {
                SignupViewModel(repo) as i
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class:"+modelClass.name)
        }
    }

    companion object{
        @Volatile
        private var INSTANCE: ViewModelFactory? = null
        @JvmStatic
        fun getInstance(context: Context): ViewModelFactory{
            if (INSTANCE == null){
                synchronized(ViewModelFactory::class.java){
                    INSTANCE = ViewModelFactory(Injection.provideRepository(context))
                }
            }
            return INSTANCE as ViewModelFactory
        }
    }
}