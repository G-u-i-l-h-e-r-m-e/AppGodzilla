package com.example.godzilla

import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.godzilla.network.ApiService
import com.example.godzilla.network.Coleta
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class CertificadoColetaActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CertificadoColetaAdapter
    private lateinit var apiService: ApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_certificado_coleta)


        // === RecyclerView ===
        recyclerView = findViewById(R.id.recyclerViewCertificadoColetas)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val btnVoltar = findViewById<ImageButton>(R.id.btnVoltar)

        btnVoltar.setOnClickListener {
            onBackPressedDispatcher.onBackPressed() // Android moderno (API 33+)
        }

        // === Retrofit (apenas para listar coletas) ===
        val logging = HttpLoggingInterceptor { Log.d("OkHttp", it) }
            .apply { level = HttpLoggingInterceptor.Level.BODY }

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

        val gson = GsonBuilder().setLenient().create()

        val retrofit = Retrofit.Builder()
            .baseUrl("http://192.168.1.110/")          // IP do servidor XAMPP
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

        apiService = retrofit.create(ApiService::class.java)

        carregarColetas()

        // Ajuste edge-to-edge (status/nav bar)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val sb = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(sb.left, sb.top, sb.right, sb.bottom)
            insets
        }
    }

    private fun carregarColetas() {
        apiService.getHistoricoColetas().enqueue(object : Callback<List<Coleta>> {
            override fun onResponse(call: Call<List<Coleta>>, response: Response<List<Coleta>>) {
                if (response.isSuccessful) {
                    val coletas = response.body().orEmpty()
                    Log.d("API", "Coletas recebidas: ${coletas.size}")

                    // Adapter só precisa de contexto + lista
                    adapter = CertificadoColetaAdapter(
                        context = this@CertificadoColetaActivity,
                        listaColetas = coletas.toMutableList()
                    )
                    recyclerView.adapter = adapter
                } else {
                    Log.e("API", "Erro HTTP ${response.code()}")
                    Toast.makeText(
                        this@CertificadoColetaActivity,
                        "Erro ao carregar dados",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<List<Coleta>>, t: Throwable) {
                Log.e("API", "Falha na requisição", t)
                Toast.makeText(
                    this@CertificadoColetaActivity,
                    "Erro de rede: ${t.localizedMessage}",
                    Toast.LENGTH_LONG
                ).show()
            }
        })
    }
}
