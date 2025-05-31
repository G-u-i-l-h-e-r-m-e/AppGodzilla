package com.example.godzilla

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
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
import com.example.godzilla.network.Coleta


class HistoricoColetasActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var editarColetaLauncher: ActivityResultLauncher<Intent>
    private lateinit var adapter: HistoricoColetaAdapter
    private lateinit var apiService: ApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_historico_coletas)

        recyclerView = findViewById(R.id.recyclerViewHistoricoColetas)
        recyclerView.layoutManager = LinearLayoutManager(this)

        editarColetaLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                carregarColetas()
                Log.d("RELOAD", "Recarregando coletas após edição")
            }
        }

        val logging = HttpLoggingInterceptor { message ->
            Log.d("OkHttp", message)
        }.apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

        val gson = GsonBuilder()
            .setLenient()
            .create()

        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.135.111.26/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

        apiService = retrofit.create(ApiService::class.java)

        carregarColetas()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun carregarColetas() {
        apiService.getHistoricoColetas().enqueue(object : Callback<List<Coleta>> {
            override fun onResponse(call: Call<List<Coleta>>, response: Response<List<Coleta>>) {
                if (response.isSuccessful) {
                    val coletas = response.body() ?: emptyList()
                    adapter = HistoricoColetaAdapter(coletas.toMutableList(), apiService) { coleta ->
                        val intent = Intent(this@HistoricoColetasActivity, EditarHistoricoColetas::class.java).apply {
                            putExtra("ID", coleta.id)
                            putExtra("nome_fantasia", coleta.nome_fantasia)
                            putExtra("DATA_HORA", coleta.data_hora)
                            putExtra("QTD_OLEO_LITROS", coleta.qtdOleoLitros)
                        }
                        editarColetaLauncher.launch(intent)
                    }
                    recyclerView.adapter = adapter
                } else {
                    Log.e("API Error", "Código: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<Coleta>>, t: Throwable) {
                Log.e("API Failure", "Erro na requisição", t)
            }
        })
    }



}
