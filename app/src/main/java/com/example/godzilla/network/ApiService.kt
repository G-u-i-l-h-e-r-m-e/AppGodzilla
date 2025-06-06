package com.example.godzilla.network

import com.example.godzilla.HistoricoColetasActivity
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    @GET("/apis/routes/historicoColetas.php")
    fun getHistoricoColetas(): Call<List<Coleta>>


    @GET("/apis/routes/listarClientes.php")
    suspend fun listarEmpresas(): List<Empresa>


    @POST("/apis/routes/editarHistoricoColetas.php")
    fun editarColeta(
        @Body coleta: ColetaRequest
    ): Call<Void>

    // Interface Retrofit
    @POST("/apis/routes/excluirHistoricoColetas.php")
    fun deletarColeta(@Body request: ExcluirColetaRequest): Call<Void>

    @POST("/apis/routes/addColeta.php")
    suspend fun incluirColeta(@Body novaColeta: NovaColetaRequest): retrofit2.Response<Map<String, Any>>

    @GET("apis/routes/certificadoColeta.php")
    fun gerarCertificado(
        @Query("coleta_id") coletaId: Int
    ): Call<ResponseBody>

}
