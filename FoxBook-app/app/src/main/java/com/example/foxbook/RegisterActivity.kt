package com.example.foxbook

import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.foxbook.api.Register
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class RegisterActivity : AppCompatActivity() {


    private val REQUEST_INTERNET_PERMISSION = 1
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
        startRegister.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET)
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this, arrayOf<String>(Manifest.permission.INTERNET),
                    REQUEST_INTERNET_PERMISSION
                )
            } else {
                // Permission already granted, perform your actions
                performNetworkOperation()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_INTERNET_PERMISSION) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, perform your actions
                performNetworkOperation()
            } else {
                // Permission denied, handle accordingly
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun performNetworkOperation() {
        validateAllData()
    }

    private var name = ""
    private var email = ""
    private var password = ""
    private var passwordAgain = ""

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
        passwordAgain = edtPasswordTwo.text.toString().trim()

        // Валідуємо
        if (name.isEmpty()){
            Toast.makeText(this, "Введіть ім'я!", Toast.LENGTH_SHORT).show()
        }
        else if (email.isEmpty()){
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
        else if (passwordAgain.isEmpty()){
            Toast.makeText(this, "Підтвердіть пароль!", Toast.LENGTH_SHORT).show()
        }
        else if (password != passwordAgain) {
            Toast.makeText(this, "Паролі не співпадлають!", Toast.LENGTH_SHORT).show()
        }
        else {
            createUserBeforeValidation()
        }
    }


    private fun createUserBeforeValidation() {
        val userRegistration = Register(email, name, password, passwordAgain)
        val requestCall = ClientAPI.apiService.register(userRegistration)
        val request = requestCall.request()
        val url = request.url().toString()
        Log.d("response", url)
        Log.d("response", request.body()?.contentType().toString())

        requestCall.enqueue(object: Callback<ResponseBody> {

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                Log.d("response", response.toString())

                if (response.isSuccessful){ // успішне надсилання запиту
                    Toast.makeText(this@RegisterActivity, "На Вашу пошту надіслано код підтвердження!", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@RegisterActivity, ValidateEmailActivity::class.java)

                    Log.d("response", "дані")
                    Log.d("response", name)
                    Log.d("response", email)
                    Log.d("response", password)
                    intent.putExtra("name", name) // передаємо дані
                    intent.putExtra("email", email)
                    intent.putExtra("password", password)
                    Log.d("response", "лог перед старт")
                    try {
                        Log.d("response", "лог перед старт в трай")
                        startActivity(intent)
                        Log.d("response", "лог після старт в трай")
                    } catch (e: Exception) {
                        Log.d("response", e.toString())
                        e.printStackTrace()
                    }
                    Log.d("response", "лог після старт")
//                    startActivity(intent) // перехід на сторінку підтвердження пошти
                } else {// помилка надсилання запиту
                    Toast.makeText(this@RegisterActivity, "Не вдалося відправити дані!", Toast.LENGTH_SHORT).show()
                }
            }
            // помилка надсилання запиту
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.d("response", t.toString())
                Log.d("response", userRegistration.toString())
                Log.d("response", requestCall.toString())
                Toast.makeText(this@RegisterActivity, "Не вдалося відправити дані!", Toast.LENGTH_SHORT).show()
            }
        })
    }
}