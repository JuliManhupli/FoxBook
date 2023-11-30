package com.example.foxbook

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.foxbook.api.Email
import com.example.foxbook.api.Login
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ForgotPasswordActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        // Пошук кнопки реєстрації за айді
        val linkToLoginActivity: Button = findViewById(R.id.btnToRegisterForgotPassword)

        // За натиском переходимо на сторінку реєстрації
        linkToLoginActivity.setOnClickListener{
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        // Пошук кнопки надсилання коду на пошту
        val sendCodeToEmail: Button = findViewById(R.id.btnSendCodeForNewPassword)

        // За натиском робимо перевіряємо дані
        sendCodeToEmail.setOnClickListener{
            validateAllData()
        }
    }

    private var email = ""

    private fun validateAllData() {
        // Знаходимо поля редагування за айді
        val edtEmail: EditText = findViewById(R.id.edtEmailForgotPassword)

        // Беремо введені дані
        email = edtEmail.text.toString().trim()

        // Валідуємо
        if (email.isEmpty()){
            Toast.makeText(this, "Введіть пошту!", Toast.LENGTH_SHORT).show()
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            Toast.makeText(this, "Неправильно введенна пошта!", Toast.LENGTH_SHORT).show()
        }
        else {
            sendCodeForNewPassword()
        }
    }

    private fun sendCodeForNewPassword() {
//        progressDialog.setMessage("Авторизація користувача...")
//        progressDialog.show()

        val sendCodeForgotPassword = Email(email)
        val requestCall = ClientAPI.apiService.passwordResetRequest(sendCodeForgotPassword)

        requestCall.enqueue(object: Callback<ResponseBody> {
            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {
                if (response.isSuccessful){// успішне надсилання запиту
                    when (response.body()?.string()) {
                        null -> {
                            Toast.makeText(this@ForgotPasswordActivity, "Не вдалося отримати відповідь!", Toast.LENGTH_SHORT).show()
                        }
                        "Пошта не підтверджена" -> {
                            Toast.makeText(this@ForgotPasswordActivity, "Пошта не підтверджена!", Toast.LENGTH_SHORT).show()
                        }
                        "Користувача з такою поштою не знайдено" -> {
                            Toast.makeText(this@ForgotPasswordActivity, "Користувача з такою поштою не знайдено!", Toast.LENGTH_SHORT).show()
                        }
                        "Ми відправили код для скидання пароля на пошту $email" -> {
                            Toast.makeText(this@ForgotPasswordActivity, "Новий код відправлено!", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this@ForgotPasswordActivity, ResetPasswordCodeActivity::class.java)
                            intent.putExtra("email", email) // передаємо дані
                            startActivity(intent) // перехід на сторінку підтвердження скидання паролю
                        }
                    }
                } else {
                    Toast.makeText(this@ForgotPasswordActivity, "Помилка скидання паролю!", Toast.LENGTH_SHORT).show()
                }
            }
            // помилка надсилання запиту
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(this@ForgotPasswordActivity, "Помилка авторизації!", Toast.LENGTH_SHORT).show()
            }

        })
    }
}