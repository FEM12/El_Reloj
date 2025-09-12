package com.example.el_reloj

import android.content.Intent
import android.graphics.drawable.Icon
import android.os.Bundle
import android.text.InputType
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.google.android.material.button.MaterialButton

class MainActivity : AppCompatActivity() {

    lateinit var txtPassword: EditText
    lateinit var btnPasswordVisibility: MaterialButton
    lateinit var btnSignUp1: Button

    override fun onCreate(savedInstanceState: Bundle?) {

        var isActive = false

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        txtPassword = findViewById<EditText>(R.id.txtPassword)
        btnPasswordVisibility = findViewById<MaterialButton>(R.id.btnPasswordVisibility)
        btnSignUp1 = findViewById<Button>(R.id.btnSignUp1)

        btnPasswordVisibility.setOnClickListener {

            isActive = !isActive

            txtPassword.inputType = if(isActive) InputType.TYPE_CLASS_TEXT else InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            txtPassword.typeface = ResourcesCompat.getFont(this, R.font.suezone_regular)
            btnPasswordVisibility.setIconResource(if(isActive) R.drawable.baseline_visibility_off_24 else R.drawable.baseline_visibility_24)

        }

        btnSignUp1.setOnClickListener {

            val intent = Intent(this, SignUp::class.java)
            startActivity(intent)
            finish()

        }

    }

}