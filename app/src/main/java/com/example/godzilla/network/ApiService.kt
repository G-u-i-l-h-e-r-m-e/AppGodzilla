package com.example.godzilla.network

import com.example.godzilla.HistoricoColetasActivity
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    @GET("/apis/routes/historicoColetas.php")
    fun getHistoricoColetas(): Call<List<HistoricoColetasActivity.Coleta>>

    @FormUrlEncoded
    @POST("/apis/routes/editarHistoricoColetas.php")
    fun editarColeta(
        @Field("COLETA_ID") coletaId: Int,
        @Field("NOME_FANTASIA") nomeFantasia: String,
        @Field("DATA_HORA") dataHora: String,
        @Field("QTD_OLEO_LITROS") qtdOleoLitros: Double
    ): Call<Void>

    @FormUrlEncoded
    @POST("/apis/routes/excluirHistoricoColetas.php")
    fun deletarColeta(
        @Field("ID") id: Int
    ): Call<Void>

}
