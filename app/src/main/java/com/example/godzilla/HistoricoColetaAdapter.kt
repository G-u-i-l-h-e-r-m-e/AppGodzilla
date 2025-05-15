package com.example.godzilla

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.godzilla.network.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class HistoricoColetaAdapter(
    private val dataSet: MutableList<HistoricoColetasActivity.Coleta>,
    private val apiService: ApiService,
    private val onEditarClick: (HistoricoColetasActivity.Coleta) -> Unit
) : RecyclerView.Adapter<HistoricoColetaAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nome: TextView = view.findViewById(R.id.nome_fantasia)
        val dataHora: TextView = view.findViewById(R.id.data_hora)
        val qtdOleoLitros: TextView = view.findViewById(R.id.qtdOleoLitros)
        val btnEditar: Button = view.findViewById(R.id.btnEditar)
        val btnExcluir: Button = view.findViewById(R.id.btnExcluir)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.item_historico_coletas, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val coleta = dataSet[position]
        viewHolder.nome.text = coleta.nome_fantasia
        viewHolder.dataHora.text = coleta.data_hora
        viewHolder.qtdOleoLitros.text = coleta.qtdOleoLitros.toString()

        viewHolder.btnEditar.setOnClickListener {
            onEditarClick(coleta) // ← chama a função da Activity
        }

        viewHolder.btnExcluir.setOnClickListener { btnView ->
            val context = btnView.context

            AlertDialog.Builder(context)
                .setTitle("Confirmar exclusão")
                .setMessage("Tem certeza que deseja excluir esta coleta?")
                .setPositiveButton("Sim") { _, _ ->
                    apiService.deletarColeta(coleta.id).enqueue(object : Callback<Void> {
                        override fun onResponse(call: Call<Void>, response: Response<Void>) {
                            if (response.isSuccessful) {
                                Toast.makeText(context, "Coleta deletada com sucesso!", Toast.LENGTH_SHORT).show()

                                val pos = viewHolder.adapterPosition
                                if (pos != RecyclerView.NO_POSITION) {
                                    dataSet.removeAt(pos)
                                    notifyItemRemoved(pos)
                                }
                            } else {
                                Toast.makeText(context, "Erro ao deletar coleta", Toast.LENGTH_SHORT).show()
                            }
                        }

                        override fun onFailure(call: Call<Void>, t: Throwable) {
                            Toast.makeText(context, "Erro na conexão ao deletar", Toast.LENGTH_SHORT).show()
                        }
                    })
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }

    }

    override fun getItemCount() = dataSet.size
}
