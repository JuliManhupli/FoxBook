package com.example.foxbook

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class LoginActivity : AppCompatActivity() {

    private lateinit var progressDialog: ProgressDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val linkToLoginActivity: Button = findViewById(R.id.btnToRegister)

        linkToLoginActivity.setOnClickListener{
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        // Вікно завантаження, поки йде авторизація
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Зачекайте, будь ласка...")
        progressDialog.setCanceledOnTouchOutside(false)

        // Пошук кнопки реєстрації за айді
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
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
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

        Toast.makeText(this, "Користувача авторизовано!", Toast.LENGTH_SHORT).show()
    }
}