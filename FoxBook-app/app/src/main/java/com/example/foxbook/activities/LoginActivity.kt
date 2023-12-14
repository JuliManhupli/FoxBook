package com.example.foxbook.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.foxbook.ClientAPI
import com.example.foxbook.R
import com.example.foxbook.api.Login
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Пошук кнопки реєстрації за айді
        val linkToLoginActivity: Button = findViewById(R.id.btnToRegister)

        // За натиском переходимо на сторінку реєстрації
        linkToLoginActivity.setOnClickListener{
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        // Пошук кнопки відновлення паролю за айді
        val forgotPassword: Button = findViewById(R.id.btnForgotPassword)

        // За натиском переходимо на сторінку вводу пошти
        forgotPassword.setOnClickListener{
            val intent = Intent(this, ForgotPasswordActivity::class.java)
            startActivity(intent)
        }

        // Пошук кнопки авторизації за айді
        val startRegister: Button = findViewById(R.id.btnLogin)

        // За натиском робимо перевіряємо дані
        startRegister.setOnClickListener{
            validateAllData()
        }
    }

    private var email = ""
    private var password = ""

    private fun validateAllData() {
        // Знаходимо поля редагування за айді
        val edtEmail: EditText = findViewById(R.id.edtEmailLogin)
        val edtPasswordOne: EditText = findViewById(R.id.edtPasswordLogin)

        // Беремо введені дані
        email = edtEmail.text.toString().trim()
        password = edtPasswordOne.text.toString().trim()

        // Валідуємо
        if (email.isEmpty()){
            Toast.makeText(this, "Введіть пошту!", Toast.LENGTH_SHORT).show()
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            Toast.makeText(this, "Неправильно введенна пошта!", Toast.LENGTH_SHORT).show()
        }
        else if (password.isEmpty()){
            Toast.makeText(this, "Введіть пароль!", Toast.LENGTH_SHORT).show()
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
        else {
            loginUser()
        }
    }

    private fun loginUser() {
        val authoriseUser = Login(email, password)
        val requestCall = ClientAPI.apiService.login(authoriseUser)

        requestCall.enqueue(object: Callback<ResponseBody> {
            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {
                if (response.isSuccessful){
                    // Extract tokens from the response
                    val tokens = extractTokensFromResponse(response)
                    if (tokens != null) {
                        saveTokens(tokens)

                        // Start the UserActivity
                        val intent = Intent(this@LoginActivity, UserActivity::class.java)
                        startActivity(intent)

                        Toast.makeText(this@LoginActivity, "Користувача авторизовано!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@LoginActivity, "Помилка вилучення токенів!", Toast.LENGTH_LONG).show()
                    }
                } else {
                    try {
                        val jObjError = JSONObject(response.errorBody()!!.string())
                        val message = jObjError.getString("message")
                        if (message == "Пошта не підтверджена") {
                            val intent = Intent(this@LoginActivity, ValidateEmailActivity::class.java)
                            intent.putExtra("name", "")
                            intent.putExtra("email", email)
                            intent.putExtra("password", password)
                            startActivity(intent)
                        } else {
                            Toast.makeText(this@LoginActivity, message, Toast.LENGTH_LONG).show()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(this@LoginActivity, "Помилка авторизації!", Toast.LENGTH_LONG).show()
                    }
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.d("response", t.toString())
                Toast.makeText(this@LoginActivity, "Помилка підключення!", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun extractTokensFromResponse(response: Response<ResponseBody>): Pair<String, String>? {
        return try {
            val jsonObject = JSONObject(response.body()?.string())
            val accessToken = jsonObject.getString("access_token")
            val refreshToken = jsonObject.getString("refresh_token")
            Pair(accessToken, refreshToken)
        } catch (e: Exception) {
            null
        }
    }
    private fun saveTokens(tokens: Pair<String, String>?) {
        if (tokens != null) {
            val sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
            with(sharedPreferences.edit()) {
                Log.d("qwe", tokens.first.toString())
                Log.d("qwe", tokens.second.toString())
                Log.d("qwe", "bookArrayList")
                putString("access_token", tokens.first)
                putString("refresh_token", tokens.second)
                apply()
            }
        }
    }


}