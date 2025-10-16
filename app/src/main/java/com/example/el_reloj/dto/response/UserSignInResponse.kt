package com.example.el_reloj.dto.response

import com.google.gson.annotations.SerializedName

data class UserSignInResponse(

    @SerializedName("full_name")
    val fullName: String? = null,

    @SerializedName("email")
    val email: String? = null,

    @SerializedName("token")
    val token: String? = null

)
