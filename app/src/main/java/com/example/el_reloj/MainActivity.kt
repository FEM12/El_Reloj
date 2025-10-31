package com.example.el_reloj

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.el_reloj.databinding.ActivityMainBinding
import com.example.el_reloj.dto.request.UserSignInRequest
import com.example.el_reloj.dto.response.Response
import com.example.el_reloj.service.RetrofitClient
import com.example.el_reloj.utils.Utils
import com.google.gson.Gson
import retrofit2.Call

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init(this)
        initEvents(this)

    }

    fun init(context: Context) {

        val token = Utils.getToken(context)

        if (!token.isNullOrEmpty() && !Utils.isTokenExpired(token.toString())) {

            val intent = Intent(context, UpdatePasswordActivity::class.java)
            startActivity(intent)
            finish()

        }
    }

    fun initEvents(context: Context) {

        var isVisible = false

        binding.btnPasswordVisibility.setOnClickListener {

            isVisible = Utils.togglePasswordVisibility(
                binding.txtPassword,
                binding.btnPasswordVisibility,
                isVisible,
                context
            )

        }

        binding.btnSignIn.setOnClickListener {

            val email = binding.txtEmail.text.toString().trim()
            val password = binding.txtPassword.text.toString().trim()

            createRequest(email,password,context)

        }

        binding.btnSignUp.setOnClickListener {

            val intent = Intent(context, SignUp::class.java)
            startActivity(intent)

        }

        binding.lblPasswordRecovery.setOnClickListener {

            val intent = Intent(context, PasswordRecoveryActivity::class.java)
            startActivity(intent)

        }

    }

    fun createRequest(email: String,password: String,context: Context) {

        val userSignInRequest = UserSignInRequest(email,password)
        val call = RetrofitClient.apiService.signIn(userSignInRequest)

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

                val name = apiResponse?.userSignIn?.fullName!!.split(" ")

                Toast.makeText(context,"Welcome ${name[0]} ${name[2]}",Toast.LENGTH_LONG).show()

                Utils.saveToken(context,apiResponse.userSignIn?.token.toString())

                val intent = Intent(context, UpdatePasswordActivity::class.java)
                intent.putExtra("EXTRA_NAME","${name[0]} ${name[2]}")
                intent.putExtra("EXTRA_EMAIL",apiResponse.userSignIn?.email)
                startActivity(intent)
                finish()

            }
            override fun onFailure(call: Call<Response?>,t: Throwable) {
                Toast.makeText(context,"Error connecting to the server",Toast.LENGTH_LONG).show()
            }

        })

    }

}