package com.royputra.submissionstory.data.retrofit.response

import com.google.gson.annotations.SerializedName

data class SignupResponse (
    @field:SerializedName("error")
    val error: Boolean? = null,

    @field:SerializedName("message")
    val message: String? = null
)