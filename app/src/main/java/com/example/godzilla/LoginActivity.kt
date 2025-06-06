package com.example.godzilla

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.gson.GsonBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

class LoginActivity : AppCompatActivity() {
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText


    // SharedPreferences para armazenar o token
    private lateinit var sharedPreferences: SharedPreferences
    private val sharedPrefFile = "com.example.godzilla.preferences"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        val loginButton: Button = findViewById(R.id.loginButton)

        sharedPreferences = getSharedPreferences(sharedPrefFile, MODE_PRIVATE)
        val token = sharedPreferences.getString("jwt_token", null)
        if (token != null) {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }

        loginButton.setOnClickListener {
            blockLogin()
        }
    }

    private fun blockLogin() {
        val email = emailEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()


        // Configurando o Gson para ser lenient com o JSON
        val gson = GsonBuilder()
            .setLenient() // Torna o parser mais tolerante com o JSON malformado
            .create()

        val retrofit = Retrofit.Builder()
            .baseUrl("http://192.168.1.110/")
            //.baseUrl("http://localhost/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

        val apiService = retrofit.create(ApiService::class.java)

        val call = apiService.login(email, password)
        call.enqueue(object : Callback<ApiResponse<LoginData>> {
            override fun onResponse(
                call: Call<ApiResponse<LoginData>>,
                response: Response<ApiResponse<LoginData>>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    val apiResponse = response.body()!!
                    val token = apiResponse.data.token
                    val nomeUsuario = apiResponse.data.usuario_nome

                    val editor = sharedPreferences.edit()
                    editor.putString("jwt_token", token)
                    editor.putString("usuario_nome", nomeUsuario)
                    editor.apply()

                    val intent = Intent(this@LoginActivity, HomeActivity::class.java)
                    intent.putExtra("nome", nomeUsuario)
                    startActivity(intent)
                    finish()

                } else {
                    Toast.makeText(this@LoginActivity, "Usuário ou senha inválidos", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<ApiResponse<LoginData>>, t: Throwable) {
                Toast.makeText(this@LoginActivity, "Erro: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    interface ApiService {
        @FormUrlEncoded
        @POST("apis/routes/login.php")
        fun login(
            @Field("usuario") usuario: String,
            @Field("senha") senha: String
        ): Call<ApiResponse<LoginData>>
    }


    data class ApiResponse<T>(
        val status: Boolean,
        val message: String,
        val data: T
    )

    data class LoginData(
        val token: String,
        val usuario_nome: String
    )

}


