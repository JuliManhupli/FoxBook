package com.example.foxbook

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class RegisterActivity : AppCompatActivity() {

    private lateinit var progressDialog: ProgressDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Пошук кнопки повернення до авторизації за айді
        val linkToLoginActivity: Button = findViewById(R.id.btnToLogin)

        // За натиском кнопки переходимо на сторінку авторизації
        linkToLoginActivity.setOnClickListener{
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        // Вікно завантаження, поки створюється акаунт
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Зачекайте, будь ласка...")
        progressDialog.setCanceledOnTouchOutside(false)

        // Пошук кнопки реєстрації за айді
        val startRegister: Button = findViewById(R.id.btnRegister)

        // За натиском робимо перевіряємо дані
        startRegister.setOnClickListener{
            validateAllData()
        }
    }

    private var name = ""
    private var email = ""
    private var password = ""

    private fun validateAllData() {
        // Знаходимо поля редагування за айді
        val edtName: EditText = findViewById(R.id.edtNameRegister)
        val edtEmail: EditText = findViewById(R.id.edtEmailRegister)
        val edtPasswordOne: EditText = findViewById(R.id.edtPasswordRegister)
        val edtPasswordTwo: EditText = findViewById(R.id.edtPasswordAgainRegister)

        // Беремо введені дані
        name = edtName.text.toString().trim()
        email = edtEmail.text.toString().trim()
        password = edtPasswordOne.text.toString().trim()
        val passwordAgain = edtPasswordTwo.text.toString().trim()

        // Валідуємо
        if (name.isEmpty()){
            Toast.makeText(this, "Введіть ім'я!", Toast.LENGTH_SHORT).show()
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
        else if (passwordAgain.isEmpty()){
            Toast.makeText(this, "Підтвердіть пароль!", Toast.LENGTH_SHORT).show()
        }
        else if (password != passwordAgain) {
            Toast.makeText(this, "Паролі не співпадлають!", Toast.LENGTH_SHORT).show()
        }
        else {
//            Toast.makeText(this, "Акаунт створено!", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, ValidateEmailActivity::class.java)
            startActivity(intent)
        }
    }

    private fun createUser() {
//        progressDialog.setMessage("Створюється акаунт...")
//        progressDialog.show()

        Toast.makeText(this, "Акаунт створено!", Toast.LENGTH_SHORT).show()
    }
}