package com.example.godzilla

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.godzilla.network.Coleta

class CertificadoColetaAdapter(
    private val context: Context,
    private val listaColetas: MutableList<Coleta>
) : RecyclerView.Adapter<CertificadoColetaAdapter.VH>() {

    inner class VH(v: View) : RecyclerView.ViewHolder(v) {
        private val nomeFantasia  = v.findViewById<TextView>(R.id.nome_fantasia)
        private val dataHora      = v.findViewById<TextView>(R.id.data_hora)
        private val qtdLitros     = v.findViewById<TextView>(R.id.qtdOleoLitros)
        private val btnGerarPdf   = v.findViewById<ImageView>(R.id.btnGerar)

        fun bind(c: Coleta) {
            nomeFantasia.text = c.nome_fantasia
            dataHora.text     = c.data_hora
            qtdLitros.text    = "${c.qtdOleoLitros} L"

            btnGerarPdf.setOnClickListener {
                val url = "http://192.168.1.110/apis/routes/certificadoColeta.php?coleta_id=${c.id}"
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VH(
        LayoutInflater.from(parent.context)
            .inflate(R.layout.item_certificado_coletas, parent, false)
    )

    override fun onBindViewHolder(holder: VH, position: Int) =
        holder.bind(listaColetas[position])

    override fun getItemCount() = listaColetas.size
}
