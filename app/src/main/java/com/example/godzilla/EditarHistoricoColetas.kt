package com.example.godzilla

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.godzilla.network.Coleta
import com.example.godzilla.network.ApiService
import com.example.godzilla.network.ColetaRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

class EditarHistoricoColetas : AppCompatActivity() {

    private lateinit var clienteIdEditText: EditText
    private lateinit var dataHoraEditText: EditText
    private lateinit var qtdOleoLitrosEditText: EditText
    private lateinit var salvarButton: Button

    private var coletaId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_editar_historico_coletas)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        clienteIdEditText = findViewById(R.id.nome_fantasia)
        dataHoraEditText = findViewById(R.id.data_hora)
        qtdOleoLitrosEditText = findViewById(R.id.qtdOleoLitros)
        salvarButton = findViewById(R.id.btnSalvar)

        // Resgatar os dados passados pela Intent
        coletaId = intent.getIntExtra("ID", 0)
        Log.d("DEBUG", "ID recebido na edição: $coletaId")
        clienteIdEditText.setText(intent.getStringExtra("nome_fantasia"))
        dataHoraEditText.setText(intent.getStringExtra("DATA_HORA"))
        qtdOleoLitrosEditText.setText(intent.getDoubleExtra("QTD_OLEO_LITROS", 0.0).toString())

        // Configuração do Retrofit
        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.135.111.26/apis/routes/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(ApiService::class.java)

        salvarButton.setOnClickListener {
            val coletaRequest = ColetaRequest(
            id = coletaId,
            data_hora = dataHoraEditText.text.toString(),
            qtdOleoLitros = qtdOleoLitrosEditText.text.toString()
        )

            apiService.editarColeta(coletaRequest).enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@EditarHistoricoColetas, "Coleta atualizada com sucesso!", Toast.LENGTH_LONG).show()
                        setResult(Activity.RESULT_OK)
                        finish()
                    } else {
                        val errorBody = response.errorBody()?.string()
                        Log.e("API_ERROR", "Código: ${response.code()} - Erro: $errorBody")
                        Toast.makeText(this@EditarHistoricoColetas, "Erro na atualização", Toast.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Toast.makeText(this@EditarHistoricoColetas, "Erro ao atualizar a coleta", Toast.LENGTH_LONG).show()
                    Log.e("API_ERROR", "Falha: ${t.message}")
                }
            })

        }

    }


}
