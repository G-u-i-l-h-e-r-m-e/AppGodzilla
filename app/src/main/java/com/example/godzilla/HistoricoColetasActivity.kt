package com.example.godzilla

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.godzilla.LoginActivity.ApiResponse
import com.example.godzilla.LoginActivity.LoginData
import com.example.godzilla.network.ApiService
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import java.time.LocalDate
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


class HistoricoColetasActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: HistoricoColetaAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_historico_coletas)

        recyclerView = findViewById(R.id.recyclerViewHistoricoColetas)
        recyclerView.layoutManager = LinearLayoutManager(this)



        // ConfiguraÇÃo do Logging Interceptor
        val logging = HttpLoggingInterceptor { message ->
            Log.d("OkHttp", message)
        }.apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        // ConfiguraÇÃo do OkHttpClient com o interceptor
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

        val gson = GsonBuilder()
            .setLenient() // Torna o parser mais tolerante com o JSON malformado
            .create()

        val retrofit = Retrofit.Builder()
            .baseUrl("http://192.168.1.110/")
            //.baseUrl("http://localhost/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

        val apiService = retrofit.create(ApiService::class.java)
        apiService.getHistoricoColetas().enqueue(object : Callback<List<Coleta>> {
            override fun onResponse(call: Call<List<Coleta>>, response: Response<List<Coleta>>) {
                if (response.isSuccessful) {
                    val coletas = response.body() ?: emptyList()
                    adapter = HistoricoColetaAdapter(coletas, apiService)
                    recyclerView.adapter = adapter
                } else {
                    Log.e("API Error", "Response not successful. Code: ${response.code()}")
                }
            }
            override fun onFailure(call: Call<List<Coleta>>, t: Throwable) {
                Log.e("API Failure", "Error fetching products", t)
            }
        })

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }


    data class Coleta(
        val id: Int,
        val nome_fantasia: String,
        val data_hora: String,
        val qtdOleoLitros: Double
    )

}