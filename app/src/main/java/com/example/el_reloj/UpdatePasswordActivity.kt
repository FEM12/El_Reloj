package com.example.el_reloj

import android.content.Context
import android.os.Bundle
import android.text.InputType
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.example.el_reloj.databinding.ActivityUpdatePasswordBinding
import com.example.el_reloj.dto.request.UpdatePasswordRequest
import com.example.el_reloj.dto.response.Response
import com.example.el_reloj.service.ApiService
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class UpdatePasswordActivity : AppCompatActivity() {

    lateinit var binding: ActivityUpdatePasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityUpdatePasswordBinding.inflate(layoutInflater)
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
            isVisible1 = togglePasswordVisibility(binding.txtNewPassword, binding.btnPasswordVisibility1, isVisible1)
        }

        binding.btnPasswordVisibility2.setOnClickListener {
            isVisible2 = togglePasswordVisibility(binding.txtConfirmPassword, binding.btnPasswordVisibility2, isVisible2)
        }

        binding.btnUpdatePassword.setOnClickListener {
            val newPassword = binding.txtNewPassword.text.toString().trim()
            val confirmPassword = binding.txtConfirmPassword.text.toString().trim()

            if (newPassword != confirmPassword) {
                Toast.makeText(context, "Las contraseñas no coinciden", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            val token = getSharedPreferences("user_session", MODE_PRIVATE).getString("token", "") ?: ""
            val request = UpdatePasswordRequest(newPassword)

            val call = apiService.updatePassword("Bearer $token", request)
            call.enqueue(object : retrofit2.Callback<Response> {
                override fun onResponse(call: Call<Response>, response: retrofit2.Response<Response>) {
                    val apiResponse = if (response.isSuccessful) response.body()
                    else response.errorBody()?.charStream()?.let {
                        Gson().fromJson(it, Response::class.java)
                    }

                    if (!response.isSuccessful) {
                        apiResponse?.messages?.forEach {
                            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                        }
                        return
                    }

                    Toast.makeText(context, "Contraseña actualizada correctamente", Toast.LENGTH_LONG).show()
                    finish()
                }

                override fun onFailure(call: Call<Response>, t: Throwable) {
                    Toast.makeText(context, "Error de conexión con el servidor", Toast.LENGTH_LONG).show()
                }
            })
        }
    }

    private fun togglePasswordVisibility(
        editText: android.widget.EditText,
        button: com.google.android.material.button.MaterialButton,
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
}
