package com.example.el_reloj

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.el_reloj.databinding.ActivityPasswordRecoveryBinding
import com.google.firebase.auth.FirebaseAuth

class PasswordRecoveryActivity : AppCompatActivity() {

    lateinit var binding: ActivityPasswordRecoveryBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityPasswordRecoveryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initEvents(this)

    }

    fun initEvents(context: Context) {

        val auth = FirebaseAuth.getInstance()

        binding.btnSignIn.setOnClickListener {

            val email = binding.txtEmail.text.toString().trim()
            val regex = Regex("^[\\w\\.-]+@[a-zA-Z\\d\\.-]+\\.[a-zA-Z]{2,6}\$")

            if(email.isEmpty() || !regex.matches(email)) {
                Toast.makeText(context,"Enter a valid email (example0108@yahoo.com)",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.createUserWithEmailAndPassword(email, "P4$\$w0rd!")

            Thread.sleep(1000)

            auth.sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->

                    if (task.isSuccessful)
                        Toast.makeText(context,"Recovery email sent",Toast.LENGTH_SHORT).show()
                    else
                        Toast.makeText(context,"Error",Toast.LENGTH_SHORT).show()

                }

        }

    }

}