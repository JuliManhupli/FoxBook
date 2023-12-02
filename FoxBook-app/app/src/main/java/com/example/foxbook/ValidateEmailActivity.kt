package com.example.foxbook

import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.foxbook.api.Email
import com.example.foxbook.api.VerifyEmail
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ValidateEmailActivity : AppCompatActivity() {

    private lateinit var progressDialog: ProgressDialog
//    private var name = intent.getStringExtra("name")
//    private var email = intent.getStringExtra("email")
//    private var password = intent.getStringExtra("password")

//    private var name = ""
//    private val name = intent.getStringExtra("name") ?: ""
//    private var email = intent.getStringExtra("email") ?: ""
//    private var password = intent.getStringExtra("password") ?: ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_validate_email)

        val name = intent.getStringExtra("name") ?: ""
        val email = intent.getStringExtra("email") ?: ""
        val password = intent.getStringExtra("password") ?: ""

        Log.d("response", "дані 2")
        Log.d("response", name)
        Log.d("response", email)
        Log.d("response", password)
        // Вікно завантаження, поки створюється акаунт
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Зачекайте, будь ласка...")
        progressDialog.setCanceledOnTouchOutside(false)

        val edtNum1: EditText = findViewById(R.id.edtEmailValid1)
        val edtNum2: EditText = findViewById(R.id.edtEmailValid2)
        val edtNum3: EditText = findViewById(R.id.edtEmailValid3)
        val edtNum4: EditText = findViewById(R.id.edtEmailValid4)
        val edtNum5: EditText = findViewById(R.id.edtEmailValid5)
        val edtNum6: EditText = findViewById(R.id.edtEmailValid6)

        // Створюємо список, що є репрезентацією коду валідації
        val validationCodeNums = arrayListOf(edtNum1, edtNum2, edtNum3, edtNum4, edtNum5, edtNum6)

        for (i in 0 until validationCodeNums.size) {
            validationCodeNums[i].addTextChangedListener (object: TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) { // Додаткова функція для зміни
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    // Додаткова функція для зміни
                }

                // Пісял введення числа на місце EditText
                override fun afterTextChanged(s: Editable?) {
                    if (s?.length == 1) { // Якщо число вже вписали
                        if (i + 1 < validationCodeNums.size) { // Якщо позиція не остання
                            validationCodeNums[i + 1].requestFocus() // То переходимо на наступний
                        }
                    }
                }
            })

            // Після видалення числа на місці EditText
            validationCodeNums[i].setOnKeyListener { _, keyCodeValue, event ->
                // Якщо це клавіша видалення і вона натиснута
                if (keyCodeValue == KeyEvent.KEYCODE_DEL && event.action == KeyEvent.ACTION_DOWN) {
                    // Якщо позиція не перша і пуста
                    if (i > 0 && validationCodeNums[i].text.isEmpty()) {
                        validationCodeNums[i - 1].requestFocus() // То переходимо на попередню
                        validationCodeNums[i - 1].text.clear() // І очищаємо цифру
                    }
                    else if (i > 0) { // Якщо не пуста, то просто очищаємо
                        validationCodeNums[i].text.clear()
                    }
                    true
                } else {
                    false
                }
            }
        }

        // Пошук кнопки повторного відправлення коду підтвердження
        val sendCodeAgain: Button = findViewById(R.id.btnSendValidationCodeAgain)
        // За натиском робимо перевіряємо дані
        sendCodeAgain.setOnClickListener{
            sendVerificationCode(email)
        }

        // Пошук кнопки реєстрації за айді
        val emailValidation: Button = findViewById(R.id.btnValidateEmail)
        // За натиском робимо перевіряємо дані
        emailValidation.setOnClickListener{
            // Цифри коду
            val codeNum1 = edtNum1.text.toString()
            val codeNum2 = edtNum2.text.toString()
            val codeNum3 = edtNum3.text.toString()
            val codeNum4 = edtNum4.text.toString()
            val codeNum5 = edtNum5.text.toString()
            val codeNum6 = edtNum6.text.toString()

            val fullCode = "$codeNum1$codeNum2$codeNum3$codeNum4$codeNum5$codeNum6"

            validateAllData(email, name, password, fullCode)
        }
    }

    private fun sendVerificationCode(email: String?) {
        // Валідуємо
        if (email == null){
            Toast.makeText(this, "Помилка перевірки пошти!", Toast.LENGTH_SHORT).show()
        }
        else {
            val resendCodeToEmail = Email(email)
            val requestCall = ClientAPI.apiService.resendVerification(resendCodeToEmail)

            requestCall.enqueue(object: Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    if (response.isSuccessful){// успішне надсилання запиту
                        when (response.body()?.string()) {
                            null -> {
                                Toast.makeText(this@ValidateEmailActivity, "Не вдалося отримати відповідь!", Toast.LENGTH_SHORT).show()
                            }
                            "Акаунт вже підтверджено" -> {
                                Toast.makeText(this@ValidateEmailActivity, "Акаунт вже підтверджено!", Toast.LENGTH_SHORT).show()
                            }
                            "Користувача з такою поштою не знайдено" -> {
                                Toast.makeText(this@ValidateEmailActivity, "Користувача з такою поштою не знайдено!", Toast.LENGTH_SHORT).show()
                            }
                            "Ми відправили новий код підтвердження на пошту $email" -> {
                                Toast.makeText(this@ValidateEmailActivity, "Новий код відправлено!", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        Toast.makeText(this@ValidateEmailActivity, "Помилка повторного надсилання коду!", Toast.LENGTH_SHORT).show()
                    }
                }
                // помилка надсилання запиту
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Toast.makeText(this@ValidateEmailActivity, "Помилка повторного надсилання коду!", Toast.LENGTH_SHORT).show()
                }

            })
        }
    }

    private fun validateAllData(email: String?, name: String?, password: String?, code: String?) {
        // Валідуємо
        if (code == null){
            Toast.makeText(this, "Введіть код!", Toast.LENGTH_SHORT).show()
        }
        else if (name == null){
            Toast.makeText(this, "Помилка перевірки імені!", Toast.LENGTH_SHORT).show()
        }
        else if (email == null){
            Toast.makeText(this, "Помилка перевірки пошти!", Toast.LENGTH_SHORT).show()
        }
        else if (password == null){
            Toast.makeText(this, "Помилка перевірки паролю!", Toast.LENGTH_SHORT).show()
        }
        else {
            createUserAfterValidation(code)
        }
    }

    private fun createUserAfterValidation(vefificationCode: String) {
//        progressDialog.setMessage("Створюється акаунт...")
//        progressDialog.show()

        val userEmailValidation = VerifyEmail(vefificationCode)
        val requestCall = ClientAPI.apiService.verify(userEmailValidation)

        requestCall.enqueue(object: retrofit2.Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {// успішне надсилання запиту
                    when (response.body()?.string()) {
                        null -> {
                            Toast.makeText(this@ValidateEmailActivity, "Не вдалося отримати відповідь!", Toast.LENGTH_SHORT).show()
                        }
                        "Код не було знайдено" -> {
                            Toast.makeText(this@ValidateEmailActivity, "Код не було знайдено!", Toast.LENGTH_SHORT).show()
                        }
                        "Код вже був використаний" -> {
                            Toast.makeText(this@ValidateEmailActivity, "Код вже був використаний!", Toast.LENGTH_SHORT).show()
                        }
                        "Пошту успішно підтверджено!" -> {
                            Toast.makeText(this@ValidateEmailActivity, "Пошту успішно підтверджено!", Toast.LENGTH_SHORT).show()

                //                        progressDialog.setMessage("Створюється акаунт...")
                //                        progressDialog.show()

                //                        val account =
                        }
                    }
                } else {// помилка надсилання запиту
                    Toast.makeText(this@ValidateEmailActivity, "Не вдалося підтвердити код!", Toast.LENGTH_SHORT).show()
                }
            }
            // помилка надсилання запиту
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(this@ValidateEmailActivity, "Не вдалося підтвердити код!", Toast.LENGTH_SHORT).show()
            }
        })
    }
}