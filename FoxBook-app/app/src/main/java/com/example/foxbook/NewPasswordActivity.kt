package com.example.foxbook

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.foxbook.api.Login
import com.example.foxbook.api.SetNewPassword
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NewPasswordActivity : AppCompatActivity() {

    private var code = intent.getStringExtra("code")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_password)

        // Пошук кнопки зміни паролю
        val setNewPassword: Button = findViewById(R.id.btnNewPassword)

        // За натиском робимо перевіряємо дані
        setNewPassword.setOnClickListener{
            validateAllData()
        }
    }

    private var password = ""
    private var passwordAgain = ""

    private fun validateAllData() {
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
        else {
            setCompletelyNewPassword()
        }
    }

    private fun setCompletelyNewPassword() {
//        progressDialog.setMessage("Авторизація користувача...")
//        progressDialog.show()

        val userNewPassword = SetNewPassword(code.toString(), password, passwordAgain)
        val requestCall = ClientAPI.apiService.passwordResetSetPassword(userNewPassword)

        requestCall.enqueue(object: Callback<ResponseBody> {
            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {
                if (response.isSuccessful){// успішне надсилання запиту
                    Toast.makeText(this@NewPasswordActivity, "Користувача авторизовано!", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@NewPasswordActivity, LoginActivity::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(this@NewPasswordActivity, "Помилка авторизації!", Toast.LENGTH_SHORT).show()
                }
            }
            // помилка надсилання запиту
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(this@NewPasswordActivity, "Помилка авторизації!", Toast.LENGTH_SHORT).show()
            }

        })
    }
}