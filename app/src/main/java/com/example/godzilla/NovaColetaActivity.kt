package com.example.godzilla

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.godzilla.network.ApiService
import com.example.godzilla.network.Empresa
import com.example.godzilla.network.NovaColetaRequest
import com.google.gson.GsonBuilder
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.Calendar
import java.util.concurrent.TimeUnit

class NovaColetaActivity : AppCompatActivity() {

    private lateinit var spinnerEmpresas: Spinner
    private var listaEmpresas: List<Empresa> = emptyList()
    private lateinit var apiService: ApiService
    private lateinit var dataHoraEditText: EditText
    private lateinit var qtdOleoLitrosEditText: EditText
    private lateinit var salvarButton: Button

    // Variável que armazenará o ID da empresa selecionada
    private var clienteIdSelecionado: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nova_coleta)

        val btnVoltar = findViewById<ImageButton>(R.id.btnVoltar)

        btnVoltar.setOnClickListener {
            onBackPressedDispatcher.onBackPressed() // Android moderno (API 33+)
        }

        dataHoraEditText = findViewById(R.id.data_hora)
        qtdOleoLitrosEditText = findViewById(R.id.qtdOleoLitros)
        salvarButton = findViewById(R.id.btnSalvar)
        spinnerEmpresas = findViewById(R.id.spinnerEmpresas)

        salvarButton.setOnClickListener {
            salvarColeta()
        }

        dataHoraEditText.setOnClickListener {
            mostrarDateTimePicker()
        }

        configurarRetrofit()
        carregarEmpresas()

        // Se estiver usando window insets (opcional)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun salvarColeta() {
        val dataHora = dataHoraEditText.text.toString().trim()
        val quantidadeStr = qtdOleoLitrosEditText.text.toString().trim()

        if (clienteIdSelecionado == null || dataHora.isEmpty() || quantidadeStr.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos corretamente", Toast.LENGTH_SHORT).show()
            return
        }

        val quantidade = quantidadeStr.toDoubleOrNull()
        if (quantidade == null || quantidade <= 0) {
            Toast.makeText(this, "Quantidade inválida", Toast.LENGTH_SHORT).show()
            return
        }

        val novaColeta = NovaColetaRequest(
            cliente_id = clienteIdSelecionado!!,
            usuario_id = 1, // Aqui você coloca o ID real do usuário logado
            data_hora = dataHora,
            quantidade_oleo_litros = quantidade
        )

        lifecycleScope.launch {
            try {
                val response = apiService.incluirColeta(novaColeta)
                if (response.isSuccessful && response.body()?.get("success") == true) {
                    Toast.makeText(this@NovaColetaActivity, "Coleta salva com sucesso!", Toast.LENGTH_SHORT).show()

                    lifecycleScope.launch {
                        delay(1500) // Espera 2 segundos

                        // Abre tela de Histórico
                        val intentHistorico = Intent(this@NovaColetaActivity, HistoricoColetasActivity::class.java)
                        startActivity(intentHistorico)

                    }

            } else {
                    Toast.makeText(this@NovaColetaActivity, "Erro ao salvar coleta", Toast.LENGTH_LONG).show()
                    Log.e("API", "Erro: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Toast.makeText(this@NovaColetaActivity, "Falha na requisição", Toast.LENGTH_LONG).show()
                e.printStackTrace()
            }
        }
    }

    private fun mostrarDateTimePicker() {
        val calendar = Calendar.getInstance()

        val datePicker = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                val horaAtual = calendar.get(Calendar.HOUR_OF_DAY)
                val minutoAtual = calendar.get(Calendar.MINUTE)

                val timePicker = TimePickerDialog(
                    this,
                    { _, hourOfDay, minute ->
                        val dataFormatada = String.format(
                            "%04d-%02d-%02d %02d:%02d:00",
                            year, month + 1, dayOfMonth, hourOfDay, minute
                        )
                        dataHoraEditText.setText(dataFormatada)
                    },
                    horaAtual,
                    minutoAtual,
                    true
                )
                timePicker.show()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        datePicker.show()
    }



    private fun configurarRetrofit() {
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
            .baseUrl("http://192.168.1.110/") // Troque pelo IP do seu servidor
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

        apiService = retrofit.create(ApiService::class.java)
    }

    private fun carregarEmpresas() {
        lifecycleScope.launch {
            try {
                listaEmpresas = apiService.listarEmpresas()

                if (listaEmpresas.isEmpty()) {
                    Toast.makeText(this@NovaColetaActivity, "Nenhuma empresa disponível", Toast.LENGTH_LONG).show()
                    return@launch
                }

                Log.d("API", "Empresas recebidas: ${listaEmpresas}")

                val nomesEmpresas = listaEmpresas.map { it.nome ?: "Empresa sem nome" }

                val adapter = ArrayAdapter(
                    this@NovaColetaActivity,
                    android.R.layout.simple_spinner_item,
                    nomesEmpresas
                )
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinnerEmpresas.adapter = adapter

                configurarSelecaoSpinner()

            } catch (e: Exception) {
                Toast.makeText(this@NovaColetaActivity, "Erro ao carregar empresas", Toast.LENGTH_SHORT).show()
                e.printStackTrace()
            }
        }

    }

    private fun configurarSelecaoSpinner() {
        spinnerEmpresas.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: android.widget.AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                val empresaSelecionada = listaEmpresas[position]
                clienteIdSelecionado = empresaSelecionada.id

                Log.d("EmpresaSelecionada", "ID=${empresaSelecionada.id}, Nome=${empresaSelecionada.nome}")
                Toast.makeText(
                    this@NovaColetaActivity,
                    "Empresa selecionada: ${empresaSelecionada.nome}",
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onNothingSelected(parent: android.widget.AdapterView<*>) {
                clienteIdSelecionado = null
            }
        }
    }
}
