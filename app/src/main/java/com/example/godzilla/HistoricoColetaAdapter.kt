package com.example.godzilla

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.time.format.DateTimeFormatter


class HistoricoColetaAdapter (private val dataSet: List<HistoricoColetasActivity.Coleta>):

    RecyclerView.Adapter<HistoricoColetaAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nome: TextView = view.findViewById(R.id.nome_fantasia)
        val dataHora: TextView = view.findViewById(R.id.data_hora)
        val qtdOleoLitros: TextView = view.findViewById(R.id.qtdOleoLitros)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.item_historico_coletas, viewGroup, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val produto = dataSet[position]
        viewHolder.nome.text = produto.nome_fantasia
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        viewHolder.dataHora.text = produto.data_hora.format(formatter)
        viewHolder.qtdOleoLitros.text = produto.qtdOleoLitros.toString()
    }


    override fun getItemCount() = dataSet.size
}

