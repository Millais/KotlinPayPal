package com.kotlin.patrick.paypal

import com.beust.klaxon.JsonObject
import com.kotlin.patrick.ConfigSamplesHelper
import com.kotlin.patrick.paypal.models.AccessTokenResponse
import com.kotlin.patrick.paypal.models.CreatePaymentResponse
import com.kotlin.patrick.paypal.models.ExecutePaymentResponse
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface PayPalService {

    // An interceptor can be used as an alternate way to authenticate each request
    @POST("oauth2/token")
    fun getToken(@Query("grant_type") grantType: String,
                 @Header("Authorization") authorization: String
    ): Call<AccessTokenResponse>

    @POST("payments/payment")
    fun createPayment(@Header("Authorization") authorization: String,
                      @Header("Content-Type") contentType: String,
                      @Body body: JsonObject): Call<CreatePaymentResponse>

    @POST("payments/payment/{payment_Id}/execute")
    fun executePayment(@Header("Authorization") authorization: String,
                       @Header("Content-Type") contentType: String,
                       @Header("PayPal-Client-Metadata-Id") clientMetadataId: String,
                       @Path("payment_Id") id: String,
                       @Body body: JsonObject): Call<ExecutePaymentResponse>

    companion object Factory {

        fun create(): PayPalService {
            // Log all network requests and responses
            val interceptor = HttpLoggingInterceptor()
            interceptor.level = HttpLoggingInterceptor.Level.BODY
            val client = OkHttpClient.Builder().addInterceptor(interceptor).build()

            val retrofit = Retrofit.Builder()
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .baseUrl(ConfigSamplesHelper.getConfigValue("paypal-endpoint"))
                    .build()

            return retrofit.create<PayPalService>(PayPalService::class.java)
        }
    }
}