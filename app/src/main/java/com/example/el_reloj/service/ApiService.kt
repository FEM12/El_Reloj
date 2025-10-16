package com.example.el_reloj.service

import com.example.el_reloj.dto.request.UserSignInRequest
import com.example.el_reloj.dto.request.UserSignUpRequest
import com.example.el_reloj.dto.response.Response
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {

    @POST("users/sign_in")
    fun signIn(@Body userSignInRequest: UserSignInRequest): Call<Response>

    @POST("users/sign_up")
    fun signUp(@Body userSignUpRequest: UserSignUpRequest): Call<Response>

}