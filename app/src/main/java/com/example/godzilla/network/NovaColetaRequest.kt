package com.example.godzilla.network

data class NovaColetaRequest(
    val cliente_id: Int,
    val usuario_id: Int,
    val data_hora: String,
    val quantidade_oleo_litros: Double
)
