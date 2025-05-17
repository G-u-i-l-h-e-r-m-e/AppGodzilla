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
import com.example.godzilla.network.Coleta

class CertificadoColetaAdapter(
    private val dataSet: MutableList<Coleta>,
    private val apiService: ApiService,
    private val onEditarClick: (Coleta) -> Unit
) : RecyclerView.Adapter<CertificadoColetaAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nome: TextView = view.findViewById(R.id.nome_fantasia)
        val dataHora: TextView = view.findViewById(R.id.data_hora)
        val qtdOleoLitros: TextView = view.findViewById(R.id.qtdOleoLitros)
        val btnGerar: Button = view.findViewById(R.id.btnGerar)

    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.item_certificado_coletas, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val coleta = dataSet[position]
        viewHolder.nome.text = coleta.nome_fantasia
        viewHolder.dataHora.text = coleta.data_hora
        viewHolder.qtdOleoLitros.text = coleta.qtdOleoLitros.toString()

        viewHolder.btnGerar.setOnClickListener {
            val context = it.context
            Toast.makeText(context, "Gerar certificado para ID: ${coleta.id}", Toast.LENGTH_SHORT).show()

            // Aqui você pode abrir uma nova tela ou iniciar geração de certificado
        }


    }

    override fun getItemCount() = dataSet.size
}
