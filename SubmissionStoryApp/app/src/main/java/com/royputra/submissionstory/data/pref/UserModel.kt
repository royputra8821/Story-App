package com.royputra.submissionstory.data.pref

data class UserModel (
    val userId: String,
    val name: String,
    val email: String,
    val token: String,
    val isLogin: Boolean
)