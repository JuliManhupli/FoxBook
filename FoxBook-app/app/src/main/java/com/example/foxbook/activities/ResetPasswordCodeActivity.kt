package com.example.foxbook.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.foxbook.ClientAPI
import com.example.foxbook.R
import com.example.foxbook.api.AccountData
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ResetPasswordCodeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password_code)

        val email = intent.getStringExtra("email") ?: ""

        val edtNum1: EditText = findViewById(R.id.edtCodeResetPassword1)
        val edtNum2: EditText = findViewById(R.id.edtCodeResetPassword2)
        val edtNum3: EditText = findViewById(R.id.edtCodeResetPassword3)
        val edtNum4: EditText = findViewById(R.id.edtCodeResetPassword4)
        val edtNum5: EditText = findViewById(R.id.edtCodeResetPassword5)
        val edtNum6: EditText = findViewById(R.id.edtCodeResetPassword6)

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
        val sendCodeAgain: Button = findViewById(R.id.btnResetPasswordCodeAgain)
        // За натиском робимо перевіряємо дані
        sendCodeAgain.setOnClickListener{
            sendResetCode(email)
        }

        // Пошук кнопки скидання паролю
        val resetOldPassword: Button = findViewById(R.id.btnToNewPassword)
        // За натиском перевіряємо дані
        resetOldPassword.setOnClickListener{
            // Цифри коду
            val codeNum1 = edtNum1.text.toString()
            val codeNum2 = edtNum2.text.toString()
            val codeNum3 = edtNum3.text.toString()
            val codeNum4 = edtNum4.text.toString()
            val codeNum5 = edtNum5.text.toString()
            val codeNum6 = edtNum6.text.toString()

            val fullCode = "$codeNum1$codeNum2$codeNum3$codeNum4$codeNum5$codeNum6"

            validateAllData(fullCode, email)
        }
    }

    private fun sendResetCode(email: String?) {
        // Валідуємо
        if (email == null){
            Toast.makeText(this, "Помилка перевірки пошти!", Toast.LENGTH_SHORT).show()
        }
        else {
            val resendCodeResetPassword = AccountData.Email(email)
            val requestCall = ClientAPI.apiService.passwordResetRequest(resendCodeResetPassword)

            requestCall.enqueue(object: Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    if (response.isSuccessful){// успішне надсилання запиту
                        Toast.makeText(this@ResetPasswordCodeActivity, "Новий код відправлено!", Toast.LENGTH_SHORT).show()
                    } else {
                        try {
                            val jObjError = JSONObject(response.errorBody()!!.string())
                            Toast.makeText(this@ResetPasswordCodeActivity, jObjError.getString("message"), Toast.LENGTH_LONG).show()
                        } catch (e: Exception) {
                            Toast.makeText(this@ResetPasswordCodeActivity, "Помилка повторного надсилання коду!", Toast.LENGTH_LONG).show()
                        }
                    }
                }
                // помилка надсилання запиту
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Toast.makeText(this@ResetPasswordCodeActivity, "Помилка підключення!", Toast.LENGTH_SHORT).show()
                }

            })
        }
    }

    private fun validateAllData(code: String?, email: String?) {
        // Валідуємо
        if (code == null){
            Toast.makeText(this, "Введіть код!", Toast.LENGTH_SHORT).show()
        }
        else if (email == null){
            Toast.makeText(this, "Помилка перевірки пошти!", Toast.LENGTH_SHORT).show()
        }
        else {
            resetPassword(code, email)
        }
    }

    private fun resetPassword(vefificationCode: String, email: String?) {

        val resetPasswordCompletely =
            AccountData.PasswordResetVerify(email.toString(), vefificationCode)
        val requestCall = ClientAPI.apiService.passwordResetVerify(resetPasswordCompletely)

        requestCall.enqueue(object: Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {// успішне надсилання запиту
                    Toast.makeText(this@ResetPasswordCodeActivity, "Пароль успішно скинуто!", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@ResetPasswordCodeActivity, NewPasswordActivity::class.java)
                    intent.putExtra("code", vefificationCode)// передаємо дані
                    startActivity(intent) // перехід на сторінку створення нового паролю
                } else {// помилка надсилання запиту
                    try {
                        val jObjError = JSONObject(response.errorBody()!!.string())
                        Toast.makeText(this@ResetPasswordCodeActivity, jObjError.getString("message"), Toast.LENGTH_LONG).show()
                    } catch (e: Exception) {
                        Toast.makeText(this@ResetPasswordCodeActivity, "Помилка підтвердження коду!", Toast.LENGTH_LONG).show()
                    }
                }
            }
            // помилка надсилання запиту
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(this@ResetPasswordCodeActivity, "Помилка підключення!", Toast.LENGTH_SHORT).show()
            }
        })
    }
}