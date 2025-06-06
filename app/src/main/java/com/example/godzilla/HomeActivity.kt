package com.example.godzilla

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Usa o mesmo nome do arquivo de SharedPreferences
        val prefs = getSharedPreferences("com.example.godzilla.preferences", MODE_PRIVATE)
        val nomeSalvo = prefs.getString("usuario_nome", null)

        val saudacao = findViewById<TextView>(R.id.txtVSaudacao)

        if (!nomeSalvo.isNullOrEmpty()) {
            saudacao.text = "OLÁ, BEM-VINDO, ${nomeSalvo.uppercase()}!"
        } else {
            saudacao.text = "OLÁ, NOME NÃO ENCONTRADO"
        }

        val btnAbrirSite = findViewById<Button>(R.id.btnAbrirSite)
        btnAbrirSite.setOnClickListener {
            val url = "https://www.google.com"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            if (intent.resolveActivity(packageManager) != null) {
                startActivity(intent)
            } else {
                Toast.makeText(this, "Nenhum navegador encontrado.", Toast.LENGTH_SHORT).show()
            }
        }

        val btnColetas = findViewById<Button>(R.id.btnColetas)
        btnColetas.setOnClickListener {
            val intent = Intent(this@HomeActivity, ColetasActivity::class.java)
            startActivity(intent)
        }

        val btnLogout = findViewById<ImageButton>(R.id.btnLogout)
        btnLogout.setOnClickListener {
            val prefs = getSharedPreferences("com.example.godzilla.preferences", MODE_PRIVATE)
            val editor = prefs.edit()
            editor.clear()
            editor.apply()

            val intent = Intent(this@HomeActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}

