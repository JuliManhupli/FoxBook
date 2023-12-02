package com.example.foxbook.activities

import android.app.ProgressDialog
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

    private lateinit var progressDialog: ProgressDialog
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

        // Вікно завантаження, поки йде авторизація
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Зачекайте, будь ласка...")
        progressDialog.setCanceledOnTouchOutside(false)

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
//        progressDialog.setMessage("Авторизація користувача...")
//        progressDialog.show()

        val authoriseUser = Login(email, password)
        val requestCall = ClientAPI.apiService.login(authoriseUser)

        requestCall.enqueue(object: Callback<ResponseBody> {
            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {
                if (response.isSuccessful){// успішне надсилання запиту
                    Toast.makeText(this@LoginActivity, "Користувача авторизовано!", Toast.LENGTH_SHORT).show()
                } else {
                    try {
                        val jObjError = JSONObject(response.errorBody()!!.string())
                        Toast.makeText(this@LoginActivity, jObjError.getString("message"), Toast.LENGTH_LONG).show()
                    } catch (e: Exception) {
                        Toast.makeText(this@LoginActivity, "Помилка авторизації!", Toast.LENGTH_LONG).show()
                    }
                }
            }
            // помилка надсилання запиту
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.d("response", t.toString())
                Toast.makeText(this@LoginActivity, "Помилка підключення!", Toast.LENGTH_SHORT).show()
            }

        })
    }
}