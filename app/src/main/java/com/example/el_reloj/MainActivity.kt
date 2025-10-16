package com.example.el_reloj

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.example.el_reloj.databinding.ActivityMainBinding
import com.example.el_reloj.dto.request.UserSignInRequest
import com.example.el_reloj.dto.response.Response
import com.example.el_reloj.service.ApiService
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initEvents(this)

    }

    fun initEvents(context: Context) {

        var isActive = false
        val retrofit = Retrofit.Builder()
            .baseUrl("http://192.168.0.102:3000/api/v1/auth/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val apiService = retrofit.create(ApiService::class.java)

        binding.btnPasswordVisibility.setOnClickListener {

            isActive = !isActive
            val start = binding.txtPassword.selectionStart
            val end = binding.txtPassword.selectionEnd

            binding.txtPassword.inputType = if(isActive) InputType.TYPE_CLASS_TEXT else InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            binding.txtPassword.typeface = ResourcesCompat.getFont(this, R.font.suezone_regular)
            binding.txtPassword.setSelection(start, end)
            binding.btnPasswordVisibility.setIconResource(if(isActive) R.drawable.baseline_visibility_24 else R.drawable.baseline_visibility_off_24)

        }

        binding.btnSignIn.setOnClickListener {

            val email = binding.txtEmail.text.toString().trim()
            val password = binding.txtPassword.text.toString().trim()

            createRequest(apiService,email,password,context)

        }

        binding.btnSignUp.setOnClickListener {

            val intent = Intent(this, SignUp::class.java)
            startActivity(intent)

        }

    }

    fun createRequest(apiService: ApiService,email: String,password: String,context: Context) {

        val userSignInRequest = UserSignInRequest(email,password)
        val call = apiService.signIn(userSignInRequest)

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

                Toast.makeText(context,"${apiResponse?.userSignIn?.token}",Toast.LENGTH_LONG).show()

            }
            override fun onFailure(call: Call<Response?>,t: Throwable) {
                Toast.makeText(context,"Error connecting to the server",Toast.LENGTH_LONG).show()
            }

        })

    }

}