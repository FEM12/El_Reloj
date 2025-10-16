package com.example.el_reloj.dto.response

import com.google.gson.annotations.SerializedName

data class Response(

    @SerializedName("status")
    var status: String? = null,

    @SerializedName("message")
    var message: String? = null,

    @SerializedName("messages")
    var messages: List<String>? = null,

    @SerializedName("data")
    var data: List<*>? = null,

    @SerializedName("user_sign_in")
    var userSignIn: UserSignInResponse? = null

)
