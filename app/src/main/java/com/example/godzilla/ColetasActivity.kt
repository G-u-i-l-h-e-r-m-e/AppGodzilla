package com.example.godzilla

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ColetasActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_coletas)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val btnHistoricoColetas =  findViewById<Button>(R.id.historicoColetas)

        val btnNovaColeta =  findViewById<Button>(R.id.btnNovaColeta)

        btnNovaColeta.setOnClickListener{
            val intent = Intent(this@ColetasActivity, NovaColetaActivity::class.java)
            startActivity(intent)
            finish()
        }

        btnHistoricoColetas.setOnClickListener{
            val intent = Intent(this@ColetasActivity, HistoricoColetasActivity::class.java)
            startActivity(intent)
            finish()
        }
    }


}