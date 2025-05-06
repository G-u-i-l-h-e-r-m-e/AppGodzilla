package com.example.godzilla

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.godzilla.network.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class HistoricoColetaAdapter(
    private val dataSet: List<HistoricoColetasActivity.Coleta>,
    private val apiService: ApiService // <-- Recebendo a API
) : RecyclerView.Adapter<HistoricoColetaAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nome: TextView = view.findViewById(R.id.nome_fantasia)
        val dataHora: TextView = view.findViewById(R.id.data_hora)
        val qtdOleoLitros: TextView = view.findViewById(R.id.qtdOleoLitros)
        val btnEditar: Button = view.findViewById(R.id.btnEditar)
        val btnExcluir: Button = view.findViewById(R.id.btnExcluir)
    }

    private val entradaFormatter = DateTimeFormatter.ISO_LOCAL_DATE
    private val saidaFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.item_historico_coletas, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val coleta = dataSet[position]
        viewHolder.nome.text = coleta.nome_fantasia

        try {
            val data = LocalDate.parse(coleta.data_hora, entradaFormatter)
            viewHolder.dataHora.text = data.format(saidaFormatter)
        } catch (e: Exception) {
            viewHolder.dataHora.text = coleta.data_hora
        }

        viewHolder.qtdOleoLitros.text = coleta.qtdOleoLitros.toString()

        // ✅ Botão Editar
        viewHolder.btnEditar.setOnClickListener {
            val context = it.context
            val intent = Intent(context, EditarHistoricoColetas::class.java)


            intent.putExtra("nome_fantasia", coleta.nome_fantasia)
            intent.putExtra("DATA_HORA", coleta.data_hora)
            intent.putExtra("QTD_OLEO_LITROS", coleta.qtdOleoLitros)

            context.startActivity(intent)
        }

        // ✅ Botão Deletar
        viewHolder.btnExcluir.setOnClickListener { btnView ->
            apiService.deletarColeta(coleta.id).enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        Toast.makeText(btnView.context, "Coleta deletada com sucesso!", Toast.LENGTH_LONG).show()
                        // Aqui você poderia notificar para atualizar a lista (idealmente)
                    } else {
                        Toast.makeText(btnView.context, "Erro ao deletar coleta", Toast.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Toast.makeText(btnView.context, "Erro na conexão ao deletar", Toast.LENGTH_LONG).show()
                }
            })
        }
    }

    override fun getItemCount() = dataSet.size
}
