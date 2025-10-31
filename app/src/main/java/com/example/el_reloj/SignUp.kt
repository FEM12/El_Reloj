package com.example.el_reloj

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.example.el_reloj.databinding.ActivitySignUpBinding
import com.example.el_reloj.dto.request.UserSignInRequest
import com.example.el_reloj.dto.request.UserSignUpRequest
import com.example.el_reloj.dto.response.Response
import com.example.el_reloj.service.ApiService
import com.example.el_reloj.service.RetrofitClient
import com.example.el_reloj.utils.Utils
import com.google.android.material.button.MaterialButton
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SignUp : AppCompatActivity() {

    lateinit var binding: ActivitySignUpBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initEvents(this)

    }

    fun initEvents(context: Context) {

        var isVisible1 = false
        var isVisible2 = false

        binding.btnPasswordVisibility1.setOnClickListener {

            isVisible1 = Utils.togglePasswordVisibility(
                binding.txtPassword,
                binding.btnPasswordVisibility1,
                isVisible1,
                context
            )

        }

        binding.btnPasswordVisibility2.setOnClickListener {

            isVisible2 = Utils.togglePasswordVisibility(
                binding.txtConfirmPassword,
                binding.btnPasswordVisibility2,
                isVisible2,
                context
            )

        }

        binding.btnSignUp.setOnClickListener {

            val full_name = binding.txtFullName.text.toString().trim()
            val email = binding.txtEmail.text.toString().trim()
            val password = binding.txtPassword.text.toString().trim()
            val confirmPassword = binding.txtConfirmPassword.text.toString().trim()

            if(!password.equals(confirmPassword)) {
                Toast.makeText(context,"You must enter the same password",Toast.LENGTH_LONG).show()
            }
            else createRequest(full_name,email,password,context)

        }

    }

    fun createRequest(full_name: String,email: String,password: String,context: Context) {

        val userSignUpRequest = UserSignUpRequest(full_name,email,password)
        val call = RetrofitClient.apiService.signUp(userSignUpRequest)

        call.enqueue(object: retrofit2.Callback<Response> {

            override fun onResponse(call: Call<Response>,response: retrofit2.Response<Response>) {

                val apiResponse = if(response.isSuccessful) response.body()
                else {

                    response.errorBody()?.charStream()?.let {
                        Gson().fromJson(it, Response::class.java)
                    }

                }

                if(!response.isSuccessful) {

                    var i: Int = 0
                    while(i < apiResponse?.messages!!.size) {

                        Toast.makeText(context,"${apiResponse.messages?.get(i)}",Toast.LENGTH_SHORT).show()
                        i++

                    }
                    return

                }

                Toast.makeText(context,"${apiResponse?.messages?.get(0)}",Toast.LENGTH_LONG).show()

                val intent = Intent(context, MainActivity::class.java)
                startActivity(intent)
                finish()

            }
            override fun onFailure(call: Call<Response?>,t: Throwable) {
                Toast.makeText(context,"Error connecting to the server",Toast.LENGTH_LONG).show()
            }

        })

    }

}