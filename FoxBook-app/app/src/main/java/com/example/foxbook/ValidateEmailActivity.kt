package com.example.foxbook

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class ValidateEmailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_validate_email)

        val edtNum1: EditText = findViewById(R.id.edtEmailValid1)
        val edtNum2: EditText = findViewById(R.id.edtEmailValid2)
        val edtNum3: EditText = findViewById(R.id.edtEmailValid3)
        val edtNum4: EditText = findViewById(R.id.edtEmailValid4)
        val edtNum5: EditText = findViewById(R.id.edtEmailValid5)
        val edtNum6: EditText = findViewById(R.id.edtEmailValid6)

        // Створюємо список, що є репрезентацією коду валідації
        val validationCodeNums = arrayListOf<EditText>(edtNum1, edtNum2, edtNum3, edtNum4, edtNum5, edtNum6)

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

        // Пошук кнопки реєстрації за айді
        val startRegister: Button = findViewById(R.id.btnValidateEmail)
        // За натиском робимо перевіряємо дані
        startRegister.setOnClickListener{
            Toast.makeText(this, "Акаунт створено!", Toast.LENGTH_SHORT).show()
//            validateAllData()
        }
    }
}