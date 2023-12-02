package com.example.foxbook

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.foxbook.api.Email
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.security.AccessController.getContext


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
        val request = requestCall.request()
        val url = request.url().toString()

        requestCall.enqueue(object: Callback<ResponseBody> {
            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {

                if (response.isSuccessful){// успішне надсилання запиту
                    Log.d("response", "isSuccessful")

                    Toast.makeText(this@ForgotPasswordActivity, "Новий код відправлено!", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@ForgotPasswordActivity, ResetPasswordCodeActivity::class.java)
                    intent.putExtra("email", email) // передаємо дані
                    startActivity(intent) // перехід на сторінку підтвердження скидання паролю
                } else {

                    try {
                        val jObjError = JSONObject(response.errorBody()!!.string())

                        Toast.makeText(
                            this@ForgotPasswordActivity,
                            jObjError.getString("message"),
                            Toast.LENGTH_LONG
                        ).show()
                    } catch (e: Exception) {
                        Toast.makeText(this@ForgotPasswordActivity, "Помилка скидання паролю!", Toast.LENGTH_LONG).show()
                    }
                }
            }
            // помилка надсилання запиту
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(this@ForgotPasswordActivity, "Помилка авторизації!", Toast.LENGTH_SHORT).show()
            }

        })
    }
}