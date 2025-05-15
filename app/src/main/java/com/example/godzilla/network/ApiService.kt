package com.example.godzilla.network

import com.example.godzilla.HistoricoColetasActivity
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    @GET("/apis/routes/historicoColetas.php")
    fun getHistoricoColetas(): Call<List<HistoricoColetasActivity.Coleta>>

    @POST("/apis/routes/editarHistoricoColetas.php")
    fun editarColeta(
        @Body coleta: ColetaRequest
    ): Call<Void>

    @FormUrlEncoded
    @POST("/apis/routes/excluirHistoricoColetas.php")
    fun deletarColeta(
        @Field("ID") id: Int
    ): Call<Void>

}
