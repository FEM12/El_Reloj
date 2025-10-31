package com.example.el_reloj.utils

import android.content.Context
import android.text.InputType
import android.widget.EditText
import androidx.core.content.res.ResourcesCompat
import com.example.el_reloj.R
import com.google.android.material.button.MaterialButton
import org.json.JSONObject

object Utils {

    fun togglePasswordVisibility(
        editText: EditText,
        button: MaterialButton,
        isVisible: Boolean,
        context: Context
    ): Boolean {

        val newState = !isVisible
        val start = editText.selectionStart
        val end = editText.selectionEnd

        editText.inputType = if (newState) InputType.TYPE_CLASS_TEXT
        else InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD

        editText.typeface = ResourcesCompat.getFont(context, R.font.suezone_regular)
        editText.setSelection(start, end)

        button.setIconResource(
            if (newState) R.drawable.baseline_visibility_24
            else R.drawable.baseline_visibility_off_24
        )

        return newState
    }

    fun saveToken(context: Context,token: String?) {

        val sharedPref = context.getSharedPreferences("MyApp", Context.MODE_PRIVATE)

        with(sharedPref.edit()) {
            putString("TOKEN",token)
            apply()
        }

    }

    fun getToken(context: Context): String? {

        val sharedPref = context.getSharedPreferences("MyApp", Context.MODE_PRIVATE)
        return sharedPref.getString("TOKEN", null)

    }

    fun getTokenExpiration(token: String): Long? {

        return try {

            val parts = token.split(".")
            if (parts.size != 3) return null

            // Decodificar el payload
            val payload = String(android.util.Base64.decode(parts[1], android.util.Base64.URL_SAFE))
            val json = JSONObject(payload)

            json.getLong("exp")

        } catch (e: Exception) { null }

    }

    fun isTokenExpired(token: String): Boolean {

        val exp = getTokenExpiration(token) ?: return true
        val currentTime = System.currentTimeMillis() / 1000
        return currentTime >= exp

    }


}