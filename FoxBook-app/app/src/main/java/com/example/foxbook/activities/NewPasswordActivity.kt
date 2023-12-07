package com.example.foxbook.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.foxbook.ClientAPI
import com.example.foxbook.R
import com.example.foxbook.api.SetNewPassword
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NewPasswordActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_password)

        val code = intent.getStringExtra("code") ?: ""

        // Пошук кнопки зміни паролю
        val setNewPassword: Button = findViewById(R.id.btnNewPassword)

        // За натиском робимо перевіряємо дані
        setNewPassword.setOnClickListener{
            validateAllData(code)
        }
    }

    private var password = ""
    private var passwordAgain = ""

    private fun validateAllData(code: String) {
        // Знаходимо поля редагування за айді
        val edtPasswordOne: EditText = findViewById(R.id.edtNewPassword)
        val edtPasswordTwo: EditText = findViewById(R.id.edtNewPasswordAgain)

        // Беремо введені дані
        password = edtPasswordOne.text.toString().trim()
        passwordAgain = edtPasswordTwo.text.toString().trim()

        // Валідуємо
        if (password.isEmpty()){
            Toast.makeText(this, "Введіть новий пароль!", Toast.LENGTH_SHORT).show()
        }
        else if (password.length < 8){
            Toast.makeText(this, "Пароль менше 8 симовлів!", Toast.LENGTH_SHORT).show()
        }
        else if (password.none { it.isLowerCase() }){
            Toast.makeText(this, "Пароль без маленької літери!", Toast.LENGTH_SHORT).show()
        }
        else if (password.none { it.isUpperCase() }){
            Toast.makeText(this, "Пароль без великої літери!", Toast.LENGTH_SHORT).show()
        }
        else if (password.none { it.isDigit() }){
            Toast.makeText(this, "Пароль без цифр!", Toast.LENGTH_SHORT).show()
        }
        else if (password != passwordAgain) {
            Toast.makeText(this, "Паролі не співпадлають!", Toast.LENGTH_SHORT).show()
        }
        else if (code.isEmpty()) {
            Toast.makeText(this, "Код пустий!", Toast.LENGTH_SHORT).show()
        }
        else {
            setCompletelyNewPassword(code)
        }
    }

    private fun setCompletelyNewPassword(code: String) {

        val userNewPassword = SetNewPassword(code, password, passwordAgain)
        val requestCall = ClientAPI.apiService.passwordResetSetPassword(userNewPassword)

        requestCall.enqueue(object: Callback<ResponseBody> {
            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {
                Log.d("1", response.toString())
                val request = call.request()
                val url = request.url().toString()
                Log.d("1", url.toString())
                if (response.isSuccessful){// успішне надсилання запиту
                    Toast.makeText(this@NewPasswordActivity, "Пароль змінено!", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@NewPasswordActivity, LoginActivity::class.java)
                    startActivity(intent)
                } else {
                    try {
                        val jObjError = JSONObject(response.errorBody()!!.string())
                        Toast.makeText(this@NewPasswordActivity, jObjError.getString("message"), Toast.LENGTH_LONG).show()
                    } catch (e: Exception) {
                        Log.d("1", e.toString())
                        Toast.makeText(this@NewPasswordActivity, "Помилка зміни паролю!", Toast.LENGTH_LONG).show()
                    }
                }
            }
            // помилка надсилання запиту
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(this@NewPasswordActivity, "Помилка підключення!", Toast.LENGTH_SHORT).show()
            }

        })
    }
}