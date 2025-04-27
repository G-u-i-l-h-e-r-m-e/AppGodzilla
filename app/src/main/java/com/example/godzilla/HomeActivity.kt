package com.example.godzilla

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
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

//        var nomeSaudacao = intent.getStringExtra("nome") //passar o valor de uma activity para outra
//        val saudacao = findViewById<TextView>(R.id.txtVSaudacao)
//
//        saudacao.text = "OLÁ, BEM VINDO $nomeSaudacao!"

        val btnColetas =  findViewById<Button>(R.id.btnColetas)

        btnColetas.setOnClickListener{
            val intent = Intent(this@HomeActivity, ColetasActivity::class.java)
            startActivity(intent)
            finish()
        }



    }
}