package com.example.el_reloj

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.el_reloj.databinding.ActivityUpdatePasswordBinding
import com.example.el_reloj.dto.request.UpdatePasswordRequest
import com.example.el_reloj.dto.request.UserSignUpRequest
import com.example.el_reloj.dto.response.Response
import com.example.el_reloj.service.RetrofitClient
import com.example.el_reloj.utils.Utils
import com.google.gson.Gson
import okhttp3.internal.UTC
import retrofit2.Call

class UpdatePasswordActivity : AppCompatActivity() {

    lateinit var binding: ActivityUpdatePasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityUpdatePasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
        initEvents(this)

    }

    fun init() {
        binding.lblUSer.text = intent.getStringExtra("EXTRA_NAME")
    }

    fun initEvents(context: Context) {

        var isVisible1 = false
        var isVisible2 = false

        binding.btnBack.setOnClickListener {}

        binding.btnPasswordVisibility1.setOnClickListener {

            isVisible1 = Utils.togglePasswordVisibility(
                binding.txtNewPassword,
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

        binding.btnUpdatePassword.setOnClickListener {

            val newPassword = binding.txtNewPassword.text.toString().trim()
            val confirmPassword = binding.txtConfirmPassword.text.toString().trim()

            if(!newPassword.equals(confirmPassword)) {
                Toast.makeText(context,"You must enter the same password",Toast.LENGTH_LONG).show()
            }
            else createRequest(newPassword,context)

        }

        binding.btnCloseSession.setOnClickListener {

            Utils.saveToken(context,null)

            val intent = Intent(context, MainActivity::class.java)
            startActivity(intent)
            finish()

        }

    }

    fun createRequest(new_password: String,context: Context) {

        val updatePasswordRequest = UpdatePasswordRequest(new_password)
        val call = RetrofitClient.apiService.updatePassword(
            "Bearer ${Utils.getToken(context)}",
            updatePasswordRequest
        )

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

            }
            override fun onFailure(call: Call<Response?>,t: Throwable) {
                Toast.makeText(context,"Error connecting to the server",Toast.LENGTH_LONG).show()
            }

        })

    }

}