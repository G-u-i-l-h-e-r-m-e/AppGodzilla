package com.example.godzilla

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.godzilla.network.ApiService
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import com.example.godzilla.network.Coleta


class CertificadoColetaActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CertificadoColetaAdapter
    private lateinit var apiService: ApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_certificado_coleta)

        recyclerView = findViewById(R.id.recyclerViewCertificadoColetas)
        recyclerView.layoutManager = LinearLayoutManager(this)

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
            .baseUrl("http://10.135.111.23/")
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
            override fun onResponse(
                call: Call<List<Coleta>>,
                response: Response<List<Coleta>>
            ) {
                if (response.isSuccessful) {
                    val coletas = response.body() ?: emptyList()
                    Log.d("API", "Coletas recebidas: ${coletas.size}")

                    adapter = CertificadoColetaAdapter(coletas.toMutableList(), apiService) { coleta ->
                        Toast.makeText(this@CertificadoColetaActivity, "Gerar certificado para ${coleta.nome_fantasia}", Toast.LENGTH_SHORT).show()
                        // Aqui você pode abrir uma nova tela ou gerar PDF
                    }
                    recyclerView.adapter = adapter
                } else {
                    Log.e("API Error", "Erro ${response.code()}")
                    Toast.makeText(this@CertificadoColetaActivity, "Erro ao carregar dados", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Coleta>>, t: Throwable) {
                Log.e("API Failure", "Falha ao carregar coletas", t)
                Toast.makeText(this@CertificadoColetaActivity, "Erro: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }


}
