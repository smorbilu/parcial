package com.primera.evaluacion.network

import com.primera.evaluacion.data.ProductResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ProductApiService {
    @GET("objects")
    suspend fun getProducts(): Response<List<ProductResponse>>

    @GET("objects/{id}")
    suspend fun getProductById(@Path("id") id: String): Response<ProductResponse>
}
