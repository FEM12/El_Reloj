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
        val retrofit = Retrofit.Builder()
            .baseUrl("http://192.168.0.102:3000/api/v1/auth/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val apiService = retrofit.create(ApiService::class.java)

        binding.btnPasswordVisibility1.setOnClickListener {

            isVisible1 = togglePasswordVisibility(
                binding.txtPassword,
                binding.btnPasswordVisibility1,
                isVisible1
            )

        }

        binding.btnPasswordVisibility2.setOnClickListener {

            isVisible2 = togglePasswordVisibility(
                binding.txtConfirmPassword,
                binding.btnPasswordVisibility2,
                isVisible2
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
            else createRequest(apiService,full_name,email,password,context)

        }

    }

    private fun togglePasswordVisibility(
        editText: EditText,
        button: MaterialButton,
        isVisible: Boolean
    ): Boolean {

        val newState = !isVisible
        val start = editText.selectionStart
        val end = editText.selectionEnd

        editText.inputType = if (newState) InputType.TYPE_CLASS_TEXT
        else InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD

        editText.typeface = ResourcesCompat.getFont(this, R.font.suezone_regular)
        editText.setSelection(start, end)

        button.setIconResource(
            if (newState) R.drawable.baseline_visibility_24
            else R.drawable.baseline_visibility_off_24
        )

        return newState
    }

    fun createRequest(apiService: ApiService,full_name: String,email: String,password: String,context: Context) {

        val userSignUpRequest = UserSignUpRequest(full_name,email,password)
        val call = apiService.signUp(userSignUpRequest)

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