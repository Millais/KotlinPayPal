package com.kotlin.patrick.braintree

import com.beust.klaxon.JsonObject
import com.kotlin.patrick.ConfigSamplesHelper
import com.kotlin.patrick.braintree.models.CheckoutResponse
import com.kotlin.patrick.braintree.models.ClientTokenResponse
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface BraintreeService {

    @GET("client_token")
    fun getClientToken(): Call<ClientTokenResponse>

    @POST("checkout")
    fun checkoutPayment(@Body body: JsonObject): Call<CheckoutResponse>

    companion object Factory {

        fun create(): BraintreeService {
            // Log all network requests and responses
            val interceptor = HttpLoggingInterceptor()
            interceptor.level = HttpLoggingInterceptor.Level.BODY
            val client = OkHttpClient.Builder().addInterceptor(interceptor).build()

            val retrofit = Retrofit.Builder()
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .baseUrl(ConfigSamplesHelper.getConfigValue("braintree-endpoint"))
                    .build()

            return retrofit.create<BraintreeService>(BraintreeService::class.java)
        }
    }
}