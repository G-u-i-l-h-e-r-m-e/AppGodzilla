package com.example.godzilla.network

import com.example.godzilla.HistoricoColetasActivity
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    @GET("/apis/routes/historicoColetas.php")
    fun getHistoricoColetas(): Call<List<Coleta>>

    @POST("/apis/routes/editarHistoricoColetas.php")
    fun editarColeta(
        @Body coleta: ColetaRequest
    ): Call<Void>

    // Interface Retrofit
    @POST("/apis/routes/excluirHistoricoColetas.php")
    fun deletarColeta(@Body request: ExcluirColetaRequest): Call<Void>

}
