package com.kotlin.patrick.paypal

import android.util.Base64
import android.util.Log
import com.kotlin.patrick.ConfigSamplesHelper
import com.kotlin.patrick.KotlinApplication
import com.kotlin.patrick.paypal.models.AccessTokenResponse
import com.kotlin.patrick.paypal.models.CreatePaymentResponse
import com.kotlin.patrick.paypal.models.ExecutePaymentResponse
import com.paypal.android.lib.riskcomponent.RiskComponent
import com.paypal.android.lib.riskcomponent.SourceApp
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PayPalNetwork(var presenter: PayPalPresenter) {


    val api by lazy { PayPalService.create() }
    var accessToken: String? = null
    private val exampleRequest = ConfigSamplesHelper.getCreatePaymentExample().replace(
            "{{INVOICE_NUMBER}}", System.currentTimeMillis().toString())

    fun getAccessToken() {

        // IMPORTANT: *Never* expose API credentials client-side! This is for sample purposes only.
        val clientID = ConfigSamplesHelper.getConfigValue("client-id")
        val secret = ConfigSamplesHelper.getConfigValue("secret")

        val call = api.getToken("client_credentials", "Basic " +
                Base64.encodeToString((clientID + ":" + secret).toByteArray(), Base64.NO_WRAP))

        call.enqueue(object : Callback<AccessTokenResponse> {
            override fun onResponse(call: Call<AccessTokenResponse>?, response: Response<AccessTokenResponse>?) =

                    if (response != null && response.isSuccessful) {
                        accessToken = response.body()?.access_token
                        presenter.onAccessTokenSuccess(accessToken)
                    } else {
                        Log.d("PAYPAL", "Fail: Bad response. See OkHttp logging.")
                        presenter.onAccessTokenFailure()
                    }

            override fun onFailure(call: Call<AccessTokenResponse>?, t: Throwable?) {
                Log.d("PAYPAL", "Fail: No response. See OkHttp logging.")
                presenter.onAccessTokenFailure()
            }
        }
        )
    }

    fun createPayment() {

        val call = api.createPayment("Bearer " + accessToken, "application/json",
                ConfigSamplesHelper.getJSON(exampleRequest))

        call.enqueue(object : Callback<CreatePaymentResponse> {
            override fun onResponse(call: Call<CreatePaymentResponse>?, response: Response<CreatePaymentResponse>?) =

                    if (response != null && response.isSuccessful) {
                        val responseBody = response.body()
                        if (responseBody?.id != null) {
                            // Initialise the risk component with the payment ID
                            val additionalParams = HashMap<String, Any>()
                            additionalParams.put("RISK_MANAGER_PAIRING_ID", responseBody.id)
                            //System.setProperty("dyson.debug.mode", true.toString())
                            RiskComponent.getInstance().init(KotlinApplication.appContext,
                                    SourceApp.UNKNOWN, "1.0", additionalParams)
                        }
                        presenter.onCreatePaymentSuccess(responseBody)

                    } else {
                        Log.d("PAYPAL", "Fail: Bad response. See OkHttp logging.")
                        presenter.onCreatePaymentFailure()
                    }

            override fun onFailure(call: Call<CreatePaymentResponse>?, t: Throwable?) {
                Log.d("PAYPAL", "Fail: No response. See OkHttp logging.")
                presenter.onCreatePaymentFailure()
            }
        }
        )
    }

    fun finalisePayment(id: String, payerId: String) {

        val riskPairingId = RiskComponent.getInstance().pairingID
        val call = api.executePayment("Bearer " + accessToken, "application/json",
                riskPairingId, id, ConfigSamplesHelper.getJSON("{\"payer_id\":\"$payerId\",}"))

        call.enqueue(object : Callback<ExecutePaymentResponse> {
            override fun onResponse(call: Call<ExecutePaymentResponse>?, response: Response<ExecutePaymentResponse>?) =
                    if (response != null && response.isSuccessful) {
                        presenter.onExecutePaymentSuccess(response.body())
                    } else {
                        Log.d("PAYPAL", "Fail: Bad response. See OkHttp logging.")
                        presenter.onExecutePaymentFailure()
                    }

            override fun onFailure(call: Call<ExecutePaymentResponse>?, t: Throwable?) {
                Log.d("PAYPAL", "Fail: No response. See OkHttp logging.")
                presenter.onExecutePaymentFailure()
            }
        }
        )
    }
}